package ramazan.sahin.ecommerce.service;

import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.exception.BadRequestException;
import ramazan.sahin.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.CUSTOMER);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(User.Status.BANNED);
        return userRepository.save(user);
    }

    public User unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(User.Status.ACTIVE);
        return userRepository.save(user);
    }

    public User changeUserRole(Long userId, String role) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    try {
        User.Role newRole = User.Role.valueOf(role.toUpperCase());
        user.setRole(newRole);
    } catch (IllegalArgumentException e) {
        throw new BadRequestException("Invalid role: " + role);
    }

    return userRepository.save(user);
}

}
