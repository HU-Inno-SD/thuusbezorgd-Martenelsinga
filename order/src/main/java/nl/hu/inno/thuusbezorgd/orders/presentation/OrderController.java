package nl.hu.inno.thuusbezorgd.orders.presentation;

import common.Address;
import common.DTO.DishDTO;
import common.User;
import common.UserRepository;
import common.exception.UserNotFoundException;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.exception.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.rabbitmq.client.ConnectionFactory;

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
                            ReportService reports) {
        this.orderRepository = orders;
        this.users = users;
        this.deliveries = deliveries;
        this.timeProvider = timeProvider;
        this.reports = reports;
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
    public OrderDTO placeOrder(User user, @Validated @RequestBody MultiValueMap<String, String> paramMap) throws URISyntaxException {
        List<DishDTO> orderedDishes = new ArrayList<>();
        for (String d : paramMap.get("dish")) {
            long id = Long.parseLong(d);
            orderedDishes.add(new DishDTO(id, ""));
        }

        String city = paramMap.getFirst("city");
        String street = paramMap.getFirst("street");
        String nr = paramMap.getFirst("nr");
        String zip = paramMap.getFirst("zip");

        //Todo: validate

        return placeOrders(user, new OrdersDto(new AddressDto(street, nr, city, zip), orderedDishes));
    }


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    public ResponseEntity<OrdersResponseDto> placeOrder(User user, @RequestBody OrdersDto newOrders) throws URISyntaxException {
        Orders created = new Orders(user, newOrders.address.toAddress());
        for (DishDTO orderedDish : newOrders.dishes()) {
            Optional<Dish> d = this.dishes.findById(orderedDish.id());
            if (d.isPresent()) {
                created.addDish(d.get());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Dish %s %s not found".formatted(orderedDish.id(), orderedDish.name()));
            }
        }

        Orders savedOrders = this.orderRepository.save(created);
        savedOrders.process(this.timeProvider.now());

        Delivery newDelivery = deliveries.scheduleDelivery(savedOrders);
        savedOrders.setDelivery(newDelivery);

        return ResponseEntity
                .created(new URI("/orders/%d".formatted(savedOrders.getId())))
                .body(OrdersResponseDto.fromOrders(savedOrders));

    }

    @GetMapping("/stock/")
    public boolean checkItemStock(@RequestBody String ingredient) throws Exception{

    }

    @GetMapping("report")
    public ResponseEntity<List<OrdersPerDayDTO>> getReport(){
        List<ReportService.OrdersPerDayDTO> orders = this.reports.generateOrdersPerDayReport();

        return ResponseEntity.ok(orders.stream().map(o -> new OrdersPerDayDTO(o.year(), o.month(), o.day(), o.count())).toList());
    }

    public record OrdersPerDayDTO(int year, int month, int day, int orders) {
    }
}