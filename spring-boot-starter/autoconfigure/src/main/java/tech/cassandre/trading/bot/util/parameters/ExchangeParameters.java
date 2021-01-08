package tech.cassandre.trading.bot.util.parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "cassandre.trading.bot.exchange")
public class ExchangeParameters {

    /** Exchange name parameter. */
    public static final String PARAMETER_EXCHANGE_NAME = "cassandre.trading.bot.exchange.name";

    /** Username parameter. */
    public static final String PARAMETER_EXCHANGE_USERNAME = "cassandre.trading.bot.exchange.username";

    /** Passphrase parameter. */
    public static final String PARAMETER_EXCHANGE_PASSPHRASE = "cassandre.trading.bot.exchange.passphrase";

    /** Key parameter. */
    public static final String PARAMETER_EXCHANGE_KEY = "cassandre.trading.bot.exchange.key";

    /** Secret parameter. */
    public static final String PARAMETER_EXCHANGE_SECRET = "cassandre.trading.bot.exchange.secret";

    /** Proxy host. */
    public static final String PARAMETER_EXCHANGE_PROXY_HOST = "cassandre.trading.bot.exchange.proxyHost";

    /** Proxy port. */
    public static final String PARAMETER_EXCHANGE_PROXY_PORT = "cassandre.trading.bot.exchange.proxyPort";

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

    /** Proxy host. */
    private String proxyHost;

    /** Proxy port. */
    private Integer proxyPort;

    /** Modes. */
    @Valid
    private Modes modes = new Modes();

    /** API Calls rates. */
    @Valid
    private Rates rates = new Rates();

    /** Exchange API rate calls. */
    @Validated
    @Getter
    @Setter
    @ToString
    @ConfigurationProperties(prefix = "cassandre.trading.bot.exchange.modes")
    public class Modes {

        /** Sandbox parameter. */
        public static final String PARAMETER_EXCHANGE_SANDBOX = "cassandre.trading.bot.exchange.modes.sandbox";

        /** Dry parameter. */
        public static final String PARAMETER_EXCHANGE_DRY = "cassandre.trading.bot.exchange.modes.dry";

        /** Set it to true to use the sandbox. */
        @NotNull(message = "Sandbox parameter required, set it to true to use the sandbox")
        private Boolean sandbox;

        /** Set it to true to use the dry mode. */
        @NotNull(message = "Dry parameter required, set it to true to use the dry mode")
        private Boolean dry;

    }

    /** Exchange API rate calls. */
    @Validated
    @Getter
    @Setter
    @ToString
    @ConfigurationProperties(prefix = "cassandre.trading.bot.exchange.rates")
    public static class Rates {

        /** Rate for account parameter. */
        public static final String PARAMETER_EXCHANGE_RATE_ACCOUNT = "cassandre.trading.bot.exchange.rates.account";

        /** Rate for ticker parameter. */
        public static final String PARAMETER_EXCHANGE_RATE_TICKER = "cassandre.trading.bot.exchange.rates.ticker";

        /** Rate for order parameter. */
        public static final String PARAMETER_EXCHANGE_RATE_TRADE = "cassandre.trading.bot.exchange.rates.trade";

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

    }

}
