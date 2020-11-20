package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Order;

import java.util.List;

/**
 * Order repository.
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, String> {

    /**
     * Find all orders by timestamp.
     * @return positions
     */
    List<Order> findByOrderByTimestampAsc();

}
