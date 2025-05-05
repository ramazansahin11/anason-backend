package ramazan.sahin.ecommerce.service.impl;

import ramazan.sahin.ecommerce.dto.CartItemDTO;
import ramazan.sahin.ecommerce.entity.Cart;
import ramazan.sahin.ecommerce.entity.CartItem;
import ramazan.sahin.ecommerce.entity.Order;
import ramazan.sahin.ecommerce.entity.OrderItem;
import ramazan.sahin.ecommerce.entity.Product;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.exception.BadRequestException;
import ramazan.sahin.ecommerce.repository.CartItemRepository;
import ramazan.sahin.ecommerce.repository.CartRepository;
import ramazan.sahin.ecommerce.repository.OrderItemRepository;
import ramazan.sahin.ecommerce.repository.OrderRepository;
import ramazan.sahin.ecommerce.repository.ProductRepository;
import ramazan.sahin.ecommerce.repository.UserRepository;
import ramazan.sahin.ecommerce.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        

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
  @Override // getMyCart metodunu @Override ile işaretlemek iyi bir pratiktir.
public List<CartItemDTO> getMyCart() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email)); // Daha açıklayıcı mesaj

    // Cart'ı user ID ile bulmak daha verimli olabilir
    Cart cart = cartRepository.findByUserId(user.getId())
            .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID: " + user.getId()));

    // CartItem'ları direkt Cart üzerinden alalım (ilişki zaten var)
    // List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId()); // Bu sorguya gerek kalmayabilir

    if (cart.getItems() == null) { // Null kontrolü
        return Collections.emptyList(); // Boş liste döndür
    }

    // Stream API ile DTO'ya map'leme
    return cart.getItems().stream().map(item -> {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId()); // CartItem ID'sini set et
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());

        // Product bilgilerini ekle
        Product product = item.getProduct(); // İlişkili ürünü al
        if (product != null) { // Null kontrolü
             dto.setProductName(product.getName());
             dto.setPrice(product.getPrice());       // Fiyatı ekle
             dto.setImageUrl(product.getImageUrl()); // Resmi ekle
        } else {
            // Ürün bulunamazsa varsayılan değerler atanabilir
            dto.setProductName("Product not found");
            dto.setPrice(BigDecimal.ZERO);
            dto.setImageUrl(null);
        }
        return dto;
    }).collect(Collectors.toList()); // .toList() daha modern
}



    // Sepetten ürün çıkar
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cartItemRepository.delete(cartItem);
    }

    //sepette ki ürünleri güncelle
    public void updateCartItem(Long cartItemId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
    
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
    }

    // Sepeti temizle (tüm ürünleri kaldır)
    public void clearCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
    
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    
        cart.getItems().clear(); // Sepetteki tüm ürünleri temizle
        cartRepository.save(cart); // Değişikliği kaydet
    }
    @Override
public void checkout() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Cart cart = cartRepository.findByUserId(user.getId())
            .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

    List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

    if (cartItems.isEmpty()) {
        throw new BadRequestException("Cart is empty, cannot checkout.");
    }

    Order order = new Order();
    order.setUser(user);
    order.setStatus(Order.Status.PENDING);
    order.setCreatedAt(LocalDateTime.now());
    order.setTotalPrice(BigDecimal.ZERO);

    Order savedOrder = orderRepository.save(order);

    BigDecimal totalPrice = BigDecimal.ZERO;

    for (CartItem cartItem : cartItems) {
        Product product = cartItem.getProduct();

        OrderItem orderItem = OrderItem.builder()
                .order(savedOrder)
                .product(product)
                .quantity(cartItem.getQuantity())
                .unitPrice(product.getPrice())
                .build();

        orderItemRepository.save(orderItem);

        totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
    }

    savedOrder.setTotalPrice(totalPrice);
    orderRepository.save(savedOrder);

    cartItemRepository.deleteAll(cartItems);
}


}