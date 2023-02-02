package delivery.presentation;

import common.messages.AddDeliveryCommand;
import common.messages.ConfirmDeliveryCommand;
import common.messages.DeliveryError;
import delivery.data.DeliveryRepository;
import delivery.data.RiderRepository;
import delivery.domain.*;

import delivery.exception.DeliveryNotFoundException;
import delivery.infrastructure.DeliveryPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

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

    // TODO: Deze doet nog niks met het bericht
    @RabbitListener(queues = "deliveryQueue")
    public void fixDelivery(AddDeliveryCommand command) {
        if(this.deliveries.findByOrderId(command.getOrderId()).isPresent()){
            publisher.throwError(new DeliveryError("OrderId " + command.getOrderId() + " already has an associated delivery"));
            return;
        }
        Delivery delivery = new Delivery(command.getOrderId(), command.getAddress());
        deliveries.save(delivery);
        ConfirmDeliveryCommand newCommand = new ConfirmDeliveryCommand(command.getUser(), command.getOrderId(), delivery.getId());
        publisher.confirmDelivery(newCommand);
    }


    @GetMapping("{id}")
    public Delivery getDelivery(@PathVariable long id) throws DeliveryNotFoundException {
        Optional<Delivery> delivery = this.deliveries.findById(id);

        if (delivery.isEmpty()) {
            throw new DeliveryNotFoundException("Delivery not found");
        }
        return new Delivery(delivery.get().getId(), delivery.get().getOrderId(), delivery.get().isCompleted(), delivery.get().getAddress());
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