package stock.presentation;

import common.StockObject;
import common.dto.IngredientDTO;
import common.messages.PlaceOrderCommand;
import common.messages.RandomStockCheck;
import common.messages.RandomStockCheckReply;
import common.messages.StockCheckRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.web.bind.annotation.*;
import stock.data.DishRepository;
import stock.data.IngredientRepository;
import stock.domain.Dish;
import stock.domain.Ingredient;
import common.exception.DishNotFoundException;
import stock.dto.RestockObject;
import stock.exception.DishAlreadyExistsException;
import stock.exception.IngredientAlreadyExistsException;
import stock.exception.IngredientNotFoundException;
import stock.exception.OutOfStockException;
import stock.infrastructure.StockPublisher;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private final StockPublisher publisher;

    private final Jackson2JsonMessageConverter converter;

    public MenuController(DishRepository dishes, IngredientRepository ingredientRepository) {
        this.dishRepository = dishes;
        this.ingredientRepository = ingredientRepository;
        this.publisher = new StockPublisher();
        this.converter = new Jackson2JsonMessageConverter();
    }

    @PutMapping("/restock")
    public Ingredient restock(@RequestBody RestockObject restockObject) throws IngredientNotFoundException {
        Optional<Ingredient> ingredient = this.ingredientRepository.findById(restockObject.getId());
        if (ingredient.isEmpty()) {
            throw new IngredientNotFoundException("Ingredient not found");
        }
        int newstock = ingredient.get().getNrInStock() + restockObject.getAmount();
        Ingredient realIngredient = new Ingredient(ingredient.get().getName(), ingredient.get().isVegetarian(), newstock);
        realIngredient.setId(ingredient.get().getId());
        this.ingredientRepository.save(realIngredient);
        return realIngredient;
    }

    @PostMapping("/newdish")
    public String addDish(@RequestBody Dish dish) throws DishAlreadyExistsException {
        if (dishRepository.findByName(dish.getName()).isPresent()) {
            throw new DishAlreadyExistsException("Dish already exists!");
        }
        List<Ingredient> ingredients = new ArrayList<>();
        for (Ingredient ingredient : dish.getIngredients()) {
            if (ingredientRepository.findByName(ingredient.getName()).isEmpty()) {
                ingredientRepository.save(ingredient);
            }
            ingredients.add(ingredientRepository.findByName(ingredient.getName()).get());
        }
        Dish dishToSave = new Dish(dish.getName(), ingredients);
        this.dishRepository.save(dishToSave);
        return "Dish successfully saved";
    }

    @PostMapping("/newdishes")
    public String addDishes(@RequestBody List<Dish> dishes) throws DishAlreadyExistsException {
        for (Dish dish : dishes) {
            if (dishRepository.findByName(dish.getName()).isPresent()) {
                throw new DishAlreadyExistsException("Dish already exists: " + dish.getName());
            }
        }
        for (Dish dish : dishes) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (Ingredient ingredient : dish.getIngredients()) {
                if (ingredientRepository.findByName(ingredient.getName()).isEmpty()) {
                    ingredientRepository.save(ingredient);
                }
                ingredients.add(ingredientRepository.findByName(ingredient.getName()).get());
            }
            Dish dishToSave = new Dish(dish.getName(), ingredients);
            this.dishRepository.save(dishToSave);
        }
        return "Menu successfully updated";

    }

    @PostMapping("/newingredient")
    public String addIngredient(@RequestBody Ingredient ingredient) throws IngredientAlreadyExistsException {
        if (ingredientRepository.findByName(ingredient.getName()).isPresent()) {
            throw new IngredientAlreadyExistsException("Ingredient already exists!");
        }
        this.ingredientRepository.save(ingredient);
        return "Ingredient successfully added to the list";
    }

    @PostMapping("/newingredients")
    public String addIngredients(@RequestBody List<Ingredient> ingredients) throws IngredientAlreadyExistsException {
        for (Ingredient ingredient : ingredients) {
            if (ingredientRepository.findByName(ingredient.getName()).isPresent()) {
                throw new IngredientAlreadyExistsException("Ingredient already exists: " + ingredient.getName());
            }
        }
        this.ingredientRepository.saveAll(ingredients);
        return "Ingredients successfully added to the list";
    }

    @GetMapping()
    public List<Dish> getAllDishes() {
        List<Dish> list = this.dishRepository.findAll();
        List<Dish> realList = new ArrayList<>();
        for (Dish i : list) {
            realList.add(new Dish(i.getDishId(), i.getName(), i.getIngredients()));
        }
        return realList;
    }

    @GetMapping("/{id}")
    public Dish getDish(@PathVariable("id") long id) throws DishNotFoundException {
        Optional<Dish> d = this.dishRepository.findById(id);
        if (d.isPresent()) {
            return new Dish(d.get().getDishId(), d.get().getName(), d.get().getIngredients());
        } else {
            throw new DishNotFoundException("Dish not found");
        }
    }

    @GetMapping("/ingredients")
    public List<IngredientDTO> getIngredients() {
        List<Ingredient> allIngredients = ingredientRepository.findAll();
        List<IngredientDTO> ingredientList = new ArrayList<>();
        for (Ingredient i : allIngredients) {
            ingredientList.add(new IngredientDTO(i.getId(), i.getName(), i.isVegetarian()));
        }
        return ingredientList;
    }

    @GetMapping("/ingredients/{id}")
    public IngredientDTO getIngredient(@PathVariable("id") long id) throws IngredientNotFoundException {
        Optional<Ingredient> i = ingredientRepository.findById(id);
        if (i.isPresent()) {
            return new IngredientDTO(i.get().getId(), i.get().getName(), i.get().isVegetarian());
        } else {
            throw new IngredientNotFoundException("Ingredient not found");
        }
    }

    @GetMapping("/dishes/name/{name}")
    public Dish getDishByName(@PathVariable("name") String name) throws DishNotFoundException {
        Optional<Dish> d = this.dishRepository.findByName(name);
        if (d.isPresent()) {
            return new Dish(d.get().getDishId(), d.get().getName(), d.get().getIngredients());
        } else {
            throw new DishNotFoundException("Dish not found");
        }
    }


    // This method checks ALL dishes' stock
    @RabbitListener(queues = "stockQueue")
    public void randomStockCheck(RandomStockCheck check){
        List<Dish> dishes = this.dishRepository.findAll();
        List<StockObject> stock = new ArrayList<>();
        for(Dish d : dishes){
            int lowestStock = -1;
            List<Ingredient> ingredients = d.getIngredients();
            List<Ingredient> ingredientsWithStock = new ArrayList<>();
            for(Ingredient i : ingredients){
                ingredientsWithStock.add(ingredientRepository.findByName(i.getName()).get());
            }
            for(Ingredient p : ingredientsWithStock){
                if(lowestStock == -1){
                    lowestStock = p.getNrInStock();
                }
                else{
                    if(lowestStock < p.getNrInStock()){
                        lowestStock = p.getNrInStock();
                    }
                }
            }
            stock.add(new StockObject(d.getDishId(), lowestStock));
        }
        RandomStockCheckReply reply = new RandomStockCheckReply(stock);
        this.publisher.stockCheckReply(reply);
    }

    @Transactional
    @RabbitListener(queues = "stockQueue")
    public void checkStockForDishes(StockCheckRequest request){
        List<Dish> dishes = new ArrayList<>();
        // This checks if the dish exists
        for (Long dish : request.getDishList()) {
            Optional<Dish> foundDish = dishRepository.findById(dish);
            if (foundDish.isEmpty()) {
                this.publisher.throwError("Dish not found: " + dish);
                return;
            }
            dishes.add(foundDish.get());
        }

        // Then we need to check the stock. First, we need a list of ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        for (Dish dish : dishes) {
            for (Ingredient ingredient : dish.getIngredients()) {
                Optional<Ingredient> optIng = ingredientRepository.findByName(ingredient.getName());
                if (optIng.isPresent()) {
                    Ingredient ingr = optIng.get();
                    ingredients.add(ingr);
                }
            }
        }

        // Then, we need to subtract one of every ingredient to see if it's in stock
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getNrInStock() > 0) {
                ingredient.take(1);
            } else {
                this.publisher.throwError("Ingredient not in stock: " + ingredient);
                return;
            }
        }

        // If we get here, all ingredients are stocked up enough, so we can save the transaction
        this.ingredientRepository.saveAll(ingredients);

        // Now all that remains is to send it to delivery
        PlaceOrderCommand command = new PlaceOrderCommand(request.getUserName(), request.getDishList(), request.getAddress());
        this.publisher.returnOrderCommand(command);
    }
}