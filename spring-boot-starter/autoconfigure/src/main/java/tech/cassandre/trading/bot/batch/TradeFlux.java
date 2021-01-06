package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseExternalFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Trade flux - push {@link TradeDTO}.
 */
public class TradeFlux extends BaseExternalFlux<TradeDTO> {

    /** Trade service. */
    private final TradeService tradeService;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /**
     * Constructor.
     *
     * @param newTradeService    trade service
     * @param newOrderRepository order repository
     * @param newTradeRepository trade repository
     */
    public TradeFlux(final TradeService newTradeService,
                     final OrderRepository newOrderRepository,
                     final TradeRepository newTradeRepository) {
        this.tradeRepository = newTradeRepository;
        this.orderRepository = newOrderRepository;
        this.tradeService = newTradeService;
    }

    @Override
    protected final Set<TradeDTO> getNewValues() {
        logger.debug("TradeFlux - Retrieving new values");
        Set<TradeDTO> newValues = new LinkedHashSet<>();

        // Finding which trades has been updated.
        tradeService.getTrades().forEach(trade -> {
            logger.debug("TradeFlux - Treating trade : {}", trade.getTradeId());
            final Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId(trade.getTradeId());
            if (tradeInDatabase.isEmpty() || !tradeMapper.mapToTradeDTO(tradeInDatabase.get()).equals(trade)) {
                logger.info("TradeFlux - Trade {} has changed : {}", trade.getTradeId(), trade);
                newValues.add(trade);
            }
        });
        logger.debug("TradeFlux - {} trade(s) updated", newValues.size());
        return newValues;
    }

    @Override
    public final void saveValue(final TradeDTO newValue) {
        Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId(newValue.getTradeId());
        tradeInDatabase.ifPresentOrElse(trade -> {
            // Update trade.
            tradeMapper.updateOrder(newValue, trade);
            tradeRepository.save(trade);
            logger.debug("TradeFlux - trade updated in database {}", trade);
        }, () -> {
            // Create trade.
            final Trade newTrade = tradeMapper.mapToTrade(newValue);
            // Retrieve the existing order of the trade.
            final Optional<Order> order = orderRepository.findByOrderId(newValue.getOrderId());
            order.ifPresent(value -> newTrade.setOrder(value.getId()));
            tradeRepository.save(newTrade);
            logger.debug("TradeFlux - trade created in database {}", newTrade);
        });
    }

}
