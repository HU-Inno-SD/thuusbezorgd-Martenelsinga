package common.messages;

import common.Address;

import java.util.List;
import java.util.UUID;

public class AddDeliveryCommand implements Command {
    private String userName;
    private Address address;
    private List<Long> dishIds;

    private UUID orderId;

    public AddDeliveryCommand(String userName, Address address, List<Long> dishIds, UUID orderId) {
        this.userName = userName;
        this.address = address;
        this.dishIds = dishIds;
        this.orderId = orderId;
    }

    public String getUser() {
        return userName;
    }

    public void setUser(String user) {
        this.userName = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Long> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<Long> dishIds) {
        this.dishIds = dishIds;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}
