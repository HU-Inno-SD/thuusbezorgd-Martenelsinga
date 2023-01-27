package stock.infrastructure;

import common.DishList;
import common.dto.DishDTO;
import common.exception.DishNotFoundException;
import common.requests.placeOrderCommand;
import common.requests.stockCheckRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import stock.data.DishRepository;
import stock.domain.Dish;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StockConsumer {
    private DishRepository repo;
    private StockPublisher publisher;

    @RabbitListener(queues = "stockQueue")
    public void checkStockForDishes(stockCheckRequest request) throws DishNotFoundException {
        List<DishDTO> dishes = new ArrayList<>();
        for(String dish : request.getDishList().getDishes()){
            Optional<Dish> foundDish = repo.findByName(dish);
            if(!foundDish.isPresent()){
                throw new DishNotFoundException("Dish not found: " + dish);
            }
            dishes.add(new DishDTO(foundDish.get().getName(), foundDish.get().getIngredients()));
        }
        placeOrderCommand command = new placeOrderCommand(request.getUser(), dishes, request.getAddress());
        this.publisher.returnOrderCommand(command);
    }
}
