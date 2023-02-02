package delivery.domain;


import common.Address;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Delivery {
    @Id
    @GeneratedValue
    private UUID id;

    private boolean completed;

    public UUID getId() {
        return id;
    }


    public UUID getOrderId() {
        return orderId;
    }

    private Address address;


    private UUID orderId;

    public Address getAddress(){
        return this.address;
    }
    protected Delivery(){}

    public Delivery(UUID orderId, Address address){
        this.orderId = orderId;
        this.address = address;
    }

    public Delivery(UUID deliveryId, UUID orderId, boolean completed, Address address){
        this.id = deliveryId;
        this.orderId = orderId;
        this.completed = completed;
        this.address = address;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted(){
        this.completed = true;
    }
}