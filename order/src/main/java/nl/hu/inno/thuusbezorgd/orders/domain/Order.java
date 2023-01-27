package nl.hu.inno.thuusbezorgd.orders.domain;

import common.dto.DishDTO;
import common.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders") //Order is een keyword in sql, so this works around some wonky sql-generator implementations
public class Order {
    @Id
    @GeneratedValue
    private Long id;

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

    @OneToMany
    private List<DishDTO> orderedDishes;

    protected Order() {}

    public Order(User u, List<DishDTO> dishes, LocalDateTime moment) {
        this.user = u;
        this.orderedDishes = dishes;
        this.status = OrderStatus.Received;
        this.orderDate = moment;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<DishDTO> getOrderedDishes() {
        return orderedDishes;
    }


    public void addDish(DishDTO dish) {
        this.orderedDishes.add(dish);
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

//    public void process(LocalDateTime orderMoment) {
//        this.orderDate = orderMoment;
//        for (DishDTO d : this.getOrderedDishes()) {
//            d.prepare();
//        }
//    }
}