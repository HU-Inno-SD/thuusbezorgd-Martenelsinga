package nl.hu.inno.thuusbezorgd.orders.presentation;

import common.Address;
import common.StockObject;
import common.messages.*;
import nl.hu.inno.thuusbezorgd.orders.domain.User;
import nl.hu.inno.thuusbezorgd.orders.data.UserRepository;
import common.exception.UserNotFoundException;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.dto.OrderDTO;
import nl.hu.inno.thuusbezorgd.orders.exception.OrderNotFoundException;
import nl.hu.inno.thuusbezorgd.orders.infrastructure.OrderPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository users;
    private final OrderPublisher publisher;
    private List<StockObject> stock;

    public OrderController(
                            OrderRepository orders,
                            UserRepository users) {
        this.orderRepository = orders;
        this.users = users;
        this.publisher = new OrderPublisher();
        this.stock = new ArrayList<>();
        randomStockCheck(3);

    }

    @GetMapping("/user/{name}")
    public List<Order> getOrdersByUsername(@PathVariable String name) throws UserNotFoundException {
        Optional<User> optionalUser = this.users.findByName(name);
        if(!optionalUser.isPresent()){
            throw new UserNotFoundException("User not found!");
        }
        User user = optionalUser.get();
        List<Order> orders = orderRepository.findByUser(user);
        if(orders.isEmpty()){
            throw new UserNotFoundException("User has no orders");
        }
        return orderRepository.findByUser(user);
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody User user){
        users.save(user);
    }

    @PostMapping("/order")
    @Transactional
    public String placeOrder(@Validated @RequestBody OrderDTO orderDTO) throws UserNotFoundException{
        Optional<User> optUser = this.users.findByName(orderDTO.getUserName());
        if(!optUser.isPresent()){
            throw new UserNotFoundException("User not found!");
        }
        Address address = orderDTO.getAddress();
        StockCheckRequest request = new StockCheckRequest(optUser.get().getName(), orderDTO.getDishIds(), address);
        for(Long dishId : orderDTO.getDishIds()){
            for(StockObject s : stock){
                if(s.getDishId() == dishId){
                    if(s.getNrInStock() > 5){
                        s.takeOne();
                    }
                    else{
                        return "Sorry, we don't have enough of " + dishId + " right now";
                    }
                }
            }
        }
        publisher.checkStock(request);
        this.randomStockCheck(3);
        return "Order has been received";
    }

    @GetMapping("/startup")
    private void randomStockCheck(int override){
        double random = Math.random() * 5;
        int num = (int)Math.round(random);
        if(num == 3 || override == 3){
            publisher.randomStockCheck(new RandomStockCheck("randomStockCheck"));
        }
    }

    @RabbitListener(queues = "orderQueue")
    public void randomStockCheckReply(RandomStockCheckReply reply){
        this.stock = reply.getStock();
    }

    @RabbitListener(queues = "orderQueue")
    public void orderValidated(PlaceOrderCommand command){
        Optional<User> optionalUser = users.findByName(command.getUserName());
        Order newOrder = new Order(optionalUser.get(), command.getDishList(), LocalDateTime.now());
        // We checked the stock and it's there, so we advance the order to 'InPreparation'
        newOrder.advanceOrder();
        this.orderRepository.save(newOrder);
        AddDeliveryCommand newCommand = new AddDeliveryCommand(command.getUserName(),command.getAddress(), command.getDishList(), newOrder.getId());
        this.publisher.deliver(newCommand);
    }

    @RabbitListener(queues = "orderQueue")
    public void deliveryValidated(ConfirmDeliveryCommand command){
        Optional<Order> optOrder = orderRepository.findById(command.getOrderId());
        if(optOrder.isPresent()){
            Order order = optOrder.get();
            order.setDeliveryId(command.getDeliveryId());
            // A delivery has been assigned to the order, so we can advance it to 'Underway'
            order.advanceOrder();
            this.orderRepository.save(order);
        }
    }


    @PutMapping("/{id}/advance")
    public void advanceOrder(@PathVariable Long id) throws OrderNotFoundException{
        Optional<Order> optOrder = this.orderRepository.findById(id);
        if(!optOrder.isPresent()){
            throw new OrderNotFoundException("Order not found");
        }
        Order order = optOrder.get();
        order.advanceOrder();
        this.orderRepository.save(order);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) throws OrderNotFoundException{
        Optional<Order> optionalOrder = this.orderRepository.findById(id);
        if(!optionalOrder.isPresent()){
            throw new OrderNotFoundException("Order not found");
        }
        return optionalOrder.get();
    }

}