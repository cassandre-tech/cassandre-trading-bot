package tech.cassandre.trading.bot.strategy;

import org.mapstruct.factory.Mappers;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.mapper.CurrencyMapper;
import tech.cassandre.trading.bot.util.mapper.OrderMapper;
import tech.cassandre.trading.bot.util.mapper.PositionMapper;
import tech.cassandre.trading.bot.util.mapper.TradeMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;

/**
 * Generic Cassandre strategy implementation.
 */
@SuppressWarnings("checkstyle:DesignForExtension")
public abstract class GenericCassandreStrategy implements CassandreStrategyInterface {

    /** Currency mapper. */
    protected final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

    /** Order mapper. */
    protected final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    /** Trade mapper. */
    protected final TradeMapper tradeMapper = Mappers.getMapper(TradeMapper.class);

    /** Position mapper. */
    protected final PositionMapper positionMapper = Mappers.getMapper(PositionMapper.class);

    /** Strategy. */
    private StrategyDTO strategy;

    /** Order repository. */
    private OrderRepository orderRepository;

    /** Trade repository. */
    private TradeRepository tradeRepository;

    /** Position repository. */
    private PositionRepository positionRepository;

    /** Exchange service. */
    private ExchangeService exchangeService;

    /** Trade service. */
    private TradeService tradeService;

    /** Position service. */
    private PositionService positionService;

    /** The accounts owned by the user. */
    private final Map<String, AccountDTO> userAccounts = new LinkedHashMap<>();

    /** Positions previous status. */
    private final Map<Long, PositionStatusDTO> previousPositionsStatus = new LinkedHashMap<>();

    /** Last tickers received. */
    private final Map<CurrencyPairDTO, TickerDTO> lastTickers = new LinkedHashMap<>();

    // =================================================================================================================
    // Internal methods to setup dependencies.

    /**
     * Getter strategyDTO.
     *
     * @return strategyDTO
     */
    public final StrategyDTO getStrategyDTO() {
        return strategy;
    }

    /**
     * Getter exchangeService.
     *
     * @return exchangeService
     */
    public final ExchangeService getExchangeService() {
        return exchangeService;
    }

    @Override
    public final void setStrategy(final StrategyDTO newStrategyDTO) {
        this.strategy = newStrategyDTO;
    }

    @Override
    public void initializeAccounts(final Map<String, AccountDTO> newAccounts) {
        userAccounts.putAll(newAccounts);
    }

    @Override
    public final void setPositionRepository(final PositionRepository newPositionRepository) {
        this.positionRepository = newPositionRepository;
    }

    @Override
    public final void setOrderRepository(final OrderRepository newOrderRepository) {
        this.orderRepository = newOrderRepository;
    }

    @Override
    public void setExchangeService(final ExchangeService newExchangeService) {
        this.exchangeService = newExchangeService;
    }

    @Override
    public final void setTradeRepository(final TradeRepository newTradeRepository) {
        this.tradeRepository = newTradeRepository;
    }

    @Override
    public final void setTradeService(final TradeService newTradeService) {
        this.tradeService = newTradeService;
    }

    @Override
    public final void setPositionService(final PositionService newPositionService) {
        this.positionService = newPositionService;
        // We set the previous positions status from database.
        this.positionService.getPositions()
                .stream()
                .filter(p -> p.getStatus() != CLOSED)
                .forEach(p -> previousPositionsStatus.put(p.getId(), p.getStatus()));
    }

    // =================================================================================================================
    // Internal methods for event management.
    @Override
    public void accountsUpdates(final Set<AccountDTO> accounts) {
        // We notify the strategy.
        final Map<String, AccountDTO> accountsUpdates = accounts
                .stream()
                .peek(accountDTO -> userAccounts.put(accountDTO.getAccountId(), accountDTO)) // We store the account values in the strategy.
                .collect(Collectors.toMap(AccountDTO::getAccountId, Function.identity(), (id, value)->id, LinkedHashMap::new));

        // We notify the strategy.
        onAccountsUpdates(accountsUpdates);
    }

