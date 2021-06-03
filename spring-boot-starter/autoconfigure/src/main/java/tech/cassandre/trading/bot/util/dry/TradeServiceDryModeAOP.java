package tech.cassandre.trading.bot.util.dry;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.LimitOrder;
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
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.knowm.xchange.dto.Order.OrderType.ASK;
import static org.knowm.xchange.dto.Order.OrderType.BID;
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

    /** Trade account ID. */
    private static final String TRADE_ACCOUNT_ID = "trade";

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Order counter. */
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    /** User service - dry mode. */
    private final UserServiceDryModeAOP userService;

    @Around(value = "execution(* org.knowm.xchange.service.trade.TradeService.placeMarketOrder(..)) && args(marketOrder)", argNames = "pjp, marketOrder")
    public final String placeMarketOrder(final ProceedingJoinPoint pjp, final MarketOrder marketOrder) throws IOException {
        // We get the currency pair utils.
        final CurrencyPair currencyPair = (CurrencyPair) marketOrder.getInstrument();
        final Currency base = ((CurrencyPair) (marketOrder.getInstrument())).base;
        final Currency counter = ((CurrencyPair) (marketOrder.getInstrument())).counter;

        // We retrieve the ticker received by the strategy.
        // TODO Find a better way to get the last ticker.
        final Optional<TickerDTO> t = applicationContext.getBeansWithAnnotation(CassandreStrategy.class)
                .values()
                .stream()
                .map(cassandreStrategy -> ((GenericCassandreStrategy) cassandreStrategy))
                .map(cassandreStrategy -> cassandreStrategy.getLastTickerByCurrencyPair(currencyMapper.mapToCurrencyPairDTO(currencyPair)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        // We create the order.
        if (t.isPresent()) {
            // If we don't have enough assets, we can't buy.
            // Example :
            // ETH/BTC quote currency => BTC.
            // ETH/BTC base currency => ETH.

            // We check that we have the trade account.
            final AccountInfo accountInfo = userService.getAccountInfo();
            final Wallet tradeWallet = accountInfo.getWallet(TRADE_ACCOUNT_ID);
            if (tradeWallet == null) {
                throw new IOException("Trade wallet was not found : " + TRADE_ACCOUNT_ID);
            }

            // We check if we have enough assets to buy/sell.
            if (marketOrder.getType().equals(BID)) {
                // Buying order - we buy ETH from BTC.
                // We are buying the following amount : ticker last price * amount
                Balance balance = tradeWallet.getBalance(counter);
                if (balance != null) {
                    BigDecimal ownedAssets = balance.getAvailable();
                    BigDecimal cost = t.get().getLast().multiply(marketOrder.getOriginalAmount());
                    if (cost.compareTo(ownedAssets) > 0) {
                        final String errorMessage = "Not enough assets (costs : " + cost + " " + counter + " - owned assets : " + ownedAssets + " " + counter + ")";
                        throw new IOException(errorMessage);
                    }
                } else {
                    throw new IOException("No assets for " + counter);
                }
            } else {
                // Selling order - we sell ETH for BTC.
                // We are selling the amount
                Balance balance = tradeWallet.getBalance(base);
                if (balance != null) {
                    BigDecimal ownedAssets = balance.getAvailable();
                    if (marketOrder.getOriginalAmount().compareTo(ownedAssets) > 0) {
                        final String errorMessage = "Not enough assets (amount : " + marketOrder.getOriginalAmount() + " " + counter + " - owned assets : " + ownedAssets + " " + base;
                        throw new IOException(errorMessage);
                    }
                } else {
                    throw new IOException("No assets for " + base);
                }
            }

            // We update the balances of the account with the values of the trade.
            if (marketOrder.getType().equals(BID)) {
                userService.addToBalance(base, marketOrder.getOriginalAmount());
                userService.addToBalance(counter, marketOrder.getOriginalAmount().multiply(t.get().getLast()).multiply(new BigDecimal("-1")));
            } else {
                userService.addToBalance(base, marketOrder.getOriginalAmount().multiply(new BigDecimal("-1")));
                userService.addToBalance(counter, marketOrder.getOriginalAmount().multiply(t.get().getLast()));
            }

            // We create and returns the result.
            return DRY_ORDER_PREFIX.concat(String.format("%09d", orderCounter.getAndIncrement()));
        } else {
            throw new RuntimeException("Ticker not found");
        }
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
                .stream() // TODO Add a filter to only select the trade that are now already in database.
                .map(orderMapper::mapToOrderDTO)
                .forEach(order -> {
                    org.knowm.xchange.dto.Order.OrderType type; // TODO Optimise with mapper.
                    if (order.getType().equals(OrderTypeDTO.BID)) {
                        type = BID;
                    } else {
                        type = ASK;
                    }

                    trades.add(UserTrade.builder()
                            .id(order.getOrderId().replace(DRY_ORDER_PREFIX, DRY_TRADE_PREFIX))
                            .type(type)
                            .orderId(order.getOrderId())
                            .currencyPair(currencyMapper.mapToCurrencyPair(order.getCurrencyPair()))
                            .originalAmount(order.getAmount().getValue())
                            .price(order.getMarketPrice().getValue())
                            .feeAmount(BigDecimal.ZERO)
                            .timestamp(Timestamp.valueOf(order.getTimestamp().toLocalDateTime()))
                            .build());
                });
        return new UserTrades(trades, SortByTimestamp);
    }

}
