package common.messages;

import common.Address;
import common.User;
import common.dto.DishDTO;

import java.util.List;

public class PlaceOrderCommand implements Command {
    private User user;
    private List<DishDTO> dishList;
    private Address address;

    public PlaceOrderCommand(User user, List<DishDTO> dishList, Address address) {
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
