package ramazan.sahin.ecommerce.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ramazan.sahin.ecommerce.dto.OrderDTO;
import ramazan.sahin.ecommerce.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. Yeni Sipariş Oluşturma
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDto) {
        OrderDTO savedOrder = orderService.createOrder(orderDto);
        return ResponseEntity.ok(savedOrder);
    }

    // 2. Kullanıcının Kendi Siparişlerini Listeleme
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrdersForUser(@RequestParam Long userId) {
        List<OrderDTO> orders = orderService.getAllOrdersForUser(userId);
        return ResponseEntity.ok(orders);
    }

    // 3. Belirli Siparişi Getirme
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // 4. Sipariş Durumunu Güncelleme (sadece Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }


    // 5. Siparişi Silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    //6. Siparişi İptal Etme
    @PutMapping("/{orderId}/cancel")
public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
    OrderDTO canceledOrder = orderService.cancelOrder(orderId);
    return ResponseEntity.ok(canceledOrder);
}
//7, Siparişleri Listeleme
@GetMapping("/my")
public ResponseEntity<List<OrderDTO>> getMyOrders() {
    List<OrderDTO> myOrders = orderService.getMyOrders();
    return ResponseEntity.ok(myOrders);
}

@PostMapping("/complete")
public ResponseEntity<?> completeOrder(@RequestParam Long orderId, @RequestParam String paymentIntentId) {
    orderService.completeOrderIfPaymentSucceeded(orderId, paymentIntentId);
    return ResponseEntity.ok("Order has been successfully delivered.");
}




}
