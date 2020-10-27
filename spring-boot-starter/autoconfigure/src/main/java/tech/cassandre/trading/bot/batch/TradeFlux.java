package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
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
        Trade t = new Trade();
        t.setId(newValue.getId());
        t.setOrderId(newValue.getOrderId());
        t.setType(newValue.getType().toString());
        t.setOriginalAmount(newValue.getOriginalAmount());
        t.setCurrencyPair(newValue.getCurrencyPair().toString());
        t.setPrice(newValue.getPrice());
        t.setTimestamp(newValue.getTimestamp());
        t.setFeeAmount(newValue.getFee().getValue());
        t.setFeeCurrency(newValue.getFee().getCurrency().toString());
        tradeRepository.save(t);
    }

    @Override
    public final void restoreValues() {
        getLogger().info("Restoring trades from database");
        tradeRepository.findByOrderByTimestampAsc()
                .forEach(trade -> {
                    TradeDTO t = TradeDTO.builder()
                            .id(trade.getId())
                            .orderId(trade.getOrderId())
                            .type(OrderTypeDTO.valueOf(trade.getType()))
                            .originalAmount(trade.getOriginalAmount())
                            .currencyPair(new CurrencyPairDTO(trade.getCurrencyPair()))
                            .price(trade.getPrice())
                            .timestamp(trade.getTimestamp())
                            .feeAmount(trade.getFeeAmount())
                            .feeCurrency(new CurrencyDTO(trade.getFeeCurrency()))
                            .create();
                    previousValues.put(t.getId(), t);
                    tradeService.restoreTrade(t);
                    getLogger().info("Trade " + trade.getOrderId() + " restored : " + t);
                });
    }

}
