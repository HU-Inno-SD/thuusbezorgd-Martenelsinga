package common.messages;

import common.Address;

import java.io.Serializable;
import java.util.List;

public class PlaceOrderCommand implements Serializable {
    private String userName;
    private List<Long> dishList;
    private Address address;

    public PlaceOrderCommand(String userName, List<Long> dishList, Address address) {
        this.userName = userName;
        this.dishList = dishList;
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user) {
        this.userName = user;
    }

    public List<Long> getDishList() {
        return dishList;
    }

    public void setDishList(List<Long> dishList) {
        this.dishList = dishList;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
