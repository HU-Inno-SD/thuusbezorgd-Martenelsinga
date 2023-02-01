package delivery.data;


import delivery.domain.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    Optional<Rider> findById(Long RiderId);
//    Optional<Rider> findFirstByNrOfDeliveries();
}
