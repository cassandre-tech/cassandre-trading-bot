package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.util.base.BaseService;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Position service implementation.
 */
public class PositionServiceImplementation extends BaseService implements PositionService {

    /** Position counter. */
    private final AtomicInteger positionCounter = new AtomicInteger(1);

    /** List of positions. */
    private final Map<Long, PositionDTO> positions = new LinkedHashMap<>();

    /** Trade service. */
    private final TradeService tradeService;

    /**
     * Constructor.
     *
     * @param newTradeService trade service
     */
    public PositionServiceImplementation(final TradeService newTradeService) {
        this.tradeService = newTradeService;
    }

    @Override
    public final Set<PositionDTO> getPositions() {
        return new LinkedHashSet<>(positions.values());
    }

    @Override
    public final Optional<PositionDTO> getPositionById(final long id) {
        return Optional.ofNullable(positions.get(id));
    }

    @Override
    public final PositionCreationResultDTO createPosition(final CurrencyPairDTO currencyPair, final BigDecimal amount, final PositionRulesDTO rules) {
        // Trying to create an order.
        final OrderCreationResultDTO orderCreationResult = tradeService.createBuyMarketOrder(currencyPair, amount);

        // If it works, create the position.
        if (orderCreationResult.getOrderId().isPresent()) {
            // Creates the position.
            PositionDTO p = new PositionDTO(positionCounter.getAndIncrement(), orderCreationResult.getOrderId().get(), rules);
            positions.put(p.getId(), p);
            getLogger().info("Position {} opened with order {}", p.getId(), orderCreationResult.getOrderId().get());

            // Creates the result.
            return new PositionCreationResultDTO(p.getId(), orderCreationResult.getOrderId().get());
        } else {
            // If it doesn't work, returns the error.
            return new PositionCreationResultDTO(orderCreationResult.getErrorMessage().get(), orderCreationResult.getException().get());
        }
    }

    @Override
    public final void tickerUpdate(final TickerDTO ticker) {
        positions.values().stream()
                .filter(p -> p.shouldBeClosed(ticker))
                .forEach(p -> {
                    final OrderCreationResultDTO orderCreationResult = tradeService.createSellMarketOrder(ticker.getCurrencyPair(), p.getOpenTrade().getOriginalAmount());
                    if (orderCreationResult.getOrderId().isPresent()) {
                        p.setCloseOrderId(orderCreationResult.getOrderId().get());
                        getLogger().info("Position {} closed with order {}", p.getId(), orderCreationResult.getOrderId().get());
                    }
                });
    }

    @Override
    public final void tradeUpdate(final TradeDTO trade) {
        positions.values().forEach(p -> p.tradeUpdate(trade));
    }

}
