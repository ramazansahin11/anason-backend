package ramazan.sahin.ecommerce.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ramazan.sahin.ecommerce.entity.Product; 

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ramazan.sahin.ecommerce.dto.ReviewDTO;
import ramazan.sahin.ecommerce.entity.Review;
import ramazan.sahin.ecommerce.entity.User;
import ramazan.sahin.ecommerce.repository.ProductRepository;
import ramazan.sahin.ecommerce.repository.ReviewRepository;
import ramazan.sahin.ecommerce.repository.UserRepository;
import ramazan.sahin.ecommerce.service.ReviewService;


@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository; 

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDto, Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); 

        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

    
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

       
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        Review savedReview = reviewRepository.save(review);

        return mapToDTO(savedReview);
    }

    @Override
    @Transactional(readOnly = true) // Sadece okuma işlemi yapıldığı için performansı artırabilir
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));
        return mapToDTO(review);
    }

    @Override
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(this::mapToDTO) // Her bir Review nesnesini DTO'ya çevir
                .collect(Collectors.toList()); // Sonuçları liste olarak topla
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDto) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                  .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + currentUsername)); // orElseThrow kullanımı iyileştirildi

        // Yorumu yapan kullanıcı veya admin değilse hata fırlat
        // TODO: currentUser.isAdmin() gibi bir metot veya rol kontrolü ekleyin
        boolean isAdmin = auth.getAuthorities().stream()
                              .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        if (!existingReview.getUser().equals(currentUser) && !isAdmin ) {
           throw new SecurityException("You are not authorized to update this review.");
        }

        existingReview.setRating(reviewDto.getRating());
        existingReview.setComment(reviewDto.getComment());
        Review updatedReview = reviewRepository.save(existingReview);
        return mapToDTO(updatedReview);
    }


    @Override
     public void deleteReview(Long id) {
         Review reviewToDelete = reviewRepository.findById(id)
                 .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));

         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         String currentUsername = auth.getName();
         User currentUser = userRepository.findByEmail(currentUsername)
                  .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + currentUsername));

         // TODO: currentUser.isAdmin() gibi bir metot veya rol kontrolü ekleyin
          boolean isAdmin = auth.getAuthorities().stream()
                              .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
         if (!reviewToDelete.getUser().equals(currentUser) && !isAdmin) {
            throw new SecurityException("You are not authorized to delete this review.");
         }

         reviewRepository.delete(reviewToDelete);
     }

    // --- Yardımcı Metotlar ---

    /**
     * Review entity'sini ReviewDTO'ya dönüştürür.
     * @param review Dönüştürülecek Review nesnesi.
     * @return ReviewDTO nesnesi.
     */
    private ReviewDTO mapToDTO(Review review) {
        if (review == null) {
            return null;
        }
        return ReviewDTO.builder()
                .id(review.getId())
                // User ve Product nesneleri null değilse ID'lerini al, null ise null ata
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

     @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        // Ürünün var olup olmadığını kontrol etmek isteyebilirsiniz
         if (!productRepository.existsById(productId)) {
             throw new EntityNotFoundException("Product not found with ID: " + productId);
         }
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsForSellerProducts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User seller = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with email: " + currentUsername));

        // Satıcının ürünlerini bul (ProductRepository'de findBySellerId veya findBySeller metodu olmalı)
        List<Product> sellerProducts = productRepository.findBySellerId(seller.getId()); // Veya findBySeller(seller)

        if (sellerProducts.isEmpty()) {
            return Collections.emptyList(); // Satıcının ürünü yoksa boş liste dön
        }

        // Ürün ID'lerini al
        List<Long> productIds = sellerProducts.stream()
                                            .map(Product::getId)
                                            .collect(Collectors.toList());

        // Bu ürünlere ait yorumları bul (ReviewRepository'de findByProductIdIn metodu olmalı)
        List<Review> reviews = reviewRepository.findByProductIdIn(productIds);

        // DTO'ya çevir ve döndür
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

}