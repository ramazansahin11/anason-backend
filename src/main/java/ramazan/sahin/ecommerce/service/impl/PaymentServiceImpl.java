package ramazan.sahin.ecommerce.service.impl;

import ramazan.sahin.ecommerce.dto.PaymentDTO;
import ramazan.sahin.ecommerce.dto.PaymentRequest;
import ramazan.sahin.ecommerce.entity.Order;
import ramazan.sahin.ecommerce.entity.Payment;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.entity.Order.Status;
import ramazan.sahin.ecommerce.exception.BadRequestException;
import ramazan.sahin.ecommerce.repository.OrderRepository;
import ramazan.sahin.ecommerce.repository.PaymentRepository;
import ramazan.sahin.ecommerce.repository.UserRepository;
import ramazan.sahin.ecommerce.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.stripe.model.PaymentIntent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public PaymentDTO processPayment(PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new BadRequestException("Order not found."));

                
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(Payment.PaymentMethod.valueOf(paymentRequest.getPaymentMethod().toUpperCase()))
                .paymentStatus(Payment.PaymentStatus.COMPLETED) // Şu anda sahte tamamlanmış kabul ediyoruz
                .amount(paymentRequest.getAmount())
                .createdAt(LocalDateTime.now())
                .build();
         
        Payment savedPayment = paymentRepository.save(payment);
        if(payment.getPaymentStatus() == Payment.PaymentStatus.COMPLETED) {
                order.setStatus(Status.PROCESSING);

                
        }else{
                order.setStatus(Status.CANCELLED);
        }

        orderRepository.save(order);


        return PaymentDTO.builder()
                .id(savedPayment.getId())
                .orderId(savedPayment.getOrder().getId())
                .paymentMethod(savedPayment.getPaymentMethod().name())
                .paymentStatus(savedPayment.getPaymentStatus().name())
                .amount(savedPayment.getAmount())
                .createdAt(savedPayment.getCreatedAt())
                .build();
    }

    @Override
    public List<PaymentDTO> getAllPaymentByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        List<Payment> payment = paymentRepository.findByOrderUserId(user.getId());

        return payment.stream().map(p -> PaymentDTO.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .paymentMethod(p.getPaymentMethod().name())
                .paymentStatus(p.getPaymentStatus().name())
                .amount(p.getAmount())
                .createdAt(p.getCreatedAt())
                .build()).toList();

                
    }

    @Override
    public boolean isPaymentSucceeded(String paymentIntentId) {
        try
        {
                final boolean isSucceeded = PaymentIntent.retrieve(paymentIntentId).getStatus().equalsIgnoreCase("succeeded");
                return isSucceeded;
        } catch (Exception e) {
            throw new BadRequestException("Payment intent not found.");
        }
   
       
    }

    @Override
public PaymentDTO createPaymentAfterCheckout(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("Order not found"));

    Payment payment = Payment.builder()
        .order(order)
        .paymentMethod(Payment.PaymentMethod.STRIPE) // varsayılan olarak STRIPE
        .paymentStatus(Payment.PaymentStatus.COMPLETED) // demo senaryosu için
        .amount(order.getTotalPrice())
        .createdAt(LocalDateTime.now())
        .build();

    Payment savedPayment = paymentRepository.save(payment);

    return PaymentDTO.builder()
        .id(savedPayment.getId())
        .orderId(savedPayment.getOrder().getId())
        .paymentMethod(savedPayment.getPaymentMethod().name())
        .paymentStatus(savedPayment.getPaymentStatus().name())
        .amount(savedPayment.getAmount())
        .createdAt(savedPayment.getCreatedAt())
        .build();
}

@Override
@Transactional
public void markPaymentAsSucceeded(Long orderId) {
    // Sipariş durumunu güncelle
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BadRequestException("Order not found"));
            order.setStatus(Order.Status.PROCESSING); // String değil, enum sabiti!

    orderRepository.save(order);

    BigDecimal totalAmount = order.getOrderItems().stream()
    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
    .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Ödeme kaydı oluştur
    Payment payment = Payment.builder()
    .order(order)
    .amount(totalAmount)
    .paymentMethod(Payment.PaymentMethod.STRIPE) // İsteğe bağlı
    .paymentStatus(Payment.PaymentStatus.COMPLETED)
    .createdAt(LocalDateTime.now())
    .build();

    paymentRepository.save(payment);

    // (İsteğe bağlı) Kullanıcıya e-posta gönderilebilir
}


}
