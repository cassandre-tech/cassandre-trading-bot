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
 * Two methods override from super class:
 * - getNewValues(): calling trade service to retrieve trades from exchange (only if orders exists already in database).
 * - saveValues(): saving/updating trades in database.
 * To get a deep understanding of how it works, read the documentation of {@link BaseFlux}.
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
        return tradeService.getTrades()
                .stream()
                .peek(tradeDTO -> logger.debug("Retrieved trade from exchange: {}", tradeDTO))
                // We only save trades when the order is already present in database.
                .filter(tradeDTO -> orderRepository.findByOrderId(tradeDTO.getOrderId()).isPresent())
                .<TradeDTO>mapMulti((tradeDTO, consumer) -> {
                    final Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId(tradeDTO.getTradeId());

                    // We consider that we have a new value to send to strategies in those cases:
                    if (tradeInDatabase.isEmpty()) {
                        // The trade is not in database.
                        logger.debug("New trade: {}", tradeDTO);
                        consumer.accept(tradeDTO);
                    }
                    if (tradeInDatabase.isPresent() && !TRADE_MAPPER.mapToTradeDTO(tradeInDatabase.get()).equals(tradeDTO)) {
                        // The trade is already in database but the trade values from the server changed.
                        logger.debug("Updated trade: {}", tradeDTO);
                        consumer.accept(tradeDTO);
                    }
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    protected final Set<TradeDTO> saveValues(final Set<TradeDTO> newValues) {
        return newValues.stream()
                .peek(tradeDTO -> logger.debug("Checking trade in database: {}", tradeDTO))
                // We check in the database if it's a new trade and a trade to update.
                .<Trade>mapMulti((tradeDTO, consumer) -> {
                    final Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId(tradeDTO.getTradeId());

                    // We check if the trade exists in database.
                    if (tradeInDatabase.isEmpty()) {
                        // The trade does not exist in database, we create it.
                        logger.debug("Updating the trade: {}", tradeDTO);
                        final Trade newTrade = TRADE_MAPPER.mapToTrade(tradeDTO);
                        // Order is always present as we check it in getNewValues().
                        orderRepository.findByOrderId(tradeDTO.getOrderId()).ifPresent(newTrade::setOrder);
                        consumer.accept(newTrade);
                    } else {
                        // The trade exists in database, we update it.
                        logger.debug("Creating a new trade: {}", tradeDTO);
                        TRADE_MAPPER.updateTrade(tradeDTO, tradeInDatabase.get());
                        consumer.accept(tradeInDatabase.get());
                    }
                })
                // We save the trade in database.
                .map(tradeRepository::save)
                // We transform it to TradeDTO in order to return it to Cassandre.
                .map(TRADE_MAPPER::mapToTradeDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
