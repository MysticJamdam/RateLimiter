package jamdam.barrier.main.entity;

import java.util.concurrent.atomic.AtomicLong;

public class StatsResponse {
    private long totalRequests;
    private long allowedRequests;
    private long blockedRequests;
    private long redisFailures;

    public long getRedisFailures() {
        return redisFailures;
    }

    public void setRedisFailures(long redisFailures) {
        this.redisFailures = redisFailures;
    }

    public StatsResponse(long totalRequests, long allowedRequests, long blockedRequests, long redisFailures) {
        this.totalRequests = totalRequests;
        this.allowedRequests = allowedRequests;
        this.blockedRequests = blockedRequests;
        this.redisFailures = redisFailures;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getAllowedRequests() {
        return allowedRequests;
    }

    public void setAllowedRequests(long allowedRequests) {
        this.allowedRequests = allowedRequests;
    }

    public long getBlockedRequests() {
        return blockedRequests;
    }

    public void setBlockedRequests(long blockedRequests) {
        this.blockedRequests = blockedRequests;
    }
}
