package data;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import domain.Dish;

@NoRepositoryBean
public interface DishRepository extends Repository<Dish, Long> {
    Optional<Dish> findById(Long id);
    List<Dish> findAll();
}