package jamdam.barrier.main.entity;

public class RateLimitResult {
    private boolean allowed;
    private long remainingTokens;
    private long retryAfter;
    private long resetTime;

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public long getRemainingTokens() {
        return remainingTokens;
    }

    public void setRemainingTokens(long remainingTokens) {
        this.remainingTokens = remainingTokens;
    }

    public long getRetryAfter() {
        return retryAfter;
    }

    public void setRetryAfter(long retryAfter) {
        this.retryAfter = retryAfter;
    }

    public long getResetTime() {
        return resetTime;
    }

    public void setResetTime(long resetTime) {
        this.resetTime = resetTime;
    }
}
