package jamdam.barrier.main.entity;

public class RateLimitPolicy {
    private int capacity;
    private double refillrate;
    private int cost;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getRefillrate() {
        return refillrate;
    }

    public void setRefillrate(double refillrate) {
        this.refillrate = refillrate;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
