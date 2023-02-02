package nl.hu.inno.thuusbezorgd.orders.data;

import nl.hu.inno.thuusbezorgd.orders.domain.User;
import nl.hu.inno.thuusbezorgd.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User currentUser);
    Optional<Order> findByOrderId(UUID orderId);
}