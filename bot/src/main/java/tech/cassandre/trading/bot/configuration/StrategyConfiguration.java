package tech.cassandre.trading.bot.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.Strategy;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * This class contains the strategy configuration..
 */
@Configuration
public class StrategyConfiguration extends BaseConfiguration {

	/** Application context. */
	private final ApplicationContext applicationContext;

	/** Cassandre strategy. */
	private CassandreStrategy strategy;

	/**
	 * Constructor.
	 *
	 * @param newApplicationContext application context
	 */
	public StrategyConfiguration(final ApplicationContext newApplicationContext) {
		this.applicationContext = newApplicationContext;
	}

	/**
	 * Search for the strategy and instantiate it.
	 */
	@PostConstruct
	public void configure() {
		// Retrieving all the beans have the annotation @Strategy.
		final Map<String, Object> strategyBeans = applicationContext.getBeansWithAnnotation(Strategy.class);

		// Check if there is no strategy.
		if (strategyBeans.isEmpty()) {
			getLogger().error("No strategy found");
			throw new ConfigurationException("No strategy found",
					"You must have one class with @Strategy");
		}

		// Check if there are several strategies.
		if (strategyBeans.size() > 1) {
			getLogger().error("Several strategies were found");
			strategyBeans.forEach((s, o) -> getLogger().error(" - " + s));
			throw new ConfigurationException("Several strategies were found",
					"Cassandre trading bot only supports one strategy at a time (@Strategy)");
		}

		// Check if the strategy extends CassandreStrategy.
		Object o = strategyBeans.values().iterator().next();
		if (!(o instanceof CassandreStrategy)) {
			throw new ConfigurationException("Your strategy doesn't extends CassandreStrategy",
					o.getClass() + " must extends CassandreStrategy");
		}

		// Setting up the strategy.
		strategy = (CassandreStrategy) o;

		// Displaying strategy name.
		Strategy strategyAnnotation = o.getClass().getAnnotation(Strategy.class);
		getLogger().info("Running strategy '{}'", strategyAnnotation.name());

		// Displaying requested currency pairs.
		getLogger().info("The strategy requires the following currency pair(s) : ");
		strategy.getRequestedCurrencyPairs().forEach(cp -> getLogger().info("- " + cp));
	}

}
