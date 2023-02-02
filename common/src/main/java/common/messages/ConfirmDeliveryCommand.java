package common.messages;

import common.Address;

import java.util.List;
import java.util.UUID;

public class ConfirmDeliveryCommand {
    private String userName;

    private UUID orderId;
    private UUID deliveryId;

    protected ConfirmDeliveryCommand(){}

    public ConfirmDeliveryCommand(String userName, UUID orderId, UUID deliveryId) {
        this.userName = userName;
        this.orderId = orderId;
        this.deliveryId = deliveryId;
    }

    public String getUser() {
        return userName;
    }

    public void setUser(String user) {
        this.userName = user;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }
}
