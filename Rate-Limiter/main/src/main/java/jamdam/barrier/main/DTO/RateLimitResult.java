package jamdam.barrier.main.DTO;

public class RateLimitResult {
    private boolean allowed;
    private double remainingTokens;
    private long retryAfter;
    private long resetTime;

    public RateLimitResult(boolean allowed, double remainingTokens, long retryAfter, long resetTime) {
        this.allowed = allowed;
        this.remainingTokens = remainingTokens;
        this.retryAfter = retryAfter;
        this.resetTime = resetTime;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public double getRemainingTokens() {
        return remainingTokens;
    }

    public void setRemainingTokens(double remainingTokens) {
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
