package nl.hu.inno.thuusbezorgd.orders.data;

import common.User;
import nl.hu.inno.thuusbezorgd.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User currentUser);
}