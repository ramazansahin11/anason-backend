package ramazan.sahin.ecommerce.controller;

import ramazan.sahin.ecommerce.dto.PaymentDTO;
import ramazan.sahin.ecommerce.service.PaymentService;
import ramazan.sahin.ecommerce.service.StripeService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final StripeService stripeService;

   /*  @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentDTO paymentDTO = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(paymentDTO);
    }*/

    @GetMapping("/my")
    public ResponseEntity<List<PaymentDTO>> getPaymentList() {
        List<PaymentDTO> paymentDTOs = paymentService.getAllPaymentByUser();
        return ResponseEntity.ok(paymentDTOs);
    }

    @PostMapping("/intent")
    public ResponseEntity<String> createPaymentIntent(@RequestParam Long amount) {
        String clientSecret = stripeService.createPaymentIntent(amount);
        return ResponseEntity.ok(clientSecret);

    }

    @PostMapping("/checkout-session")
    public ResponseEntity<String> createCheckoutSession(@RequestParam Long orderId) {
        String checkoutUrl = stripeService.createCheckoutSession(orderId);
        return ResponseEntity.ok(checkoutUrl);
    }

}
