package tech.cassandre.trading.bot.batch;

import com.google.common.collect.Iterators;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.util.base.batch.BaseExternalFlux;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Ticker flux - push {@link TickerDTO}.
 */
public class TickerFlux extends BaseExternalFlux<TickerDTO> {

    /** Market service. */
    private final MarketService marketService;

    /** Cycle iterator over requested currency pairs. */
    private final Set<CurrencyPairDTO> currencyPairs = new LinkedHashSet<>();

    /** Cycle iterator over requested currency pairs. */
    private Iterator<CurrencyPairDTO> currencyPairsIterator;

    /** Previous values. */
    private final Map<CurrencyPairDTO, TickerDTO> previousValues = new LinkedHashMap<>();

    /**
     * Constructor.
     *
     * @param newMarketService market service.
     */
    public TickerFlux(final MarketService newMarketService) {
        this.marketService = newMarketService;
    }

    /**
     * Update the list of requested currency pairs.
     *
     * @param requestedCurrencyPairs list of requested currency pairs.
     */
    public void updateRequestedCurrencyPairs(final Set<CurrencyPairDTO> requestedCurrencyPairs) {
        currencyPairs.addAll(requestedCurrencyPairs);
        currencyPairsIterator = Iterators.cycle(requestedCurrencyPairs);
    }

    @Override
    protected final Set<TickerDTO> getNewValues() {
        logger.debug("TickerFlux - Retrieving new values");
        Set<TickerDTO> newValues = new LinkedHashSet<>();

        try {
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
