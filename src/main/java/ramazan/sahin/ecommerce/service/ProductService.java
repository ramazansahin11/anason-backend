package ramazan.sahin.ecommerce.service;



import ramazan.sahin.ecommerce.dto.ProductDTO;
import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDto);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getAllProducts();

    ProductDTO updateProduct(Long id, ProductDTO productDto);

    void deleteProduct(Long id);
}
