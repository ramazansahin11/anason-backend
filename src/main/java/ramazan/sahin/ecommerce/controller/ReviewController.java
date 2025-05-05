package ramazan.sahin.ecommerce.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus; // Eklendi
import org.springframework.http.ResponseEntity; // Eklendi
import org.springframework.web.bind.annotation.*; // *, PathVariable, PutMapping, DeleteMapping için

// Doğru RequestBody importu
import org.springframework.web.bind.annotation.RequestBody;

import ramazan.sahin.ecommerce.dto.ReviewDTO;
import ramazan.sahin.ecommerce.service.ReviewService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.GetMapping; // @RestController içinde zaten var
// import org.springframework.web.bind.annotation.PostMapping; // @RestController içinde zaten var
// import org.springframework.web.bind.annotation.RequestParam; // @PathVariable kullanılacak

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ✅ 1. Kullanıcı - Ürün Yorumları Görüntüleme (PathVariable ile)
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews); // 200 OK
    }

    // ✅ 2. Kullanıcı - Ürün Yorumları Oluşturma (Doğru RequestBody kullanımı, ResponseEntity dönüşü)
    // productId'yi DTO'ya eklemek veya path variable yapmak daha iyi olabilir, şimdilik böyle bırakıldı.
    @PostMapping("/create")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDto, @RequestParam Long productId) {
        ReviewDTO createdReview = reviewService.createReview(reviewDto, productId);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED); // 201 Created
    }

    // ✅ 3. Kullanıcı - Ürün Yorumları Güncelleme (PutMapping, PathVariable, Doğru RequestBody)
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Yetkilendirme service katmanında da kontrol ediliyor
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDto) {
        ReviewDTO updatedReview = reviewService.updateReview(id, reviewDto);
        return ResponseEntity.ok(updatedReview); // 200 OK
    }

    // ✅ 4. Kullanıcı - Ürün Yorumları Silme (DeleteMapping, PathVariable)
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Yetkilendirme service katmanında da kontrol ediliyor
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // ✅ 5. Admin - Tüm Ürün Yorumları Görüntüleme
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews); // 200 OK
    }

    // ✅ 6. Seller - Kendi Ürünlerinin Yorumlarını Görüntüleme (Parametresiz, kimliği doğrulanmış satıcı için)
    @GetMapping("/seller/my-products") // Daha açıklayıcı bir path
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ReviewDTO>> getReviewsForMySellerProducts() {
        // Servis metodu kimliği doğrulanmış kullanıcıyı içeriden alacak
        List<ReviewDTO> reviews = reviewService.getReviewsForSellerProducts();
        return ResponseEntity.ok(reviews);
    }

    // İsteğe bağlı: Belirli bir satıcının ID'si ile yorumları getirme (Admin için?)
    /*
    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')") // Belki sadece adminler başka satıcıların yorumlarını görmeli
    public ResponseEntity<List<ReviewDTO>> getReviewsBySellerId(@PathVariable Long sellerId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForSellerProductsById(sellerId); // Yeni servis metodu
        return ResponseEntity.ok(reviews);
    }
    */
}