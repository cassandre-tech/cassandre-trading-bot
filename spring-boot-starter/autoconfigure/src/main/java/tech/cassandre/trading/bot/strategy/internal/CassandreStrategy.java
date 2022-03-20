package tech.cassandre.trading.bot.strategy.internal;

import lombok.NonNull;
import tech.cassandre.trading.bot.dto.market.CandleDTO;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static tech.cassandre.trading.bot.util.math.MathConstants.BIGINTEGER_SCALE;

/**
 * CassandreStrategy is the class that every strategy used by user ({@link BasicCassandreStrategy} must extend.
 * It contains methods to access data and manage orders, trades, positions.
 * <p>
 * These are the classes used by Cassandre to manage a position.
 * - CassandreStrategyInterface list the methods a strategy type must implement to be able to interact with the Cassandre framework.
 * - CassandreStrategyConfiguration contains the configuration of the strategy.
 * - CassandreStrategyDependencies contains all the dependencies required by a strategy and provided by the Cassandre framework.
 * - CassandreStrategyImplementation is the default implementation of CassandreStrategyInterface, this code manages the interaction between Cassandre framework and a strategy.
 * - CassandreStrategy (class) is the class that every strategy used by user ({@link BasicCassandreStrategy} must extend. It contains methods to access data and manage orders, trades, positions.
 * There are the classes used by the developer.
 * - CassandreStrategy (interface) is the annotation allowing you Cassandre to recognize a user strategy.
 * - BasicCassandreStrategy - User inherits this class this one to make a basic strategy.
 */
@SuppressWarnings("checkstyle:DesignForExtension")
public abstract class CassandreStrategy extends CassandreStrategyImplementation {

    // =================================================================================================================
    // Methods to retrieve data related to accounts.

    /**
     * Returns the trade account selected by the strategy developer.
     *
     * @return trade account
     */
    public final Optional<AccountDTO> getTradeAccount() {
        return getTradeAccount(new LinkedHashSet<>(getAccounts().values()));
    }

    /**
     * Returns trade account balances.
     *
     * @return trade account balances
     */
    public final Map<CurrencyDTO, BalanceDTO> getTradeAccountBalances() {
        Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
        getTradeAccount().ifPresent(accountDTO ->
                accountDTO.getBalances()
                        .forEach(balanceDTO -> balances.put(balanceDTO.getCurrency(), balanceDTO))
        );
        return balances;
    }

    /**
     * Returns list of accounts.
     *
     * @return accounts
     */
    public final Map<String, AccountDTO> getAccounts() {
        return userAccounts;
    }

    /**
     * Search and return an account by its id.
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

    /**
     * Returns the amounts locked by position.
     *
     * @return amounts locked
     */
    public final Map<Long, CurrencyAmountDTO> getAmountsLockedByPosition() {
        return dependencies.getPositionService().getAmountsLockedByPosition();
    }

    /**
     * Returns the amounts locked for a specific currency.
     *
     * @param currency currency
     * @return amounts locked
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
    // Methods to retrieve data related to tickers.

    /**
     * Return last received tickers.
     *
     * @return ticker
     */
    public final Map<CurrencyPairDTO, TickerDTO> getLastTickers() {
        return lastTickers;
    }

    /**
     * Return the list of imported candles (ordered by timestamp).
     *
     * @return imported candles
     */
    public final List<CandleDTO> getImportedCandles() {
        return dependencies.getImportedCandleRepository()
                .findByOrderByTimestampAsc()
                .stream()
                .map(CANDLE_MAPPER::mapToCandleDTO)
                .toList();
    }

    /**
     * Return the list of imported candles for a specific currency pair (ordered by timestamp).
     *
     * @param currencyPair currency pair
     * @return imported candles
     */
    public final List<CandleDTO> getImportedCandles(@NonNull final CurrencyPairDTO currencyPair) {
        return dependencies.getImportedCandleRepository()
                .findByCurrencyPairOrderByTimestampAsc(currencyPair.toString())
                .stream()
                .map(CANDLE_MAPPER::mapToCandleDTO)
                .toList();
    }

    /**
     * Return the list of imported tickers (ordered by timestamp).
     *
     * @return imported tickers
     */
    public final List<TickerDTO> getImportedTickers() {
        return dependencies.getImportedTickerRepository()
                .findByOrderByTimestampAsc()
                .stream()
                .map(TICKER_MAPPER::mapToTickerDTO)
                .toList();
    }

    /**
     * Return the list of imported tickers for a specific currency pair (ordered by timestamp).
     *
     * @param currencyPair currency pair
     * @return imported tickers
     */
    public final List<TickerDTO> getImportedTickers(@NonNull final CurrencyPairDTO currencyPair) {
        return dependencies.getImportedTickerRepository()
                .findByCurrencyPairOrderByTimestampAsc(currencyPair.toString())
                .stream()
                .map(TICKER_MAPPER::mapToTickerDTO)
                .toList();
    }

