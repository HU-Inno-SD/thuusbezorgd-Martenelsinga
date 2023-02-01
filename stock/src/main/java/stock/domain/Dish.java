package stock.domain;


import common.dto.IngredientDTO;
import stock.exception.OutOfStockException;

import javax.persistence.*;
import java.util.*;

@Entity
public class Dish {
    @Id
    @GeneratedValue
    private Long dishId;

    private String name;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Ingredient> ingredients;

    protected Dish() {
    }

    public Dish(String name, List<Ingredient> ingredients) {
        if (ingredients.size() == 0) {
            throw new IllegalArgumentException("Cannot have 0 ingredients");
        }

        this.name = name;
        this.ingredients = ingredients;
    }

    public Dish(Long dishId, String name, List<Ingredient> ingredients) {
        if (ingredients.size() == 0) {
            throw new IllegalArgumentException("Cannot have 0 ingredients");
        }
        this.dishId = dishId;
        this.name = name;
        this.ingredients = ingredients;
    }

    public Long getDishId() {
        return dishId;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean isVegetarian() {
        return this.ingredients.stream().allMatch(Ingredient::isVegetarian);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(dishId, dish.dishId) && Objects.equals(name, dish.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishId);
    }

    public boolean isAvailable(Ingredient ingredient) {
        if(ingredient.getNrInStock() > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public void prepare() throws OutOfStockException {
        for(Ingredient i: this.ingredients){
            if(isAvailable(i) == false){
                throw new OutOfStockException("Sorry, ingredient " + i.getName() + " is not in stock");
            }
        }
        for(Ingredient i: this.ingredients){
            i.take(1);
        }
    }
}

