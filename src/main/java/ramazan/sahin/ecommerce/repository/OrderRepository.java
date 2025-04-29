package ramazan.sahin.ecommerce.repository;

import ramazan.sahin.ecommerce.entity.Order;
import ramazan.sahin.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(Order.Status status);

}