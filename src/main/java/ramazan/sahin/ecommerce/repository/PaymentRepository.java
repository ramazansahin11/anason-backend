package ramazan.sahin.ecommerce.repository;
import ramazan.sahin.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}