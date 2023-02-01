package nl.hu.inno.thuusbezorgd.orders.presentation;

import common.Address;
import common.messages.AddDeliveryCommand;
import common.messages.PlaceOrderCommand;
import common.messages.StockCheckRequest;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository users;
    private final OrderPublisher publisher;

    public OrderController(
                            OrderRepository orders,
                            UserRepository users) {
        this.orderRepository = orders;
        this.users = users;
        this.publisher = new OrderPublisher();
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
        publisher.checkStock(request);
        return "Order has been received, we'll be back with an update soon!";
    }

    @RabbitListener(queues = "orderQueue")
    public String orderValidated(PlaceOrderCommand command){
        Optional<User> optionalUser = users.findByName(command.getUserName());
        Order newOrder = new Order(optionalUser.get(), command.getDishList(), LocalDateTime.now());
        this.orderRepository.save(newOrder);
        AddDeliveryCommand newCommand = new AddDeliveryCommand(command.getUserName(),command.getAddress(), command.getDishList(), newOrder.getId());
        this.publisher.deliver(newCommand);
        return "Order has been validated! We'll get to delivering real soon <3";
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

//    @GetMapping("report")
//    public ResponseEntity<List<OrdersPerDayDTO>> getReport(){
//        List<ReportService.OrdersPerDayDTO> orders = this.reports.generateOrdersPerDayReport();
//
//        return ResponseEntity.ok(orders.stream().map(o -> new OrdersPerDayDTO(o.year(), o.month(), o.day(), o.count())).toList());
//    }
//
//    public record OrdersPerDayDTO(int year, int month, int day, int orders) {
//    }
}