package ramazan.sahin.ecommerce.service.impl;

import ramazan.sahin.ecommerce.dto.ProductDTO;
import ramazan.sahin.ecommerce.entity.Product;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.exception.BadRequestException;
import ramazan.sahin.ecommerce.repository.ProductRepository;
import ramazan.sahin.ecommerce.repository.UserRepository;
import ramazan.sahin.ecommerce.service.ProductService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getStatus() == User.Status.BANNED) {
            throw new AccessDeniedException("Banned users cannot create products.");
        }

        if (productDto.getPrice() == null || productDto.getPrice().doubleValue() <= 0) {
            throw new BadRequestException("Product price must be greater than zero.");
        }

        if (productDto.getStockQuantity() == null || productDto.getStockQuantity() < 0) {
            throw new BadRequestException("Stock quantity cannot be negative.");
        }

        Product product = mapToEntity(productDto);
        product.setSeller(user); // Ürün kim tarafından eklendiğini bağladık!

        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapToDto(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!product.getSeller().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to update this product.");
        }

        if (productDto.getPrice() == null || productDto.getPrice().doubleValue() <= 0) {
            throw new BadRequestException("Product price must be greater than zero.");
        }

        if (productDto.getStockQuantity() == null || productDto.getStockQuantity() < 0) {
            throw new BadRequestException("Stock quantity cannot be negative.");
        }

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(productDto.getCategory());
        product.setImageUrl(productDto.getImageUrl());

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin && !product.getSeller().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this product.");
        }

        productRepository.delete(product);
    }

    // ====== Mappers ======

    private ProductDTO mapToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }

    private Product mapToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());
        return product;
    }
}
