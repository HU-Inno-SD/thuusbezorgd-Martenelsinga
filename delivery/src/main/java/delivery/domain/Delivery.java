package delivery.domain;


import common.Address;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Delivery {
    @Id
    @GeneratedValue
    private Long id;

    private boolean completed;

    public Long getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public UUID getOrderId() {
        return orderId;
    }

    private Address address;

    @ManyToOne
    private Rider rider;

    private UUID orderId;

    public Address getAddress(){
        return this.address;
    }
    protected Delivery(){}

    public Delivery(UUID orderId, Rider rider, Address address){
        this.orderId = orderId;
        this.rider = rider;
        this.address = address;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted(){
        this.completed = true;
    }
}