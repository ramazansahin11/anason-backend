package ramazan.sahin.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import ramazan.sahin.ecommerce.dto.OrderDTO;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.service.ProductService;
import ramazan.sahin.ecommerce.service.UserService;
import ramazan.sahin.ecommerce.service.OrderService; // Sipariş servisi ekleniyor

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 🔥 Buraya dikkat
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService; // Sipariş servisi ekleniyor

    @GetMapping("/orders")
public List<OrderDTO> getAllOrders() {
    return orderService.getAllOrdersForUser(null); // null verirsek tüm siparişleri döneriz
}

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


}
