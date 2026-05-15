package jamdam.barrier.main.entity;

public class StatsResponse {
    private long totalRequests;
    private long allowedRequests;
    private long blockedRequests;

    public StatsResponse(long totalRequests, long allowedRequests, long blockedRequests) {
        this.totalRequests = totalRequests;
        this.allowedRequests = allowedRequests;
        this.blockedRequests = blockedRequests;
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
