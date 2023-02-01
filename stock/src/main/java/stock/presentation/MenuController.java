package stock.presentation;

import common.dto.DishDTO;
import common.dto.IngredientDTO;
import common.messages.PlaceOrderCommand;
import common.messages.StockCheckRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;
import stock.data.DishRepository;
import stock.data.IngredientRepository;
import stock.domain.Dish;
import stock.domain.Ingredient;
import common.exception.DishNotFoundException;
import stock.exception.IngredientNotFoundException;
import stock.exception.OutOfStockException;
import stock.infrastructure.StockPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/menu")
public class MenuController {

    // Yes this is a controller and a service in one.
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private StockPublisher publisher;

    public MenuController(DishRepository dishes, IngredientRepository ingredientRepository) {
        this.dishRepository = dishes;
        this.ingredientRepository = ingredientRepository;
        this.publisher = new StockPublisher();
    }

    @PutMapping("/restock")
    public void restock(@RequestBody Long id, @RequestBody int amount ) throws IngredientNotFoundException{
        Optional<Ingredient> ingredient = this.ingredientRepository.findById(id);
        if(!ingredient.isPresent()){
            throw new IngredientNotFoundException("Ingredient not found");
        }
        int newstock = ingredient.get().getNrInStock() + amount;
        Ingredient realIngredient = new Ingredient(ingredient.get().getName(), ingredient.get().isVegetarian(), newstock);
        realIngredient.setId(ingredient.get().getId());
        this.ingredientRepository.save(realIngredient);
    }

    @PostMapping("/newdish")
    public void addDish(@RequestBody Dish dish){
        this.dishRepository.save(dish);
    }

    @PostMapping("/newingredient")
    public void addIngredient(@RequestBody Ingredient ingredient){
        this.ingredientRepository.save(ingredient);
    }

    @GetMapping()
    public List<DishDTO> getAllDishes() {
        List<Dish> list = this.dishRepository.findAll();
        List<DishDTO> realList = new ArrayList<>();
        for(Dish i : list){
            realList.add(new DishDTO(i.getId(), i.getName(), i.getIngredients()));
        }
        return realList;
    }

    @GetMapping("/{id}")
    public DishDTO getDish(@PathVariable("id") long id) throws DishNotFoundException {
        Optional<Dish> d = this.dishRepository.findById(id);
        if (d.isPresent()) {
            return new DishDTO(d.get().getId(), d.get().getName(), d.get().getIngredients());
        } else {
            throw new DishNotFoundException("Dish not found");
        }
    }

    @GetMapping("/ingredients")
    public List<IngredientDTO> getIngredients() {
        List<Ingredient> allIngredients = ingredientRepository.findAll();
        List<IngredientDTO> ingredientList = new ArrayList<>();
        for(Ingredient i : allIngredients){
            ingredientList.add(new IngredientDTO(i.getName(), i.isVegetarian()));
        }
        return ingredientList;
    }

    @GetMapping("/ingredients/{id}")
    public IngredientDTO getIngredient(@PathVariable("id") long id) throws IngredientNotFoundException {
        Optional<Ingredient> i = ingredientRepository.findById(id);
        if (i.isPresent()) {
            return new IngredientDTO(i.get().getName(), i.get().isVegetarian());
        } else {
            throw new IngredientNotFoundException("Ingredient not found");
        }
    }

    @GetMapping("/dishes/name/{name}")
    public DishDTO getDishByName(@PathVariable("name") String name) throws DishNotFoundException{
        Optional<Dish> d = this.dishRepository.findByName(name);
        if (d.isPresent()) {
            return new DishDTO(d.get().getId(), d.get().getName(), d.get().getIngredients());
        } else {
            throw new DishNotFoundException("Dish not found");
        }
    }


    //TODO: Unnecessary?
    @GetMapping("/ingredients/stock/{id}")
    public int getIngredientStock(@PathVariable("id") long id) throws IngredientNotFoundException{
        Optional<Ingredient> i = ingredientRepository.findById(id);
        if (i.isPresent()) {
            return i.get().getNrInStock();
        } else {
            throw new IngredientNotFoundException("Ingredient not found");
        }
    }

    @RabbitListener(queues = "stockQueue")
    public void checkStockForDishes(StockCheckRequest request) throws DishNotFoundException, OutOfStockException {
        List<DishDTO> dishes = new ArrayList<>();
        List<Dish> actualDishes = new ArrayList<>();
        // This checks if the dish exists
        for(Long dish : request.getDishList()){
            Optional<Dish> foundDish = dishRepository.findById(dish);
            if(!foundDish.isPresent()){
                throw new DishNotFoundException("Dish not found: " + dish);
            }
            // Then we make a new DishDTO list to parse to our returnOrderCommand later
            dishes.add(new DishDTO(foundDish.get().getId(), foundDish.get().getName(), foundDish.get().getIngredients()));
        }

        // Then we need to check the stock. First, we need a list of ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        for(Dish dish : actualDishes){
            for(IngredientDTO ingredient : dish.getIngredients()){
                Optional<Ingredient> optIng = ingredientRepository.findByName(ingredient.getName());
                if(optIng.isPresent()){
                    Ingredient ingr = new Ingredient(optIng.get().getName(), optIng.get().isVegetarian(), optIng.get().getNrInStock());
                    ingredients.add(ingr);
                }
            }
        }
        // Then, we need to subtract one of every ingredient to see if it's in stock
        for(Ingredient ingredient : ingredients){
            if(ingredient.getNrInStock() > 0){
                ingredient.take(1);
            }
            else{
                throw new OutOfStockException("Dish not in stock");
            }
        }

        // If we get here, all ingredients are stocked up enough, so we can save the transaction
        this.ingredientRepository.saveAll(ingredients);

        // Now all that remains is to send it to delivery
        PlaceOrderCommand command = new PlaceOrderCommand(request.getUserName(), request.getDishList(), request.getAddress());
        this.publisher.returnOrderCommand(command);
    }


//    public record ReviewDTO(String dish, String reviewerName, int rating) {
//        public static ReviewDTO fromReview(DishReview review) {
//            return new ReviewDTO(review.getDish().getName(), review.getUser().getName(), review.getRating().toInt());
//        }
//    }
//
//    public record PostedReviewDTO(int rating) {
//
//    }

//    @GetMapping("/{id}/reviews")
//    public ResponseEntity<List<ReviewDTO>> getDishReviews(@PathVariable("id") long id) {
//        Optional<Dish> d = this.dishRepository.findById(id);
//        if (d.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        List<DishReview> reviews = this.reviews.findDishReviews(d.get());
//        return ResponseEntity.ok(reviews.stream().map(ReviewDTO::fromReview).toList());
//    }

//    @PostMapping("/{id}/reviews")
//    @Transactional
//    public ResponseEntity<ReviewDTO> postReview(User user, @PathVariable("id") long id, @RequestBody PostedReviewDTO reviewDTO) {
//        Optional<Dish> found = this.dishRepository.findById(id);
//        if (found.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        DishReview review = new DishReview(found.get(), ReviewRating.fromInt(reviewDTO.rating()), user);
//        reviews.save(review);
//
//        return ResponseEntity.ok(ReviewDTO.fromReview(review));
//    }
}