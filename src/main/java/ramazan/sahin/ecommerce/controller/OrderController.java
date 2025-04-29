package ramazan.sahin.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ramazan.sahin.ecommerce.dto.OrderDTO;
import ramazan.sahin.ecommerce.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ✅ 1. Yeni Sipariş Oluşturma
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDto) {
        OrderDTO savedOrder = orderService.createOrder(orderDto);
        return ResponseEntity.ok(savedOrder);
    }

    // ✅ 2. Kullanıcının Kendi Siparişlerini Listeleme
    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        List<OrderDTO> myOrders = orderService.getMyOrders();
        return ResponseEntity.ok(myOrders);
    }

    // ✅ 3. Admin - Tüm Siparişleri Listeleme
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrdersForUser(null); // userId null → tüm siparişler
        return ResponseEntity.ok(orders);
    }

    // ✅ 4. Belirli Siparişi Getirme
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // ✅ 5. Sipariş Durumunu Güncelleme (Sadece Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // ✅ 6. Siparişi İptal Etme (Kullanıcı kendi siparişini)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        OrderDTO canceledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(canceledOrder);
    }

    // ✅ 7. Siparişi Silme (Admin veya Sahibi)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ 8. Ödeme Başarılıysa Siparişi Tamamla
    @PostMapping("/complete")
    public ResponseEntity<String> completeOrder(
            @RequestParam Long orderId,
            @RequestParam String paymentIntentId) {
        orderService.completeOrderIfPaymentSucceeded(orderId, paymentIntentId);
        return ResponseEntity.ok("Order has been successfully delivered.");
    }

    @PreAuthorize("hasRole('ADMIN')")
@GetMapping("/filter")
public ResponseEntity<List<OrderDTO>> filterOrdersByStatus(@RequestParam String status) {
    List<OrderDTO> orders = orderService.getOrdersByStatus(status);
    return ResponseEntity.ok(orders);
}


}
