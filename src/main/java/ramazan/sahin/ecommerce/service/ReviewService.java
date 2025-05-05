package ramazan.sahin.ecommerce.service;

import ramazan.sahin.ecommerce.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDto, Long productId);
    ReviewDTO getReviewById(Long id);
    List<ReviewDTO> getAllReviews();
    ReviewDTO updateReview(Long id, ReviewDTO reviewDto);
    void deleteReview(Long id);
    List<ReviewDTO> getReviewsByProductId(Long productId);

    // Yeni eklenen metotlar
    List<ReviewDTO> getReviewsForSellerProducts(); // Kimliği doğrulanmış satıcı için
    // List<ReviewDTO> getReviewsForSellerProductsById(Long sellerId); // Belirli ID'li satıcı için (isteğe bağlı)
}