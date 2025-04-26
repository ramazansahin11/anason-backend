package ramazan.sahin.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LogisticsProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contactInfo;
}