package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;

import java.math.BigDecimal;
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
     * Find orders by its status.
     *
     * @param orderStatusDTO order status
     * @return orders
     */
    List<Order> findByStatusNot(OrderStatusDTO orderStatusDTO);

    /**
     * Retrieve all orders by its timestamp.
     *
     * @return orders
     */
    List<Order> findByOrderByTimestampAsc();

    /**
     * Update order amount.
     * (WARNING: Only used by dry mode, please do not use).
     *
     * @param id    order id
     * @param value new amount
     */
    @Transactional
    @Modifying
    @Query("update Order o set o.amount.value = :value where o.id = :id")
    void updateAmount(@Param("id") Long id, @Param("value") BigDecimal value);

}
