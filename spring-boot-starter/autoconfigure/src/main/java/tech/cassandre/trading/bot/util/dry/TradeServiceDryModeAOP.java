package tech.cassandre.trading.bot.util.dry;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.math.BigDecimal.ZERO;
import static org.knowm.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;

/**
 * AOP for trade service in dry mode.
 */
@Aspect
@Component
@ConditionalOnExpression("${cassandre.trading.bot.exchange.modes.dry:true}")
@RequiredArgsConstructor
public class TradeServiceDryModeAOP extends BaseService {

    /** Dry order prefix. */
    private static final String DRY_ORDER_PREFIX = "DRY_ORDER_";

    /** Dry trade prefix. */
    private static final String DRY_TRADE_PREFIX = "DRY_TRADE_";

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Order repository. */
    private final OrderRepository orderRepository;

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
        userService.addToBalance(currencyMapper.mapToCurrency(currencyPair.getBaseCurrency()), amount);
        userService.addToBalance(currencyMapper.mapToCurrency(currencyPair.getQuoteCurrency()), amount.multiply(ticker.get().getLast()).multiply(new BigDecimal("-1")));

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
        userService.addToBalance(currencyMapper.mapToCurrency(currencyPair.getBaseCurrency()), amount.multiply(new BigDecimal("-1")));
        userService.addToBalance(currencyMapper.mapToCurrency(currencyPair.getQuoteCurrency()), amount.multiply(ticker.get().getLast()));

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
        return new OpenOrders(Collections.emptyList());
    }

    @Around(value = "execution(* org.knowm.xchange.service.trade.TradeService.getTradeHistory(..)) && args(params))", argNames = "pjp, params")
    public final UserTrades getTradeHistory(final ProceedingJoinPoint pjp, final TradeHistoryParams params) {
        List<UserTrade> trades = new LinkedList<>();

        // For every orders in database, we will simulate an equivalent trade to close things.
        orderRepository.findByOrderByTimestampAsc()
                .stream()
                .map(orderMapper::mapToOrderDTO)
                .filter(orderDTO -> !orderDTO.isFulfilled())
                .forEach(order -> trades.add(UserTrade.builder()
                        .id(order.getOrderId().replace(DRY_ORDER_PREFIX, DRY_TRADE_PREFIX))
                        .type(utilMapper.mapToOrderType(order.getType()))
                        .orderId(order.getOrderId())
                        .currencyPair(currencyMapper.mapToCurrencyPair(order.getCurrencyPair()))
                        .originalAmount(order.getAmount().getValue())
                        .price(order.getMarketPrice().getValue())
                        .feeAmount(ZERO)
                        .timestamp(Timestamp.valueOf(order.getTimestamp().toLocalDateTime()))
                        .build()));

        return new UserTrades(trades, SortByTimestamp);
    }

}
