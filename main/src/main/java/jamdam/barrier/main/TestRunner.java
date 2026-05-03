package jamdam.barrier.main;

import jamdam.barrier.main.services.BucketServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRunner {

    @Bean
    CommandLineRunner run(BucketServices limiter) {
        return args -> {

            String user = "user1";

            for (int i = 0; i < 150; i++) {
                boolean allowed = limiter.allow(user, 1);
                System.out.println("Request " + i + " → " + allowed);
            }

            System.out.println("Waiting 2 seconds...");
            Thread.sleep(2000);

            for (int i = 0; i < 50; i++) {
                boolean allowed = limiter.allow(user, 1);
                System.out.println("After refill " + i + " → " + allowed);
            }
        };
    }
}