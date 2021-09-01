package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.cassandre.trading.bot.domain.BacktestingTicker;
import tech.cassandre.trading.bot.domain.BacktestingTickerId;

import java.util.List;

/**
 * {@link BacktestingTicker} repository.
 */
public interface BacktestingTickerRepository extends JpaRepository<BacktestingTicker, BacktestingTickerId> {

    /**
     * Find backtesting tickers for a session and a sequence.
     *
     * @param testSessionId test session id
     * @param responseSequenceId response sequence id
     * @return list of tickers for that sequence in that test.
     */
    List<BacktestingTicker> findByIdTestSessionIdAndIdResponseSequenceId(String testSessionId, long responseSequenceId);

}
