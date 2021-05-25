package tech.cassandre.trading.bot.util.dry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.FILLED;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

/**
 * AOP for trade service in dry mode.
 */
@Aspect
@Configuration
@ConditionalOnExpression("${cassandre.trading.bot.exchange.modes.dry:true}")
public class TradeServiceDryModeAOP extends BaseService {

    /** Delay before order arrives. */
    private static final int DELAY_BEFORE_ORDER_ARRIVES = 100;

    /** Delay before trade arrives. */
    private static final int DELAY_BEFORE_TRADE_ARRIVES = 200;

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Waiting time before sending orders and trades to flux. */
    private static final long WAITING_TIME = 20000L;

    /** Dry order prefix. */
    private static final String DRY_ORDER_PREFIX = "DRY_ORDER_";

    /** Dry trade prefix. */
    private static final String DRY_TRADE_PREFIX = "DRY_TRADE_";

    /** Trade account ID. */
    private static final String TRADE_ACCOUNT_ID = "trade";

    /** Order counter. */
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    /** Trade counter. */
    private final AtomicInteger tradeCounter = new AtomicInteger(1);

    /** User service - dry mode. */
    private final UserServiceDryModeAOP userService;

    /** Hashmap used to store ZonedDateTime of orders created locally. */
    private final Map<String, ZonedDateTime> localOrdersCreationDates = new ConcurrentHashMap<>();

    /** Hashmap used to store orders created locally. */
    private final Map<String, OrderDTO> localOrders = new ConcurrentHashMap<>();

    /** Hashmap used to store ZonedDateTime of orders created locally. */
    private final Map<String, ZonedDateTime> localTradesCreationDates = new ConcurrentHashMap<>();

    /** Hashmap used to store trades created locally. */
    private final Map<String, TradeDTO> localTrades = new ConcurrentHashMap<>();

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     * @param newUserService        user service
     */
    public TradeServiceDryModeAOP(final ApplicationContext newApplicationContext,
                                  final UserServiceDryModeAOP newUserService) {
        this.applicationContext = newApplicationContext;
        this.userService = newUserService;
    }

