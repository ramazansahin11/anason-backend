package ramazan.sahin.ecommerce.repository;

import ramazan.sahin.ecommerce.entity.Complaint;
import ramazan.sahin.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUser(User user);
}
