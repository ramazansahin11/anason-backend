package ramazan.sahin.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class OrderDTO {

    private Long id; // Response için gerekli olabilir (CREATE sonrası)

    private Long userId; // User ID'yi backend'den de alabiliriz, requestte almak opsiyonel

    @NotNull(message = "Shipping address must not be null")
    private Long shippingAddressId;

    private String status = "PENDING"; // Default değer

    private BigDecimal totalPrice; // Hesaplanabilir de olabilir

    @NotNull(message = "Order items must not be empty")
    private List<OrderItemDTO> orderItems;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
