package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;

import java.util.List;
import java.util.Optional;

/**
 * Order repository.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // TODO Check if JPA repository is a good idea ?

    /**
     * Find by order id.
     *
     * @param orderId order id
     * @return order
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Find order by status.
     *
     * @param orderStatusDTO order status
     * @return orders
     */
    List<Order> findByStatus(OrderStatusDTO orderStatusDTO);

    /**
     * Find all orders by timestamp.
     *
     * @return positions
     */
    List<Order> findByOrderByTimestampAsc();

}
