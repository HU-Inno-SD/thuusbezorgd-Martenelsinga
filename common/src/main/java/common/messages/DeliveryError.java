package common.messages;

public class DeliveryError {
    private String message;

    public DeliveryError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