    @Override
    public void tickersUpdates(final Set<TickerDTO> tickers) {
        // We only retrieve the tickers requested by the strategy.
        final Map<CurrencyPairDTO, TickerDTO> tickersUpdates = tickers.stream()
                .filter(ticker -> getRequestedCurrencyPairs().contains(ticker.getCurrencyPair()))
                .collect(Collectors.toMap(TickerDTO::getCurrencyPair, Function.identity(), (id, value)->id, LinkedHashMap::new));

        // We update the values of the last tickers that can be found in the strategy.
        tickersUpdates.values().forEach(ticker -> lastTickers.put(ticker.getCurrencyPair(), ticker));

        // We notify the strategy.
        onTickersUpdates(tickersUpdates);
    }

    @Override
    public void ordersUpdates(final Set<OrderDTO> orders) {
        // We only retrieve the orders for the strategy.
        final Map<String, OrderDTO> ordersUpdates = orders.stream()
                .filter(orderDTO -> orderDTO.getStrategy().getId().equals(strategy.getId()))
                .collect(Collectors.toMap(OrderDTO::getOrderId, Function.identity(), (id, value)->id, LinkedHashMap::new));

        // We notify the strategy.
        onOrdersUpdates(ordersUpdates);
    }

    @Override
    public void tradesUpdates(final Set<TradeDTO> trades) {
        // We only retrieve the orders for the strategy.
        final Map<String, TradeDTO> tradesUpdates = trades.stream()
                .filter(tradeDTO -> tradeDTO.getOrder().getStrategy().getId().equals(strategy.getId()))
                .collect(Collectors.toMap(TradeDTO::getTradeId, Function.identity(), (id, value)->id, LinkedHashMap::new));

        // We notify the strategy.
        onTradesUpdates(tradesUpdates);
    }

    @Override
    public void positionsUpdates(final Set<PositionDTO> positions) {
        final Map<Long, PositionDTO> positionsUpdates = new HashMap<>();
        final Map<Long, PositionDTO> positionsStatusUpdates = new HashMap<>();

        positions.forEach(positionDTO -> {
            // For every position update.
            if (positionDTO.getStrategy().getId().equals(strategy.getId())) {
                positionsUpdates.put(positionDTO.getPositionId(), positionDTO);

                // From positionUpdate(), we see if it's also a onPositionStatusUpdate().
                if (previousPositionsStatus.get(positionDTO.getId()) != positionDTO.getStatus()) {
                    previousPositionsStatus.put(positionDTO.getId(), positionDTO.getStatus());
                    if (positionDTO.getStatus() == CLOSED) {
                        // As CLOSED positions cannot change anymore, we don't need to store their previous positions.
                        previousPositionsStatus.remove(positionDTO.getId());
                    }
                    positionsStatusUpdates.put(positionDTO.getPositionId(), positionDTO);
                }
            }
        });

        // We notify the strategy.
        onPositionsUpdates(positionsUpdates);
        onPositionsStatusUpdates(positionsStatusUpdates);
    }

    // =================================================================================================================
    // Related to accounts.

    /**
     * Returns list of accounts.
     *
     * @return accounts
     */
    public final Map<String, AccountDTO> getAccounts() {
        return userAccounts;
    }

