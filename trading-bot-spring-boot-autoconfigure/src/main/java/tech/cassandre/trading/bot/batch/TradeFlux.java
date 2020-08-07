package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseFlux;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Trade flux - push {@link TradeDTO}.
 */
public class TradeFlux extends BaseFlux<TradeDTO> {

    /** Trade service. */
    private final TradeService tradeService;

    /** Previous values. */
    private final Map<String, TradeDTO> previousValues = new LinkedHashMap<>();

    /**
     * Constructor.
     *
     * @param newTradeService trade service
     */
    public TradeFlux(final TradeService newTradeService) {
        this.tradeService = newTradeService;
    }

    @Override
    @SuppressWarnings("unused")
    protected final Set<TradeDTO> getNewValues() {
        getLogger().debug("TradeFlux - Retrieving new values");
        Set<TradeDTO> newValues = new LinkedHashSet<>();

        // Finding which trades has been updated.
        tradeService.getTrades().forEach(trade -> {
            getLogger().debug("TradeFlux - Treating trade : {}", trade.getId());
            TradeDTO existingTrade = previousValues.get(trade.getId());
            if (existingTrade == null || !existingTrade.equals(trade)) {
                getLogger().debug("TradeFlux - Trade {} has changed : {}", trade.getId(), trade);
                previousValues.put(trade.getId(), trade);
                newValues.add(trade);
            }
        });
        getLogger().debug("TradeFlux - {} trade(s) updated", newValues.size());
        return newValues;
    }

}
