package nl.hu.inno.thuusbezorgd.orders.dto;

import common.Address;
import common.dto.DishDTO;
import nl.hu.inno.thuusbezorgd.orders.domain.User;
import nl.hu.inno.thuusbezorgd.orders.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private String username;
    private OrderStatus status;
    private List<Long> dishIds;
    private Address address;
    public OrderDTO(String username, List<Long> dishIds, Address address) {
        this.username = username;
        this.status = OrderStatus.Received;
        this.dishIds = dishIds;
        this.address = address;
    }

    public String getUserName() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<Long> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<Long> dishIds) {
        this.dishIds = dishIds;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
