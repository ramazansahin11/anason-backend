package ramazan.sahin.ecommerce.repository;


import ramazan.sahin.ecommerce.entity.Review;
import ramazan.sahin.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
}
