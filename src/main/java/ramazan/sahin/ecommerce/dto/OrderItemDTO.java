package ramazan.sahin.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    @NotNull(message = "Product id must not be null")
    private Long productId;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price must not be null")
    private BigDecimal unitPrice;

}
