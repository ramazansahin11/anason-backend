package ramazan.sahin.ecommerce.service;

import ramazan.sahin.ecommerce.dto.CartItemDTO;

import java.util.List;

public interface CartService {

    // Sepete ürün ekleme
    void addToCart(Long productId, int quantity);

    // Kullanıcının sepetini getirme
    List<CartItemDTO> getMyCart();

    // Sepetten ürün çıkarma
    void removeFromCart(Long cartItemId);

    // Sepetteki ürün adedini güncelleme
    void updateCartItem(Long cartItemId, int newQuantity);

    // Sepeti temizleme
    void clearCart();

    // Sepeti satın alma işlemi (checkout)
    void checkout();
}
