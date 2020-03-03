package tech.cassandre.trading.bot.test.util.strategy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.strategy.Strategy;

import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;

/**
 * Invalid strategy.
 */
@Strategy
@ConditionalOnProperty(
		value = PARAMETER_INVALID_STRATEGY_ENABLED,
		havingValue = "true")
public class InvalidStrategy {

}
