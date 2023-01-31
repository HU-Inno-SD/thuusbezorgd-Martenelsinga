package nl.hu.inno.thuusbezorgd.orders.dto;

import common.dto.DishDTO;
import nl.hu.inno.thuusbezorgd.orders.domain.User;
import nl.hu.inno.thuusbezorgd.orders.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long id;
    private User user;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<DishDTO> orderedDishes;

    public OrderDTO(Long id, User user, LocalDateTime orderDate, OrderStatus status, List<DishDTO> orderedDishes) {
        this.id = id;
        this.user = user;
        this.orderDate = orderDate;
        this.status = status;
        this.orderedDishes = orderedDishes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<DishDTO> getOrderedDishes() {
        return orderedDishes;
    }

    public void setOrderedDishes(List<DishDTO> orderedDishes) {
        this.orderedDishes = orderedDishes;
    }
}
