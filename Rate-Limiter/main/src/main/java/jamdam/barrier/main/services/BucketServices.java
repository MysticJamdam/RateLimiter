package jamdam.barrier.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class BucketServices {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisScript<Long> tokenBucketScript;

    public boolean allow(String userId, int cost) {

        if (cost <= 0) {
            throw new IllegalArgumentException(
                    "cost must be greater than 0"
            );
        }

        String key = "rate_limit:" + userId;

        Long result = redisTemplate.execute(
                tokenBucketScript,
                Collections.singletonList(key),
                10,
                1,
                System.currentTimeMillis(),
                cost
        );

        return result != null && result == 1;
    }
}