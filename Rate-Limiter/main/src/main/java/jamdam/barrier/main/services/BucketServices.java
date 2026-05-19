package jamdam.barrier.main.services;
import jamdam.barrier.main.entity.RateLimitPolicy;
import jamdam.barrier.main.DTO.RateLimitResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;


@Service
public class BucketServices {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisScript<List> tokenBucketScript;

    public RateLimitResult allow(
            String userId,
            RateLimitPolicy policy
    ) {

        String key =
                "rate_limit:{" + userId + "}";

        List result =
                redisTemplate.execute(
                        tokenBucketScript,
                        Collections.singletonList(key),
                        String.valueOf(policy.getCapacity()),
                        String.valueOf(policy.getRefillrate()),
                        String.valueOf(System.currentTimeMillis()),
                        String.valueOf(policy.getCost())
                );

        if (result == null || result.size() < 4) {
            throw new RuntimeException(
                    "Invalid Lua response"
            );
        }

        boolean allowed =
                ((Long) result.get(0)) == 1;

        double remainingTokens =
                ((Number) result.get(1)).doubleValue();

        long retryAfter =
                ((Number) result.get(2)).longValue();

        long resetTime =
                ((Number) result.get(3)).longValue();

        return new RateLimitResult(
                allowed,
                remainingTokens,
                retryAfter,
                resetTime
        );
    }
}