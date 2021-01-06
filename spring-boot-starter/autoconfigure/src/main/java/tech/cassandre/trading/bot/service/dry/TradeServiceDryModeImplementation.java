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
import tech.cassandre.trading.bot.util.base.BaseService;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

    /** Hashmap used to store orders created locally. */
    private final HashMap<String, OrderDTO> localOrders = new HashMap<>();

    /** Hashmap used to store trades created locally. */
    private final HashMap<String, TradeDTO> localTrades = new HashMap<>();

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
                    .orderId(orderId)
                    .currencyPair(currencyPair)
                    .type(orderTypeDTO)
                    .status(FILLED)
                    .averagePrice(CurrencyAmountDTO.builder()
                            .value(t.getLast())
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .timestamp(ZonedDateTime.now())
                    .build();

            // We create and send the trade.
            final String tradeId = getNextTradeNumber();
            final TradeDTO trade = TradeDTO.builder()
                    .tradeId(tradeId)
                    .orderId(orderId)
                    .currencyPair(currencyPair)
                    .type(orderTypeDTO)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .price(CurrencyAmountDTO.builder()
                            .value(t.getLast())
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .timestamp(ZonedDateTime.now())
                    .fee(CurrencyAmountDTO.ZERO)
                    .build();
            // Sending the results after the method returns the result.
            Executors.newFixedThreadPool(1).submit(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(WAITING_TIME);
                } catch (InterruptedException e) {
                    logger.debug("InterruptedException");
                }
                localOrders.put(orderId, order);
                orderFlux.emitValue(order);
                try {
                    TimeUnit.MILLISECONDS.sleep(WAITING_TIME);
                } catch (InterruptedException e) {
                    logger.debug("InterruptedException");
                }

                localTrades.put(tradeId, trade);
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
            return new OrderCreationResultDTO(order);
        } else {
            return new OrderCreationResultDTO("Ticker not found", new Exception("Ticker not found"));
        }
    }

    @Override
    public final OrderCreationResultDTO createBuyMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(BID, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createSellMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(OrderTypeDTO.ASK, currencyPair, amount);
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
    public final Set<OrderDTO> getOrders() {
        return orderRepository.findByOrderByTimestampAsc()
                .stream()
                .map(orderMapper::mapToOrderDTO)
                .collect(Collectors.toSet());
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
    public final Set<TradeDTO> getTrades() {
        final Map<String, TradeDTO> results = tradeRepository.findByOrderByTimestampAsc()
                .stream()
                .map(tradeMapper::mapToTradeDTO)
                .collect(Collectors.toMap(TradeDTO::getTradeId, trade -> trade));
        localTrades.values()
                .stream()
                .filter(trade -> !results.containsKey(trade.getTradeId()))
                .forEach(trade -> results.put(trade.getTradeId(), trade));
        return new HashSet<>(results.values());
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
