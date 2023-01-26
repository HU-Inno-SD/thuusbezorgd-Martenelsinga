package common.DTO;

public class IngredientDTO {
    private String name;
    private boolean vegetarian;

    protected IngredientDTO(){}
    public IngredientDTO(String name, boolean vegetarian) {
        this.name = name;
        this.vegetarian = vegetarian;
    }

    public String getName(){
        return this.name;
    }

    public boolean isVegetarian(){
        return this.vegetarian;
    }
}
