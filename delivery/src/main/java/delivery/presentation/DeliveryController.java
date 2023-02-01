package delivery.presentation;

import common.messages.AddDeliveryCommand;
import delivery.data.DeliveryRepository;
import delivery.data.RiderRepository;
import delivery.domain.*;

import delivery.dto.DeliveryDTO;
import delivery.exception.DeliveryNotFoundException;
import delivery.exception.NoRidersAvailableException;
import delivery.infrastructure.DeliveryPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryRepository deliveries;
    private final RiderRepository riders;
    private final DeliveryPublisher publisher;

    public DeliveryController(DeliveryRepository deliveries, RiderRepository riders) {
        this.deliveries = deliveries;
        this.publisher = new DeliveryPublisher();
        this.riders = riders;
    }

    @RabbitListener(queues = "deliveryQueue")
    public void fixDelivery(AddDeliveryCommand command) {
        System.out.println("Aangekomen in delivery!");
//        Optional<Rider> rider = this.riders.findFirstByNrOfDeliveries();
//        if(rider.isPresent()){
//            Delivery delivery = new Delivery(command.getOrderId(), rider.get(), command.getAddress());
//            deliveries.save(delivery);
//        } else {
//            throw new NoRidersAvailableException("No riders currently available");
//        }
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