package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * Find an order by its order id.
     *
     * @param orderId order id
     * @return order
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Find orders with a specific status.
     *
     * @param orderStatusDTO order status
     * @return orders
     */
    List<Order> findByStatus(OrderStatusDTO orderStatusDTO);

    /**
     * Find orders with a status different from the one passed as a parameter.
     *
     * @param orderStatusDTO order status
     * @return orders
     */
    List<Order> findByStatusNot(OrderStatusDTO orderStatusDTO);

    /**
     * Retrieve all orders (sorted by timestamp).
     *
     * @return orders
     */
    List<Order> findByOrderByTimestampAsc();

    /**
     * Update order amount.
     * WARNING: Only used by the dry mode, please do not use it.
     *
     * @param uid   order uid
     * @param value new amount
     */
    @Transactional
    @Modifying
    @Query("update Order o set o.amount.value = :value where o.uid = :uid")
    void updateAmount(@Param("uid") Long uid, @Param("value") BigDecimal value);

}
