package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Trade;

import java.util.List;
import java.util.Optional;

/**
 * {@link Trade} repository.
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * Find a trade by its trade id.
     *
     * @param tradeId trade id
     * @return trade
     */
    Optional<Trade> findByTradeId(String tradeId);

    /**
     * Retrieve all trades order by its timestamp.
     *
     * @return trades
     */
    List<Trade> findByOrderByTimestampAsc();

}
