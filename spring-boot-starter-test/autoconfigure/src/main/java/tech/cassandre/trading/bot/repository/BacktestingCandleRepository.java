package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.cassandre.trading.bot.domain.BacktestingCandle;
import tech.cassandre.trading.bot.domain.BacktestingCandleId;

import java.util.List;

/**
 * {@link BacktestingCandle} repository.
 */
public interface BacktestingCandleRepository extends JpaRepository<BacktestingCandle, BacktestingCandleId> {

    /**
     * Find backtesting candles for a session and a sequence.
     *
     * @param testSessionId test session id
     * @param responseSequenceId response sequence id
     * @return list of candles for that sequence in that test.
     */
    List<BacktestingCandle> findByIdTestSessionIdAndIdResponseSequenceId(String testSessionId, long responseSequenceId);

    /**
     * Returns imported candles of a specific currency pair.
     *
     * @param currencyPair currency pair
     * @return imported candles
     */
    List<BacktestingCandle> findByIdCurrencyPair(String currencyPair);

}
