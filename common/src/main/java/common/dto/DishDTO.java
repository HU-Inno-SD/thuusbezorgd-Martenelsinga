package common.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;


public class DishDTO {
    private Long id;
    private String name;
    @ManyToMany
    private List<IngredientDTO> ingredients;
    protected DishDTO(){}
    public DishDTO(Long id, String name, List<IngredientDTO> ingredients){
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
    }
    public String getName(){
        return this.name;
    }

    public List<String> getIngredients(){
        List<String> ingr = new ArrayList<>();
        for(IngredientDTO i : ingredients){
            ingr.add(i.getName());
        }
        return ingr;
    }

    public boolean isVegetarian(){
        for(IngredientDTO i : ingredients){
            if(i.isVegetarian() == false){
                return false;
            }
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
