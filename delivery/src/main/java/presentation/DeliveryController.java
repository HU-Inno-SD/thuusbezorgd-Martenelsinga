package presentation;

import application.DeliveryService;
import common.messages.AddDeliveryCommand;
import data.DeliveryRepository;
import data.RiderRepository;
import domain.*;

import dto.DeliveryDTO;
import exception.DeliveryNotFoundException;
import infrastructure.DeliveryPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryRepository deliveries;
    private final RiderRepository riders;

    public DeliveryController(DeliveryService deliveryService, DeliveryRepository deliveries, RiderRepository riders) {
        this.deliveryService = deliveryService;
        this.deliveries = deliveries;
        DeliveryPublisher publisher = new DeliveryPublisher();
        this.riders = riders;
    }

    @RabbitListener(queues = "deliveryQueue")
    public void fixDelivery(AddDeliveryCommand command) {
        this.deliveryService.scheduleDelivery(command.getOrderId(), command.getAddress());
    }


    @GetMapping("/user}")
    public List<DeliveryDTO> deliveries(@RequestBody Long userId) {
        List<Delivery> found = deliveries.findByOrder_UserId(userId);
        List<DeliveryDTO> dtos = new ArrayList<>();
        for (Delivery d : found) {
            dtos.add(new DeliveryDTO(d.getId(), d.isCompleted(), d.getRider()));
        }
        return dtos;
    }

    @GetMapping("{id}")
    public DeliveryDTO getDelivery(@PathVariable long id) throws DeliveryNotFoundException {
        Optional<Delivery> delivery = this.deliveries.findById(id);

        if (delivery.isEmpty()) {
            throw new DeliveryNotFoundException("Delivery not found");
        }
        return new DeliveryDTO(delivery.get().getId(), delivery.get().isCompleted(), delivery.get().getRider());
    }

    @GetMapping()
    public List<Delivery> getDeliveries(){
        return this.deliveries.findAll();
    }

    @PostMapping("/new/")
    public void addDelivery(@RequestBody Delivery delivery) {
        this.deliveries.save(delivery);
    }

    @PostMapping("/riders/new")
    public void addRider(@RequestBody Rider rider) {
        this.riders.save(rider);
    }
}