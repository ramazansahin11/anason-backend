package ramazan.sahin.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ramazan.sahin.ecommerce.dto.ProductAttributeValueDTO;
import ramazan.sahin.ecommerce.dto.ProductDTO;
import ramazan.sahin.ecommerce.service.ProductService;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/filter/{category}")
    public ResponseEntity<List<ProductDTO>> productsFilter(@PathVariable String category) {
        List<ProductDTO> filteredProducts = productService.productsFilter(category);
        return ResponseEntity.ok(filteredProducts);
    }
      /**
     * Get all attribute values associated with a specific product.
     * Example URL: GET /api/products/123/attributes
     */
    @GetMapping("/{productId}/attributes")
    public ResponseEntity<Set<ProductAttributeValueDTO>> getProductAttributes(@PathVariable Long productId) {
        Set<ProductAttributeValueDTO> attributes = productService.getProductAttributes(productId);
        return ResponseEntity.ok(attributes);
    }

    /**
     * Add a single existing attribute value to a product.
     * Example URL: POST /api/products/123/attributes/{attributeValueId}
     * Note: Using POST here on a sub-resource ID might feel slightly less RESTful than
     * POSTing to /api/products/123/attributes with the ID in the body, but it's common.
     * Alternatively, use PUT if you consider this an update to the product's attribute set.
     */
    @PostMapping("/{productId}/attributes/{attributeValueId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
    public ResponseEntity<ProductDTO> addAttributeToProduct(@PathVariable Long productId, @PathVariable Long attributeValueId) {
        ProductDTO updatedProduct = productService.addAttributeToProduct(productId, attributeValueId);
        return ResponseEntity.ok(updatedProduct);
    }

     /**
     * Add multiple existing attribute values to a product.
     * Example URL: POST /api/products/123/attributes
     * Request Body: [ 5, 8, 12 ]  (List of attributeValueIds)
     */
    @PostMapping("/{productId}/attributes")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
    public ResponseEntity<ProductDTO> addAttributesToProduct(@PathVariable Long productId, @RequestBody List<Long> attributeValueIds) {
        ProductDTO updatedProduct = productService.addAttributesToProduct(productId, attributeValueIds);
        return ResponseEntity.ok(updatedProduct);
    }


    /**
     * Remove an attribute value association from a product.
     * Example URL: DELETE /api/products/123/attributes/{attributeValueId}
     */
    @DeleteMapping("/{productId}/attributes/{attributeValueId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
    public ResponseEntity<Void> removeAttributeFromProduct(@PathVariable Long productId, @PathVariable Long attributeValueId) {
        productService.removeAttributeFromProduct(productId, attributeValueId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Replace ALL attribute values for a product with a new set.
     * Example URL: PUT /api/products/123/attributes
     * Request Body: [ 5, 9 ] (List of attributeValueIds to be associated)
     * Any previously associated attributes NOT in this list will be disassociated.
     */
    @PutMapping("/{productId}/attributes")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SELLER')")
    public ResponseEntity<ProductDTO> updateProductAttributes(@PathVariable Long productId, @RequestBody List<Long> attributeValueIds) {
        ProductDTO updatedProduct = productService.updateProductAttributes(productId, attributeValueIds);
        return ResponseEntity.ok(updatedProduct);
    }


}
