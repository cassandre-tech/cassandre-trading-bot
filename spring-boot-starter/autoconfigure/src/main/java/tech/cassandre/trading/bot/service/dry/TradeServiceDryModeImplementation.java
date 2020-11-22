package tech.cassandre.trading.bot.service.dry;

import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.FILLED;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

/**
 * Trade service in dry mode.
 */
public class TradeServiceDryModeImplementation extends BaseService implements TradeService {

    /** Waiting time before sending orders and trades to flux. */
    private static final long WAITING_TIME = 500L;

    /** Trade account ID. */
    public static final String TRADE_ACCOUNT_ID = "trade";

    /** Order counter. */
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    /** Trade counter. */
    private final AtomicInteger tradeCounter = new AtomicInteger(1);

    /** Order flux. */
    private OrderFlux orderFlux;

    /** Trade flux. */
    private TradeFlux tradeFlux;

    /** Last received tickers. */
    private final Map<CurrencyPairDTO, TickerDTO> lastTickers = new LinkedHashMap<>();

    /** Orders. */
    private final Map<String, OrderDTO> orders = new LinkedHashMap<>();

    /** User service - dry mode. */
    private final UserServiceDryModeImplementation userService;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Order repository. */
    private final OrderRepository orderRepository;

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
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order creation result
     */
    private OrderCreationResultDTO createMarketOrder(final OrderTypeDTO orderTypeDTO, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
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
                    .id(orderId)
                    .currencyPair(currencyPair)
                    .type(orderTypeDTO)
                    .status(FILLED)
                    .averagePrice(t.getLast())
                    .originalAmount(amount)
                    .fee(BigDecimal.ZERO)
                    .timestamp(ZonedDateTime.now())
                    .create();

            // We crate and send the trade.
            final String tradeId = getNextTradeNumber();
            final TradeDTO trade = TradeDTO.builder()
                    .id(tradeId)
                    .orderId(orderId)
                    .currencyPair(currencyPair)
                    .type(orderTypeDTO)
                    .originalAmount(amount)
                    .price(t.getLast())
                    .timestamp(ZonedDateTime.now())
                    .feeAmount(BigDecimal.ZERO)
                    .feeCurrency(currencyPair.getBaseCurrency())
                    .create();

            // Sending the results after the return.
            Executors.newFixedThreadPool(1).submit(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(WAITING_TIME);
                } catch (InterruptedException e) {
                    getLogger().debug("InterruptedException");
                }
                orderFlux.emitValue(order);
                orders.put(orderId, order);
                tradeFlux.emitValue(trade);
            });

            // We update the balances of the account because of the trade.
            if (orderTypeDTO.equals(BID)) {
                userService.addToBalance(currencyPair.getBaseCurrency(), amount);
                userService.addToBalance(currencyPair.getQuoteCurrency(), amount.multiply(t.getLast()).multiply(new BigDecimal("-1")));
            } else {
                userService.addToBalance(currencyPair.getBaseCurrency(), amount.multiply(new BigDecimal("-1")));
                userService.addToBalance(currencyPair.getQuoteCurrency(), amount.multiply(t.getLast()));
            }

            // We create the result.
            return new OrderCreationResultDTO(orderId);
        } else {
            return new OrderCreationResultDTO("Ticker not found", new Exception("Ticker not found"));
        }
    }

    @Override
    public final OrderCreationResultDTO createBuyMarketOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(BID, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createSellMarketOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(OrderTypeDTO.ASK, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createBuyLimitOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Override
    public final OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Override
    public final Optional<OrderDTO> getOpenOrderByOrderId(final String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public final Set<OrderDTO> getOpenOrders() {
        return new LinkedHashSet<>(orders.values());
    }

    @Override
    public final Set<OrderDTO> getOrdersFromDatabase() {
        return orderRepository.findByOrderByTimestampAsc()
                .stream()
                .map(order -> getMapper().mapToOrderDTO(order))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public final boolean cancelOrder(final String orderId) {
        return orders.remove(orderId) != null;
    }

    @Override
    public final Set<TradeDTO> getTrades() {
        return tradeRepository.findByOrderByTimestampAsc()
                .stream()
                .map(trade -> getMapper().mapToTradeDTO(trade))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public final Set<TradeDTO> getTradesFromDatabase() {
        return tradeRepository.findByOrderByTimestampAsc()
                .stream()
                .map(trade -> getMapper().mapToTradeDTO(trade))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Returns next order number.
     *
     * @return next order number
     */
    private String getNextOrderNumber() {
        return "DRY_ORDER_".concat(String.format("%09d", orderCounter.getAndIncrement()));
    }

    /**
     * Returns next trade number.
     *
     * @return next trade number
     */
    private String getNextTradeNumber() {
        return "DRY_TRADE_".concat(String.format("%09d", tradeCounter.getAndIncrement()));
    }

    /**
     * Method called by streams at every ticker update.
     *
     * @param ticker ticker
     */
    public void tickerUpdate(final TickerDTO ticker) {
        lastTickers.put(ticker.getCurrencyPair(), ticker);
    }

}
