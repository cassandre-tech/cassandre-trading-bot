package tech.cassandre.trading.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.position.PositionTypeDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_UP;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.SHORT;

/**
 * Position service implementation.
 */
@RequiredArgsConstructor
public class PositionServiceCassandreImplementation extends BaseService implements PositionService {

    /** Big decimal scale for division. */
    private static final int SCALE = 8;

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Trade service. */
    private final TradeService tradeService;

    /** Position flux. */
    private final PositionFlux positionFlux;

    @Override
    public final PositionCreationResultDTO createLongPosition(final GenericCassandreStrategy strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final PositionRulesDTO rules) {
        return createPosition(strategy, LONG, currencyPair, amount, rules);
    }

    @Override
    public final PositionCreationResultDTO createShortPosition(final GenericCassandreStrategy strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final PositionRulesDTO rules) {
        return createPosition(strategy, SHORT, currencyPair, amount, rules);
    }

    /**
     * Creates a position.
     *
     * @param strategy     strategy
     * @param type         long or short
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    public final PositionCreationResultDTO createPosition(final GenericCassandreStrategy strategy,
                                                          final PositionTypeDTO type,
                                                          final CurrencyPairDTO currencyPair,
                                                          final BigDecimal amount,
                                                          final PositionRulesDTO rules) {
        logger.debug("PositionService - Creating a {} position for {} on {} with the rules : {}",
                type.toString().toLowerCase(Locale.ROOT),
                amount,
                currencyPair,
                rules);

        // =============================================================================================================
        // Creates the order.
        final OrderCreationResultDTO orderCreationResult;
        if (type == LONG) {
            // Long position - we buy.
            orderCreationResult = tradeService.createBuyMarketOrder(strategy, currencyPair, amount);
        } else {
            // Short position - we sell.
            orderCreationResult = tradeService.createSellMarketOrder(strategy, currencyPair, amount);
        }

        // If it works, creates the position.
        if (orderCreationResult.isSuccessful()) {
            // =========================================================================================================
            // Creates the position in database.
            Position position = new Position();
            position.setStrategy(strategyMapper.mapToStrategy(strategy.getStrategyDTO()));
            position = positionRepository.save(position);

            // =========================================================================================================
            // Creates the position dto.
            PositionDTO p = new PositionDTO(position.getId(), type, strategy.getStrategyDTO(), currencyPair, amount, orderCreationResult.getOrder(), rules);
            positionRepository.save(positionMapper.mapToPosition(p));
            logger.debug("PositionService - Position {} opened with order {}",
                    p.getPositionId(),
                    orderCreationResult.getOrder().getOrderId());

            // =========================================================================================================
            // Creates the result & emit the position.
            positionFlux.emitValue(p);
            return new PositionCreationResultDTO(p);
        } else {
            logger.error("PositionService - Position creation failure : {}", orderCreationResult.getErrorMessage());
            return new PositionCreationResultDTO(orderCreationResult.getErrorMessage(), orderCreationResult.getException());
        }
    }

    @Override
    public final void updatePositionRules(final long id, final PositionRulesDTO newRules) {
        logger.debug("PositionService - Update position {} with the rules: {}", id, newRules);
        final Optional<Position> p = positionRepository.findById(id);
        // If position exists and position is not closed.
        if (p.isPresent() && p.get().getStatus() != CLOSED) {
            // Stop gain.
            if (newRules.isStopGainPercentageSet()) {
                positionRepository.updateStopGainRule(id, newRules.getStopGainPercentage());
            } else {
                positionRepository.updateStopGainRule(id, null);
            }
            // Stop loss.
            if (newRules.isStopLossPercentageSet()) {
                positionRepository.updateStopLossRule(id, newRules.getStopLossPercentage());
            } else {
                positionRepository.updateStopLossRule(id, null);
            }
        }
    }

    @Override
    public final void closePosition(final long id) {
        logger.debug("PositionService - Force closing position {}", id);
        positionRepository.updateForceClosing(id, true);
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
        logger.debug("PositionService - Retrieving position by id {}", id);
        final Optional<Position> position = positionRepository.findById(id);
        return position.map(positionMapper::mapToPositionDTO);
    }

    @Override
    public final void ordersUpdates(final Set<OrderDTO> orders) {
        orders.forEach(orderDTO -> {
            logger.debug("PositionService - Updating positions with order {}", orderDTO);
            positionRepository.findByStatusNot(CLOSED)
                    .stream()
                    .map(positionMapper::mapToPositionDTO)
                    .forEach(p -> {
                        if (p.orderUpdate(orderDTO)) {
                            logger.debug("PositionService - Position {} updated with order {}",
                                    p.getPositionId(),
                                    orderDTO);
                            positionFlux.emitValue(p);
                        }
                    });
        });
    }

    @Override
    public final void tradesUpdates(final Set<TradeDTO> trades) {
        trades.forEach(tradeDTO -> {
            logger.debug("PositionService - Updating positions with trade {}", tradeDTO);
            positionRepository.findByStatusNot(CLOSED)
                    .stream()
                    .map(positionMapper::mapToPositionDTO)
                    .forEach(p -> {
                        if (p.tradeUpdate(tradeDTO)) {
                            logger.debug("PositionService - Position {} updated with trade {}",
                                    p.getPositionId(),
                                    tradeDTO);
                            positionFlux.emitValue(p);
                        }
                    });
        });
    }

    @Override
    public final void tickersUpdates(final Set<TickerDTO> tickers) {
        // With the ticker received, we check for every opened position, if it should be closed.
        logger.debug("PositionService - Updating position with {} ticker", tickers.size());
        tickers.forEach(ticker -> positionRepository.findByStatusNot(CLOSED)
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .filter(p -> p.tickerUpdate(ticker))
                .peek(p -> logger.debug("PositionService - Position {} updated with ticker {}", p.getPositionId(), ticker))
                .forEach(p -> {
                    // We close the position if it triggers the rules.
                    // Or if the position was forced to close.
                    if (p.isForceClosing() || p.shouldBeClosed()) {
                        final OrderCreationResultDTO orderCreationResult;
                        // We retrieve the strategy that created the position.
                        final Optional<GenericCassandreStrategy> strategy = applicationContext.getBeansWithAnnotation(CassandreStrategy.class)
                                .values()  // We get the list of all required cp of all strategies.
                                .stream()
                                .map(o -> ((GenericCassandreStrategy) o))
                                .filter(cassandreStrategy -> cassandreStrategy.getStrategyDTO().getStrategyId().equals(p.getStrategy().getStrategyId()))
                                .findFirst();

                        // Here, we treat the order creation depending on the position type (Short or long).
                        if (strategy.isPresent()) {
                            if (p.getType() == LONG) {
                                // Long - We just sell.
                                orderCreationResult = tradeService.createSellMarketOrder(strategy.get(), ticker.getCurrencyPair(), p.getAmount().getValue());
                            } else {
                                // Short - We buy back with the money we get from the original selling.
                                // On opening, we had :
                                // CP2 : ETH/USDT - 1 ETH costs 10 USDT - We sold 1 ETH and it will give us 10 USDT.
                                // We will use those 10 USDT to buy back ETH when the rule is triggered.
                                // CP2 : ETH/USDT - 1 ETH costs 2 USDT - We buy 5 ETH and it will costs us 10 USDT.
                                // We can now use those 10 USDT to buy 5 ETH (amount sold / price).
                                final BigDecimal amountToBuy = p.getAmountToLock().getValue().divide(ticker.getLast(), HALF_UP).setScale(SCALE, FLOOR);
                                orderCreationResult = tradeService.createBuyMarketOrder(strategy.get(), ticker.getCurrencyPair(), amountToBuy);
                            }

                            if (orderCreationResult.isSuccessful()) {
                                p.closePositionWithOrder(orderCreationResult.getOrder());
                                logger.debug("PositionService - Position {} closed with order {}", p.getPositionId(), orderCreationResult.getOrder().getOrderId());
                            } else {
                                logger.error("PositionService - Position {} not closed: {}", p.getPositionId(), orderCreationResult.getErrorMessage());
                            }
                        } else {
                            logger.error("Strategy {} not found", p.getStrategy().getStrategyId());
                        }
                    }
                    positionFlux.emitValue(p);
                }));
    }

    @Override
    public final Map<Long, CurrencyAmountDTO> amountsLockedByPosition() {
        // List of status that locks amounts.
        Set<PositionStatusDTO> status = new HashSet<>();
        status.add(OPENING);
        status.add(OPENED);

        return positionRepository.findByStatusIn(status)
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .collect(Collectors.toMap(PositionDTO::getId, PositionDTO::getAmountToLock, (key, value) -> key, HashMap::new));
    }

    @Override
    public final HashMap<CurrencyDTO, GainDTO> getGains() {
        HashMap<CurrencyDTO, BigDecimal> totalBefore = new LinkedHashMap<>();
        HashMap<CurrencyDTO, BigDecimal> totalAfter = new LinkedHashMap<>();
        List<CurrencyAmountDTO> totalFees = new LinkedList<>();
        HashMap<CurrencyDTO, GainDTO> gains = new LinkedHashMap<>();

        // We calculate, by currency, the amount bought & sold.
        positionRepository.findByStatus(CLOSED)
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .forEach(p -> {
                    // We retrieve the currency and initiate the maps if they are empty
                    CurrencyDTO currency;
                    if (p.getType() == LONG) {
                        // LONG.
                        currency = p.getCurrencyPair().getQuoteCurrency();
                    } else {
                        // SHORT.
                        currency = p.getCurrencyPair().getBaseCurrency();
                    }
                    gains.putIfAbsent(currency, null);
                    totalBefore.putIfAbsent(currency, ZERO);
                    totalAfter.putIfAbsent(currency, ZERO);

                    // We calculate the amounts bought and amount sold.
                    if (p.getType() == LONG) {
                        totalBefore.put(currency, p.getOpeningOrder().getTrades()
                                .stream()
                                .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                                .reduce(totalBefore.get(currency), BigDecimal::add));
                        totalAfter.put(currency, p.getClosingOrder().getTrades()
                                .stream()
                                .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                                .reduce(totalAfter.get(currency), BigDecimal::add));
                    } else {
                        totalBefore.put(currency, p.getOpeningOrder().getTrades()
                                .stream()
                                .map(t -> t.getAmount().getValue())
                                .reduce(totalBefore.get(currency), BigDecimal::add));
                        totalAfter.put(currency, p.getClosingOrder().getTrades()
                                .stream()
                                .map(t -> t.getAmount().getValue())
                                .reduce(totalAfter.get(currency), BigDecimal::add));
                    }

                    // And now the fees.
                    Stream.concat(p.getOpeningOrder().getTrades().stream(), p.getClosingOrder().getTrades().stream()).forEach(t -> totalFees.add(t.getFee()));
                });

        gains.keySet()
                .forEach(currency -> {
                    // We make the calculation.
                    BigDecimal before = totalBefore.get(currency);
                    BigDecimal after = totalAfter.get(currency);
                    BigDecimal gainAmount = after.subtract(before);
                    BigDecimal gainPercentage = ((after.subtract(before)).divide(before, HALF_UP)).multiply(new BigDecimal("100"));

                    // We calculate the fees for the currency.
                    final BigDecimal fees = totalFees.stream()
                            .filter(amount -> amount.getCurrency().equals(currency))
                            .map(CurrencyAmountDTO::getValue)
                            .reduce(ZERO, BigDecimal::add);

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
