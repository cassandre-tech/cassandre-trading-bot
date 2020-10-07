package tech.cassandre.trading.bot.service.intern;

import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;

/**
 * Position service implementation.
 */
public class PositionServiceImplementation extends BaseService implements PositionService {

    /** List of positions. */
    private final Map<Long, PositionDTO> positions = new LinkedHashMap<>();

    /** Trade service. */
    private final TradeService tradeService;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param newTradeService       trade service
     * @param newPositionRepository position repository
     */
    public PositionServiceImplementation(final TradeService newTradeService,
                                         final PositionRepository newPositionRepository) {
        this.tradeService = newTradeService;
        this.positionRepository = newPositionRepository;
    }

    @Override
    public final Set<PositionDTO> getPositions() {
        getLogger().debug("PositionService - Retrieving all positions");
        return new LinkedHashSet<>(positions.values());
    }

    @Override
    public final Optional<PositionDTO> getPositionById(final long id) {
        getLogger().debug("PositionService - Retrieving position {}", id);
        return Optional.ofNullable(positions.get(id));
    }

    @Override
    public final PositionCreationResultDTO createPosition(final CurrencyPairDTO currencyPair, final BigDecimal amount, final PositionRulesDTO rules) {
        // Trying to create an order.
        getLogger().debug("PositionService - Creating a position for {} on {} with the rules : {}", amount, currencyPair, rules);
        final OrderCreationResultDTO orderCreationResult = tradeService.createBuyMarketOrder(currencyPair, amount);
        // If it works, create the position.
        if (orderCreationResult.isSuccessful()) {
            // =========================================================================================================
            // Creates the position in database.
            Position position = new Position();
            position.setStatus(OPENING.toString());
            if (rules.isStopGainPercentageSet()) {
                position.setStopGainPercentageRule(rules.getStopGainPercentage());
            }
            if (rules.isStopLossPercentageSet()) {
                position.setStopLossPercentageRule(rules.getStopLossPercentage());
            }
            position.setOpenOrderId(orderCreationResult.getOrderId());
            position = positionRepository.save(position);
            // =========================================================================================================

            // =========================================================================================================
            // Creates the position dto.
            PositionDTO p = new PositionDTO(position.getId(), orderCreationResult.getOrderId(), rules);
            positions.put(p.getId(), p);
            getLogger().debug("PositionService - Position {} opened with order {}", p.getId(), orderCreationResult.getOrderId());

            // =========================================================================================================
            // Creates the result.
            return new PositionCreationResultDTO(p.getId(), orderCreationResult.getOrderId());
        } else {
            getLogger().error("PositionService - Position creation failure : {}", orderCreationResult.getErrorMessage());
            // If it doesn't work, returns the error.
            return new PositionCreationResultDTO(orderCreationResult.getErrorMessage(), orderCreationResult.getException());
        }
    }

    @Override
    public final void tickerUpdate(final TickerDTO ticker) {
        // With the ticker received, we check for every position, if it should be closed.
        positions.values().stream()
                .filter(p -> p.getStatus().equals(OPENED))
                .filter(p -> p.getCurrencyPair() != null)
                .filter(p -> p.getCurrencyPair().equals(ticker.getCurrencyPair()))
                .filter(p -> p.shouldBeClosed(ticker))
                .forEach(p -> {
                    final OrderCreationResultDTO orderCreationResult = tradeService.createSellMarketOrder(ticker.getCurrencyPair(), p.getOpenTrade().getOriginalAmount());
                    if (orderCreationResult.isSuccessful()) {
                        p.setCloseOrderId(orderCreationResult.getOrderId());
                        getLogger().debug("PositionService - Position {} closed with order {}", p.getId(), orderCreationResult.getOrderId());
                    }
                });
    }

    @Override
    public final void tradeUpdate(final TradeDTO trade) {
        positions.values().forEach(p -> p.tradeUpdate(trade));
    }

    @Override
    public final void restorePosition(final PositionDTO position) {
        positions.put(position.getId(), position);
    }

    @Override
    public final void backupPosition(final PositionDTO position) {
        Optional<Position> p = positionRepository.findById(position.getId());
        if (p.isPresent()) {
            p.get().setId(position.getId());
            p.get().setStatus(position.getStatus().toString());
            if (position.getRules().isStopGainPercentageSet()) {
                p.get().setStopGainPercentageRule(position.getRules().getStopGainPercentage());
            }
            if (position.getRules().isStopLossPercentageSet()) {
                p.get().setStopLossPercentageRule(position.getRules().getStopLossPercentage());
            }
            p.get().setOpenOrderId(position.getOpenOrderId());
            p.get().setCloseOrderId(position.getCloseOrderId());
            p.get().setLowestPrice(position.getLowestPrice());
            p.get().setHighestPrice(position.getHighestPrice());
            positionRepository.save(p.get());
        } else {
            // Position was not found.
            getLogger().error("Position {} was not saved because it was not found in database", position.getId());
        }
    }

}
