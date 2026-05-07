package jamdam.barrier.main.DTO;

public class AllowResponse {
    private boolean allowed;

    public AllowResponse(boolean allowed) {
        this.allowed = allowed;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

}
