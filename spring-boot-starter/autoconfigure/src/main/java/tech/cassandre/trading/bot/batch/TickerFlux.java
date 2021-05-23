package tech.cassandre.trading.bot.batch;

import com.google.common.collect.Iterators;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.batch.BaseExternalFlux;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ticker flux - push {@link TickerDTO}.
 */
public class TickerFlux extends BaseExternalFlux<TickerDTO> {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Market service. */
    private final MarketService marketService;

    /** Cycle iterator over requested currency pairs. */
    private Iterator<CurrencyPairDTO> currencyPairsIterator;

    /** Previous values. */
    private final Map<CurrencyPairDTO, TickerDTO> previousValues = new LinkedHashMap<>();

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     * @param newMarketService      market service.
     */
    public TickerFlux(final ApplicationContext newApplicationContext,
                      final MarketService newMarketService) {
        this.applicationContext = newApplicationContext;
        this.marketService = newMarketService;
    }

    /**
     * Update the list of requested currency pairs.
     *
     * @param requestedCurrencyPairs list of requested currency pairs.
     */
    public void updateRequestedCurrencyPairs(final Set<CurrencyPairDTO> requestedCurrencyPairs) {
        currencyPairsIterator = Iterators.cycle(requestedCurrencyPairs);
    }

    @Override
    protected final Set<TickerDTO> getNewValues() {
        logger.debug("TickerFlux - Retrieving new values");
        Set<TickerDTO> newValues = new LinkedHashSet<>();

        try {
            // We retrieve the list of currency pairs asked by every strategy.
            final LinkedHashSet<CurrencyPairDTO> currencyPairs = applicationContext
                    .getBeansWithAnnotation(CassandreStrategy.class)
                    .values()
                    .stream()
                    .map(o -> ((CassandreStrategyInterface) o))
                    .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                    .flatMap(Set::stream)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // GetTickers from market service is available so we retrieve all tickers at once.
            marketService.getTickers(currencyPairs).forEach(ticker -> {
                if (!ticker.equals(previousValues.get(ticker.getCurrencyPair()))) {
                    logger.debug("TickerFlux - New ticker received : {}", ticker);
                    previousValues.put(ticker.getCurrencyPair(), ticker);
                    newValues.add(ticker);
                }
            });
        } catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
            logger.debug("MarketService - getTickers not available {}", e.getMessage());
            // GetTickers from market service is unavailable so we do ticker by ticker.
            marketService.getTicker(currencyPairsIterator.next()).ifPresent(t -> {
                if (!t.equals(previousValues.get(t.getCurrencyPair()))) {
                    logger.debug("TickerFlux - New ticker received : {}", t);
                    previousValues.put(t.getCurrencyPair(), t);
                    newValues.add(t);
                }
            });
        }
        return newValues;
    }

    @Override
    protected final Optional<TickerDTO> saveValue(final TickerDTO newValue) {
        return Optional.ofNullable(newValue);
    }

}
