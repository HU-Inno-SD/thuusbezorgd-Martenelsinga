package common.messages;

import common.Address;

import java.util.List;

public class StockCheckRequest implements Request {
    private String userName;
    private List<Long> dishList;
    private Address address;

    public StockCheckRequest(String userName, List<Long> dishList, Address address) {
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
