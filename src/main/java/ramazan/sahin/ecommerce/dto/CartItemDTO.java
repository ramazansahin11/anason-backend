package ramazan.sahin.ecommerce.dto;


import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private java.math.BigDecimal price;
    private String imageUrl;
}
