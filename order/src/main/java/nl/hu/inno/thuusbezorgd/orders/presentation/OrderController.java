package nl.hu.inno.thuusbezorgd.orders.presentation;

import common.Address;
import common.DishList;
import common.User;
import common.UserRepository;
import common.exception.DishNotFoundException;
import common.exception.UserNotFoundException;
import common.requests.stockCheckRequest;
import nl.hu.inno.thuusbezorgd.orders.application.OrderService;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.dto.OrderDTO;
import nl.hu.inno.thuusbezorgd.orders.exception.OrderNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    public record AddressDto(String street, String nr, String city, String zip) {
        public AddressDto(Address a) {
            this(a.getStreet(), a.getHousenr(), a.getCity(), a.getZipcode());
        }

        public Address toAddress() {
            return new Address(city(), street(), nr(), zip());
        }
    }

    private final OrderService service;
    private final OrderRepository orderRepository;
    private final UserRepository users;
    private final DeliveryService deliveries;
    private TimeProvider timeProvider;
    private ReportService reports;


    public OrderController( //Dit begint al aardige constructor overinjection te worden!
                            OrderRepository orders,
                            UserRepository users,
                            DeliveryService deliveries,
                            TimeProvider timeProvider,
                            ReportService reports,
                            OrderService service) {
        this.orderRepository = orders;
        this.users = users;
        this.deliveries = deliveries;
        this.timeProvider = timeProvider;
        this.reports = reports;
        this.service = service;
    }

    @GetMapping("/user/{name}")
    public List<Order> getOrders(@PathVariable User user) throws UserNotFoundException {
        List<Order> orders = orderRepository.findByUser(user);
        if(orders.isEmpty()){
            throw new UserNotFoundException("User not found or they have no orders");
        }
        return orderRepository.findByUser(user);
    }

    @GetMapping("/user/{name}/{id}")
    public Optional<Order> getOrder(User user, @PathVariable long id) throws UserNotFoundException, OrderNotFoundException {
        Optional<Order> order = this.orderRepository.findById(id);
        if(order.isEmpty()){
            throw new OrderNotFoundException("Order not found");
        }
        if(order.get().getUser() != user){
            throw new UserNotFoundException("User not found");
        }
        return order;
    }

    @PostMapping("/order")
    @Transactional
    public void placeOrder(User user, @Validated @RequestBody DishList order, @Validated @RequestBody Address address){
        this.service.placeOrder(user, order, address);
    }


    @GetMapping("report")
    public ResponseEntity<List<OrdersPerDayDTO>> getReport(){
        List<ReportService.OrdersPerDayDTO> orders = this.reports.generateOrdersPerDayReport();

        return ResponseEntity.ok(orders.stream().map(o -> new OrdersPerDayDTO(o.year(), o.month(), o.day(), o.count())).toList());
    }

    public record OrdersPerDayDTO(int year, int month, int day, int orders) {
    }
}