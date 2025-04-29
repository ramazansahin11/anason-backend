package ramazan.sahin.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ramazan.sahin.ecommerce.entity.Cart;
import ramazan.sahin.ecommerce.entity.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Long userId);

}
