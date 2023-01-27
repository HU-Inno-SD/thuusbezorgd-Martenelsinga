package common;

import java.util.List;

public class DishList {
    private List<String> dishes;

    public DishList(List<String> dishes) {
        this.dishes = dishes;
    }

    public List<String> getDishes() {
        return dishes;
    }

    public void setDishes(List<String> dishes) {
        this.dishes = dishes;
    }
}
