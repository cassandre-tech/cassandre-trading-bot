package tech.cassandre.trading.bot.configuration;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * ExchangeConfiguration class configure exchange connection.
 */
@Configuration
public class ExchangeConfiguration {

	/** XChange user sandbox parameter. */
	private static final String USE_SANDBOX_PARAMETER = "Use_Sandbox";

	/** XChange passphrase parameter. */
	private static final String PASSPHRASE_PARAMETER = "passphrase";

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
		if (exchangeParameters.getName() != null) {
			return xChangeClassPackage                                                      // Package (org.knowm.xchange.).
					.concat(exchangeParameters.getName().toLowerCase())                     // domain (kucoin).
					.concat(".")                                                            // A dot (.)
					.concat(exchangeParameters.getName().substring(0, 1).toUpperCase())     // First letter uppercase (K).
					.concat(exchangeParameters.getName().substring(1).toLowerCase())        // The rest of the exchange name (ucoin).
					.concat(xChangeCLassSuffix);                                            // Adding exchange (Exchange).
		} else {
			return "";
		}
	}

}
