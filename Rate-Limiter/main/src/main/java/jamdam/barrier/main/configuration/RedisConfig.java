package jamdam.barrier.main.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(
                new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(
                new StringRedisSerializer());
        redisTemplate.setValueSerializer(
                new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(
                new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisScript<Long> rateLimiterScript() {

        ResourceScriptSource scriptSource =
                new ResourceScriptSource(
                        new ClassPathResource("scripts/tokenBucket.lua")
                );

        DefaultRedisScript<Long> script =
                new DefaultRedisScript<>();

        script.setScriptSource(scriptSource);
        script.setResultType(Long.class);

        return script;
    }
}