    /**
     * Creates a fake market order.
     *
     * @param strategy     strategy
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order creation result
     */
    private OrderCreationResultDTO createMarketOrder(final StrategyDTO strategy, final OrderTypeDTO orderTypeDTO, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        // We retrieve the ticker received by the strategy.
        final Optional<TickerDTO> t = applicationContext.getBeansWithAnnotation(CassandreStrategy.class)
                .values()
                .stream()
                .filter(o -> o.getClass().getAnnotation(CassandreStrategy.class).strategyId().equals(strategy.getStrategyId()))
                .map(cassandreStrategy -> ((GenericCassandreStrategy) cassandreStrategy))
                .map(cassandreStrategy -> cassandreStrategy.getLastTickerByCurrencyPair(currencyPair))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        // We create the order.
        if (t.isPresent()) {
            // If we don't have enough assets, we can't buy.
            // Example :
            // ETH/BTC quote currency => BTC.
            // ETH/BTC base currency => ETH.

            // We check that we have a user and a trade account.
            final Optional<UserDTO> user = userService.getUser();
            final AccountDTO account;
            if (user.isPresent()) {
                account = user.get().getAccounts().get(TRADE_ACCOUNT_ID);
                if (account == null) {
                    return new OrderCreationResultDTO("No trade account", new Exception("No trade account"));
                }
            } else {
                return new OrderCreationResultDTO("No data for user", new Exception("No data for user"));
            }

            // We check if we have enough assets to buy/sell.
            if (orderTypeDTO.equals(BID)) {
                // Buying order - we buy ETH from BTC.
                // We are buying the following amount : ticker last price * amount
                Optional<BalanceDTO> balance = account.getBalance(currencyPair.getQuoteCurrency());
                if (balance.isPresent()) {
                    BigDecimal ownedAssets = balance.get().getAvailable();
                    BigDecimal cost = t.get().getLast().multiply(amount);
                    if (cost.compareTo(ownedAssets) > 0) {
                        final String errorMessage = "Not enough assets (costs : " + cost + " " + currencyPair.getQuoteCurrency() + " - owned assets : " + ownedAssets + " " + currencyPair.getQuoteCurrency();
                        return new OrderCreationResultDTO(errorMessage, new Exception(errorMessage));
                    }
                } else {
                    return new OrderCreationResultDTO("No assets for " + currencyPair.getQuoteCurrency(), new Exception("No assets for " + currencyPair.getQuoteCurrency()));
                }
            } else {
                // Selling order - we sell ETH for BTC.
                // We are selling the amount
                Optional<BalanceDTO> balance = account.getBalance(currencyPair.getBaseCurrency());
                if (balance.isPresent()) {
                    BigDecimal ownedAssets = balance.get().getAvailable();
                    if (amount.compareTo(ownedAssets) > 0) {
                        final String errorMessage = "Not enough assets (amount : " + amount + " " + currencyPair.getQuoteCurrency() + " - owned assets : " + ownedAssets + " " + currencyPair.getBaseCurrency();
                        return new OrderCreationResultDTO(errorMessage, new Exception(errorMessage));
                    }
                } else {
                    return new OrderCreationResultDTO("No assets for " + currencyPair.getBaseCurrency(), new Exception("No assets for " + currencyPair.getBaseCurrency()));
                }
            }

            // We update the balances of the account with the values of the trade.
            if (orderTypeDTO.equals(BID)) {
                userService.addToBalance(currencyPair.getBaseCurrency(), amount);
                userService.addToBalance(currencyPair.getQuoteCurrency(), amount.multiply(t.get().getLast()).multiply(new BigDecimal("-1")));
            } else {
                userService.addToBalance(currencyPair.getBaseCurrency(), amount.multiply(new BigDecimal("-1")));
                userService.addToBalance(currencyPair.getQuoteCurrency(), amount.multiply(t.get().getLast()));
            }

            // We create and send the order.
            final String orderId = getNextOrderNumber();
            final OrderDTO order = OrderDTO.builder()
                    .orderId(orderId)
                    .type(orderTypeDTO)
                    .strategy(strategy)
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .averagePrice(CurrencyAmountDTO.builder()
                            .value(t.get().getLast())
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .status(FILLED)
                    .cumulativeAmount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .timestamp(t.get().getTimestamp())
                    .build();
            localOrders.put(orderId, order);

            // We create and send the trade.
            final String tradeId = getNextTradeNumber();
            final TradeDTO trade = TradeDTO.builder()
                    .tradeId(tradeId)
                    .type(orderTypeDTO)
                    .orderId(orderId)
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .price(CurrencyAmountDTO.builder()
                            .value(t.get().getLast())
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .fee(CurrencyAmountDTO.ZERO)
                    .timestamp(t.get().getTimestamp())
                    .build();
            localTrades.put(tradeId, trade);

            localOrdersCreationDates.put(orderId, ZonedDateTime.now());
            localTradesCreationDates.put(tradeId, ZonedDateTime.now());

            // We create and returns the result.
            return new OrderCreationResultDTO(order);
        } else {
            return new OrderCreationResultDTO("Ticker not found", new Exception("Ticker not found"));
        }
    }

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.createBuyMarketOrder(..)) && args(strategy, currencyPair, amount)", argNames = "pjp,strategy,currencyPair,amount")
    public final OrderCreationResultDTO createBuyMarketOrder(final ProceedingJoinPoint pjp,
                                                             final StrategyDTO strategy,
                                                             final CurrencyPairDTO currencyPair,
                                                             final BigDecimal amount) {
        return createMarketOrder(strategy, BID, currencyPair, amount);
    }

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.createSellMarketOrder(..)) && args(strategy, currencyPair, amount)", argNames = "pjp, strategy, currencyPair, amount")
    public final OrderCreationResultDTO createSellMarketOrder(final ProceedingJoinPoint pjp,
                                                        final StrategyDTO strategy,
                                                        final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount) {
        return createMarketOrder(strategy, ASK, currencyPair, amount);
    }

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.createBuyLimitOrder(..)) && args(strategy, currencyPair, amount, limitPrice)", argNames = "pjp, strategy, currencyPair, amount, limitPrice")
    public final OrderCreationResultDTO createBuyLimitOrder(final ProceedingJoinPoint pjp,
                                                      final StrategyDTO strategy,
                                                      final CurrencyPairDTO currencyPair,
                                                      final BigDecimal amount,
                                                      final BigDecimal limitPrice) {
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.createSellLimitOrder(..)) && args(strategy, currencyPair, amount, limitPrice))", argNames = "pjp, strategy, currencyPair, amount, limitPrice")
    public final OrderCreationResultDTO createSellLimitOrder(final ProceedingJoinPoint pjp,
                                                       final StrategyDTO strategy,
                                                       final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final BigDecimal limitPrice) {
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Around(value = "execution(* tech.cassandre.trading.bot.service.TradeService.cancelOrder(..)) && args(orderId))", argNames = "pjp, orderId")
    public final boolean cancelOrder(final ProceedingJoinPoint pjp, final String orderId) {
        return localOrders.remove(orderId) != null;
    }

    @Around("execution(* tech.cassandre.trading.bot.service.TradeService.getOrders())")
    public final Set<OrderDTO> getOrders(final ProceedingJoinPoint pjp) {
        return localOrders.values()
                .stream()
                .filter(orderDTO -> ZonedDateTime.now().isAfter(localOrdersCreationDates.get(orderDTO.getOrderId()).plus(Duration.ofMillis(DELAY_BEFORE_ORDER_ARRIVES))))
                .collect(Collectors.toSet());
    }

    @Around("execution(* tech.cassandre.trading.bot.service.TradeService.getTrades())")
    public final Set<TradeDTO> getTrades(final ProceedingJoinPoint pjp) {
        return localTrades.values()
                .stream()
                .filter(tradeDTO -> ZonedDateTime.now().isAfter(localTradesCreationDates.get(tradeDTO.getTradeId()).plus(Duration.ofMillis(DELAY_BEFORE_TRADE_ARRIVES))))
                .collect(Collectors.toSet());
    }

    /**
     * Returns next order number.
     *
     * @return next order number
     */
    private String getNextOrderNumber() {
        return DRY_ORDER_PREFIX.concat(String.format("%09d", orderCounter.getAndIncrement()));
    }

    /**
     * Returns next trade number.
     *
     * @return next trade number
     */
    private String getNextTradeNumber() {
        return DRY_TRADE_PREFIX.concat(String.format("%09d", tradeCounter.getAndIncrement()));
    }

}
