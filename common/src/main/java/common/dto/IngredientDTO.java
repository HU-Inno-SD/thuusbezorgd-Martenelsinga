package common.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class IngredientDTO {
    @GeneratedValue
    @Id
    private Long id;
    private String name;
    private boolean vegetarian;

    protected IngredientDTO(){}
    public IngredientDTO(Long id, String name, boolean vegetarian) {
        this.id = id;
        this.name = name;
        this.vegetarian = vegetarian;
    }

    public String getName(){
        return this.name;
    }

    public boolean isVegetarian(){
        return this.vegetarian;
    }

    public Long getid(){return this.id;}
}
