package application;

import common.Address;
import data.DeliveryRepository;
import data.RiderRepository;
import domain.Delivery;
import domain.Rider;
import exception.NoRidersAvailableException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DeliveryService {

    private final RiderRepository riders;
    private final DeliveryRepository deliveries;

    public DeliveryService(RiderRepository riders, DeliveryRepository deliveries) {
        this.riders = riders;
        this.deliveries = deliveries;
    }

    @Transactional
    public Delivery scheduleDelivery(UUID orderid, Address address) {
        Optional<Rider> rider = this.riders.findFirstByNrOfDeliveriesAsc();
        if(rider.isPresent()){
            Delivery delivery = new Delivery(orderid, rider.get(), address);
            deliveries.save(delivery);
            return delivery;
        } else {
            throw new NoRidersAvailableException("No riders currently available");
        }
    }
}