package tech.cassandre.trading.bot.test.util.strategies;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;

/**
 * Invalid strategy (used for tests).
 */
@CassandreStrategy(strategyName = "Invalid strategy")
@ConditionalOnProperty(
		value = PARAMETER_INVALID_STRATEGY_ENABLED,
		havingValue = "true")
public class InvalidStrategy {

	/** Invalid strategy enabled parameter. */
	public static final String PARAMETER_INVALID_STRATEGY_ENABLED = "invalidStrategy.enabled";

}
