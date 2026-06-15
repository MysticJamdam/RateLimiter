package jamdam.barrier.main;

import jamdam.barrier.main.DTO.RateLimitResult;
import jamdam.barrier.main.entity.RateLimitPolicy;
import jamdam.barrier.main.services.BucketServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class LoadTest {

    @Autowired
    private BucketServices bucketServices;

    @Test
    void concurrentTest() throws Exception {

        int requests = 10000;

        RateLimitPolicy policy = new RateLimitPolicy();
        policy.setCapacity(100);
        policy.setRefillrate(1);
        policy.setCost(1);

        ExecutorService executor =
                Executors.newFixedThreadPool(200);

        CountDownLatch latch =
                new CountDownLatch(requests);

        AtomicInteger allowed =
                new AtomicInteger();

        AtomicInteger blocked =
                new AtomicInteger();

        long start = System.currentTimeMillis();

        for (int i = 0; i < requests; i++) {

            executor.submit(() -> {

                try {

                    RateLimitResult result =
                            bucketServices.allow(
                                    "load-test-user",
                                    policy
                            );

                    if (result.isAllowed()) {
                        allowed.incrementAndGet();
                    } else {
                        blocked.incrementAndGet();
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    latch.countDown();

                }
            });
        }

        latch.await();

        long end = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("--------------------------------");
        System.out.println("Requests = " + requests);
        System.out.println("Allowed  = " + allowed.get());
        System.out.println("Blocked  = " + blocked.get());
        System.out.println("Duration = " + (end - start) + " ms");
        System.out.println("--------------------------------");
    }
}