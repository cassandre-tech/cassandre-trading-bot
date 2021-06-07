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

    /** Driver class name. For example : coinbase, kraken, kucoin. */
    @NotEmpty(message = "Driver class  name required, for example : org.knowm.xchange.coinbasepro.CoinbaseProExchange")
    private String driverClassName;

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

    /** Secure API endpoint. */
    private String sslUri;

    /** Plain text API endpoint. */
    private String plainTextUri;

    /** Exchange port parameter. */
    private String host;

    /** Exchange port parameter. */
    private String port;

    /** Modes. */
    @Valid
    private Modes modes = new Modes();

    /** API Calls rates. */
    @Valid
    private Rates rates = new Rates();

    /** Exchange modes. */
    @Validated
    @Getter
    @Setter
    @ToString
    @ConfigurationProperties(prefix = "cassandre.trading.bot.exchange.modes")
    public static class Modes {

        /** Set it to true to use the sandbox. */
        @NotNull(message = "Sandbox parameter required, set it to true to use the exchange sandbox")
        private Boolean sandbox;

        /** Set it to true to use the dry mode. */
        @NotNull(message = "Dry parameter required, set it to true to use the dry mode (simulated exchange)")
        private Boolean dry;

    }

    /** Exchange API rate calls. */
    @Validated
    @Getter
    @Setter
    @ToString
    @ConfigurationProperties(prefix = "cassandre.trading.bot.exchange.rates")
    public static class Rates {

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
