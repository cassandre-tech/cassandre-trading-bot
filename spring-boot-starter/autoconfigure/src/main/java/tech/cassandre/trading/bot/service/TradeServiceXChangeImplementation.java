package tech.cassandre.trading.bot.service;

import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.springframework.beans.factory.annotation.Value;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.service.BaseService;
import tech.cassandre.trading.bot.util.xchange.CancelOrderParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.math.RoundingMode.FLOOR;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

/**
 * Trade service - XChange implementation of {@link TradeService}.
 */
public class TradeServiceXChangeImplementation extends BaseService implements TradeService {

    /** Size of the random string generated. */
    private static final int GENERATED_ORDER_SIZE = 32;

    /** Okex broker id. */
    private static final String OKEX_BROKER_ID = "3fba96c2a09c42BC";

    /** Driver class name. */
    @Value("${cassandre.trading.bot.exchange.driver-class-name}")
    private String driverClassName;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** XChange service. */
    private final org.knowm.xchange.service.trade.TradeService tradeService;

    /**
     * Constructor.
     *
     * @param rate               rate in ms
     * @param newOrderRepository order repository
     * @param newTradeService    market data service
     */
    public TradeServiceXChangeImplementation(final long rate,
                                             final OrderRepository newOrderRepository,
                                             final org.knowm.xchange.service.trade.TradeService newTradeService) {
        super(rate);
        this.orderRepository = newOrderRepository;
        this.tradeService = newTradeService;
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createBuyMarketOrder(@NonNull final CassandreStrategyInterface strategy,
                                                       @NonNull final CurrencyPairDTO currencyPair,
                                                       @NonNull final BigDecimal amount) {
        return createMarketOrder(strategy, BID, currencyPair, amount);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createSellMarketOrder(@NonNull final CassandreStrategyInterface strategy,
                                                        @NonNull final CurrencyPairDTO currencyPair,
                                                        @NonNull final BigDecimal amount) {
        return createMarketOrder(strategy, ASK, currencyPair, amount);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createBuyLimitOrder(@NonNull final CassandreStrategyInterface strategy,
                                                      @NonNull final CurrencyPairDTO currencyPair,
                                                      @NonNull final BigDecimal amount,
                                                      @NonNull final BigDecimal limitPrice) {
        return createLimitOrder(strategy, BID, currencyPair, amount, limitPrice);
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public OrderCreationResultDTO createSellLimitOrder(@NonNull final CassandreStrategyInterface strategy,
                                                       @NonNull final CurrencyPairDTO currencyPair,
                                                       @NonNull final BigDecimal amount,
                                                       @NonNull final BigDecimal limitPrice) {
        return createLimitOrder(strategy, ASK, currencyPair, amount, limitPrice);
    }

    /**
     * Creates market order.
     *
     * @param strategy     strategy
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order creation result
     */
    private OrderCreationResultDTO createMarketOrder(final CassandreStrategyInterface strategy,
                                                     final OrderTypeDTO orderTypeDTO,
                                                     final CurrencyPairDTO currencyPair,
                                                     final BigDecimal amount) {
        try {
            // Making the order.
            MarketOrder m = new MarketOrder(UTIL_MAPPER.mapToOrderType(orderTypeDTO),
                    amount.setScale(currencyPair.getBaseCurrencyPrecision(), FLOOR),
                    CURRENCY_MAPPER.mapToCurrencyPair(currencyPair),
                    getGeneratedOrderId(),
                    null);
            logger.debug("Sending market order: {} - {} - {}",
                    orderTypeDTO,
                    currencyPair,
                    amount.setScale(currencyPair.getBaseCurrencyPrecision(), FLOOR));

            // We create the order in database with the PENDING_NEW status.
            OrderDTO order = OrderDTO.builder()
                    .orderId(tradeService.placeMarketOrder(m))
                    .type(orderTypeDTO)
                    .strategy(strategy.getConfiguration().getStrategyDTO())
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .cumulativeAmount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .averagePrice(CurrencyAmountDTO.builder()
                            .value(strategy.getLastPriceForCurrencyPair(currencyPair))
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .marketPrice(CurrencyAmountDTO.builder()
                            .value(strategy.getLastPriceForCurrencyPair(currencyPair))
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .status(PENDING_NEW)
                    .timestamp(ZonedDateTime.now())
                    .build();

            // We save the order.
            Optional<Order> savedOrder = orderRepository.findByOrderId(order.getOrderId());
            if (savedOrder.isEmpty()) {
                savedOrder = Optional.of(orderRepository.save(ORDER_MAPPER.mapToOrder(order)));
            }
            final OrderCreationResultDTO result = new OrderCreationResultDTO(ORDER_MAPPER.mapToOrderDTO(savedOrder.get()));
            logger.debug("Order created: {}", result);
            return result;
        } catch (Exception e) {
            final String errorMessage = "Error calling createMarketOrder for " + amount + " " + currencyPair + ": " + e.getMessage();
            e.printStackTrace();
            logger.error(errorMessage);
            return new OrderCreationResultDTO(errorMessage, e);
        }
    }

    /**
     * Creates limit order.
     *
     * @param strategy     strategy
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   In a BID this is the highest acceptable price, in an ASK this is the lowest acceptable price
     * @return order creation result
     */
    private OrderCreationResultDTO createLimitOrder(final CassandreStrategyInterface strategy,
                                                    final OrderTypeDTO orderTypeDTO,
                                                    final CurrencyPairDTO currencyPair,
                                                    final BigDecimal amount,
                                                    final BigDecimal limitPrice) {
        try {
            // Making the order.
            LimitOrder l = new LimitOrder(UTIL_MAPPER.mapToOrderType(orderTypeDTO),
                    amount.setScale(currencyPair.getBaseCurrencyPrecision(), FLOOR),
                    CURRENCY_MAPPER.mapToCurrencyPair(currencyPair),
                    getGeneratedOrderId(),
                    null,
                    limitPrice);
            logger.debug("Sending limit order: {} - {} - {}",
                    orderTypeDTO,
                    currencyPair,
                    amount.setScale(currencyPair.getBaseCurrencyPrecision(), FLOOR));

            // We create the order in database with the PENDING_NEW status.
            OrderDTO order = OrderDTO.builder()
                    .orderId(tradeService.placeLimitOrder(l))
                    .type(orderTypeDTO)
                    .strategy(strategy.getConfiguration().getStrategyDTO())
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .cumulativeAmount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .averagePrice(CurrencyAmountDTO.builder()
                            .value(strategy.getLastPriceForCurrencyPair(currencyPair))
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .limitPrice(CurrencyAmountDTO.builder()
                            .value(limitPrice)
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .marketPrice(CurrencyAmountDTO.builder()
                            .value(strategy.getLastPriceForCurrencyPair(currencyPair))
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .status(PENDING_NEW)
                    .timestamp(ZonedDateTime.now())
                    .build();

            // We save the order.
            Optional<Order> savedOrder = orderRepository.findByOrderId(order.getOrderId());
            if (savedOrder.isEmpty()) {
                savedOrder = Optional.of(orderRepository.save(ORDER_MAPPER.mapToOrder(order)));
            }
            final OrderCreationResultDTO result = new OrderCreationResultDTO(ORDER_MAPPER.mapToOrderDTO(savedOrder.get()));
            logger.debug("Order creation result: {}", result);
            return result;
        } catch (Exception e) {
            final String errorMessage = "Error calling createLimitOrder for " + amount + " " + currencyPair + ": " + e.getMessage();
            e.printStackTrace();
            logger.error(errorMessage);
            return new OrderCreationResultDTO(errorMessage, e);
        }
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean cancelOrder(final long orderUid) {
        logger.debug("Canceling order with uid {}", orderUid);
        try {
            // We retrieve the order information.
            final Optional<Order> order = orderRepository.findById(orderUid);
            if (order.isPresent()) {
                OrderDTO orderDTO = ORDER_MAPPER.mapToOrderDTO(order.get());

                // Using a special object to specify which order to cancel.
                final CancelOrderParams cancelOrderParams = new CancelOrderParams(
                        order.get().getOrderId(),
                        CURRENCY_MAPPER.mapToCurrencyPair(orderDTO.getCurrencyPair()),
                        UTIL_MAPPER.mapToOrderType(orderDTO.getType()));
                logger.debug("Canceling order {}", order.get().getOrderId());
                return tradeService.cancelOrder(cancelOrderParams);
            } else {
                logger.error("Error canceling order with uid {}: order not found in database", orderUid);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error canceling order {}: {}", orderUid, e.getMessage());
            return false;
        }
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Set<OrderDTO> getOrders() {
        logger.debug("Getting orders from exchange");
        try {
            // We check if we have some local orders to push.
            // When we create them in local, they have the PENDING_NEW status.
            // We retrieve them all to return them in localOrders.
            // And for each, we update their status to NEW.
            final Set<OrderDTO> localOrders = orderRepository.findByStatus(PENDING_NEW)
                    .stream()
                    .map(ORDER_MAPPER::mapToOrderDTO)
                    .sorted(Comparator.comparing(OrderDTO::getTimestamp))
                    .peek(orderDTO -> logger.debug("Local order retrieved: {}", orderDTO))
                    .peek(orderDTO -> orderDTO.updateStatus(NEW))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // If we have local orders to push, we return them.
            if (!localOrders.isEmpty()) {
                return localOrders;
            } else {
                try {
                    // Consume a token from the token bucket.
                    // If a token is not available this method will block until the refill adds one to the bucket.
                    bucket.asBlocking().consume(1);
                    return tradeService.getOpenOrders()
                            .getOpenOrders()
                            .stream()
                            .map(ORDER_MAPPER::mapToOrderDTO)
                            .peek(orderDTO -> logger.debug("Remote order retrieved: {}", orderDTO))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                } catch (NotAvailableFromExchangeException e) {
                    // If the classical call to getOpenOrders() is not implemented, we use the specific parameters that asks for currency pair.
                    Set<OrderDTO> orders = new LinkedHashSet<>();
                    orderRepository.findAll()
                            .stream()
                            .map(ORDER_MAPPER::mapToOrderDTO)
                            // We only ask for currency pairs of the non fulfilled orders.
                            .filter(orderDTO -> !orderDTO.isFulfilled())
                            .map(OrderDTO::getCurrencyPair)
                            .distinct()
                            .forEach(currencyPairDTO -> {
                                try {
                                    // Consume a token from the token bucket.
                                    // If a token is not available this method will block until the refill adds one to the bucket.
                                    bucket.asBlocking().consume(1);
                                    orders.addAll(tradeService.getOpenOrders(new DefaultOpenOrdersParamCurrencyPair(CURRENCY_MAPPER.mapToCurrencyPair(currencyPairDTO)))
                                            .getOpenOrders()
                                            .stream()
                                            .map(ORDER_MAPPER::mapToOrderDTO)
                                            .peek(orderDTO -> logger.debug("Remote order retrieved: {}", orderDTO))
                                            .collect(Collectors.toCollection(LinkedHashSet::new)));
                                } catch (IOException | InterruptedException specificOrderException) {
                                    logger.error("Error retrieving orders: {}", specificOrderException.getMessage());
                                }
                            });
                    return orders;
                } catch (IOException e) {
                    logger.error("Error retrieving orders: {}", e.getMessage());
                    return Collections.emptySet();
                }
            }
        } catch (InterruptedException e) {
            return Collections.emptySet();
        }
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Set<TradeDTO> getTrades() {
        logger.debug("Getting trades from exchange");

        // Query trades from the last 24 jours (24 hours is the maximum because of Binance limitations).
        TradeHistoryParamsAll params = new TradeHistoryParamsAll();
        Date now = new Date();
        Date startDate = DateUtils.addDays(now, -1);
        params.setStartTime(startDate);
        params.setEndTime(now);

        // We only ask for trades with currency pairs that was used in the previous orders we made.
        // And we only choose the orders that are not fulfilled.
        final LinkedHashSet<CurrencyPairDTO> currencyPairs = orderRepository.findByOrderByTimestampAsc()
                .stream()
                .map(ORDER_MAPPER::mapToOrderDTO)
                .filter(orderDTO -> !orderDTO.isFulfilled())
                .map(OrderDTO::getCurrencyPair)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // We set currency pairs on each param (required for exchanges like Gemini or Binance).
        Set<TradeDTO> results = new LinkedHashSet<>();
        if (!currencyPairs.isEmpty()) {
            currencyPairs.forEach(pair -> {
                params.setCurrencyPair(CURRENCY_MAPPER.mapToCurrencyPair(pair));
                try {
                    // Consume a token from the token bucket.
                    // If a token is not available this method will block until the refill adds one to the bucket.
                    bucket.asBlocking().consume(1);
                    results.addAll(
                            tradeService.getTradeHistory(params)
                                    .getUserTrades()
                                    .stream()
                                    .map(TRADE_MAPPER::mapToTradeDTO)
                                    .sorted(Comparator.comparing(TradeDTO::getTimestamp))
                                    .collect(Collectors.toCollection(LinkedHashSet::new))
                    );
                } catch (IOException e) {
                    logger.error("Error retrieving trades: {}", e.getMessage());
                } catch (InterruptedException e) {
                    logger.error("InterruptedException: {}", e.getMessage());
                }
            });
        }
        logger.debug("{} trade(s) found", results.size());
        return results;
    }

    /**
     * Returns a local generated order id.
     *
     * @return generated order id
     */
    private String getGeneratedOrderId() {
        if (driverClassName.toLowerCase(Locale.ROOT).contains("okex")) {
            // If we are on Okex, we use Cassandre broker id to get a reward.
            return OKEX_BROKER_ID + RandomStringUtils.random(GENERATED_ORDER_SIZE - OKEX_BROKER_ID.length(), true, true);
        } else {
            return null;
        }
    }

}
