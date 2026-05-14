local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local cost = tonumber(ARGV[4])

local tokens = tonumber(redis.call('HGET', key, 'tokens'))
local lastRefill = tonumber(redis.call('HGET', key, 'last_refill_time'))

if tokens == nil then
    tokens = capacity
    lastRefill = now
end

local elapsed = (now - lastRefill) / 1000.0

tokens = math.min(
        capacity,
        tokens + (elapsed * refillRate)
)

local allowed = 0

if tokens >= cost then
    tokens = tokens - cost
    allowed = 1
end

redis.call(
        'HSET',
        key,
        'tokens', tokens,
        'last_refill_time', now
)

redis.call('EXPIRE', key, 3600)

return allowed