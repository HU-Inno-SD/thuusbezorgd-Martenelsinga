package stock.domain;


import common.dto.IngredientDTO;
import stock.exception.OutOfStockException;

import javax.persistence.*;
import java.util.*;

@Entity
public class Dish {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Ingredient> ingredients;

    protected Dish() {
    }

    public Dish(String name, Ingredient... ingredients) {
        if (ingredients.length == 0) {
            throw new IllegalArgumentException("Cannot have 0 ingredients");
        }

        this.name = name;
        this.ingredients = Arrays.asList(ingredients);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<IngredientDTO> getIngredients() {
        List<IngredientDTO> ingr = new ArrayList<>();
        for(Ingredient i : this.ingredients){
            ingr.add(new IngredientDTO(i.getName(), i.isVegetarian()));
        }
        return ingr;
    }

    public boolean isVegetarian() {
        return this.ingredients.stream().allMatch(Ingredient::isVegetarian);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

