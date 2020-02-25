package tech.cassandre.trading.bot.configuration;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import si.mazi.rescu.HttpStatusIOException;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import javax.annotation.PostConstruct;
import java.util.StringJoiner;

/**
 * ExchangeConfiguration class configure exchange connection.
 */
@Configuration
public class ExchangeConfiguration extends BaseConfiguration {

	/** XChange user sandbox parameter. */
	private static final String USE_SANDBOX_PARAMETER = "Use_Sandbox";

	/** XChange passphrase parameter. */
	private static final String PASSPHRASE_PARAMETER = "passphrase";

	/** Unauthorized http status code. */
	public static final int UNAUTHORIZED_STATUS_CODE = 401;

	/** Exchange parameters. */
	private final ExchangeParameters exchangeParameters;

	/** Exchange. */
	private Exchange exchange;

	/** Market data service. */
	private MarketDataService marketDataService;

	/** Account service. */
	private AccountService accountService;

	/** Trade service. */
	private TradeService tradeService;

	/**
	 * Constructor.
	 *
	 * @param newExchangeParameters exchange parameters
	 */
	public ExchangeConfiguration(final ExchangeParameters newExchangeParameters) {
		this.exchangeParameters = newExchangeParameters;
	}

	/**
	 * Instantiating the exchange based on the parameter.
	 */
	@PostConstruct
	private void configure() {
		try {
			// Instantiate exchange.
			@SuppressWarnings("rawtypes")
			Class exchangeClass = Class.forName(getExchangeClassName());
			//noinspection unchecked
			ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClass);

			// Exchange configuration.
			exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX_PARAMETER, exchangeParameters.isSandbox());
			exchangeSpecification.setUserName(exchangeParameters.getUsername());
			exchangeSpecification.setExchangeSpecificParametersItem(PASSPHRASE_PARAMETER, exchangeParameters.getPassphrase());
			exchangeSpecification.setApiKey(exchangeParameters.getKey());
			exchangeSpecification.setSecretKey(exchangeParameters.getSecret());
			exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);

			// Create services.
			marketDataService = exchange.getMarketDataService();
			accountService = exchange.getAccountService();
			tradeService = exchange.getTradeService();

			// Force login to check credentials.
			accountService.getAccountInfo();
			getLogger().info("ExchangeConfiguration - Connection to {} successful", exchangeParameters.getName());

			// Prints all the supported currency pairs.
			StringJoiner currencyPairList = new StringJoiner(", ");
			exchange.getExchangeMetaData()
					.getCurrencyPairs()
					.forEach((currencyPair, currencyPairMetaData) -> currencyPairList.add(currencyPair.toString()));
			getLogger().info("ExchangeConfiguration - Supported currency pairs : " + currencyPairList);

		} catch (ClassNotFoundException e) {
			// If we can't find the exchange class.
			throw new ConfigurationException("Impossible to find the exchange you requested : " + exchangeParameters.getName(),
					"Choose a valid exchange (https://github.com/knowm/XChange) and/or add the dependency to Cassandre");
		} catch (HttpStatusIOException e) {
			if (e.getHttpStatusCode() == UNAUTHORIZED_STATUS_CODE) {
				// Authorization failure.
				throw new ConfigurationException("Invalid credentials for " + exchangeParameters.getName(),
						"Check your exchange credentials " + e.getMessage());
			} else {
				// Another HTTP failure.
				throw new ConfigurationException("Error while connecting to the exchange " + e.getMessage());
			}
		} catch (Exception e) {
			throw new ConfigurationException("Unknown Configuration error : " + e.getMessage());
		}
	}

	/**
	 * Returns the XChange class based on the exchange name.
	 *
	 * @return XChange class name
	 */
	private String getExchangeClassName() {
		// XChange class package name and suffix.
		final String xChangeClassPackage = "org.knowm.xchange.";
		final String xChangeCLassSuffix = "Exchange";

		// Returns the XChange package name.
		return xChangeClassPackage                                                      // Package (org.knowm.xchange.).
				.concat(exchangeParameters.getName().toLowerCase())                     // domain (kucoin).
				.concat(".")                                                            // A dot (.)
				.concat(exchangeParameters.getName().substring(0, 1).toUpperCase())     // First letter uppercase (K).
				.concat(exchangeParameters.getName().substring(1).toLowerCase())        // The rest of the exchange name (ucoin).
				.concat(xChangeCLassSuffix);                                            // Adding exchange (Exchange).
	}

	/**
	 * Getter for exchange.
	 *
	 * @return exchange
	 */
	@Bean
	public Exchange getExchange() {
		return exchange;
	}

	/**
	 * Getter for marketDataService.
	 *
	 * @return marketDataService
	 */
	@Bean
	public MarketDataService getMarketDataService() {
		return marketDataService;
	}

	/**
	 * Getter for accountService.
	 *
	 * @return accountService
	 */
	@Bean
	public AccountService getAccountService() {
		return accountService;
	}

	/**
	 * Getter for tradeService.
	 *
	 * @return tradeService
	 */
	@Bean
	public TradeService getTradeService() {
		return tradeService;
	}

}
