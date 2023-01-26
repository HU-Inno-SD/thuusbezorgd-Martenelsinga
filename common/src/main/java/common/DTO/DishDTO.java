package common.DTO;

import java.util.ArrayList;
import java.util.List;

public class DishDTO {
    private String name;
    private List<IngredientDTO> ingredients;
    protected DishDTO(){}
    public DishDTO(String name, List<IngredientDTO> ingredients){
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
}
