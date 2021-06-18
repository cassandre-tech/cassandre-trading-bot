package tech.cassandre.trading.bot.util.dry;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static org.knowm.xchange.dto.Order.OrderStatus.FILLED;
import static org.knowm.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.CLOSED;

/**
 * AOP for trade service in dry mode.
 */
@Aspect
@Component
@ConditionalOnExpression("${cassandre.trading.bot.exchange.modes.dry:true}")
@RequiredArgsConstructor
public class TradeServiceDryModeAOP extends BaseService {

    /** Big integer scale. */
    private static final int BIGINTEGER_SCALE = 8;

    /** Dry order prefix. */
    private static final String DRY_ORDER_PREFIX = "DRY_ORDER_";

    /** Dry trade prefix. */
    private static final String DRY_TRADE_PREFIX = "DRY_TRADE_";

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Order counter. */
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    /** User service - dry mode. */
    private final UserServiceDryModeAOP userService;

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.createBuyMarketOrder(..)) && args(strategy, currencyPair, amount)", argNames = "pjp, strategy, currencyPair, amount")
    public final OrderCreationResultDTO createBuyMarketOrder(final ProceedingJoinPoint pjp,
                                                             final GenericCassandreStrategy strategy,
                                                             final CurrencyPairDTO currencyPair,
                                                             final BigDecimal amount) {
        // We check that we have the trade account.
        final Optional<AccountDTO> tradeAccount = strategy.getTradeAccount();
        if (tradeAccount.isEmpty()) {
            throw new RuntimeException("Trade account was not found");
        }

        // We check if we have enough assets to buy.
        // Buying order - we buy ETH with BTC.
        // We are buying the following amount : ticker last price * amount
        Optional<BalanceDTO> balance = tradeAccount.get().getBalance(currencyPair.getQuoteCurrency());
        final Optional<TickerDTO> ticker = strategy.getLastTickerByCurrencyPair(currencyPair);

        if (balance.isPresent() && ticker.isPresent()) {
            BigDecimal ownedAssets = balance.get().getAvailable();
            BigDecimal cost = ticker.get().getLast().multiply(amount);
            if (cost.compareTo(ownedAssets) > 0) {
                final String errorMessage = "Not enough assets (costs : " + cost + " " + currencyPair.getQuoteCurrency() + " - owned assets : " + ownedAssets + " " + currencyPair.getQuoteCurrency() + ")";
                return new OrderCreationResultDTO(errorMessage, new RuntimeException());
            }
        } else {
            return new OrderCreationResultDTO("No assets (" + currencyPair.getQuoteCurrency() + ")", new RuntimeException());
        }

        // We execute the buy.
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            logger.error("Error in Dry mode AOP: {}", throwable.getMessage());
        }

        // We update the account.
        userService.addToBalance(strategy, currencyMapper.mapToCurrency(currencyPair.getBaseCurrency()), amount);
        userService.addToBalance(strategy, currencyMapper.mapToCurrency(currencyPair.getQuoteCurrency()), amount.multiply(ticker.get().getLast()).multiply(new BigDecimal("-1")));

        return (OrderCreationResultDTO) result;
    }

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.createSellMarketOrder(..)) && args(strategy, currencyPair, amount)", argNames = "pjp, strategy, currencyPair, amount")
    public final OrderCreationResultDTO createSellMarketOrder(final ProceedingJoinPoint pjp,
                                                              final GenericCassandreStrategy strategy,
                                                              final CurrencyPairDTO currencyPair,
                                                              final BigDecimal amount) {
        // We check that we have the trade account.
        final Optional<AccountDTO> tradeAccount = strategy.getTradeAccount();
        if (tradeAccount.isEmpty()) {
            throw new RuntimeException("Trade account was not found");
        }

        // Selling order - we sell ETH to buy BTC.
        // We are selling the amount
        Optional<BalanceDTO> balance = tradeAccount.get().getBalance(currencyPair.getBaseCurrency());
        final Optional<TickerDTO> ticker = strategy.getLastTickerByCurrencyPair(currencyPair);

        if (balance.isPresent() && ticker.isPresent()) {
            BigDecimal ownedAssets = balance.get().getAvailable();
            if (amount.compareTo(ownedAssets) > 0) {
                final String errorMessage = "Not enough assets (amount : " + amount + " " + currencyPair.getQuoteCurrency() + " - owned assets : " + ownedAssets + " " + currencyPair.getBaseCurrency();
                return new OrderCreationResultDTO(errorMessage, new RuntimeException());
            }
        } else {
            return new OrderCreationResultDTO("No assets (" + currencyPair.getBaseCurrency() + ")", new RuntimeException());
        }

        // We execute the sell.
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            logger.error("Error in Dry mode AOP: {}", throwable.getMessage());
        }

        // We update the account.
        userService.addToBalance(strategy, currencyMapper.mapToCurrency(currencyPair.getBaseCurrency()), amount.multiply(new BigDecimal("-1")));
        userService.addToBalance(strategy, currencyMapper.mapToCurrency(currencyPair.getQuoteCurrency()), amount.multiply(ticker.get().getLast()));

        return (OrderCreationResultDTO) result;
    }

    @Around(value = "execution(* org.knowm.xchange.service.trade.TradeService.placeMarketOrder(..)) && args(marketOrder)", argNames = "pjp, marketOrder")
    public final String placeMarketOrder(final ProceedingJoinPoint pjp, final MarketOrder marketOrder) {
        return DRY_ORDER_PREFIX.concat(String.format("%09d", orderCounter.getAndIncrement()));
    }

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.cancelOrder(..)) && args(orderId))", argNames = "pjp, orderId")
    public final boolean cancelOrder(final ProceedingJoinPoint pjp, final String orderId) {
        final Optional<Order> order = orderRepository.findByOrderId(orderId);
        if (order.isPresent()) {
            orderRepository.delete(order.get());
            return true;
        } else {
            return false;
        }
    }

    @Around("execution(* org.knowm.xchange.service.trade.TradeService.getOpenOrders())")
    public final OpenOrders getOpenOrders(final ProceedingJoinPoint pjp) {
        // For every new order in database, we send an update saying the order is filled.
        List<LimitOrder> orders = orderRepository.findByStatusNot(CLOSED)
                .stream()
                .map(orderMapper::mapToOrderDTO)
                .map(orderDTO -> new LimitOrder.Builder(utilMapper.mapToOrderType(orderDTO.getType()), currencyMapper.mapToCurrencyPair(orderDTO.getCurrencyPair()))
                        .id(orderDTO.getOrderId())
                        .originalAmount(orderDTO.getAmount().getValue())
                        .averagePrice(orderDTO.getAveragePrice().getValue())
                        // TODO Fix this
//                        .limitPrice(orderDTO.getLimitPrice().getValue())
                        .orderStatus(FILLED)
                        .cumulativeAmount(orderDTO.getCumulativeAmount().getValue())
                        .userReference(orderDTO.getUserReference())
                        .timestamp(Timestamp.valueOf(orderDTO.getTimestamp().toLocalDateTime()))
                        .build())
                .collect(Collectors.toList());

        return new OpenOrders(orders);
    }

    @Around(value = "execution(* org.knowm.xchange.service.trade.TradeService.getTradeHistory(..)) && args(params))", argNames = "pjp, params")
    public final UserTrades getTradeHistory(final ProceedingJoinPoint pjp, final TradeHistoryParams params) {
        // We will check for every order not fulfilled, if this order is used to close a position.
        // If so, we change the price of the trade to the price of the trade according to the position price.
        Map<String, BigDecimal> tradePrices = new HashMap<>();
        orderRepository.findByOrderByTimestampAsc()
                .stream()
                .map(orderMapper::mapToOrderDTO)
                .filter(orderDTO -> !orderDTO.isFulfilled())    // Only orders with trades not arrived
                .forEach(orderDTO -> {
                            tradePrices.put(orderDTO.getOrderId(), orderDTO.getMarketPrice().getValue());

                            final Optional<PositionDTO> positionDTO = positionRepository.findByStatusNot(PositionStatusDTO.CLOSED)
                                    .stream()
                                    .filter(position -> position.getClosingOrder() != null)
                                    .filter(position -> position.getClosingOrder().getOrderId().equals(orderDTO.getOrderId()))
                                    .map(positionMapper::mapToPositionDTO)
                                    .findFirst();

                            // If this order is used to close position, we calculate a new price.
                            // A gain was made, we recalculate it from the order.
                            if (positionDTO.isPresent()) {
                                final Optional<GainDTO> gainDTO = positionDTO.get().calculateGainFromPrice(orderDTO.getMarketPrice().getValue());

                                if (gainDTO.isPresent()) {
                                    // We need the opening trade to know the price the asset was bought.
                                    final TradeDTO openingTrade = positionDTO.get().getOpeningOrder().getTrades().iterator().next();

                                    if (positionDTO.get().getType().equals(LONG)) {
                                        // =====================================================================================
                                        // Treating long positions.

                                        if (positionDTO.get().getRules().isStopGainPercentageSet()
                                                && gainDTO.get().getPercentage() >= positionDTO.get().getRules().getStopGainPercentage()) {
                                            // If the position has a stop gain percentage and the real gain is superior to this percentage.
                                            // This means the stop gain won and we should transform the price.

                                            // Long position n°1 (rules : 200.0 % gain).
                                            //  Opening order: 20 000 USDT.
                                            //  Closed with trade DRY_TRADE_000000007 : 70 000 USDT.
                                            //  250 % evolution => ((70000 - 20000) / 20000) * 100 = 250 %
                                            //  How to calculate the new price.
                                            //  openingTrade market price * (( openingTrade market price * rules gain)/100)
                                            final BigDecimal augmentation = openingTrade.getPrice().getValue()
                                                    .multiply(BigDecimal.valueOf(positionDTO.get().getRules().getStopGainPercentage()))
                                                    .divide(new BigDecimal("100"), BIGINTEGER_SCALE, FLOOR);
                                            tradePrices.put(orderDTO.getOrderId(), openingTrade.getPrice().getValue().add(augmentation));

                                        } else if (positionDTO.get().getRules().isStopLossPercentageSet()
                                                && gainDTO.get().getPercentage() <= positionDTO.get().getRules().getStopLossPercentage()) {
                                            // If the position has a stop gain percentage and the real gain is superior to this percentage.
                                            // This means the stop gain won and we should transform the price.

                                            // Long position n°2 (rules : 20.0 % loss).
                                            //  Opening order: 50 000 USDT.
                                            //  Closed with trade DRY_TRADE_000000004: 30 000 USDT.
                                            //  -40 % evolution => ((30000 - 50000) / 50000) * 100 = -40 %
                                            //  How to calculate the new price.
                                            //  openingTrade market price * (( openingTrade market price * rules gain)/100)
                                            final BigDecimal reduction = openingTrade.getPrice().getValue()
                                                    .multiply(BigDecimal.valueOf(positionDTO.get().getRules().getStopLossPercentage()))
                                                    .divide(new BigDecimal("100"), BIGINTEGER_SCALE, FLOOR);
                                            tradePrices.put(orderDTO.getOrderId(), openingTrade.getPrice().getValue().subtract(reduction));
                                        }
                                        // =====================================================================================
                                    } else {
                                        // =====================================================================================
                                        // Treating short positions.

                                        if (positionDTO.get().getRules().isStopGainPercentageSet()
                                                && gainDTO.get().getPercentage() >= positionDTO.get().getRules().getStopGainPercentage()) {
                                            // If the position has a stop gain percentage and the real gain is superior to this percentage.
                                            // This means the stop gain won and we should transform the price.

                                            // Short position n°4 (rules : 100.0 % gain)
                                            //  Opening order: 70 000 USDT.
                                            //  Closed with DRY_TRADE_000000009: 25 000 USDT.
                                            //  It's a shot position so:
                                            //  We sold one bitcoin for 70 000 USDT.
                                            //  When the price reached 25 000 USDT, with the 70 000 USDT, we could buy 2.8 BTC.
                                            //  180 % evolution => ((2.8 - 1) / 1) * 100 = 180 %
                                            //  How to calculate the new price.
                                            //  Amount I gained = opening trade amount * 70 000 USDT.
                                            //  To gain 100%, I should be able to by 2 bitcoins : opening trade amount * (opening trade amount * stop gain/100)
                                            //  so the question is how much a bitcoin should cost so I can buy 2 with 70 000 USDT
                                            //  2 * price = 70 000 USDT => price = 70 000/2 = 35 000
                                            final BigDecimal augmentation = openingTrade.getAmount().getValue()
                                                    .multiply(BigDecimal.valueOf(positionDTO.get().getRules().getStopGainPercentage()))
                                                    .divide(new BigDecimal("100"), BIGINTEGER_SCALE, FLOOR);
                                            orderRepository.updateAmount(orderDTO.getId(), openingTrade.getAmount().getValue().add(augmentation));
                                            tradePrices.put(orderDTO.getOrderId(), openingTrade.getPrice().getValue().divide(openingTrade.getAmount().getValue().add(augmentation), BIGINTEGER_SCALE, FLOOR));

                                        } else if (positionDTO.get().getRules().isStopLossPercentageSet()
                                                && gainDTO.get().getPercentage() <= positionDTO.get().getRules().getStopLossPercentage()) {
                                            // If the position has a stop gain percentage and the real gain is superior to this percentage.
                                            // This means the stop gain won and we should transform the price.

                                            // Short position n°3 (rules : 10.0 % loss)
                                            //  Opening order: 40 000 USDT.
                                            //  Closed with trade DRY_TRADE_000000008: 70 000 USDT.
                                            //  It's a shot position so:
                                            //  We sold 1 bitcoin for 40 000 USDT.
                                            //  When the price reached 70 000 USDT, with the 40 000 USDT, we could buy 0.57 BTC.
                                            //  We had 1 BTC, we now only have 0.57 BTC
                                            //  -43 % evolution => ((0.57 - 1) / 1) * 100 = -43 %
                                            //  How to calculate the new price.
                                            //  Amount I gained = opening trade amount * 40 000 USDT.
                                            //  To lose 10%, I should finish by only being able to buy 0,90 BTC : opening trade amount * (opening trade amount * stop gain/100)
                                            //  so the question is how much a bitcoin should cost so I can buy 0,90 with 40 000 USDT
                                            //  0.9 * price = 40 000 USDT => price = 40 000/0.9
                                            final BigDecimal reduction = openingTrade.getAmount().getValue()
                                                    .multiply(BigDecimal.valueOf(positionDTO.get().getRules().getStopLossPercentage()))
                                                    .divide(new BigDecimal("100"), BIGINTEGER_SCALE, FLOOR);
                                            orderRepository.updateAmount(orderDTO.getId(), openingTrade.getAmount().getValue().subtract(reduction));
                                            tradePrices.put(orderDTO.getOrderId(), openingTrade.getPrice().getValue().divide(openingTrade.getAmount().getValue().subtract(reduction), BIGINTEGER_SCALE, FLOOR));
                                        }
                                        // =====================================================================================
                                    }
                                }
                            }
                        }
                );

        // For every orders in database, we will simulate an equivalent trade to close things.
        List<UserTrade> trades = orderRepository.findByOrderByTimestampAsc()
                .stream()
                .map(orderMapper::mapToOrderDTO)
                .filter(orderDTO -> !orderDTO.isFulfilled())    // Only orders with trades not arrived.
                .map(orderDTO -> UserTrade.builder()
                        .id(orderDTO.getOrderId().replace(DRY_ORDER_PREFIX, DRY_TRADE_PREFIX))
                        .type(utilMapper.mapToOrderType(orderDTO.getType()))
                        .orderId(orderDTO.getOrderId())
                        .currencyPair(currencyMapper.mapToCurrencyPair(orderDTO.getCurrencyPair()))
                        .originalAmount(orderDTO.getAmount().getValue())
                        .price(tradePrices.get(orderDTO.getOrderId()))
                        .feeAmount(ZERO)
                        .timestamp(Timestamp.valueOf(orderDTO.getTimestamp().toLocalDateTime()))
                        .build())
                .collect(Collectors.toList());

        return new UserTrades(trades, SortByTimestamp);
    }

}
