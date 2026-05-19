local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local cost = tonumber(ARGV[4])

local tokens =
tonumber(
        redis.call(
                'HGET',
                key,
                'tokens'
        )
)

local lastRefill =
tonumber(
        redis.call(
                'HGET',
                key,
                'last_refill_time'
        )
)

if tokens == nil then
    tokens = capacity
end

if lastRefill == nil then
    lastRefill = now
end

local elapsed =
(now - lastRefill) / 1000.0

tokens =
math.min(
        capacity,
        tokens + (elapsed * refillRate)
)

local allowed = 0
local retryAfter = 0

if tokens >= cost then

    tokens = tokens - cost
    allowed = 1

else

    retryAfter =
    math.ceil(
            (cost - tokens) / refillRate
    )

end

redis.call(
        'HSET',
        key,

        'tokens',
        tokens,

        'last_refill_time',
        now
)

local ttl =
math.ceil(
        capacity / refillRate
) * 2

redis.call(
        'EXPIRE',
        key,
        ttl
)

local resetTime =
math.ceil(
        (capacity - tokens) / refillRate
)

return {
    allowed,
    tokens,
    retryAfter,
    resetTime
}