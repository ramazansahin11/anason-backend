package ramazan.sahin.ecommerce.repository;

import ramazan.sahin.ecommerce.entity.Product;
import ramazan.sahin.ecommerce.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findBySeller(User user);
    List<Product> findBySellerId(Long sellerId);
    
}