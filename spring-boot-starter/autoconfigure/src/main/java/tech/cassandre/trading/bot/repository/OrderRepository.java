package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;

import java.util.List;
import java.util.Optional;

/**
 * {@link Order} repository.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find an order by its order id.
     *
     * @param orderId order id
     * @return order
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Find orders by its status.
     *
     * @param orderStatusDTO order status
     * @return orders
     */
    List<Order> findByStatus(OrderStatusDTO orderStatusDTO);

    /**
     * Retrieve all orders by its timestamp.
     *
     * @return orders
     */
    List<Order> findByOrderByTimestampAsc();

}
