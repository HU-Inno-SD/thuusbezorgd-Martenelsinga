package domain;


import javax.persistence.*;

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

    public Long getOrderId() {
        return orderId;
    }

    @ManyToOne
    private Rider rider;

    private Long orderId;

    protected Delivery(){}

    public Delivery(Long orderId, Rider rider){
        this.orderId = orderId;
        this.rider = rider;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted(){
        this.completed = true;
    }
}