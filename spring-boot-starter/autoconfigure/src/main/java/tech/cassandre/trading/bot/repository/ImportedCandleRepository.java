package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.cassandre.trading.bot.domain.ImportedCandle;

import java.util.List;

public interface ImportedCandleRepository extends JpaRepository<ImportedCandle, Long>, JpaSpecificationExecutor<ImportedCandle> {

    /**
     * Returns imported tickers (ordered by timestamp).
     *
     * @return imported tickers
     */
    List<ImportedCandle> findByOrderByTimestampAsc();

    /**
     * Returns imported tickers of a specific currency pair (ordered by timestamp).
     *
     * @param currencyPair currency pair
     * @return imported tickers
     */
    List<ImportedCandle> findByCurrencyPairOrderByTimestampAsc(String currencyPair);

}
