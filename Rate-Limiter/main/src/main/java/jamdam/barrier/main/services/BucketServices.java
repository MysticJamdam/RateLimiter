package jamdam.barrier.main.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;

@Service
public class BucketServices {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean allow(String userId, int cost) {

        if (cost <= 0) {
            throw new IllegalArgumentException("cost must be greater than 0");
        }
        String key = "rate_limit:" + userId;
        Map<Object, Object> data =
                redisTemplate.opsForHash().entries(key);
        double tokens;
        long lastRefillTime;
        if (data.isEmpty()) {
            tokens = 10;
            lastRefillTime = System.currentTimeMillis();
        }
        else {
            tokens = Double.parseDouble(
                    data.get("tokens").toString()
            );

            lastRefillTime = Long.parseLong(
                    data.get("last_refill_time").toString()
            );
        }
        long now = System.currentTimeMillis();
        double elapsedSeconds =
                (now - lastRefillTime) / 1000.0;
        tokens = Math.min(
                10,
                tokens + elapsedSeconds * 1
        );
        boolean allowed = false;
        if (tokens >= cost) {
            tokens -= cost;
            allowed = true;
        }
        redisTemplate.opsForHash().put(
                key,
                "tokens",
                String.valueOf(tokens)
        );

        redisTemplate.opsForHash().put(
                key,
                "last_refill_time",
                String.valueOf(now)
        );

        redisTemplate.expire(
                key,
                Duration.ofMinutes(10)
        );

        return allowed;
    }
}
