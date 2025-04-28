package ramazan.sahin.ecommerce.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ramazan.sahin.ecommerce.dto.CartItemDTO;

import ramazan.sahin.ecommerce.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1️⃣ Sepete ürün ekle
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        cartService.addToCart(productId, quantity);
        return ResponseEntity.ok("Product added to cart successfully.");
    }

    // 2️⃣ Sepeti görüntüle
   @GetMapping("/my-cart")
public ResponseEntity<List<CartItemDTO>> getMyCart() {
    List<CartItemDTO> cartItems = cartService.getMyCart();
    return ResponseEntity.ok(cartItems);
}

    

    // 3️⃣ Sepetten ürün sil
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok("Product removed from cart successfully.");
    }
}