    // =================================================================================================================
    // Methods to retrieve data related to orders.

    /**
     * Returns list of orders (order id is key).
     *
     * @return orders
     */
    public final Map<String, OrderDTO> getOrders() {
        return dependencies.getOrderRepository().findByOrderByTimestampAsc()
                .stream()
                .filter(order -> order.getStrategy().getUid().equals(configuration.getStrategyUid()))
                .map(ORDER_MAPPER::mapToOrderDTO)
                .collect(Collectors.toMap(OrderDTO::getOrderId, orderDTO -> orderDTO));
    }

    /**
     * Get an order by its order id.
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
    // Methods to retrieve data related to trades.

    /**
     * Returns list of trades (trade id is key).
     *
     * @return trades
     */
    public final Map<String, TradeDTO> getTrades() {
        return dependencies.getTradeRepository().findByOrderByTimestampAsc()
                .stream()
                .filter(trade -> trade.getOrder().getStrategy().getUid().equals(configuration.getStrategyUid()))
                .map(TRADE_MAPPER::mapToTradeDTO)
                .collect(Collectors.toMap(TradeDTO::getTradeId, tradeDTO -> tradeDTO));
    }

    /**
     * Get a trade by its trade id.
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
    // Methods to retrieve data related to positions.

    /**
     * Returns list of positions (position id the key).
     *
     * @return positions
     */
    public final Map<Long, PositionDTO> getPositions() {
        return dependencies.getPositionRepository()
                .findByOrderByUid()
                .stream()
                .filter(position -> position.getStrategy().getUid().equals(configuration.getStrategyUid()))
                .map(POSITION_MAPPER::mapToPositionDTO)
                .collect(Collectors.toMap(PositionDTO::getPositionId, positionDTO -> positionDTO));
    }

    /**
     * Get a position by its id.
     *
     * @param positionId position id
     * @return position
     */
    public final Optional<PositionDTO> getPositionByPositionId(final long positionId) {
        return getPositions()
                .values()
                .stream()
                .filter(positionDTO -> positionDTO.getPositionId() == positionId)
                .findFirst();
    }

    /**
     * Returns gains of all positions of the strategy.
     *
     * @return total gains
     */
    public final Map<CurrencyDTO, GainDTO> getGains() {
        return dependencies.getPositionService().getGains(configuration.getStrategyUid());
    }

    // =================================================================================================================
    // Methods to manage orders & positions (creation, cancellation, rules updates...).

    /**
     * Creates a buy market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    public OrderCreationResultDTO createBuyMarketOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount) {
        return dependencies.getTradeService().createBuyMarketOrder(this, currencyPair, amount);
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
        return dependencies.getTradeService().createSellMarketOrder(this, currencyPair, amount);
    }

    /**
     * Creates a buy limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @return order result (order id or error)
     */
    @SuppressWarnings("unused")
    public OrderCreationResultDTO createBuyLimitOrder(final CurrencyPairDTO currencyPair,
                                                      final BigDecimal amount,
                                                      final BigDecimal limitPrice) {
        return dependencies.getTradeService().createBuyLimitOrder(this, currencyPair, amount, limitPrice);
    }

