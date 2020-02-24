package tech.cassandre.trading.bot.test.util.strategy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.strategy.Strategy;

/**
 * Invalid strategy.
 */
@Strategy
@ConditionalOnProperty(
		value = "invalidStrategy.enabled",
		havingValue = "true")
public class InvalidStrategy {

}
