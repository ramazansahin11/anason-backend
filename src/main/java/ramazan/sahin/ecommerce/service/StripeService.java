package ramazan.sahin.ecommerce.service;



public interface StripeService {

    String createCheckoutSession(Long orderId);

    public String createPaymentIntent(Long amount);

    public boolean isPaymentSucceeded(String paymentIntentId) ;
    
} 