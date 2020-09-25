package tech.cassandre.trading.bot.test.util.strategies;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import static tech.cassandre.trading.bot.test.util.junit.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;

/**
 * Invalid strategy (used for tests).
 */
@CassandreStrategy(name = "Invalid strategy")
@ConditionalOnProperty(
		value = PARAMETER_INVALID_STRATEGY_ENABLED,
		havingValue = "true")
public class InvalidStrategy {

}
