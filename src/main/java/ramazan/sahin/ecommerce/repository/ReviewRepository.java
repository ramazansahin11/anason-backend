package ramazan.sahin.ecommerce.repository;


import ramazan.sahin.ecommerce.entity.Review;
import ramazan.sahin.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
    List<Review> findByProductId(Long productId);
    List<Review> findByProductIdIn(List<Long> productIds);
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    List<Review> findByProductIdWithUser(Long productId);
}
