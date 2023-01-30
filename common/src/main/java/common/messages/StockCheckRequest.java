package common.messages;

import common.Address;
import common.DishList;
import common.User;

public class StockCheckRequest implements Request {
    private User user;
    private DishList dishList;
    private Address address;

    public StockCheckRequest(User user, DishList dishList, Address address) {
        this.user = user;
        this.dishList = dishList;
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DishList getDishList() {
        return dishList;
    }

    public void setDishList(DishList dishList) {
        this.dishList = dishList;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
