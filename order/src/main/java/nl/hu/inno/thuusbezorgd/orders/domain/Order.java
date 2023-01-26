package nl.hu.inno.thuusbezorgd.orders.domain;

import common.User;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
//    @OneToOne
//    private Delivery delivery;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    @OneToMany(mappedBy = "id.order")
    @Cascade(CascadeType.PERSIST)
    private List<Dish> orderedDishes;

    protected Order() {}

    public Order(User u) {
        this.user = u;
        this.orderedDishes = new ArrayList<>();
        this.status = OrderStatus.Received;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Dish> getOrderedDishes() {
        return orderedDishes;
    }


    public void addDish(Dish dish) {
        this.orderedDishes.add(dish);
    }


    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void disputeOrder(){
        if(this.status == OrderStatus.Delivered || this.status == OrderStatus.Underway){
            this.status = OrderStatus.Disputed;
        }
    }

    public void advanceOrder(){
        this.status.next();
    }

    public void process(LocalDateTime orderMoment) {
        this.orderDate = orderMoment;
        for (Dish d : this.getOrderedDishes()) {
            d.prepare();
        }
    }
}