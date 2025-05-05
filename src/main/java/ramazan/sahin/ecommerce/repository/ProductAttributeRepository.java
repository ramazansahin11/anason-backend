package ramazan.sahin.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ramazan.sahin.ecommerce.entity.ProductAttribute;

import java.util.Optional;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    // Spring Data JPA automatically provides CRUD methods (save, findById, findAll, deleteById, etc.)

    // You can add custom query methods if needed, for example:
    Optional<ProductAttribute> findByNameIgnoreCase(String name);

}