package nl.hu.inno.thuusbezorgd.orders.presentation;

import nl.hu.inno.thuusbezorgd.TimeProvider;
import nl.hu.inno.thuusbezorgd.orders.application.DeliveryService;
import nl.hu.inno.thuusbezorgd.orders.application.ReportService;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrdersRepository;
import nl.hu.inno.thuusbezorgd.security.User;
import nl.hu.inno.thuusbezorgd.security.UserRepository;
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
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    //Todo:
    ConnectionFactory factory = new ConnectionFactory();

    public record AddressDto(String street, String nr, String city, String zip) {
        public AddressDto(Address a) {
            this(a.getStreet(), a.getHousenr(), a.getCity(), a.getZipcode());
        }

        public Address toAddress() {
            return new Address(city(), street(), nr(), zip());
        }
    }

    public record DishDto(Long id, String name) {

        public DishDto(long id) {
            this(id, null);
        }

        public DishDto(Dish d) {
            this(d.getId(), d.getName());
        }
    }

    public record OrdersDto(AddressDto address, List<DishDto> dishes) {
    }

    public record OrdersResponseDto(AddressDto address, List<DishDto> dishes, OrdersStatus status, String deliveryUrl) {
        public static OrdersResponseDto fromOrders(Orders o) {
            List<Dish> orderedDishes = o.getOrderedDishes();
            List<DishDto> dtos = orderedDishes.stream().map(DishDto::new).collect(Collectors.toList());

            return new OrderResponseDto(new AddressDto(o.getAddress()), dtos, o.getStatus(), String.format("/deliveries/%s", o.getDelivery().getId()));
        }
    }

    private final OrdersRepository orders;
    private final DishRepository dishes;
    private final UserRepository users;
    private final DeliveryService deliveries;
    private TimeProvider timeProvider;
    private ReportService reports;


    public OrdersController( //Dit begint al aardige constructor overinjection te worden!
                             OrdersRepository orders,
                             DishRepository dishes,
                             UserRepository users,
                             DeliveryService deliveries,
                             TimeProvider timeProvider,
                             ReportService reports) {
        this.orders = orders;
        this.dishes = dishes;
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
        Optional<Orders> orders = this.orders.findById(id);
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
        List<DishDto> orderedDishes = new ArrayList<>();
        for (String d : paramMap.get("dish")) {
            long id = Long.parseLong(d);
            orderedDishes.add(new DishDto(id, ""));
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
        for (DishDto orderedDish : newOrders.dishes()) {
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

    //Todo: catch afmaken
    public boolean checkItemStock(List<Ingredient> ingredients){
        try(Connection connection = factory.newConnection()){
            Channel channel = connection.createChannel();
            channel.queueDeclare("stock-check", false, false, false, null);
            for(Ingredient i : ingredients){
                String message = i.name;
                channel.basicPublish("", "stock-check", false, null, message.getBytes());
            }
        }catch{

        }
    }

    @GetMapping("report")
    public ResponseEntity<List<OrdersPerDayDTO>> getReport(){
        List<ReportService.OrdersPerDayDTO> orders = this.reports.generateOrdersPerDayReport();

        return ResponseEntity.ok(orders.stream().map(o -> new OrdersPerDayDTO(o.year(), o.month(), o.day(), o.count())).toList());
    }

    public record OrdersPerDayDTO(int year, int month, int day, int orders) {
    }
}