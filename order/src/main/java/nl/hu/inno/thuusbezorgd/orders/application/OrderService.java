package nl.hu.inno.thuusbezorgd.orders.application;


import common.Address;
import common.exception.UserNotFoundException;
import nl.hu.inno.thuusbezorgd.orders.data.UserRepository;
import nl.hu.inno.thuusbezorgd.orders.domain.User;
import common.messages.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.domain.Order;
import nl.hu.inno.thuusbezorgd.orders.infrastructure.OrderPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private OrderPublisher publisher;
    private OrderRepository repository;
    private UserRepository userRepo;

    public void placeOrder(User user, List<Long> dishIds, Address address) throws UserNotFoundException {
        Optional<User> optUser = userRepo.findByName(user.getName());
        if(!optUser.isPresent()){
            throw new UserNotFoundException("User not found");
        }
        StockCheckRequest request = new StockCheckRequest(user.getName(), dishIds, address);
        publisher.checkStock(request);
    }

    @RabbitListener(queues = "orderQueue")
    public void orderValidated(PlaceOrderCommand command){
        Optional<User> optionalUser = userRepo.findByName(command.getUserName());
        Order newOrder = new Order(optionalUser.get(), command.getDishList(), LocalDateTime.now());
        this.repository.save(newOrder);
        AddDeliveryCommand newCommand = new AddDeliveryCommand(command.getUserName(),command.getAddress(), command.getDishList(), newOrder.getId());
        this.publisher.deliver(newCommand);
    }

}
