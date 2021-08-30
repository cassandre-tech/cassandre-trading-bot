package tech.cassandre.trading.bot.test.util.strategies;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;

/**
 * Testable ta4j strategy (used for tests).
 */
@SuppressWarnings("unused")
@CassandreStrategy(
        strategyId = "01",
        strategyName = "Testable ta4j strategy")
@ConditionalOnProperty(
        value = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED,
        havingValue = "true")
@Getter
public class TestableTa4jCassandreStrategy extends BasicTa4jCassandreStrategy {

    /** Testable ta4j strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED = "testableTa4jStrategy.enabled";

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Enter count. */
    private int enterCount = 0;

    /** Exit count. */
    private int exitCount = 0;

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdateReceived = new LinkedList<>();

    @Override
    public final void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        tickersUpdateReceived.addAll(tickers.values());
    }

    @Override
    public CurrencyPairDTO getRequestedCurrencyPair() {
        return new CurrencyPairDTO(BTC, USDT);
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream().filter(a -> "trade".equals(a.getName())).findFirst();
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
        SMAIndicator sma = new SMAIndicator(closePrice, 3);
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

}
