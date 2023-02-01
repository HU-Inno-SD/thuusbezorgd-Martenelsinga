package common.messages;

import common.dto.DishDTO;

import java.io.Serializable;
import java.util.List;

public class RetractStockCommand implements Serializable {
    private List<DishDTO> dishes;

    public RetractStockCommand(List<DishDTO> dishes) {
        this.dishes = dishes;
    }

    public List<DishDTO> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishDTO> dishes) {
        this.dishes = dishes;
    }
}
