package ramazan.sahin.ecommerce.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import ramazan.sahin.ecommerce.entity.Order;
import ramazan.sahin.ecommerce.exception.BadRequestException;
import ramazan.sahin.ecommerce.repository.OrderRepository;
import ramazan.sahin.ecommerce.service.StripeService;



@RequiredArgsConstructor
@Service
public class StripeServiceImpl implements StripeService {

    

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    private final OrderRepository orderRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
    @Override
    public String createCheckoutSession(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        try {
            List<SessionCreateParams.LineItem> lineItems = order.getOrderItems().stream()
                    .map(item -> SessionCreateParams.LineItem.builder()
                            .setQuantity(Long.valueOf(item.getQuantity()))
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(100)).longValue()) // cents!
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(item.getProduct().getName())
                                            .build())
                                    .build())
                            .build())
                    .toList();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .addAllLineItem(lineItems)
                    .putMetadata("order_id", order.getId().toString())
.putMetadata("user_id", order.getUser().getId().toString())

                    .build();

            Session session = Session.create(params);
            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException("Stripe checkout oturumu oluşturulamadı", e);
        }
    }

    public boolean isPaymentSucceeded(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId)
                    .getStatus()
                    .equalsIgnoreCase("succeeded");
        } catch (Exception e) {
            throw new BadRequestException("Payment intent not found.");
        }
    }
    @Override
    public String createPaymentIntent(Long amount) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("usd")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return paymentIntent.getClientSecret();
        } catch (Exception e) {
            throw new RuntimeException("Stripe PaymentIntent oluşturulamadı", e);
        }
    }

   
    
}
