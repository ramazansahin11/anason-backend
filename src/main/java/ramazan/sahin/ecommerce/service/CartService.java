package ramazan.sahin.ecommerce.service;

import ramazan.sahin.ecommerce.dto.CartItemDTO;
import ramazan.sahin.ecommerce.entity.Cart;
import ramazan.sahin.ecommerce.entity.CartItem;
import ramazan.sahin.ecommerce.entity.Product;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.repository.CartItemRepository;
import ramazan.sahin.ecommerce.repository.CartRepository;
import ramazan.sahin.ecommerce.repository.ProductRepository;
import ramazan.sahin.ecommerce.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Kullanıcının sepetine ürün ekle
    public void addToCart(Long productId, int quantity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Kullanıcının cart'ı var mı kontrol et
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .createdAt(java.time.LocalDateTime.now())
                            .build();
                    return cartRepository.save(newCart);
                });

        // Ürünü bul
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // CartItem oluştur ve kaydet
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();

        cartItemRepository.save(cartItem);
    }

    // Kullanıcının sepetini getir
   public List<CartItemDTO> getMyCart() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

    return cart.getItems().stream().map(item -> {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        return dto;
    }).toList();
}


    // Sepetten ürün çıkar
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cartItemRepository.delete(cartItem);
    }
}
