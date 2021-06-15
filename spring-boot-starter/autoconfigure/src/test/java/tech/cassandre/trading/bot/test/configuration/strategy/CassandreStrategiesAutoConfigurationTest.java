package tech.cassandre.trading.bot.test.configuration.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy1.PARAMETER_STRATEGY_1_ENABLED;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy2.PARAMETER_STRATEGY_2_ENABLED;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy3.PARAMETER_STRATEGY_3_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.LargeTestableCassandreStrategy.PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;

@DisplayName("Configuration - Strategy - Autoconfiguration")
@Configuration({
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, value = "false"),
})
class CassandreStrategiesAutoConfigurationTest {

    @Test
    @DisplayName("Check when a valid strategy was found")
    public void checkValidStrategyFound() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_1_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_2_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_3_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
        } catch (Exception e) {
            fail("Exception raised for valid strategy" + e);
        }
    }

    @Test
    @DisplayName("Check error messages when no strategy is found")
    public void checkNoStrategyFound() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_1_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_2_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_3_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("No strategy found"));
        }
    }

    @Test
    @DisplayName("Check error messages when having an invalid strategy")
    public void checkInvalidStrategyError() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_1_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_2_ENABLED, "false");
            System.setProperty(PARAMETER_STRATEGY_3_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("doesn't extend BasicCassandreStrategy"));
        }
    }

    @Test
    @DisplayName("Check error messages if a strategy has an invalid trade account")
    public void checkStrategyWithInvalidTradeAccount() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_STRATEGY_1_ENABLED, "true");
            System.setProperty(PARAMETER_STRATEGY_2_ENABLED, "true");
            System.setProperty(PARAMETER_STRATEGY_3_ENABLED, "true");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("Your strategies specifies a trading account that doesn't exist"));
        }
    }

    @Test
    @DisplayName("Check error messages if two strategies have the same id")
    public void checkStrategiesWithDuplicatedIds() {
        try {
            System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, "false");
            System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED, "true");
            System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, "false");
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigurationException);
            assertTrue(e.getCause().getMessage().contains("You have duplicated strategy ids"));
        }
    }

}