    /**
     * Search and return and account by its id.
     *
     * @param accountId account id
     * @return account
     */
    public final Optional<AccountDTO> getAccountByAccountId(final String accountId) {
        if (userAccounts.containsKey(accountId)) {
            return Optional.of(userAccounts.get(accountId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public final Optional<AccountDTO> getTradeAccount() {
        return getTradeAccount(new LinkedHashSet<>(getAccounts().values()));
    }

    /**
     * Getter amountsLockedByPosition.
     *
     * @return amountsLockedByPosition
     */
    public final Map<Long, CurrencyAmountDTO> getAmountsLockedByPosition() {
        return positionService.amountsLockedByPosition();
    }

    /**
     * Returns the amount locked by currency.
     *
     * @param currency currency
     * @return amount
     */
    public final BigDecimal getAmountsLockedByCurrency(final CurrencyDTO currency) {
        return getAmountsLockedByPosition()
                .values()
                .stream()
                .filter(currencyAmount -> currencyAmount.getCurrency().equals(currency))
                .map(CurrencyAmountDTO::getValue)
                .reduce(ZERO, BigDecimal::add);
    }

    // =================================================================================================================
    // Related to tickers.

    /**
     * Return last received tickers.
     *
     * @return ticker
     */
    public final Map<CurrencyPairDTO, TickerDTO> getLastTickers() {
        return lastTickers;
    }

    /**
     * Return the last ticker for a currency pair.
     *
     * @param currencyPair currency pair
     * @return last ticker received
     */
    public final Optional<TickerDTO> getLastTickerByCurrencyPair(final CurrencyPairDTO currencyPair) {
        if (currencyPair == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(lastTickers.get(currencyPair));
        }
    }

    /**
     * Returns the last price received for a currency pair.
     *
     * @param currencyPair currency pair
     * @return last price
     */
    public final BigDecimal getLastPriceForCurrencyPair(final CurrencyPairDTO currencyPair) {
        return getLastTickerByCurrencyPair(currencyPair).map(TickerDTO::getLast).orElse(null);
    }

    // =================================================================================================================
    // Related to orders.

    /**
     * Returns list of orders.
     *
     * @return orders
     */
    public final Map<String, OrderDTO> getOrders() {
        return orderRepository.findByOrderByTimestampAsc()
                .stream()
                .filter(order -> order.getStrategy().getStrategyId().equals(strategy.getStrategyId()))
                .map(orderMapper::mapToOrderDTO)
                .collect(Collectors.toMap(OrderDTO::getOrderId, orderDTO -> orderDTO));
    }

    /**
     * Get an order by its id.
     *
     * @param orderId order id
     * @return order
     */
    public final Optional<OrderDTO> getOrderByOrderId(final String orderId) {
        return getOrders().values()
                .stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst();
    }

    // =================================================================================================================
    // Related to trades.

    /**
     * Returns list of trades.
     *
     * @return trades
     */
    public final Map<String, TradeDTO> getTrades() {
        return tradeRepository.findByOrderByTimestampAsc()
                .stream()
                .filter(trade -> trade.getOrder().getStrategy().getStrategyId().equals(strategy.getStrategyId()))
                .map(tradeMapper::mapToTradeDTO)
                .collect(Collectors.toMap(TradeDTO::getTradeId, tradeDTO -> tradeDTO));
    }

    /**
     * Get a trade by its id.
     *
     * @param tradeId trade id
     * @return trade
     */
    public final Optional<TradeDTO> getTradeByTradeId(final String tradeId) {
        return getTrades().values()
                .stream()
                .filter(trade -> trade.getTradeId().equals(tradeId))
                .findFirst();
    }

    // =================================================================================================================
    // Related to positions.

    /**
     * Returns list of positions.
     *
     * @return positions
     */
    public final Map<Long, PositionDTO> getPositions() {
        return positionRepository.findByOrderById()
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .collect(Collectors.toMap(PositionDTO::getId, positionDTO -> positionDTO));
    }

    /**
     * Get a position by its id.
     *
     * @param positionId position id
     * @return position
     */
    public final Optional<PositionDTO> getPositionByPositionId(final long positionId) {
        return positionRepository.findByPositionId(positionId).map(positionMapper::mapToPositionDTO);
    }

    /**
     * Returns gains of all positions.
     *
     * @return total gains
     */
    public final Map<CurrencyDTO, GainDTO> getGains() {
        return positionService.getGains();
    }

    // =================================================================================================================
    // Methods related to creating of orders & positions.

    /**
     * Creates a buy market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createBuyMarketOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount) {
        return tradeService.createBuyMarketOrder(this, currencyPair, amount);
    }

    /**
     * Creates a sell market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createSellMarketOrder(final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount) {
        return tradeService.createSellMarketOrder(this, currencyPair, amount);
    }

    /**
     * Creates a buy limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createBuyLimitOrder(final CurrencyPairDTO currencyPair,
                                                      final BigDecimal amount,
                                                      final BigDecimal limitPrice) {
        return tradeService.createBuyLimitOrder(this, currencyPair, amount, limitPrice);
    }

    /**
     * Creates a sell limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final BigDecimal limitPrice) {
        return tradeService.createSellLimitOrder(this, currencyPair, amount, limitPrice);
    }

    /**
     * Cancel order.
     *
     * @param id id
     * @return true if cancelled
     */
    boolean cancelOrder(final long id) {
        final Optional<Order> order = orderRepository.findById(id);
        return order.filter(value -> cancelOrder(value.getOrderId())).isPresent();
    }

    /**
     * Cancel order.
     *
     * @param orderId order id
     * @return true if cancelled
     */
    boolean cancelOrder(final String orderId) {
        return tradeService.cancelOrder(orderId);
    }

    /**
     * Creates a long position with its associated rules.
     * Long position is nothing but buying share.
     * If you are bullish (means you think that price of X share will rise) at that time you buy some amount of Share is called taking Long Position in share.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    public PositionCreationResultDTO createLongPosition(final CurrencyPairDTO currencyPair,
                                                        final BigDecimal amount,
                                                        final PositionRulesDTO rules) {
        return positionService.createLongPosition(this, currencyPair, amount, rules);
    }

    /**
     * Creates a short position with its associated rules.
     * Short position is nothing but selling share.
     * If you are bearish (means you think that price of xyz share are going to fall) at that time you sell some amount of share is called taking Short Position in share.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    public PositionCreationResultDTO createShortPosition(final CurrencyPairDTO currencyPair,
                                                         final BigDecimal amount,
                                                         final PositionRulesDTO rules) {
        return positionService.createShortPosition(this, currencyPair, amount, rules);
    }

    /**
     * Update position rules.
     *
     * @param id       position id
     * @param newRules new rules
     */
    public void updatePositionRules(final long id, final PositionRulesDTO newRules) {
        positionService.updatePositionRules(id, newRules);
    }

    /**
     * Close position (no matter the rules).
     * The closing will happened when the next ticker arrives.
     *
     * @param id position id
     */
    public void closePosition(final long id) {
        positionService.closePosition(id);
    }

    // =================================================================================================================
    // Methods that can be implemented by strategies.

    @Override
    public void onAccountsUpdates(final Map<String, AccountDTO> accounts) {

    }

    @Override
    public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {

    }

    @Override
    public void onOrdersUpdates(final Map<String, OrderDTO> orders) {

    }

    @Override
    public void onTradesUpdates(final Map<String, TradeDTO> trades) {

    }

    @Override
    public void onPositionsUpdates(final Map<Long, PositionDTO> positions) {

    }

    @Override
    public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions) {

    }

    // =================================================================================================================
    // Related to canBuy & canSell methods.

    /**
     * Returns the cost of buying an amount of a currency pair.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return cost
     */
    public final Optional<CurrencyAmountDTO> getEstimatedBuyingCost(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        /*
            symbol=ETH-BTC
            {
              "time": 1598626640265,
              "sequence": "1594421123246",
              "price": "0.034227",
              "size": "0.0200088",
              "bestBid": "0.034226",
              "bestBidSize": "6.3384368",
              "bestAsk": "0.034227",
              "bestAskSize": "18.6378851"
            }
            This means 1 Ether can be bought with 0.034227 Bitcoin.
         */

        // We get the last ticker from the last values received.
        final TickerDTO ticker = lastTickers.get(currencyPair);
        if (ticker == null) {
            // No ticker for this currency pair.
            return Optional.empty();
        } else {
            // Make the calculation.
            return Optional.of(CurrencyAmountDTO.builder()
                    .value(ticker.getLast().multiply(amount))
                    .currency(currencyPair.getQuoteCurrency())
                    .build());
        }
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final CurrencyPairDTO currencyPair,
                                final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canBuy(account, currencyPair, amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param currencyPair        currency pair
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final CurrencyPairDTO currencyPair,
                                final BigDecimal amount,
                                final BigDecimal minimumBalanceAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canBuy(account, currencyPair, amount, minimumBalanceAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param account      account
     * @param currencyPair currency pair
     * @param amount       amount
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final CurrencyPairDTO currencyPair,
                                final BigDecimal amount) {
        return canBuy(account, currencyPair, amount, ZERO);
    }

    /**
     * Returns true if we have enough assets to buy and if minimumBalanceAfter is left on the account after.
     *
     * @param account             account
     * @param currencyPair        currency pair
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final CurrencyPairDTO currencyPair,
                                final BigDecimal amount,
                                final BigDecimal minimumBalanceAfter) {
        // We get the amount.
        final Optional<BalanceDTO> balance = account.getBalance(currencyPair.getQuoteCurrency());
        if (balance.isPresent()) {
            // We get the estimated cost of buying.
            final Optional<CurrencyAmountDTO> estimatedBuyingCost = getEstimatedBuyingCost(currencyPair, amount);

            // We calculate.
            // Balance in the account
            // Minus
            // Estimated cost
            // Must be superior to zero
            // If there is no way to calculate the price for the moment (no ticker).
            return estimatedBuyingCost.filter(currencyAmountDTO -> balance.get().getAvailable()
                    .subtract(currencyAmountDTO.getValue().add(minimumBalanceAfter).add(getAmountsLockedByCurrency(currencyAmountDTO.getCurrency())))
                    .compareTo(ZERO) > 0).isPresent() || estimatedBuyingCost.filter(currencyAmountDTO -> balance.get().getAvailable()
                    .subtract(currencyAmountDTO.getValue().add(minimumBalanceAfter).add(getAmountsLockedByCurrency(currencyAmountDTO.getCurrency())))
                    .compareTo(ZERO) == 0).isPresent();
        } else {
            // If the is no balance in this currency, we can't buy.
            return false;
        }
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param currency currency
     * @param amount   amount
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final CurrencyDTO currency,
                                 final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canSell(account, currency, amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param currency            currency
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after selling
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final CurrencyDTO currency,
                                 final BigDecimal amount,
                                 final BigDecimal minimumBalanceAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canSell(account, currency, amount, minimumBalanceAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param account  account
     * @param currency currency pair
     * @param amount   amount
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final CurrencyDTO currency,
                                 final BigDecimal amount) {
        return canSell(account, currency, amount, ZERO);
    }

    /**
     * Returns true if we have enough assets to sell and if minimumBalanceAfter is left on the account after.
     *
     * @param account             account
     * @param currency            currency
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after selling
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final CurrencyDTO currency,
                                 final BigDecimal amount,
                                 final BigDecimal minimumBalanceAfter) {
        // We get the amount.
        final Optional<BalanceDTO> balance = account.getBalance(currency);
        // public int compareTo(BigDecimal bg) returns
        // 1 : if value of this BigDecimal is greater than that of BigDecimal object passed as parameter.
        // If the is no balance in this currency, we can't buy.
        return balance.filter(balanceDTO -> balanceDTO.getAvailable().subtract(amount).subtract(minimumBalanceAfter).subtract(getAmountsLockedByCurrency(currency)).compareTo(ZERO) > 0
                || balanceDTO.getAvailable().subtract(amount).subtract(minimumBalanceAfter).subtract(getAmountsLockedByCurrency(currency)).compareTo(ZERO) == 0).isPresent();
    }

}
