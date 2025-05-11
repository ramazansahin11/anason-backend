package ramazan.sahin.ecommerce.service;

import java.util.List;
import java.util.Optional;

import ramazan.sahin.ecommerce.entity.User;

public interface UserService {

    User register(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findAllUsers();

    User banUser(Long userId);

    User unbanUser(Long userId);
     public User changeUserRole(Long userId, String role);
}
