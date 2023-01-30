package stock.data;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.domain.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> findById(Long id);
    List<Dish> findAll();
    Optional<Dish> findByName(String name);
}