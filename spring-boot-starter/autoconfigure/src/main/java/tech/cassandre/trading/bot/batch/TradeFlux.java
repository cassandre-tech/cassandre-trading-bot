package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Trade flux - push {@link TradeDTO}.
 */
@RequiredArgsConstructor
public class TradeFlux extends BaseFlux<TradeDTO> {

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Trade service. */
    private final TradeService tradeService;

    @Override
    protected final Set<TradeDTO> getNewValues() {
        logger.debug("Retrieving trades from exchange");
        Set<TradeDTO> newValues = new LinkedHashSet<>();

        // Finding which trades have been updated.
        tradeService.getTrades()
                .stream()
                // Note: we only save trades when the order present in database.
                .filter(t -> orderRepository.findByOrderId(t.getOrderId()).isPresent())
                .forEach(trade -> {
                    logger.debug("Checking trade: {}", trade.getTradeId());
                    final Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId(trade.getTradeId());

                    // The trade is not in database.
                    if (tradeInDatabase.isEmpty()) {
                        logger.debug("New trade from exchange: {}", trade);
                        newValues.add(trade);
                    }

                    // The trade is in database but the trade values from the server changed.
                    if (tradeInDatabase.isPresent() && !TRADE_MAPPER.mapToTradeDTO(tradeInDatabase.get()).equals(trade)) {
                        logger.debug("Updated trade from exchange: {}", trade);
                        newValues.add(trade);
                    }
                });

        return newValues;
    }

    @Override
    public final Set<TradeDTO> saveValues(final Set<TradeDTO> newValues) {
        Set<Trade> trades = new LinkedHashSet<>();

        // We create or update every trade retrieved by the exchange.
        newValues.forEach(newValue -> tradeRepository.findByTradeId(newValue.getTradeId())
                .ifPresentOrElse(trade -> {
                    // Update trade.
                    TRADE_MAPPER.updateTrade(newValue, trade);
                    trades.add(tradeRepository.save(trade));
                    logger.debug("Updating trade in database: {}", trade);
                }, () -> {
                    // Create trade.
                    final Trade newTrade = TRADE_MAPPER.mapToTrade(newValue);
                    // Order is always present as we check it in getNewValues().
                    orderRepository.findByOrderId(newValue.getOrderId()).ifPresent(newTrade::setOrder);
                    trades.add(tradeRepository.save(newTrade));
                    logger.debug("Creating trade in database: {}", newTrade);
                }));

        return trades.stream()
                .map(TRADE_MAPPER::mapToTradeDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
