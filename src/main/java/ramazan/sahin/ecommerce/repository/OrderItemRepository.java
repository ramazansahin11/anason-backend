package ramazan.sahin.ecommerce.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ramazan.sahin.ecommerce.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Şimdilik ekstra metod yazmaya gerek yok, JpaRepository yetiyor.
}
 
    

