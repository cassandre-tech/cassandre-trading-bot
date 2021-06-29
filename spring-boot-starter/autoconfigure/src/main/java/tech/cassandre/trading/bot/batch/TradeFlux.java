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

    /** Trade service. */
    private final TradeService tradeService;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    @Override
    protected final Set<TradeDTO> getNewValues() {
        logger.debug("TradeFlux - Retrieving new trades from exchange");
        Set<TradeDTO> newValues = new LinkedHashSet<>();

        // Finding which trades has been updated.
        tradeService.getTrades()
                .stream()
                .filter(t -> orderRepository.findByOrderId(t.getOrderId()).isPresent())    // We only accept trades with order present in database
                .forEach(trade -> {
                    logger.debug("TradeFlux - Treating trade: {}", trade.getTradeId());
                    final Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId(trade.getTradeId());
                    if (tradeInDatabase.isEmpty() || !tradeMapper.mapToTradeDTO(tradeInDatabase.get()).equals(trade)) {
                        logger.debug("TradeFlux - Updated trade from exchange: {}", trade);
                        newValues.add(trade);
                    }
                });

        return newValues;
    }

    @Override
    public final Set<TradeDTO> saveValues(final Set<TradeDTO> newValues) {
        Set<Trade> trades = new LinkedHashSet<>();

        // We create or update every trades retrieved by the exchange.
        newValues.forEach(newValue -> tradeRepository.findByTradeId(newValue.getTradeId())
                .ifPresentOrElse(trade -> {
                    // Update trade.
                    tradeMapper.updateTrade(newValue, trade);
                    trades.add(tradeRepository.save(trade));
                    logger.debug("TradeFlux - Updating trade in database: {}", trade);
                }, () -> {
                    // Create trade.
                    final Trade newTrade = tradeMapper.mapToTrade(newValue);
                    orderRepository.findByOrderId(newValue.getOrderId()).ifPresent(newTrade::setOrder);
                    trades.add(tradeRepository.save(newTrade));
                    logger.debug("TradeFlux - Creating trade in database: {}", newTrade);
                }));

        return trades.stream()
                .map(tradeMapper::mapToTradeDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
