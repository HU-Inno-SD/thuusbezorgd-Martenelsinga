package common.messages;

import common.Address;
import common.DishList;

import java.util.UUID;

public class AddDeliveryCommand implements Command {
    private String userName;
    private Address address;
    private DishList dishList;

    private UUID orderId;

    public AddDeliveryCommand(String userName, Address address, DishList dishList, UUID orderId) {
        this.userName = userName;
        this.address = address;
        this.dishList = dishList;
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

    public DishList getDishList() {
        return dishList;
    }

    public void setDishList(DishList dishList) {
        this.dishList = dishList;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}
