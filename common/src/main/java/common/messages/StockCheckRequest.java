package common.messages;

import common.Address;
import common.DishList;

public class StockCheckRequest implements Request {
    private String userName;
    private DishList dishList;
    private Address address;

    public StockCheckRequest(String userName, DishList dishList, Address address) {
        this.userName = userName;
        this.dishList = dishList;
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUser(String user) {
        this.userName = user;
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
