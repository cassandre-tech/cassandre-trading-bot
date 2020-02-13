package tech.cassandre.trading.bot.util.parameters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Rate parameters from application.properties.
 */
@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "cassandre.trading.bot.rate")
public class RateParameters {

	/** Delay between calls to account API. */
	private long account;

	/** Delay between calls to ticker API. */
	private long ticker;

	/** Delay between calls to order API. */
	private long order;

	/**
	 * Getter account.
	 *
	 * @return account
	 */
	public final long getAccount() {
		return account;
	}

	/**
	 * Setter account.
	 *
	 * @param newAccount the account to set
	 */
	public final void setAccount(final long newAccount) {
		account = newAccount;
	}

	/**
	 * Getter ticker.
	 *
	 * @return ticker
	 */
	public final long getTicker() {
		return ticker;
	}

	/**
	 * Setter ticker.
	 *
	 * @param newTicker the ticker to set
	 */
	public final void setTicker(final long newTicker) {
		ticker = newTicker;
	}

	/**
	 * Getter order.
	 *
	 * @return order
	 */
	public final long getOrder() {
		return order;
	}

	/**
	 * Setter order.
	 *
	 * @param newOrder the order to set
	 */
	public final void setOrder(final long newOrder) {
		order = newOrder;
	}

}
