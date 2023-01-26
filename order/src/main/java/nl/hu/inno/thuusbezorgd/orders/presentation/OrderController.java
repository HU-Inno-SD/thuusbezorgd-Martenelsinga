package nl.hu.inno.thuusbezorgd.orders.presentation;

import common.Address;
import common.DTO.DishDTO;
import common.User;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
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
@RequestMapping("/order")
public class OrderController {
    public record AddressDto(String street, String nr, String city, String zip) {
        public AddressDto(Address a) {
            this(a.getStreet(), a.getHousenr(), a.getCity(), a.getZipcode());
        }

        public Address toAddress() {
            return new Address(city(), street(), nr(), zip());
        }
    }


    public record OrdersDto(AddressDto address, List<DishDTO> dishes) {
    }

    public record OrdersResponseDto(AddressDto address, List<DishDTO> dishes, OrderStatus status, String deliveryUrl) {
        public static OrdersResponseDto fromOrder(Order o) {
            List<DishDTO> orderedDishes = o.getOrderedDishes();

            return new OrderResponseDTO(new AddressDTO(o.getAddress()), dtos, o.getStatus(), String.format("/deliveries/%s", o.getDelivery().getId()));
        }
    }

    private final OrderRepository orders;
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
        this.orders = orders;
        this.users = users;
        this.deliveries = deliveries;
        this.timeProvider = timeProvider;
        this.reports = reports;
    }

    @GetMapping()
    public List<OrdersResponseDto> getOrders(User user) {
        return this.orders.findByUser(user).stream().map(OrderResponseDto::fromOrders).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<OrdersResponseDto> getOrders(User user, @PathVariable long id) {
        Optional<Order> order = this.orders.findById(id);
        if(orders.isEmpty() || order.get().getUser() != user){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrdersResponseDto.fromOrders(orders.get()));
    }

    public boolean checkStock(String dish){

    }


    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Transactional
    public ResponseEntity<OrdersResponseDto> placeOrders(User user, @RequestBody MultiValueMap<String, String> paramMap) throws URISyntaxException {
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
    public ResponseEntity<OrdersResponseDto> placeOrders(User user, @RequestBody OrdersDto newOrders) throws URISyntaxException {
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

        Orders savedOrders = this.orders.save(created);
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