package ramazan.sahin.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ramazan.sahin.ecommerce.entity.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Kullanıcının tüm adreslerini bulmak için
    List<Address> findByUserId(Long userId);

    // Adres id ile tek bir adres bulmak için (isteğe bağlı, ama JpaRepository'de zaten var)
    Optional<Address> findById(Long id);
}
