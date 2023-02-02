package nl.hu.inno.thuusbezorgd.orders.domain;

import common.dto.DishDTO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders") //Order is een keyword in sql, so this works around some wonky sql-generator implementations
public class Order {
    @Id
    private UUID id;

    @ManyToOne
    private User user;

    private LocalDateTime orderDate;

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    @Column
    @ElementCollection
    private List<Long> orderedDishIds;

    private UUID deliveryId;

    protected Order() {}

    public Order(User u, List<Long> dishIds, LocalDateTime moment) {
        this.id = UUID.randomUUID();
        this.user = u;
        this.orderedDishIds = dishIds;
        this.status = OrderStatus.Received;
        this.orderDate = moment;
    }


    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Long> getOrderedDishes() {
        return orderedDishIds;
    }


    public void addDish(DishDTO dish) {
        this.orderedDishIds.add(dish.getId());
    }


//    public Delivery getDelivery() {
//        return delivery;
//    }
//
//    public void setDelivery(Delivery delivery) {
//        this.delivery = delivery;
//    }

    public void disputeOrder(){
        if(this.status == OrderStatus.Delivered || this.status == OrderStatus.Underway){
            this.status = OrderStatus.Disputed;
        }
    }

    public void advanceOrder(){
        this.status = this.status.next();
    }

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    //    public void process(LocalDateTime orderMoment) {
//        this.orderDate = orderMoment;
//        for (DishDTO d : this.getOrderedDishes()) {
//            d.prepare();
//        }
//    }
}