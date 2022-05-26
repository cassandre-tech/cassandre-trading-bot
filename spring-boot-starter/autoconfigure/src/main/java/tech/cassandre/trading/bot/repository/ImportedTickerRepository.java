package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.ImportedTicker;

import java.util.List;

/**
 * {@link ImportedTicker} repository.
 */
@Repository
public interface ImportedTickerRepository extends JpaRepository<ImportedTicker, Long>, JpaSpecificationExecutor<ImportedTicker> {

    /**
     * Returns imported tickers (ordered by timestamp).
     *
     * @return imported tickers
     */
    List<ImportedTicker> findByOrderByTimestampAsc();

    /**
     * Returns imported tickers of a specific currency pair (ordered by timestamp).
     *
     * @param currencyPair currency pair
     * @return imported tickers
     */
    List<ImportedTicker> findByCurrencyPairOrderByTimestampAsc(String currencyPair);

}
