package nl.hu.inno.thuusbezorgd.orders.presentation;

import common.Address;
import nl.hu.inno.thuusbezorgd.orders.domain.User;
import nl.hu.inno.thuusbezorgd.orders.data.UserRepository;
import common.exception.UserNotFoundException;
import nl.hu.inno.thuusbezorgd.orders.application.OrderService;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.exception.OrderNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService service;
    private final OrderRepository orderRepository;
    private final UserRepository users;

    public OrderController(
                            OrderRepository orders,
                            UserRepository users,
                            OrderService service) {
        this.orderRepository = orders;
        this.users = users;
        this.service = service;
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

    @PostMapping("/order")
    @Transactional
    public void placeOrder(@Validated @RequestBody String username, @Validated @RequestBody List<Long> dishIds, @Validated @RequestBody Address address) throws UserNotFoundException{
        Optional<User> optUser = this.users.findByName(username);
        if(!optUser.isPresent()){
            throw new UserNotFoundException("User not found!");
        }
        this.service.placeOrder(optUser.get(), dishIds, address);
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