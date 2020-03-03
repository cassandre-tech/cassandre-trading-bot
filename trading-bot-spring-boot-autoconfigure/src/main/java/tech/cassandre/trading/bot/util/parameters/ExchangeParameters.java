package tech.cassandre.trading.bot.util.parameters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Exchange parameters from application.properties.
 */
@SuppressWarnings("unused")
@Validated
@ConfigurationProperties(prefix = "cassandre.trading.bot.exchange")
public class ExchangeParameters {

	/** Exchange name parameter. */
	public static final String PARAMETER_NAME = "cassandre.trading.bot.exchange.name";

	/** Exchange name. For example : coinbase, kraken, kucoin... */
	@NotEmpty(message = "Exchange name is required, for example : coinbase, kraken, kucoin...")
	private String name;

	/** Sandbox parameter. */
	public static final String PARAMETER_SANDBOX = "cassandre.trading.bot.exchange.sandbox";

	/** Set it to true to use the sandbox. */
	@NotNull(message = "Sandbox is required, set it to true to use the sandbox")
	private Boolean sandbox;

	/** Username parameter. */
	public static final String PARAMETER_USERNAME = "cassandre.trading.bot.exchange.username";

	/** API username. */
	@NotEmpty(message = "API username is required")
	private String username;

	/** Passphrase parameter. */
	public static final String PARAMETER_PASSPHRASE = "cassandre.trading.bot.exchange.passphrase";

	/** API passphrase. */
	@NotEmpty(message = "API passphrase is required")
	private String passphrase;

	/** Key parameter. */
	public static final String PARAMETER_KEY = "cassandre.trading.bot.exchange.key";

	/** API key. */
	@NotEmpty(message = "API key is required")
	private String key;

	/** Secret parameter. */
	public static final String PARAMETER_SECRET = "cassandre.trading.bot.exchange.secret";

	/** API secret. */
	@NotEmpty(message = "API secret is required")
	private String secret;

	/** API Calls rates. */
	@Valid
	private Rates rates = new Rates();

	/**
	 * Exchange API rate calls.
	 */
	public static class Rates {

		/** Rate for account parameter. */
		public static final String PARAMETER_RATE_ACCOUNT = "cassandre.trading.bot.exchange.rates.account";

		/** Delay between calls to account API. */
		@NotNull(message = "Delay between calls to account API is mandatory")
		@Min(value = 0L, message = "Delay between calls to account API must be positive")
		private Long account;

		/** Rate for ticker parameter. */
		public static final String PARAMETER_RATE_TICKER = "cassandre.trading.bot.exchange.rates.ticker";

		/** Delay between calls to ticker API. */
		@NotNull(message = "Delay between calls to ticker API is mandatory")
		@Min(value = 0L, message = "Delay between calls to ticker API must be positive")
		private Long ticker;

		/** Rate for order parameter. */
		public static final String PARAMETER_RATE_ORDER = "cassandre.trading.bot.exchange.rates.order";

		/** Delay between calls to order API. */
		@NotNull(message = "Delay between calls to order API is mandatory")
		@Min(value = 0L, message = "Delay between calls to order API must be positive")
		private Long order;

		/**
		 * Getter account.
		 *
		 * @return account
		 */
		public long getAccount() {
			return account;
		}

		/**
		 * Setter account.
		 *
		 * @param newAccount the account to set
		 */
		public void setAccount(final Long newAccount) {
			account = newAccount;
		}

		/**
		 * Getter ticker.
		 *
		 * @return ticker
		 */
		public Long getTicker() {
			return ticker;
		}

		/**
		 * Setter ticker.
		 *
		 * @param newTicker the ticker to set
		 */
		public void setTicker(final Long newTicker) {
			ticker = newTicker;
		}

		/**
		 * Getter order.
		 *
		 * @return order
		 */
		public Long getOrder() {
			return order;
		}

		/**
		 * Setter order.
		 *
		 * @param newOrder the order to set
		 */
		public void setOrder(final Long newOrder) {
			order = newOrder;
		}

		@Override
		public final String toString() {
			return "Rate{"
					+ " account=" + getAccount()
					+ ", ticker=" + getTicker()
					+ ", order=" + getOrder()
					+ '}';
		}

	}

	/**
	 * Getter name.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter name.
	 *
	 * @param newName the name to set
	 */
	public void setName(final String newName) {
		name = newName;
	}

	/**
	 * Getter sandbox.
	 *
	 * @return sandbox
	 */
	public Boolean isSandbox() {
		return sandbox;
	}

	/**
	 * Setter sandbox.
	 *
	 * @param newSandbox the sandbox to set
	 */
	public void setSandbox(final Boolean newSandbox) {
		sandbox = newSandbox;
	}

	/**
	 * Getter username.
	 *
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Setter username.
	 *
	 * @param newUsername the username to set
	 */
	public void setUsername(final String newUsername) {
		username = newUsername;
	}

	/**
	 * Getter passphrase.
	 *
	 * @return passphrase
	 */
	public String getPassphrase() {
		return passphrase;
	}

	/**
	 * Setter passphrase.
	 *
	 * @param newPassphrase the passphrase to set
	 */
	public void setPassphrase(final String newPassphrase) {
		passphrase = newPassphrase;
	}

	/**
	 * Getter key.
	 *
	 * @return key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter key.
	 *
	 * @param newKey the key to set
	 */
	public void setKey(final String newKey) {
		key = newKey;
	}

	/**
	 * Getter secret.
	 *
	 * @return secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * Setter secret.
	 *
	 * @param newSecret the secret to set
	 */
	public void setSecret(final String newSecret) {
		secret = newSecret;
	}

	/**
	 * Getter rate.
	 *
	 * @return rate
	 */
	public Rates getRates() {
		return rates;
	}

	/**
	 * Setter rate.
	 *
	 * @param newRates the rate to set
	 */
	public void setRates(final Rates newRates) {
		rates = newRates;
	}

	@Override
	public final String toString() {
		return "ExchangeParameters{"
				+ " name='" + getName() + '\''
				+ ", sandbox=" + isSandbox()
				+ ", username='" + getUsername() + '\''
				+ ", passphrase='" + getPassphrase() + '\''
				+ ", key='" + getKey() + '\''
				+ ", secret='" + getSecret() + '\''
				+ ", rate=" + getRates()
				+ '}';
	}

}

