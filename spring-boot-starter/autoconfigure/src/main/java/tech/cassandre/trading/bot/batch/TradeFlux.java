package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.TradeRepository;
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

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /**
     * Constructor.
     *
     * @param newTradeService    trade service
     * @param newTradeRepository trade repository
     */
    public TradeFlux(final TradeService newTradeService, final TradeRepository newTradeRepository) {
        this.tradeRepository = newTradeRepository;
        this.tradeService = newTradeService;
    }

    @Override
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

    @Override
    public final void backupValue(final TradeDTO newValue) {
        tradeRepository.save(getMapper().mapToTrade(newValue));
    }

    @Override
    public final void restoreValues() {
        getLogger().info("TradeFlux - Restoring trades from database");
        tradeRepository.findByOrderByTimestampAsc()
                .forEach(trade -> {
                    TradeDTO t = getMapper().mapToTradeDTO(trade);
                    previousValues.put(t.getId(), t);
                    tradeService.restoreTrade(t);
                    getLogger().info("TradeFlux - Trade " + trade.getOrderId() + " restored : " + t);
                });
    }

}
