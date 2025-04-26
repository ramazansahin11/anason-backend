package ramazan.sahin.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import ramazan.sahin.ecommerce.exception.BadRequestException;

@Service
public class StripeService {
    @Value("${stripe.secret.key}")
    private String secretKey;

    public StripeService(@Value("${stripe.secret.key}") String secretKey) {
    Stripe.apiKey = secretKey;
}


public String createCheckoutSession(Long amount, String successUrl, String cancelUrl) {
    try {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd") // veya "try" istiyorsan
                                                .setUnitAmount(amount) // cent bazlı! 1000 = 10$
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Test Product")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    } catch (Exception e) {
        throw new RuntimeException("Checkout session oluşturulamadı", e);
    }
}

public boolean isPaymentSucceeded(String paymentIntentId) {
    try {
        final boolean isSucceeded = PaymentIntent.retrieve(paymentIntentId)
                .getStatus()
                .equalsIgnoreCase("succeeded");
        return isSucceeded;
    } catch (Exception e) {
        throw new BadRequestException("Payment intent not found.");
    }
}

public String createPaymentIntent(Long amount){
    try {
        // Stripe kuruş bazında istiyor, mesela 1000 = 10.00₺ olur.
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount) // burada dikkat: kuruş (cent) bazında!
                .setCurrency("usd") // veya "try" (Türk Lirası) kullanabilirsin
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    } catch (Exception e) {
        throw new RuntimeException("Stripe PaymentIntent oluşturulamadı", e);
    }
}
}
