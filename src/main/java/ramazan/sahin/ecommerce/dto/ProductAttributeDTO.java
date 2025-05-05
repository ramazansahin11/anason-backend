package ramazan.sahin.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for transferring ProductAttribute data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeDTO {

    private Long id;
    private String name;

    // You might add constructors or static factory methods
    // for easy conversion from the entity if not using a mapping library.
}