package common.messages;

import common.Address;

import javax.persistence.ElementCollection;
import java.io.Serializable;
import java.util.List;

public class StockCheckRequest {
    private String userName;
    private List<Long> dishList;
    private Address address;

    protected StockCheckRequest(){}

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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
