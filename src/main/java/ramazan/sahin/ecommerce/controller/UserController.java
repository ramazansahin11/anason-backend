package ramazan.sahin.ecommerce.controller;

import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ Kullanıcı kaydı
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User saved = userService.register(user);
        return ResponseEntity.ok(saved);
    }

    // ✅ Tüm kullanıcıları getir (sadece admin kullanabilir - güvenlik sonra
    // eklenir)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // ✅ Kullanıcıyı banla
    @PutMapping("/ban/{id}")
    public ResponseEntity<User> banUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.banUser(id));
    }

    // ✅ Kullanıcının ban'ini kaldır
    @PutMapping("/unban/{id}")
    public ResponseEntity<User> unbanUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.unbanUser(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<User> changeUserRole(@PathVariable Long id, @RequestParam String role) {
        return ResponseEntity.ok(userService.changeUserRole(id, role));
    }

}
