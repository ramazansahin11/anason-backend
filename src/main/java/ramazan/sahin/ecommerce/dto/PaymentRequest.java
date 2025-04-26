package ramazan.sahin.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private Long orderId;
    private String paymentMethod; // STRIPE veya PAYPAL
    private BigDecimal amount;
}
