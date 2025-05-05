package ramazan.sahin.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ramazan.sahin.ecommerce.dto.OrderDTO;
import ramazan.sahin.ecommerce.dto.PaymentDTO;
import ramazan.sahin.ecommerce.dto.ProductDTO;
import ramazan.sahin.ecommerce.service.OrderService;
import ramazan.sahin.ecommerce.service.PaymentService;
import ramazan.sahin.ecommerce.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductService productService;

    // Endpoint to get orders for the seller's products
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersForSeller() {
        List<OrderDTO> orders = orderService.getAllOrdersForUser(null); // Implement filtering by seller in the service
                                                                        // layer
        return ResponseEntity.ok(orders);
    }

    // Endpoint to get products for the seller
    @GetMapping("/products")   
    public ResponseEntity<List<ProductDTO>> getProductsForSeller() {
        List<ProductDTO> products = productService.getAllProductsForUser(); // Implement filtering by seller in the
                                                                           // service layer
        return ResponseEntity.ok(products);
    }
    


    // Endpoint to get payments for the seller's products
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForSeller() {
        List<PaymentDTO> payments = paymentService.getAllPaymentByUser(); // Implement filtering by seller in the
                                                                          // service layer
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/product-create")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDto) {
        ProductDTO savedProduct = productService.createProduct(productDto);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/product-update/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductDTO productDto) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("product-delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}