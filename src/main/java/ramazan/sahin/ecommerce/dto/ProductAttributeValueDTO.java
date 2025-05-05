package ramazan.sahin.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ramazan.sahin.ecommerce.entity.ProductAttributeValue;
import lombok.AllArgsConstructor;

/**
 * DTO for transferring ProductAttributeValue data.
 * Includes the ID and name of the associated ProductAttribute.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeValueDTO {

    private Long id;
    private String value;
    private Long attributeId; // ID of the related ProductAttribute
    private String attributeName; // Name of the related ProductAttribute

   
     public static ProductAttributeValueDTO fromEntity(ProductAttributeValue entity) {
         if (entity == null || entity.getAttribute() == null) {
             return null; // Or handle appropriately
         }
         return new ProductAttributeValueDTO(
             entity.getId(),
             entity.getValue(),
             entity.getAttribute().getId(),
             entity.getAttribute().getName()
        );
     }
}