package stock.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import stock.domain.Dish;

public interface DishRepository extends MongoRepository<Dish, Long> {
    Optional<Dish> findById(Long id);
    List<Dish> findAll();
    Optional<Dish> findByName(String name);
}