package ramazan.sahin.ecommerce.repository;
import ramazan.sahin.ecommerce.entity.Order;
import ramazan.sahin.ecommerce.entity.Payment;
import ramazan.sahin.ecommerce.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderUserId(Long userId);


}