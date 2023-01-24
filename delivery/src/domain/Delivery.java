package domain;


import javax.persistence.*;

@Entity
@NamedNativeQuery(name = "Delivery.findRandom", resultClass = Delivery.class,
        query = "select * from delivery order by random() limit 1")
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

    public Orders getOrder() {
        return order;
    }

    @ManyToOne
    private Rider rider;

    @ManyToOne
    private Orders order;

    protected Delivery(){}

    public Delivery(Orders order, Rider rider){
        this.order = order;
        this.rider = rider;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted(){
        this.completed = true;
        this.order.setStatus(OrderStatus.Delivered);
    }
}