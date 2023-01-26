package stock.presentation;

import common.User;
import data.ReviewRepository;
import domain.Dish;
import data.DishRepository;
import domain.DishReview;
import domain.ReviewRating;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository dishes;
    private final ReviewRepository reviews;

    public record DishDto(long id, String name, boolean available) {
        public static DishDto fromDish(Dish d) {
            return new DishDto(d.getId(), d.getName(), d.getAvailable() > 0);
        }
    }

    public DishController(DishRepository dishes, ReviewRepository reviews) {
        this.dishes = dishes;
        this.reviews = reviews;
    }

    @GetMapping
    public List<DishDto> getDishes() {
        return this.dishes.findAll().stream()
                .map(DishDto::fromDish)
                .collect(Collectors.toList());
    }


    @GetMapping("/dishes")
    public ResponseEntity<List<DishDto>> getAllDishes() {
        List<DishDto> allDishes = this.dishes.findAll().stream()
                .map(DishDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allDishes);
    }

    @GetMapping("/dishes/{id}")
    public ResponseEntity<DishDto> getDish(@PathVariable("id") long id) {
        Optional<Dish> d = this.dishes.findById(id);
        if (d.isPresent()) {
            return ResponseEntity.ok(new DishDto(d.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public record IngredientDTO(long id, String name, int available) {
        public static IngredientDTO fromIngredient(Ingredient ingredient) {
            return new IngredientDTO(ingredient.getId(), ingredient.getName(), ingredient.getNrInStock());
        }
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientDTO>> getIngredients() {
        List<IngredientDTO> allIngredients = this.ingredients.findAll().stream()
                .map(i -> IngredientDTO.fromIngredient(i))
                .collect(Collectors.toList());
        return ResponseEntity.ok(allIngredients);
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<IngredientDTO> getIngredient(@PathVariable("id") long id) {
        Optional<Ingredient> i = this.ingredients.findById(id);
        if (i.isPresent()) {
            return ResponseEntity.ok(IngredientDTO.fromIngredient(i.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("{id}")
    public ResponseEntity<DishDto> getDish(@PathVariable long id) {
        Optional<Dish> dishResult = this.dishes.findById(id);
        if (dishResult.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(DishDto.fromDish(dishResult.get()));
        }
    }


    public record ReviewDTO(String dish, String reviewerName, int rating) {
        public static ReviewDTO fromReview(DishReview review) {
            return new ReviewDTO(review.getDish().getName(), review.getUser().getName(), review.getRating().toInt());
        }
    }

    public record PostedReviewDTO(int rating) {

    }


    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getDishReviews(@PathVariable("id") long id) {
        Optional<Dish> d = this.dishes.findById(id);
        if (d.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DishReview> reviews = this.reviews.findDishReviews(d.get());
        return ResponseEntity.ok(reviews.stream().map(ReviewDTO::fromReview).toList());
    }

    @PostMapping("/{id}/reviews")
    @Transactional
    public ResponseEntity<ReviewDTO> postReview(User user, @PathVariable("id") long id, @RequestBody PostedReviewDTO reviewDTO) {
        Optional<Dish> found = this.dishes.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DishReview review = new DishReview(found.get(), ReviewRating.fromInt(reviewDTO.rating()), user);
        reviews.save(review);

        return ResponseEntity.ok(ReviewDTO.fromReview(review));
    }
}