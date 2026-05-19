package jamdam.barrier.main.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<List> tokenBucketScript() {

        DefaultRedisScript<List> script =
                new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource(
                        "scripts/tokenBucket.lua"
                )
        );

        script.setResultType(List.class);

        return script;
    }
}