package ramazan.sahin.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import ramazan.sahin.ecommerce.dto.OrderDTO;
import ramazan.sahin.ecommerce.dto.PaymentDTO;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.service.ProductService;
import ramazan.sahin.ecommerce.service.UserService;
import ramazan.sahin.ecommerce.service.OrderService; // Sipariş servisi ekleniyor
import ramazan.sahin.ecommerce.service.PaymentService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService; 
    private final PaymentService paymentService;


    @PutMapping("/order-status/{orderId}")
    public OrderDTO updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @PutMapping("/ban-user/{userId}")
    public User banUser(@PathVariable Long userId) {
        return userService.banUser(userId);
    }

    @PutMapping("/unban-user/{userId}")
    public User unbanUser(@PathVariable Long userId) {
        return userService.unbanUser(userId);
    }

    // ✅ 3. Admin - Tüm Siparişleri Listeleme
    
    @GetMapping("/orders/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrdersForUser(null); // userId null → tüm siparişler
        return ResponseEntity.ok(orders);
    }

   
    @PutMapping("/{id}/role")
    public ResponseEntity<User> changeUserRole(@PathVariable Long id, @RequestParam String role) {
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

   
    @GetMapping("/order/filter")
    public ResponseEntity<List<OrderDTO>> filterOrdersByStatus(@RequestParam String status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // ✅ 7. Siparişi Silme (Admin veya Sahibi)
    @DeleteMapping("/order/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
     @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForAdmin() {
        List<PaymentDTO> payments = paymentService.getAllPaymentByUser(); // Implement filtering by seller in the
                                                                          // service layer
        return ResponseEntity.ok(payments);
    }

}
