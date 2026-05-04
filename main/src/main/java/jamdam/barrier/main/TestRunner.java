package jamdam.barrier.main;

import jamdam.barrier.main.services.BucketServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TestRunner {

    @Bean
    CommandLineRunner run(BucketServices limiter) {
        return args -> {

            String user = "user1";
            ExecutorService executor = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 200; i++) {   // > capacity
                executor.submit(() -> {
                    boolean allowed = limiter.allow("user1", 1);
                    System.out.println(allowed);
                });
            }

            executor.shutdown();

            executor.shutdown();
        };
    }
}