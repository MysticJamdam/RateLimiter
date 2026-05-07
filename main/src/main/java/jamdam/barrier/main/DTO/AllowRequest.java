package jamdam.barrier.main.DTO;

public class AllowRequest {
    private String userId;
    private int cost;

    public AllowRequest(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
