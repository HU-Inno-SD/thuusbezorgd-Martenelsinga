package common.messages;

import common.Address;
import common.DishList;
import common.User;

import java.util.UUID;

public class AddDeliveryCommand implements Command {
    private User user;
    private Address address;
    private DishList dishList;

    private UUID orderId;

    public AddDeliveryCommand(User user, Address address, DishList dishList, UUID orderId) {
        this.user = user;
        this.address = address;
        this.dishList = dishList;
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
