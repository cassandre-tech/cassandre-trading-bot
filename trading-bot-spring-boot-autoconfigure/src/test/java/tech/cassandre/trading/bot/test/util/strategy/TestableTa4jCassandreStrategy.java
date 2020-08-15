package tech.cassandre.trading.bot.test.util.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;

/**
 * Testable ta4j strategy (used for tests).
 */
@SuppressWarnings("unused")
@CassandreStrategy(name = "Testable ta4j strategy")
@ConditionalOnProperty(
        value = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED,
        havingValue = "true")
public class TestableTa4jCassandreStrategy extends BasicTa4jCassandreStrategy {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Enter count. */
    private int enterCount = 0;

    /** Exit count. */
    private int exitCount = 0;

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdateReceived = new LinkedList<>();

    @Override
    public void onTickerUpdate(TickerDTO ticker) {
        tickersUpdateReceived.add(ticker);
    }

    @Override
    public CurrencyPairDTO getRequestedCurrencyPair() {
        return new CurrencyPairDTO(BTC, USDT);
    }

    @Override
    public int getMaximumBarCount() {
        return 8;
    }

    @Override
    public Duration getDelayBetweenTwoBars() {
        return Duration.ofDays(2);
    }

    @Override
    public Strategy getStrategy() {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(getSeries());
        SMAIndicator sma = new SMAIndicator(closePrice, 3); // On 3 days.
        return new BaseStrategy(new UnderIndicatorRule(sma, closePrice), new OverIndicatorRule(sma, closePrice));
    }

    @Override
    public void shouldEnter() {
        logger.info("Enter signal at " + getSeries().getLastBar().getClosePrice());
        enterCount++;
    }

    @Override
    public void shouldExit() {
        logger.info("Exit signal at " + getSeries().getLastBar().getClosePrice());
        exitCount++;
    }

    /**
     * Getter enterCount.
     *
     * @return enterCount
     */
    public final int getEnterCount() {
        return enterCount;
    }

    /**
     * Getter exitCount.
     *
     * @return exitCount
     */
    public final int getExitCount() {
        return exitCount;
    }

    /**
     * Getter tickersUpdateReceived.
     *
     * @return tickersUpdateReceived
     */
    public final List<TickerDTO> getTickersUpdateReceived() {
        return tickersUpdateReceived;
    }

}
