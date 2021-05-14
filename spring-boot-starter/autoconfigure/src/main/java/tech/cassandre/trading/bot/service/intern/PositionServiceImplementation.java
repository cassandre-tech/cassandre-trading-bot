package tech.cassandre.trading.bot.service.intern;

import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.position.PositionTypeDTO;
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
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_UP;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.SHORT;

/**
 * Position service implementation.
 */
public class PositionServiceImplementation extends BaseService implements PositionService {

    /** Big decimal scale for division. */
    public static final int SCALE = 8;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Trade service. */
    private final TradeService tradeService;

    /** Position flux. */
    private final PositionFlux positionFlux;

    /** List of position that should be closed no matter the rules. */
    private final Collection<Long> positionsToClose = Collections.synchronizedCollection(new ArrayList<>());

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
        return createPosition(strategy, LONG, currencyPair, amount, rules);
    }

    @Override
    public final PositionCreationResultDTO createShortPosition(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final PositionRulesDTO rules) {
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
    public final PositionCreationResultDTO createPosition(final StrategyDTO strategy,
                                                          final PositionTypeDTO type,
                                                          final CurrencyPairDTO currencyPair,
                                                          final BigDecimal amount,
                                                          final PositionRulesDTO rules) {
        // Trying to create an order.
        logger.debug("PositionService - Creating a {} position for {} on {} with the rules : {}", type.toString().toLowerCase(Locale.ROOT), amount, currencyPair, rules);
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
            position.setStrategy(strategyMapper.mapToStrategy(strategy));
            position = positionRepository.save(position);

            // =========================================================================================================
            // Creates the position dto.
            PositionDTO p = new PositionDTO(position.getId(), type, strategy, currencyPair, amount, orderCreationResult.getOrderId(), rules);
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
    public final void updatePositionRules(final long id, final PositionRulesDTO newRules) {
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
        positionsToClose.add(id);
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
    public final void tickersUpdate(final Set<TickerDTO> tickers) {
        // With the ticker received, we check for every position, if it should be closed.
        logger.debug("PositionService - Updating position with {} ticker", tickers.size());
        tickers.forEach(ticker -> {
            positionRepository.findByStatus(OPENED)
                    .stream()
                    .map(positionMapper::mapToPositionDTO)
                    .filter(p -> p.tickerUpdate(ticker))
                    .peek(p -> logger.debug("PositionService - Position {} updated with ticker {}", p.getPositionId(), ticker))
                    .forEach(p -> {
                        // We close the position if it triggers the rules.
                        // Or if the position was forced to close.
                        if (p.shouldBeClosed() || positionsToClose.contains(p.getPositionId())) {
                            final OrderCreationResultDTO orderCreationResult;
                            if (p.getType() == LONG) {
                                // Long - We just sell.
                                orderCreationResult = tradeService.createSellMarketOrder(p.getStrategy(), ticker.getCurrencyPair(), p.getAmount().getValue());
                            } else {
                                // Short - We buy back with the money we get from the original selling.
                                // On opening, we had :
                                // CP2 : ETH/USDT - 1 ETH costs 10 USDT - We sold 1 ETH and it will give us 10 USDT.
                                // We will use those 10 USDT to buy back ETH when the rule is triggered.
                                // CP2 : ETH/USDT - 1 ETH costs 2 USDT - We buy 5 ETH and it will costs us 10 USDT.
                                // We can now use those 10 USDT to buy 5 ETH (amountSold / price).
                                final BigDecimal amountToBuy = p.getAmountToLock().getValue().divide(ticker.getLast(), HALF_UP).setScale(SCALE, FLOOR);
                                orderCreationResult = tradeService.createBuyMarketOrder(p.getStrategy(), ticker.getCurrencyPair(), amountToBuy);
                            }

                            if (orderCreationResult.isSuccessful()) {
                                p.closePositionWithOrderId(orderCreationResult.getOrder().getOrderId());
                                logger.debug("PositionService - Position {} closed with order {}", p.getPositionId(), orderCreationResult.getOrder().getOrderId());
                            }

                            // If the position was force to close, we write it in position.
                            if (positionsToClose.contains(p.getPositionId())) {
                                positionsToClose.remove(p.getPositionId());
                                p.setForceClosing(true);
                            }
                        }
                        positionFlux.emitValue(p);
                    });
        });
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
