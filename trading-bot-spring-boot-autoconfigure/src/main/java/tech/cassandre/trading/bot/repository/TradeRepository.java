package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Trade;

import java.util.List;

/**
 * Trade repository.
 */
@Repository
public interface TradeRepository extends CrudRepository<Trade, String> {

    /**
     * Find all orders by timestamp.
     * @return positions
     */
    List<Trade> findByOrderByTimestampAsc();

}
