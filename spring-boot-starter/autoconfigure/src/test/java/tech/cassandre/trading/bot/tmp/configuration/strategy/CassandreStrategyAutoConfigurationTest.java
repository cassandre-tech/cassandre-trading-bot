package tech.cassandre.trading.bot.tmp.configuration.strategy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;

@DisplayName("Strategy configuration - Autoconfiguration")
@Configuration({
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, value = "false"),
})
@DirtiesContext(classMode = AFTER_CLASS)
@Disabled
class CassandreStrategyAutoConfigurationTest {

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that a valid strategy was found")
    public void checkValidStrategyFound() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
        } catch (Exception e) {
            fail("Exception raised for valid strategy" + e);
        }
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that no strategy was found")
    public void checkNoStrategyFound() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("No strategy found"));
        }
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that two strategies were found")
    public void checkTwoStrategiesFound() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("Several strategies found"));
        }
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that an invalid strategy was found")
    public void checkInvalidStrategyFound() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("Your strategy doesn't extend BasicCassandreStrategy"));
        }
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check error is a strategy has an invalid trade account")
    public void checkStrategyWithInvalidTradeAccount() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "true");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("Your strategy specifies a trading account that doesn't exist"));
        }
    }

}
