package tacos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByDeliveryZip(String deliveryZip);

    List<Order> readOrdersByDeliveryZipAndPlacedAtBetween(String deliveryZip, LocalDateTime start,
        LocalDateTime end);

    List<Order> findByDeliveryStreetAndDeliveryCityAllIgnoresCase(String DeliveryTo,
        String deliveryCity);
}
