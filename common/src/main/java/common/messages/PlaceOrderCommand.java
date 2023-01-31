package common.messages;

import common.Address;
import common.dto.DishDTO;

import java.util.List;

public class PlaceOrderCommand implements Command {
    private String userName;
    private List<DishDTO> dishList;
    private Address address;

    public PlaceOrderCommand(String userName, List<DishDTO> dishList, Address address) {
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

    public List<DishDTO> getDishList() {
        return dishList;
    }

    public void setDishList(List<DishDTO> dishList) {
        this.dishList = dishList;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
