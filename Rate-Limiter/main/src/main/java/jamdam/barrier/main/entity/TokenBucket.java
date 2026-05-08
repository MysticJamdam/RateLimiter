package jamdam.barrier.main.entity;

public class TokenBucket {
    public double tokens;
    public double capacity;
    public double fillingRate;
    public long lastRefillTime;

    public TokenBucket(double tokens, double capacity, double fillingRate, long lastRefillTime) {
        this.tokens = tokens;
        this.capacity = capacity;
        this.fillingRate = fillingRate;
        this.lastRefillTime = lastRefillTime;
    }

    public TokenBucket() {
    }


    public double getTokens() {
        return tokens;
    }

    public void setTokens(double tokens) {
        this.tokens = tokens;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getFillingRate() {
        return fillingRate;
    }

    public void setFillingRate(double fillingRate) {
        this.fillingRate = fillingRate;
    }

    public long getLastRefillTime() {
        return lastRefillTime;
    }

    public void setLastRefillTime(long lastRefillTime) {
        this.lastRefillTime = lastRefillTime;
    }
}
