package ramazan.sahin.ecommerce.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.exception.BadRequestException;
import ramazan.sahin.ecommerce.repository.UserRepository;
import ramazan.sahin.ecommerce.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

   

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.CUSTOMER);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(User.Status.BANNED);
        return userRepository.save(user);
    }

    @Override
    public User unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(User.Status.ACTIVE);
        return userRepository.save(user);
    }

    @Override
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
