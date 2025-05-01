package ramazan.sahin.ecommerce.service;

import java.util.List;

import ramazan.sahin.ecommerce.dto.PaymentDTO;
import ramazan.sahin.ecommerce.dto.PaymentRequest;

public interface PaymentService {
    PaymentDTO processPayment(PaymentRequest paymentRequest);
    List<PaymentDTO> getAllPaymentByUser();
    boolean isPaymentSucceeded(String paymentIntentId);
    PaymentDTO createPaymentAfterCheckout(Long orderId);
    void markPaymentAsSucceeded(Long orderId);

    

}
