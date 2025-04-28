package ramazan.sahin.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ramazan.sahin.ecommerce.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Şu anlık ekstra metoda gerek yok!
}
