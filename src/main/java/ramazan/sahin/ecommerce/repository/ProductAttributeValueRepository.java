package ramazan.sahin.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ramazan.sahin.ecommerce.entity.ProductAttributeValue;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {

    // Spring Data JPA automatically provides CRUD methods

    // Custom query methods examples:

    // Find all values associated with a specific attribute ID
    List<ProductAttributeValue> findByAttributeId(Long attributeId);

    // Find all values associated with a specific attribute name (requires join)
    List<ProductAttributeValue> findByAttributeNameIgnoreCase(String attributeName);

    // Find a specific value for a given attribute ID
    Optional<ProductAttributeValue> findByAttributeIdAndValueIgnoreCase(Long attributeId, String value);

}