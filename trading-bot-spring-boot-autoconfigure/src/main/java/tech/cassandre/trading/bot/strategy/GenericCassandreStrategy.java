package tech.cassandre.trading.bot.strategy;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.dto.CurrencyAmountDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Generic Cassandre strategy.
 */
public abstract class GenericCassandreStrategy implements CassandreStrategyInterface {

    /** Trade service. */
    private TradeService tradeService;

    /** Position service. */
    private PositionService positionService;

    /** The accounts owned by the user. */
    private final Map<String, AccountDTO> accounts = new LinkedHashMap<>();

    /** The orders owned by the user. */
    private final Map<String, OrderDTO> orders = new LinkedHashMap<>();

    /** The trades owned by the user. */
    private final Map<String, TradeDTO> trades = new LinkedHashMap<>();

    /** The positions owned by the user. */
    private final Map<Long, PositionDTO> positions = new LinkedHashMap<>();

    /** Last ticker received. */
    private final Map<CurrencyPairDTO, TickerDTO> lastTicker = new LinkedHashMap<>();

    @Override
    public final void setTradeService(final TradeService newTradeService) {
        this.tradeService = newTradeService;
    }

    @Override
    public final void setPositionService(final PositionService newPositionService) {
        this.positionService = newPositionService;
    }

    @Override
    public final TradeService getTradeService() {
        return tradeService;
    }

    @Override
    public final PositionService getPositionService() {
        return positionService;
    }

    /**
     * Getter accounts.
     *
     * @return accounts
     */
    public final Map<String, AccountDTO> getAccounts() {
        return accounts;
    }

    /**
     * Getter orders.
     *
     * @return orders
     */
    public final Map<String, OrderDTO> getOrders() {
        return orders;
    }

    /**
     * Getter trades.
     *
     * @return trades
     */
    public final Map<String, TradeDTO> getTrades() {
        return trades;
    }

    /**
     * Getter positions.
     *
     * @return positions
     */
    public final Map<Long, PositionDTO> getPositions() {
        return positions;
    }

    /**
     * Getter lastTicker.
     *
     * @return lastTicker
     */
    public final Map<CurrencyPairDTO, TickerDTO> getLastTicker() {
        return lastTicker;
    }

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
        final TickerDTO ticker = getLastTicker().get(currencyPair);
        if (ticker == null) {
            // No ticker for this currency pair.
            return Optional.empty();
        } else {
            // Make the calculation.
            return Optional.of(new CurrencyAmountDTO(ticker.getLast().multiply(amount),
                    currencyPair.getQuoteCurrency()));
        }
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param account      account
     * @param currencyPair currency pair
     * @param amount       amount
     * @return true if we there is enough money to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final CurrencyPairDTO currencyPair,
                                final BigDecimal amount) {
        return canBuy(account, currencyPair, amount, BigDecimal.ZERO);
    }

    /**
     * Returns true if we have enough assets to buy and if minimumBalanceAfter is left on the account after.
     *
     * @param account             account
     * @param currencyPair        currency pair
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we there is enough money to buy
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
                    .subtract(currencyAmountDTO.getValue().add(minimumBalanceAfter))
                    .compareTo(BigDecimal.ZERO) > 0).isPresent();
        } else {
            // If the is no balance in this currency, we can't buy.
            return false;
        }
    }

}
