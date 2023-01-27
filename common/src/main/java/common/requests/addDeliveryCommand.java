package common.requests;

import common.Address;
import common.DishList;
import common.User;

public class addDeliveryCommand {
    private User user;
    private Address address;
    private DishList dishList;

    public addDeliveryCommand(User user, Address address, DishList dishList) {
        this.user = user;
        this.address = address;
        this.dishList = dishList;
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
}
