package jamdam.barrier.main.services;

import jamdam.barrier.main.entity.RateLimitPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class BucketServices {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Long> tokenBucketScript;

    public boolean allow(String userId, RateLimitPolicy rateLimitPolicy) {

        String key = "rate_limit:" + userId;
        System.out.println("SERVICE HIT");

        Long result = redisTemplate.execute(
                tokenBucketScript,
                Collections.singletonList(key),

                String.valueOf(rateLimitPolicy.getCapacity()),
                String.valueOf(rateLimitPolicy.getRefillrate()),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(rateLimitPolicy.getCost())
        );

        System.out.println(
                "RESULT = " + result
        );

        return result != null && result == 1;
    }
}