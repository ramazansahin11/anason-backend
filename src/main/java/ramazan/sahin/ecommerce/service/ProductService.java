package ramazan.sahin.ecommerce.service;



import ramazan.sahin.ecommerce.dto.ProductAttributeValueDTO;
import ramazan.sahin.ecommerce.dto.ProductDTO;
import java.util.List;
import java.util.Set;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDto);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getAllProducts();

    ProductDTO updateProduct(Long id, ProductDTO productDto);

    void deleteProduct(Long id);

    List<ProductDTO> productsFilter(String category);

    List<ProductDTO> getAllProductsForUser(); // Get all products for the logged-in user
    Set<ProductAttributeValueDTO> getProductAttributes(Long productId);
    ProductDTO addAttributeToProduct(Long productId, Long attributeValueId);
    ProductDTO addAttributesToProduct(Long productId, List<Long> attributeValueIds);
    void removeAttributeFromProduct(Long productId, Long attributeValueId);
    ProductDTO updateProductAttributes(Long productId, List<Long> attributeValueIds);
}
