package tech.cassandre.trading.bot.service.intern;

import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;

/**
 * Position service implementation.
 */
public class PositionServiceImplementation extends BaseService implements PositionService {

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Trade service. */
    private final TradeService tradeService;

    /** Position flux. */
    private final PositionFlux positionFlux;

    /**
     * Constructor.
     *
     * @param newPositionRepository position repository
     * @param newTradeService       trade service
     * @param newPositionFlux       position flux
     */
    public PositionServiceImplementation(final PositionRepository newPositionRepository,
                                         final TradeService newTradeService,
                                         final PositionFlux newPositionFlux) {
        this.positionRepository = newPositionRepository;
        this.tradeService = newTradeService;
        this.positionFlux = newPositionFlux;
    }

    @Override
    public final PositionCreationResultDTO createLongPosition(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final PositionRulesDTO rules) {
        // Trying to create an order.
        logger.debug("PositionService - Creating a position for {} on {} with the rules : {}", amount, currencyPair, rules);
        // =============================================================================================================
        // Creates the order.
        final OrderCreationResultDTO orderCreationResult = tradeService.createBuyMarketOrder(strategy, currencyPair, amount);

        // If it works, creates the position.
        if (orderCreationResult.isSuccessful()) {
            // =========================================================================================================
            // Creates the position in database.
            Position position = new Position();
            position.setStrategy(strategyMapper.mapToStrategy(strategy));
            position = positionRepository.save(position);

            // =========================================================================================================
            // Creates the position dto.
            Optional<OrderDTO> order = tradeService.getOrders()
                    .stream()
                    .filter(o -> orderCreationResult.getOrderId().equals(o.getOrderId()))
                    .findFirst();
            if (order.isEmpty()) {
                order = Optional.of(orderCreationResult.getOrder());
            }
            PositionDTO p = new PositionDTO(position.getId(), strategy, currencyPair, amount, order.get(), rules);
            positionRepository.save(positionMapper.mapToPosition(p));
            logger.debug("PositionService - Position {} opened with order {}", p.getPositionId(), orderCreationResult.getOrder().getOrderId());

            // =========================================================================================================
            // Creates the result.
            positionFlux.emitValue(p);
            return new PositionCreationResultDTO(p);
        } else {
            logger.error("PositionService - Position creation failure : {}", orderCreationResult.getErrorMessage());
            // If it doesn't work, returns the error.
            return new PositionCreationResultDTO(orderCreationResult.getErrorMessage(), orderCreationResult.getException());
        }
    }

    @Override
    public final Set<PositionDTO> getPositions() {
        logger.debug("PositionService - Retrieving all positions");
        return positionRepository.findByOrderById()
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public final Optional<PositionDTO> getPositionById(final long id) {
        logger.debug("PositionService - Retrieving position {}", id);
        final Optional<Position> position = positionRepository.findById(id);
        return position.map(positionMapper::mapToPositionDTO);
    }

    @Override
    public final void orderUpdate(final OrderDTO order) {
        logger.debug("PositionService - Updating position with order {}", order);
        positionRepository.findByStatusNot(CLOSED)
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .forEach(p -> {
                    if (p.orderUpdate(order)) {
                        logger.debug("PositionService - Position {} updated with order {}", p.getPositionId(), order);
                        positionFlux.emitValue(p);
                    }
                });
    }

    @Override
    public final void tradeUpdate(final TradeDTO trade) {
        logger.debug("PositionService - Updating position with trade {}", trade);
        positionRepository.findByStatusNot(CLOSED)
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .forEach(p -> {
                    if (p.tradeUpdate(trade)) {
                        logger.debug("PositionService - Position {} updated with trade {}", p.getPositionId(), trade);
                        positionFlux.emitValue(p);
                    }
                });
    }

    @Override
    public final void tickerUpdate(final TickerDTO ticker) {
        // With the ticker received, we check for every position, if it should be closed.
        logger.debug("PositionService - Updating position with ticker {}", ticker);
        positionRepository.findByStatus(OPENED)
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .filter(p -> p.tickerUpdate(ticker))
                .peek(p -> logger.debug("PositionService - Position {} updated with ticker {}", p.getPositionId(), ticker))
                .forEach(p -> {
                    // We close the position if it triggers the rules.
                    if (p.shouldBeClosed()) {
                        final OrderCreationResultDTO orderCreationResult = tradeService.createSellMarketOrder(p.getStrategy(), ticker.getCurrencyPair(), p.getAmount().getValue());
                        if (orderCreationResult.isSuccessful()) {
                            p.closePositionWithOrderId(orderCreationResult.getOrder().getOrderId());
                            logger.debug("PositionService - Position {} closed with order {}", p.getPositionId(), orderCreationResult.getOrder().getOrderId());
                        }
                    }
                    positionFlux.emitValue(p);
                });
    }

    @Override
    public final HashMap<CurrencyDTO, GainDTO> getGains() {
        HashMap<CurrencyDTO, BigDecimal> totalBought = new LinkedHashMap<>();
        HashMap<CurrencyDTO, BigDecimal> totalSold = new LinkedHashMap<>();
        HashMap<CurrencyDTO, BigDecimal> totalFees = new LinkedHashMap<>();
        HashMap<CurrencyDTO, GainDTO> gains = new LinkedHashMap<>();

        // We calculate, by currency, the amount bought & sold.
        positionRepository.findByStatus(CLOSED)
                .stream()
                .filter(p -> CLOSED.equals(p.getStatus()))
                .map(positionMapper::mapToPositionDTO)
                .forEach(p -> {
                    // We retrieve the currency and initiate the maps if they are empty
                    CurrencyDTO currency = p.getCurrencyPair().getQuoteCurrency();
                    gains.putIfAbsent(currency, null);
                    totalBought.putIfAbsent(currency, ZERO);
                    totalSold.putIfAbsent(currency, ZERO);
                    totalFees.putIfAbsent(currency, ZERO);

                    // We calculate the amounts bought and amount sold.
                    totalBought.put(currency, p.getOpeningOrder().getTrades()
                            .stream()
                            .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                            .reduce(totalBought.get(currency), BigDecimal::add));
                    totalSold.put(currency, p.getClosingOrder().getTrades()
                            .stream()
                            .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                            .reduce(totalSold.get(currency), BigDecimal::add));

                    // And now the feeds.
                    final BigDecimal fees = Stream.concat(p.getOpeningOrder().getTrades().stream(),
                            p.getClosingOrder().getTrades().stream())
                            .map(t -> t.getFee().getValue())
                            .reduce(totalFees.get(currency), BigDecimal::add);
                    totalFees.put(currency, fees);
                });

        gains.keySet()
                .forEach(currency -> {
                    // We make the calculation.
                    BigDecimal bought = totalBought.get(currency);
                    BigDecimal sold = totalSold.get(currency);
                    BigDecimal fees = totalFees.get(currency);
                    BigDecimal gainAmount = sold.subtract(bought);
                    BigDecimal gainPercentage = ((sold.subtract(bought)).divide(bought, HALF_UP)).multiply(new BigDecimal("100"));

                    GainDTO g = GainDTO.builder()
                            .percentage(gainPercentage.setScale(2, HALF_UP).doubleValue())
                            .amount(CurrencyAmountDTO.builder()
                                    .value(gainAmount)
                                    .currency(currency)
                                    .build())
                            .fees(CurrencyAmountDTO.builder()
                                    .value(fees)
                                    .currency(currency)
                                    .build())
                            .build();
                    gains.put(currency, g);
                });
        return gains;
    }

}
