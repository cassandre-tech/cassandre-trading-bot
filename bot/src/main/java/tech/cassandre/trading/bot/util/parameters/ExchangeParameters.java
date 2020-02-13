package tech.cassandre.trading.bot.util.parameters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Exchange parameters from application.properties.
 */
@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "cassandre.trading.bot.exchange")
public class ExchangeParameters {

	/** Exchange name. For example : coinbase, kraken, kucoin... */
	private String name;

	/** Set it to true to use the sandbox. */
	private boolean sandbox;

	/** API username. */
	private String username;

	/** API passphrase. */
	private String passphrase;

	/** API key. */
	private String key;

	/** API secret. */
	private String secret;

	/**
	 * Getter name.
	 *
	 * @return name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Setter name.
	 *
	 * @param newName the name to set
	 */
	public final void setName(final String newName) {
		name = newName;
	}

	/**
	 * Getter sandbox.
	 *
	 * @return sandbox
	 */
	public final boolean isSandbox() {
		return sandbox;
	}

	/**
	 * Setter sandbox.
	 *
	 * @param newSandbox the sandbox to set
	 */
	public final void setSandbox(final boolean newSandbox) {
		sandbox = newSandbox;
	}

	/**
	 * Getter username.
	 *
	 * @return username
	 */
	public final String getUsername() {
		return username;
	}

	/**
	 * Setter username.
	 *
	 * @param newUsername the username to set
	 */
	public final void setUsername(final String newUsername) {
		username = newUsername;
	}

	/**
	 * Getter passphrase.
	 *
	 * @return passphrase
	 */
	public final String getPassphrase() {
		return passphrase;
	}

	/**
	 * Setter passphrase.
	 *
	 * @param newPassphrase the passphrase to set
	 */
	public final void setPassphrase(final String newPassphrase) {
		passphrase = newPassphrase;
	}

	/**
	 * Getter key.
	 *
	 * @return key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * Setter key.
	 *
	 * @param newKey the key to set
	 */
	public final void setKey(final String newKey) {
		key = newKey;
	}

	/**
	 * Getter secret.
	 *
	 * @return secret
	 */
	public final String getSecret() {
		return secret;
	}

	/**
	 * Setter secret.
	 *
	 * @param newSecret the secret to set
	 */
	public final void setSecret(final String newSecret) {
		secret = newSecret;
	}

}
