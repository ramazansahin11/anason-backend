package ramazan.sahin.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ramazan.sahin.ecommerce.entity.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Kullanıcının tüm adreslerini bulmak için
    List<Address> findByUserId(Long userId);

}
