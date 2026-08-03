# Barrier — Distributed Rate Limiter

A production-shaped API rate limiter built as a Spring Cloud Gateway filter, backed by Redis and a
token-bucket algorithm implemented in Lua for atomicity across concurrent requests and multiple
gateway instances.

Requests are throttled per client, per endpoint, with configurable bucket capacity, refill rate and
request cost. Rejected requests receive a standards-compliant `429` with `Retry-After`.

---

## Why Lua

The naive implementation of a token bucket reads the current token count, computes the refill, then
writes the new count back:

```
GET tokens  ->  compute  ->  SET tokens
```

Under concurrency this is a classic read-modify-write race. Two requests can both read `1 token
remaining`, both decide they are allowed, and both write back `0` — admitting two requests against a
budget of one. Adding more gateway instances makes it worse, not better.

Redis executes a Lua script as a single atomic unit. Moving the entire check-refill-decrement
sequence into `tokenBucket.lua` means no interleaving is possible, regardless of how many threads or
gateway replicas are hitting the same key. No distributed locks, no CAS retry loop, one round trip.

---

## Architecture

```
                  ┌────────────────────────────┐
   client ───────▶│  Gateway  (:8080)          │
                  │                            │
                  │  Filter                    │
                  │    ├─ IdentifierResolver   │  who is this caller?
                  │    ├─ PolicyService        │  which policy applies?
                  │    └─ BucketServices ──────┼──────▶ Redis (:6379)
                  │                            │        EVAL tokenBucket.lua
                  │  MetricsServices           │        (atomic)
                  └──────────┬─────────────────┘
                             │ allowed
                             ▼
                  ┌────────────────────────────┐
                  │  Backend service (:8081)   │
                  └────────────────────────────┘
```

| Component            | Responsibility                                                        |
| -------------------- | --------------------------------------------------------------------- |
| `Filter`             | `OncePerRequestFilter` — orchestrates the check, sets headers, emits 429 |
| `IdentifierResolver` | Derives the bucket key from the request (IP-based by default)          |
| `PolicyService`      | Resolves the endpoint's policy, falling back to the default           |
| `BucketServices`     | Executes the Lua script and maps the reply to a `RateLimitResult`      |
| `tokenBucket.lua`    | Atomic refill + consume + TTL, returns `{allowed, tokens, retryAfter, resetTime}` |
| `MetricsServices`    | Micrometer counters for total / allowed / blocked / Redis failures     |

---

## Features

- **Token bucket with fractional refill** — bursts are absorbed up to capacity, then smoothed to the
  refill rate.
- **Per-endpoint policies** — each route gets its own capacity, refill rate and cost; unmatched
  routes fall back to a default policy.
- **Weighted request costs** — expensive endpoints can consume more than one token per call.
- **Cluster-safe keys** — bucket keys use a `{userId}` hash tag so all operations for one caller land
  in a single Redis Cluster slot, which is what makes the Lua script legal in cluster mode.
- **Fail-open under Redis failure** — if Redis is unreachable the request is allowed through and the
  response is tagged `X-RateLimit-Status: DEGRADED`. Availability is preferred over enforcement; a
  rate limiter should not become the reason your API is down.
- **Self-expiring buckets** — each key gets a TTL of `ceil(capacity / refillRate) * 2` seconds, so
  idle callers cost nothing and Redis memory stays bounded without a sweeper job.
- **Standard response headers** on every request, throttled or not.
- **Prometheus-ready metrics** via Micrometer.

---

## Quick start

### Docker Compose

```bash
cd Rate-Limiter/main && ./mvnw clean package -DskipTests && cd ..
docker compose up --build
```

Gateway starts on `:8080` with Redis alongside it.

### Running locally

```bash
# 1. Redis
docker run -p 6379:6379 redis

# 2. Backend service (proxied target)
cd test && ./mvnw spring-boot:run          # :8081

# 3. Gateway
cd Rate-Limiter/main && ./mvnw spring-boot:run   # :8080
```

Requires JDK 21.

---

## Configuration

Policies live in `application.yaml` and bind to `RateLimitProperties`:

