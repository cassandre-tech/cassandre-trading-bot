package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Order;

import java.util.List;
import java.util.Optional;

/**
 * Order repository.
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    /**
     * Find by order id.
     *
     * @param orderId order id
     * @return order
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Find all orders by timestamp.
     *
     * @return positions
     */
    List<Order> findByOrderByTimestampAsc();

}
