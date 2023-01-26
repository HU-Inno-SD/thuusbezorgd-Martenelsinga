package stock.presentation;

import common.DTO.DishDTO;
import common.DTO.IngredientDTO;
import common.ReviewRepository;
import org.springframework.web.bind.annotation.*;
import stock.data.DishRepository;
import stock.data.IngredientRepository;
import stock.domain.Dish;
import stock.domain.Ingredient;
import stock.exception.DishNotFoundException;
import stock.exception.IngredientNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dishes")
public class MenuController {

    // Yes this is a controller and a service in one. I deemed it unnecessary to split them.
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private final ReviewRepository reviewRepository;

    public MenuController(DishRepository dishes, ReviewRepository reviewRepository, IngredientRepository ingredientRepository) {
        this.dishRepository = dishes;
        this.reviewRepository = reviewRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping("/menu")
    public List<DishDTO> getAllDishes() {
        List<Dish> list = this.dishRepository.findAll();
        List<DishDTO> realList = new ArrayList<>();
        for(Dish i : list){
            realList.add(new DishDTO(i.getName(), i.getIngredients()));
        }
        return realList;
    }

    @GetMapping("/menu/{id}")
    public DishDTO getDish(@PathVariable("id") long id) throws DishNotFoundException {
        Optional<Dish> d = this.dishRepository.findById(id);
        if (d.isPresent()) {
            return new DishDTO(d.get().getName(), d.get().getIngredients());
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

    @GetMapping("/menu/name/{name}")
    public DishDTO getDishByName(@PathVariable("name") String name) throws DishNotFoundException{
        Optional<Dish> d = this.dishRepository.findByName(name);
        if (d.isPresent()) {
            return new DishDTO(d.get().getName(), d.get().getIngredients());
        } else {
            throw new DishNotFoundException("Dish not found");
        }
    }

    @GetMapping("/stock/{id}")
    public int getIngredientStock(@PathVariable("id") long id) throws IngredientNotFoundException{
        Optional<Ingredient> i = ingredientRepository.findById(id);
        if (i.isPresent()) {
            return i.get().getNrInStock();
        } else {
            throw new IngredientNotFoundException("Ingredient not found");
        }
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