package nl.hu.inno.thuusbezorgd.orders.data;

import nl.hu.inno.thuusbezorgd.orders.domain.Orders;
import nl.hu.inno.thuusbezorgd.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders,Long> {
    List<Orders> findByUser(User currentUser);
}