package jamdam.barrier.main.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsServices {

    private final Counter totalRequests;
    private final Counter allowedRequests;
    private final Counter blockedRequests;
    private final Counter redisFailures;

    @Autowired
    public MetricsServices(
            MeterRegistry meterRegistry
    ) {

        totalRequests =
                Counter.builder(
                                "requests.total"
                        )
                        .description(
                                "Total requests"
                        )
                        .register(meterRegistry);

        allowedRequests =
                Counter.builder(
                                "requests.allowed"
                        )
                        .description(
                                "Allowed requests"
                        )
                        .register(meterRegistry);

        blockedRequests =
                Counter.builder(
                                "requests.blocked"
                        )
                        .description(
                                "Blocked requests"
                        )
                        .register(meterRegistry);

        redisFailures =
                Counter.builder(
                                "redis.failures"
                        )
                        .description(
                                "Redis failures"
                        )
                        .register(meterRegistry);
    }

    public void incrementTotal() {
        totalRequests.increment();
    }

    public void incrementAllowed() {
        allowedRequests.increment();
    }

    public void incrementBlocked() {
        blockedRequests.increment();
    }

    public void incrementRedisFailures() {
        redisFailures.increment();
    }
}