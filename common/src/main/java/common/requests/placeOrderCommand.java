package common.requests;

import common.Address;
import common.DishList;
import common.User;
import common.dto.DishDTO;

import java.util.List;

public class placeOrderCommand {
    private User user;
    private List<DishDTO> dishList;
    private Address address;

    public placeOrderCommand(User user, List<DishDTO> dishList, Address address) {
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
