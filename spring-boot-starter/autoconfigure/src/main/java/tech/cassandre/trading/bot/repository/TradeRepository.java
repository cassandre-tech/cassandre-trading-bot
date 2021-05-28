package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Trade;

import java.util.List;
import java.util.Optional;

/**
 * Trade repository.
 */
@Repository
public interface TradeRepository extends CrudRepository<Trade, Long> {

    /**
     * Find by trade id.
     *
     * @param tradeId trade id
     * @return trade
     */
    Optional<Trade> findByTradeId(String tradeId);

    // TODO Add a method to retrieve all the trades of an order.

    /**
     * Find all trades by timestamp.
     *
     * @return positions
     */
    List<Trade> findByOrderByTimestampAsc();

}
