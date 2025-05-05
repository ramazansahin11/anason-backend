package ramazan.sahin.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data; // Or add getters/setters manually if not using Lombok

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet; // Import HashSet
import java.util.Set;     // Import Set

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price; // Using BigDecimal is good for currency

    private Integer stockQuantity = 0;

    private String category;

    private String imageUrl;

    private LocalDateTime createdAt;

    @PrePersist // Automatically set createdAt before saving
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // --- Add this ManyToMany relationship ---
    @ManyToMany(fetch = FetchType.LAZY) // LAZY fetching is generally recommended for performance
    @JoinTable(
        name = "product_product_attribute_values", // Name for the intermediate table
        joinColumns = @JoinColumn(name = "product_id"), // Column in join table linking to Product
        inverseJoinColumns = @JoinColumn(name = "attribute_value_id") // Column linking to ProductAttributeValue
    )
    private Set<ProductAttributeValue> attributes = new HashSet<>(); // Initialize the Set
    // --- End of added relationship ---


    // --- Constructors, other methods, equals/hashCode if needed ---

    // Note: If you are NOT using Lombok's @Data annotation, you MUST manually add:
    // public Set<ProductAttributeValue> getAttributes() {
    //     return attributes;
    // }
    //
    // public void setAttributes(Set<ProductAttributeValue> attributes) {
    //     this.attributes = attributes;
    // }

}
