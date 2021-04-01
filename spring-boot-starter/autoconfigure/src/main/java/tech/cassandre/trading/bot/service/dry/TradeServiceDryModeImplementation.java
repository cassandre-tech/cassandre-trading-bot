package tech.cassandre.trading.bot.service.dry;

import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Order;
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
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.FILLED;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

/**
 * Trade service (dry mode implementation).
 */
public class TradeServiceDryModeImplementation extends BaseService implements TradeService {

    /** Waiting time before sending orders and trades to flux. */
    private static final long WAITING_TIME = 500L;

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

    /** Last received tickers. */
    private final Map<CurrencyPairDTO, TickerDTO> lastTickers = new LinkedHashMap<>();

    /** Order flux. */
    private OrderFlux orderFlux;

    /** Trade flux. */
    private TradeFlux tradeFlux;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** User service - dry mode. */
    private final UserServiceDryModeImplementation userService;

    /**
     * Constructor.
     *
     * @param newUserService     user service
     * @param newTradeRepository trade repository
     * @param newOrderRepository order repository
     */
    public TradeServiceDryModeImplementation(final UserServiceDryModeImplementation newUserService,
                                             final TradeRepository newTradeRepository,
                                             final OrderRepository newOrderRepository) {
        this.userService = newUserService;
        this.tradeRepository = newTradeRepository;
        this.orderRepository = newOrderRepository;
    }

    /**
     * Set dependencies.
     *
     * @param newOrderFlux order flux
     * @param newTradeFlux trade flux
     */
    public void setDependencies(final OrderFlux newOrderFlux,
                                final TradeFlux newTradeFlux) {
        this.orderFlux = newOrderFlux;
        this.tradeFlux = newTradeFlux;
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
        // We retrieve the last pricing from tickers.
        TickerDTO t = lastTickers.get(currencyPair);

        // We create the order.
        if (t != null) {
            // If we don't have enough assets, we can't buy.
            // Example :
            // ETH/BTC quote currency => BTC.
            // ETH/BTC base currency => ETH.

            // We check that we have a user and a trade account.
            final Optional<UserDTO> user = userService.getUser();
            final AccountDTO account;
            if (user.isPresent()) {
                account = userService.getUser().get().getAccounts().get(TRADE_ACCOUNT_ID);
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
                    BigDecimal cost = t.getLast().multiply(amount);
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
                            .value(t.getLast())
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .status(FILLED)
                    .cumulativeAmount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .timestamp(t.getTimestamp())
                    .build();

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
                            .value(t.getLast())
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .fee(CurrencyAmountDTO.ZERO)
                    .timestamp(t.getTimestamp())
                    .build();

            // Sending the results after the method returns the result.
            Executors.newFixedThreadPool(1).submit(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(WAITING_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                orderFlux.emitValue(order);
                try {
                    TimeUnit.MILLISECONDS.sleep(WAITING_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                tradeFlux.emitValue(trade);
            });

            // We update the balances of the account with the values of the trade.
            if (orderTypeDTO.equals(BID)) {
                userService.addToBalance(currencyPair.getBaseCurrency(), amount);
                userService.addToBalance(currencyPair.getQuoteCurrency(), amount.multiply(t.getLast()).multiply(new BigDecimal("-1")));
            } else {
                userService.addToBalance(currencyPair.getBaseCurrency(), amount.multiply(new BigDecimal("-1")));
                userService.addToBalance(currencyPair.getQuoteCurrency(), amount.multiply(t.getLast()));
            }

            // We create and returns the result.
            return new OrderCreationResultDTO(order);
        } else {
            return new OrderCreationResultDTO("Ticker not found", new Exception("Ticker not found"));
        }
    }

    @Override
    public final OrderCreationResultDTO createBuyMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(strategy, BID, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createSellMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(strategy, ASK, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createBuyLimitOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Override
    public final OrderCreationResultDTO createSellLimitOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Override
    public final Set<OrderDTO> getOpenOrders() {
        return getOrders();
    }

    @Override
    public final boolean cancelOrder(final String orderId) {
        final Optional<Order> order = orderRepository.findByOrderId(orderId);
        if (order.isPresent()) {
            orderRepository.delete(order.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final Set<OrderDTO> getOrders() {
        return orderRepository.findByOrderByTimestampAsc()
                .stream()
                .map(orderMapper::mapToOrderDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public final Set<TradeDTO> getTrades(final Set<CurrencyPairDTO> currencyPairs) {
        return tradeRepository.findByOrderByTimestampAsc()
                .stream()
                .map(tradeMapper::mapToTradeDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Method called by streams at every ticker update.
     *
     * @param ticker ticker
     */
    public void tickerUpdate(final TickerDTO ticker) {
        lastTickers.put(ticker.getCurrencyPair(), ticker);
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
