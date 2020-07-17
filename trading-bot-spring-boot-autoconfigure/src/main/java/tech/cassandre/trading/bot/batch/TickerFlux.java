package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.util.base.BaseFlux;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Ticker flux - push {@link TickerDTO}.
 */
public class TickerFlux extends BaseFlux<TickerDTO> {

    /** Market service. */
    private final MarketService marketService;

    /** Requested tickers. */
    private final List<CurrencyPairDTO> requestedCurrencyPairs = new LinkedList<>();

    /** Last requested currency pair. */
    private CurrencyPairDTO lastRequestedCurrencyPairs = null;

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
     * @param newRequestedCurrencyPairs new list of requested currency pairs.
     */
    public void updateRequestedCurrencyPairs(final Set<CurrencyPairDTO> newRequestedCurrencyPairs) {
        requestedCurrencyPairs.addAll(newRequestedCurrencyPairs);
        requestedCurrencyPairs.forEach(cp -> previousValues.put(cp, null));
    }

    @Override
    @SuppressWarnings("unused")
    protected final Set<TickerDTO> getNewValues() {
        getLogger().debug("TickerFlux - Retrieving new values");
        Set<TickerDTO> newValues = new LinkedHashSet<>();
        getCurrencyPairToTreat()
                .flatMap(marketService::getTicker)
                .ifPresent(t -> {
                    if (!t.equals(previousValues.get(t.getCurrencyPair()))) {
                        getLogger().debug("TickerFlux - New ticker received : {}", t);
                        previousValues.replace(t.getCurrencyPair(), t);
                        newValues.add(t);
                    }
                });
        return newValues;
    }

    /**
     * Returns the next currency pair to test.
     *
     * @return currency pair to treat.
     */
    private Optional<CurrencyPairDTO> getCurrencyPairToTreat() {
        final CurrencyPairDTO nextCurrencyPairToTreat;

        // No currency pairs required.
        if (requestedCurrencyPairs.isEmpty()) {
            return Optional.empty();
        }
        if (lastRequestedCurrencyPairs == null) {
            // If none has been retrieved.
            nextCurrencyPairToTreat = requestedCurrencyPairs.get(0);
        } else {
            // We get the position of the last requested currency pair.
            int position = requestedCurrencyPairs.indexOf(lastRequestedCurrencyPairs);
            if (position == requestedCurrencyPairs.size() - 1) {
                // We are at the last of the list, go back to first element.
                nextCurrencyPairToTreat = requestedCurrencyPairs.get(0);
            } else {
                // We take the next one.
                nextCurrencyPairToTreat = requestedCurrencyPairs.get(position + 1);
            }
        }
        lastRequestedCurrencyPairs = nextCurrencyPairToTreat;
        return Optional.of(nextCurrencyPairToTreat);
    }

}
