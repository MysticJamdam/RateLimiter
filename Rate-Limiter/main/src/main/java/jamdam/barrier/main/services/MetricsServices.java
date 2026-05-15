package jamdam.barrier.main.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsServices {
    private AtomicLong totalRequests = new AtomicLong(0);
    private AtomicLong blockedRequests = new AtomicLong(0);
    private AtomicLong allowedRequests = new AtomicLong(0);

    public void incrementTotal() {
        totalRequests.incrementAndGet();
    }

    public void incrementAllowed() {
        allowedRequests.incrementAndGet();
    }

    public void incrementBlocked() {
        blockedRequests.incrementAndGet();
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getAllowedRequests() {
        return allowedRequests.get();
    }

    public long getBlockedRequests() {
        return blockedRequests.get();
    }

}
