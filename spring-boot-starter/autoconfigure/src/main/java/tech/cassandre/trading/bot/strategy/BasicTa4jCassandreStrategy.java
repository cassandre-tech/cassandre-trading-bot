package tech.cassandre.trading.bot.strategy;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Strategy;
import org.ta4j.core.num.DoubleNum;
import reactor.core.publisher.BaseSubscriber;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.ta4j.BarAggregator;
import tech.cassandre.trading.bot.ta4j.DurationBarAggregator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Basic ta4j strategy.
 */
@SuppressWarnings("unused")
public abstract class BasicTa4jCassandreStrategy extends GenericCassandreStrategy {

    /** Timestamp of the last added bar. */
    private ZonedDateTime lastAddedBarTimestamp;

    /** Series. */
    private final BarSeries series;

    /** Ta4j Strategy. */
    private final Strategy strategy;

    private final BarAggregator barAggregator = new DurationBarAggregator(getDelayBetweenTwoBars());

    /**
     * Constructor.
     */
    public BasicTa4jCassandreStrategy() {
        // Build the series.
        series = new BaseBarSeriesBuilder()
                .withNumTypeOf(DoubleNum.class)
                .withName(getRequestedCurrencyPair().toString())
                .build();
        series.setMaximumBarCount(getMaximumBarCount());

        // Build the strategy.
        strategy = getStrategy();

        final AggregatedBarSubscriber barSubscriber = new AggregatedBarSubscriber(this::addBarAndCallStrategy);

        barAggregator.getBarFlux().subscribe(barSubscriber);
        barSubscriber.request(1);
    }

    /**
     * Implements this method to tell the bot which currency pair your strategy will receive.
     *
     * @return the list of currency pairs tickers your want to receive
     */
    public abstract CurrencyPairDTO getRequestedCurrencyPair();

    /**
     * Implements this method to tell the bot how many bars you want to keep in your bar series.
     *
     * @return maximum bar count.
     */
    @SuppressWarnings("SameReturnValue")
    public abstract int getMaximumBarCount();

    /**
     * Implements this method to set the time that should separate two bars.
     *
     * @return temporal amount
     */
    public abstract Duration getDelayBetweenTwoBars();

    /**
     * Implements this method to tell the bot which strategy to apply.
     *
     * @return strategy
     */
    public abstract Strategy getStrategy();

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        // We only support one currency pair with BasicTa4jCassandreStrategy.
        return Set.of(getRequestedCurrencyPair());
    }

    @Override
    public final void tickerUpdate(final TickerDTO ticker) {
        getLastTickers().put(ticker.getCurrencyPair(), ticker);

        barAggregator.update(ticker.getTimestamp(), ticker.getLast(), ticker.getHigh(), ticker.getLow(), ticker.getVolume());

        onTickerUpdate(ticker);
    }

    private Bar addBarAndCallStrategy(Bar bar) {
        series.addBar(bar);

        int endIndex = series.getEndIndex();
        if (strategy.shouldEnter(endIndex)) {
            // Our strategy should enter.
            shouldEnter();
        } else if (strategy.shouldExit(endIndex)) {
            // Our strategy should exit.
            shouldExit();
        }
        return bar;
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param amount amount
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(getAccounts().values()));
        return tradeAccount.filter(accountDTO -> canBuy(accountDTO, getRequestedCurrencyPair(), amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final BigDecimal amount,
                                final BigDecimal minimumBalanceAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(getAccounts().values()));
        return tradeAccount.filter(accountDTO -> canBuy(accountDTO, getRequestedCurrencyPair(), amount, minimumBalanceAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to buy.
     *
     * @param account account
     * @param amount  amount
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final BigDecimal amount) {
        return canBuy(account, getRequestedCurrencyPair(), amount);
    }

    /**
     * Returns true if we have enough assets to buy and if minimumBalanceAfter is left on the account after.
     *
     * @param account             account
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we there is enough assets to buy
     */
    public final boolean canBuy(final AccountDTO account,
                                final BigDecimal amount,
                                final BigDecimal minimumBalanceAfter) {
        return canBuy(account, getRequestedCurrencyPair(), amount, minimumBalanceAfter);
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after buying
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final BigDecimal amount,
                                 final BigDecimal minimumBalanceAfter) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(getAccounts().values()));
        return tradeAccount.filter(accountDTO -> canSell(accountDTO, getRequestedCurrencyPair().getBaseCurrency(), amount, minimumBalanceAfter)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param amount amount
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = getTradeAccount(new LinkedHashSet<>(getAccounts().values()));
        return tradeAccount.filter(accountDTO -> canSell(accountDTO, getRequestedCurrencyPair().getBaseCurrency(), amount)).isPresent();
    }

    /**
     * Returns true if we have enough assets to sell.
     *
     * @param account account
     * @param amount  amount
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final BigDecimal amount) {
        return canSell(account, getRequestedCurrencyPair().getBaseCurrency(), amount);
    }

    /**
     * Returns true if we have enough assets to sell and if minimumBalanceAfter is left on the account after.
     *
     * @param account             account
     * @param amount              amount
     * @param minimumBalanceAfter minimum balance that should be left after selling
     * @return true if we there is enough assets to sell
     */
    public final boolean canSell(final AccountDTO account,
                                 final BigDecimal amount,
                                 final BigDecimal minimumBalanceAfter) {
        return canSell(account, getRequestedCurrencyPair().getBaseCurrency(), amount, minimumBalanceAfter);
    }

    /**
     * Called when your strategy says you should enter.
     */
    public abstract void shouldEnter();

    /**
     * Called when your strategy says your should exit.
     */
    public abstract void shouldExit();

    /**
     * Getter for series.
     *
     * @return series
     */
    public final BarSeries getSeries() {
        return series;
    }

    private class AggregatedBarSubscriber extends BaseSubscriber<Bar> {

        private final Function<Bar, Bar> onNextFunction;

        public AggregatedBarSubscriber(Function<Bar, Bar> onNextFunction) {
            this.onNextFunction = onNextFunction;
        }

        @Override
        protected void hookOnNext(Bar value) {
            super.hookOnNext(value);
            onNextFunction.apply(value);
            request(1);
        }
    }

}
