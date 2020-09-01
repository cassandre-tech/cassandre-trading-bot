package tech.cassandre.trading.bot.util.parameters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import tech.cassandre.trading.bot.util.validator.Rate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Exchange parameters from application.properties.
 */
@Validated
@ConfigurationProperties(prefix = "cassandre.trading.bot.exchange")
public class ExchangeParameters {

    /** Exchange name parameter. */
    public static final String PARAMETER_NAME = "cassandre.trading.bot.exchange.name";

    /** Username parameter. */
    public static final String PARAMETER_USERNAME = "cassandre.trading.bot.exchange.username";

    /** Passphrase parameter. */
    public static final String PARAMETER_PASSPHRASE = "cassandre.trading.bot.exchange.passphrase";

    /** Key parameter. */
    public static final String PARAMETER_KEY = "cassandre.trading.bot.exchange.key";

    /** Secret parameter. */
    public static final String PARAMETER_SECRET = "cassandre.trading.bot.exchange.secret";

    /** Exchange name. For example : coinbase, kraken, kucoin. */
    @NotEmpty(message = "Exchange name required, for example : coinbase, kraken, kucoin...")
    private String name;

    /** API username. */
    @NotEmpty(message = "API username required")
    private String username;

    /** API passphrase. */
    @NotEmpty(message = "API passphrase required")
    private String passphrase;

    /** API key. */
    @NotEmpty(message = "API key required")
    private String key;

    /** API secret. */
    @NotEmpty(message = "API secret required")
    private String secret;

    /** Modes. */
    @Valid
    private static Modes modes = new Modes();

    /** API Calls rates. */
    @Valid
    private static Rates rates = new Rates();

    /** Exchange API rate calls. */
    @ConfigurationProperties(prefix = "cassandre.trading.bot.exchange.modes")
    public static class Modes {

        /** Sandbox parameter. */
        public static final String PARAMETER_SANDBOX = "cassandre.trading.bot.exchange.modes.sandbox";

        /** Dry parameter. */
        public static final String PARAMETER_DRY = "cassandre.trading.bot.exchange.modes.dry";

        /** Set it to true to use the sandbox. */
        @NotNull(message = "Sandbox parameter required, set it to true to use the sandbox")
        private Boolean sandbox;

        /** Set it to true to use the dry mode. */
        @NotNull(message = "Dry parameter required, set it to true to use the dry mode")
        private Boolean dry;

        /**
         * Getter for sandbox.
         *
         * @return sandbox
         */
        public Boolean isSandbox() {
            return sandbox;
        }

        /**
         * Setter for sandbox.
         *
         * @param newSandbox the sandbox to set
         */
        public void setSandbox(final Boolean newSandbox) {
            sandbox = newSandbox;
        }

        /**
         * Getter dry.
         *
         * @return dry
         */
        public Boolean isDry() {
            return dry;
        }

        /**
         * Setter dry.
         *
         * @param newDry the dry to set
         */
        public void setDry(final Boolean newDry) {
            dry = newDry;
        }

        @Override
        public final String toString() {
            return "Modes{"
                    + " sandbox=" + sandbox
                    + ", dry=" + dry
                    + '}';
        }

    }

    /** Exchange API rate calls. */
    @ConfigurationProperties(prefix = "cassandre.trading.bot.exchange.rates")
    public static class Rates {

        /** Rate for account parameter. */
        public static final String PARAMETER_RATE_ACCOUNT = "cassandre.trading.bot.exchange.rates.account";

        /** Rate for ticker parameter. */
        public static final String PARAMETER_RATE_TICKER = "cassandre.trading.bot.exchange.rates.ticker";

        /** Rate for order parameter. */
        public static final String PARAMETER_RATE_ORDER = "cassandre.trading.bot.exchange.rates.trade";

        /** Delay between calls to account API. */
        @NotNull(message = "Delay between calls to account API is mandatory")
        @Rate(message = "Invalid account rate - Enter a long value (ex: 123) or a standard ISO 8601 duration (ex: PT10H)")
        private String account;

        /** Delay between calls to ticker API. */
        @NotNull(message = "Delay between calls to ticker API is mandatory")
        @Rate(message = "Invalid ticker rate - Enter a long value (ex: 123) or a standard ISO 8601 duration (ex: PT10H)")
        private String ticker;

        /** Delay between calls to trade API. */
        @NotNull(message = "Delay between calls to trade API is mandatory")
        @Rate(message = "Invalid trade rate - Enter a long value (ex: 123) or a standard ISO 8601 duration (ex: PT10H)")
        private String trade;

        /**
         * Getter for account.
         *
         * @return account
         */
        public String getAccount() {
            return account;
        }

        /**
         * Setter for account.
         *
         * @param newAccount the account to set
         */
        public void setAccount(final String newAccount) {
            account = newAccount;
        }

        /**
         * Getter for ticker.
         *
         * @return ticker
         */
        public String getTicker() {
            return ticker;
        }

        /**
         * Setter for ticker.
         *
         * @param newTicker the ticker to set
         */
        public void setTicker(final String newTicker) {
            ticker = newTicker;
        }

        /**
         * Getter for order.
         *
         * @return order
         */
        public String getTrade() {
            return trade;
        }

        /**
         * Setter for order.
         *
         * @param newOrder the order
         */
        public void setTrade(final String newOrder) {
            trade = newOrder;
        }

        @Override
        public final String toString() {
            return "Rate{"
                    + " account=" + getAccount()
                    + ", ticker=" + getTicker()
                    + ", order=" + getTrade()
                    + '}';
        }

    }

    /**
     * Getter for name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name.
     *
     * @param newName the name to set
     */
    public void setName(final String newName) {
        name = newName;
    }

    /**
     * Getter modes.
     *
     * @return mode
     */
    public Modes getModes() {
        return modes;
    }

    /**
     * Setter modes.
     *
     * @param newModes the modes to set
     */
    public void setModes(final Modes newModes) {
        modes = newModes;
    }

    /**
     * Getter for username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for username.
     *
     * @param newUsername the username to set
     */
    public void setUsername(final String newUsername) {
        username = newUsername;
    }

    /**
     * Getter for passphrase.
     *
     * @return passphrase
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * Setter for passphrase.
     *
     * @param newPassphrase the passphrase to set
     */
    public void setPassphrase(final String newPassphrase) {
        passphrase = newPassphrase;
    }

    /**
     * Getter for key.
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter for key.
     *
     * @param newKey the key to set
     */
    public void setKey(final String newKey) {
        key = newKey;
    }

    /**
     * Getter for secret.
     *
     * @return secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Setter for secret.
     *
     * @param newSecret the secret to set
     */
    public void setSecret(final String newSecret) {
        secret = newSecret;
    }

    /**
     * Getter for rate.
     *
     * @return rate
     */
    public Rates getRates() {
        return rates;
    }

    /**
     * Setter for rate.
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
                + ", username='" + getUsername() + '\''
                + ", passphrase='" + getPassphrase() + '\''
                + ", key='" + getKey() + '\''
                + ", secret='" + getSecret() + '\''
                + ", modes=" + getModes()
                + ", rates=" + getRates()
                + '}';
    }

}
