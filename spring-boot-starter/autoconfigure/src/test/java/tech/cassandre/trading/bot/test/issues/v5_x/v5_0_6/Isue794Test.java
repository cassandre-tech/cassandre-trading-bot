package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_6;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;
import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;

@SpringBootTest
@DisplayName("Github issue 794")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true"),
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "true"),
        @Property(key = PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, value = "false")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Isue794Test extends BaseTest {

    @Autowired
    private TestableTa4jCassandreStrategy strategy;

    @Test
    @DisplayName("Update the Strategy on a BasicTa4jCassandreStrategy")
    public void updateTa4jStrategyInBasicTa4jCassandreStrategy() {
        assertEquals("Initial strategy", strategy.getExecutedStrategy().getName());

        // Creating a new strategy.
        ClosePriceIndicator closePrice = new ClosePriceIndicator(new BaseBarSeriesBuilder().build());
        SMAIndicator sma = new SMAIndicator(closePrice, 3);
        final BaseStrategy newStrategy = new BaseStrategy("New strategy", new UnderIndicatorRule(sma, closePrice), new OverIndicatorRule(sma, closePrice));

        // Updating the strategy and checking the change.
        strategy.updateStrategy(newStrategy);
        assertEquals("New strategy", strategy.getExecutedStrategy().getName());
    }

}