    /**
     * Creates a sell limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @return order result (order id or error)
     */
    @SuppressWarnings("unused")
    public OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair,
                                                       final BigDecimal amount,
                                                       final BigDecimal limitPrice) {
        return dependencies.getTradeService().createSellLimitOrder(this, currencyPair, amount, limitPrice);
    }

    /**
     * Cancel order.
     *
     * @param orderUid order uid
     * @return true if cancelled
     */
    @SuppressWarnings("unused")
    boolean cancelOrder(final long orderUid) {
        return dependencies.getTradeService().cancelOrder(orderUid);
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
        return dependencies.getPositionService().createLongPosition(this, currencyPair, amount, rules);
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
        return dependencies.getPositionService().createShortPosition(this, currencyPair, amount, rules);
    }

    /**
     * Update position rules.
     *
     * @param positionUid position uid
     * @param newRules    new rules
     */
    public void updatePositionRules(final long positionUid, final PositionRulesDTO newRules) {
        dependencies.getPositionService().updatePositionRules(positionUid, newRules);
    }

    /**
     * Set auto close value on a specific position.
     * If true, Cassandre will close the position according to rules.
     * if false, Cassandre will never close the position.
     *
     * @param positionUid position uid
     * @param value       auto close value
     */
    public void setAutoClose(final long positionUid, final boolean value) {
        dependencies.getPositionService().setAutoClose(positionUid, value);
    }

    /**
     * Close position (no matter the rules).
     * The closing will happen when the next ticker arrives.
     *
     * @param positionUid position uid
     */
    public void closePosition(final long positionUid) {
        dependencies.getPositionService().forcePositionClosing(positionUid);
    }

    // =================================================================================================================
    // CanBuy & canSell methods.

    /**
     * Returns the amount of a currency I can buy with a certain amount of another currency.
     *
     * @param amountToUse    amount you want to use buy the currency you want
     * @param targetCurrency the currency you want to buy
     * @return amount of currencyWanted you can buy with amountToUse
     */
    public final Optional<BigDecimal> getEstimatedBuyableAmount(final CurrencyAmountDTO amountToUse,
                                                                final CurrencyDTO targetCurrency) {
        /*
            symbol=BTC-USDT
            {
              "time": 1637270267065,
              "sequence": "1622704211505",
              "price": "58098.3",
              "size": "0.00001747",
              "bestBid": "58098.2",
              "bestBidSize": "0.038",
              "bestAsk": "60000",
              "bestAskSize": "0.27476785"
            }
            This means 1 Bitcoin can be bought with 60000 USDT.
         */
        final TickerDTO ticker = lastTickers.get(new CurrencyPairDTO(targetCurrency, amountToUse.getCurrency()));
        if (ticker == null) {
            // No ticker for this currency pair.
            return Optional.empty();
        } else {
            // Make the calculation.
            // amountToUse: 150 000 USDT.
            // CurrencyWanted: BTC.
            // How much BTC I can buy ? amountToUse / last
            return Optional.of(amountToUse.getValue().divide(ticker.getLast(), BIGINTEGER_SCALE, FLOOR));
        }
    }

    /**
     * Returns the cost of buying an amount of a currency pair.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return cost
     */
    public final Optional<CurrencyAmountDTO> getEstimatedBuyingCost(final CurrencyPairDTO currencyPair,
                                                                    final BigDecimal amount) {
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
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final CurrencyPairDTO currencyPair,
                                final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canBuy(account, currencyPair, amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param currencyPair            currency pair
     * @param amount                  amount
     * @param minimumBalanceLeftAfter minimum balance that should be left after buying
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final CurrencyPairDTO currencyPair,
                                final BigDecimal amount,
                                final BigDecimal minimumBalanceLeftAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canBuy(account, currencyPair, amount, minimumBalanceLeftAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param account      account
     * @param currencyPair currency pair
     * @param amount       amount
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final CurrencyPairDTO currencyPair,
                                final BigDecimal amount) {
        return canBuy(account, currencyPair, amount, ZERO);
    }

    /**
     * Returns true if we have enough assets to buy and if minimumBalanceAfter is left on the account after.
     *
     * @param account                 account
     * @param currencyPair            currency pair
     * @param amount                  amount
     * @param minimumBalanceLeftAfter minimum balance that should be left after buying
     * @return true if we have enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final CurrencyPairDTO currencyPair,
                                final BigDecimal amount,
                                final BigDecimal minimumBalanceLeftAfter) {
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
                    .subtract(currencyAmountDTO.getValue().add(minimumBalanceLeftAfter).add(getAmountsLockedByCurrency(currencyAmountDTO.getCurrency())))
                    .compareTo(ZERO) > 0).isPresent() || estimatedBuyingCost.filter(currencyAmountDTO -> balance.get().getAvailable()
                    .subtract(currencyAmountDTO.getValue().add(minimumBalanceLeftAfter).add(getAmountsLockedByCurrency(currencyAmountDTO.getCurrency())))
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
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final CurrencyDTO currency,
                                 final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canSell(account, currency, amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param currency                currency
     * @param amount                  amount
     * @param minimumBalanceLeftAfter minimum balance that should be left after selling
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final CurrencyDTO currency,
                                 final BigDecimal amount,
                                 final BigDecimal minimumBalanceLeftAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(userAccounts.values()));
        return tradeAccount.filter(account -> canSell(account, currency, amount, minimumBalanceLeftAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param account  account
     * @param currency currency pair
     * @param amount   amount
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final CurrencyDTO currency,
                                 final BigDecimal amount) {
        return canSell(account, currency, amount, ZERO);
    }

    /**
     * Returns true if we have enough assets to sell and if minimumBalanceAfter is left on the account after.
     *
     * @param account                 account
     * @param currency                currency
     * @param amount                  amount
     * @param minimumBalanceLeftAfter minimum balance that should be left after selling
     * @return true if we have enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final CurrencyDTO currency,
                                 final BigDecimal amount,
                                 final BigDecimal minimumBalanceLeftAfter) {
        // We get the amount.
        final Optional<BalanceDTO> balance = account.getBalance(currency);
        // public int compareTo(BigDecimal bg) returns
        // 1: if value of this BigDecimal is greater than that of BigDecimal object passed as parameter.
        // If the is no balance in this currency, we can't buy.
        return balance.filter(balanceDTO -> balanceDTO.getAvailable().subtract(amount).subtract(minimumBalanceLeftAfter).subtract(getAmountsLockedByCurrency(currency)).compareTo(ZERO) > 0
                || balanceDTO.getAvailable().subtract(amount).subtract(minimumBalanceLeftAfter).subtract(getAmountsLockedByCurrency(currency)).compareTo(ZERO) == 0).isPresent();
    }

}