```yaml
rate-limit:
  default-policy:
    capacity: 10          # bucket size — the maximum burst
    refill-rate: 1        # tokens added per second
    cost: 1               # tokens consumed per request

  endpoint-policies:
    /login:
      capacity: 10
      refill-rate: 0.5    # 1 request per 2s sustained — brute-force resistant
      cost: 1
    /search:
      capacity: 30
      refill-rate: 5
      cost: 2             # search is expensive; charge double
    /premium:
      capacity: 100
      refill-rate: 20
      cost: 1
```

Redis connection is environment-driven:

```yaml
spring.data.redis.host: ${SPRING_DATA_REDIS_HOST:localhost}
spring.data.redis.port: ${SPRING_DATA_REDIS_PORT:6379}
```

---

## Behaviour

Every response carries the current bucket state:

| Header                  | Meaning                                              |
| ----------------------- | ---------------------------------------------------- |
| `X-RateLimit-Limit`     | Bucket capacity for the matched policy                |
| `X-RateLimit-Remaining` | Tokens left after this request                        |
| `X-RateLimit-Reset`     | Seconds until the bucket is full again                |
| `Retry-After`           | Seconds until the next token is available (429 only)  |
| `X-RateLimit-Status`    | `DEGRADED` when Redis was unreachable and the request was let through |

Throttled requests return `429 Too Many Requests`.

### Try it

```bash
# Allowed — inspect the headers
curl -i http://localhost:8080/test

# Exhaust the /login bucket (capacity 10, refill 0.5/s)
for i in $(seq 1 15); do
  curl -s -o /dev/null -w "%{http_code} " http://localhost:8080/login
done
# 200 200 200 200 200 200 200 200 200 200 429 429 429 429 429

# Proxied route through the gateway
curl -i http://localhost:8080/backend
```

---

## Observability

Micrometer counters, exposed at `/actuator/prometheus`:

| Metric             | Description                                    |
| ------------------ | ---------------------------------------------- |
| `requests.total`   | All requests seen by the filter                 |
| `requests.allowed` | Requests admitted                               |
| `requests.blocked` | Requests rejected with 429                      |
| `redis.failures`   | Redis errors that triggered fail-open behaviour |

The blocked/total ratio is the signal worth alerting on — a sudden spike means either an attack or a
policy that is too tight for real traffic.

---

## Load testing

`LoadTest` drives 10,000 concurrent calls through a 200-thread pool against a bucket of capacity 100:

```bash
cd Rate-Limiter/main && ./mvnw test -Dtest=LoadTest
```

With a refill rate of 1 token/second, the admitted count should stay at roughly
`capacity + elapsed_seconds` no matter how much concurrency is thrown at it. Any excess is direct
evidence of a lost update — which is exactly what the Lua script exists to prevent.

---

## Project layout

```
Rate-Limiter/
├── docker-compose.yml           # gateway + redis
├── Dockerfile
└── main/                        # the gateway service
    └── src/main/
        ├── java/jamdam/barrier/main/
        │   ├── services/        # Filter, BucketServices, PolicyService, MetricsServices
        │   ├── resolver/        # IdentifierResolver + IP implementation
        │   ├── configuration/   # RedisConfig, RateLimitProperties
        │   ├── entity/          # RateLimitPolicy, TokenBucket
        │   └── DTO/             # RateLimitResult
        └── resources/
            ├── scripts/tokenBucket.lua
            └── application.yaml

test/                            # minimal downstream service on :8081
```

---

## Limitations & roadmap

Known gaps, kept visible rather than hidden:

- **Clock source.** Timestamps are supplied by the gateway via `System.currentTimeMillis()`. Across
  multiple instances, clock skew distorts the refill calculation. Moving to `redis.call('TIME')`
  inside the script would give every replica one authoritative clock.
- **Client identification behind a proxy.** `IpIdentifierResolver` uses `getRemoteAddr()`, which
  resolves to the load balancer's address once deployed behind one, collapsing all callers into a
  single bucket. Needs `X-Forwarded-For` parsing with a trusted-hop count — trusting the header
  blindly would let clients spoof their way into a fresh bucket per request.
- **Identification strategy.** `IdentifierResolver` is an interface precisely so API-key or
  authenticated-user keying can be dropped in; only the IP implementation exists today.
- **Algorithm.** Token bucket only. Sliding-window log and leaky bucket would make useful
  comparisons.

## License

MIT
