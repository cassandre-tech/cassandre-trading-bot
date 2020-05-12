package tech.cassandre.trading.bot.configuration;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import si.mazi.rescu.HttpStatusIOException;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.ExchangeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.MarketServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.TradeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.UserServiceXChangeImplementation;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.StringJoiner;

/**
 * ExchangeConfiguration class configures the exchange connection.
 */
@Configuration
@EnableConfigurationProperties(ExchangeParameters.class)
public class ExchangeAutoConfiguration extends BaseConfiguration {

    /** XChange user sandbox parameter. */
    private static final String USE_SANDBOX_PARAMETER = "Use_Sandbox";

    /** XChange passphrase parameter. */
    private static final String PASSPHRASE_PARAMETER = "passphrase";

    /** Unauthorized http status code. */
    public static final int UNAUTHORIZED_STATUS_CODE = 401;

    /** Exchange parameters. */
    private final ExchangeParameters exchangeParameters;

    /** Exchange service. */
    private ExchangeService exchangeService;

    /** User service. */
    private UserService userService;

    /** Market service. */
    private MarketService marketService;

    /** Trade service. */
    private TradeService tradeService;

    /** Account flux. */
    private AccountFlux accountFlux;

    /** Ticker flux. */
    private TickerFlux tickerFlux;

    /** Order flux. */
    private OrderFlux orderFlux;

    /**
     * Constructor.
     *
     * @param newExchangeParameters exchange parameters
     */
    public ExchangeAutoConfiguration(final ExchangeParameters newExchangeParameters) {
        this.exchangeParameters = newExchangeParameters;
    }

    /**
     * Instantiating the exchange based on the parameter.
     */
    @PostConstruct
    public void configure() {
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

            // Creates XChange services.
            final Exchange xChangeExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
            final AccountService xChangeAccountService = xChangeExchange.getAccountService();
            final MarketDataService xChangeMarketDataService = xChangeExchange.getMarketDataService();
            final org.knowm.xchange.service.trade.TradeService xChangeTradeService = xChangeExchange.getTradeService();

            // Retrieve rates as string value.
            long accountRate = getRateValue(exchangeParameters.getRates().getAccount(), "Invalid account rate");
            long tickerRate = getRateValue(exchangeParameters.getRates().getTicker(), "Invalid ticker rate");
            long orderRate = getRateValue(exchangeParameters.getRates().getOrder(), "Invalid order rate");

            // Creates Cassandre services.
            exchangeService = new ExchangeServiceXChangeImplementation(xChangeExchange);
            userService = new UserServiceXChangeImplementation(accountRate, xChangeAccountService);
            marketService = new MarketServiceXChangeImplementation(tickerRate, xChangeMarketDataService);
            tradeService = new TradeServiceXChangeImplementation(orderRate, xChangeTradeService);

            // Creates Cassandre flux.
            accountFlux = new AccountFlux(userService);
            tickerFlux = new TickerFlux(marketService);
            orderFlux = new OrderFlux(tradeService);

            // Force login to check credentials.
            xChangeAccountService.getAccountInfo();
            getLogger().info("ExchangeConfiguration - Connection to {} successful", exchangeParameters.getName());

            // Prints all the supported currency pairs.
            StringJoiner currencyPairList = new StringJoiner(", ");
            exchangeService.getAvailableCurrencyPairs()
                    .forEach(currencyPairDTO -> currencyPairList.add(currencyPairDTO.toString()));
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
                e.printStackTrace();
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
     * Return rate value.
     *
     * @param stringValue  string value
     * @param errorMessage error message
     * @return long value (ms)
     */
    private static long getRateValue(final String stringValue, final String errorMessage) {
        if (isNumeric(stringValue)) {
            return Long.parseLong(stringValue);
        } else {
            try {
                return Duration.parse(stringValue).toMillis();
            } catch (DateTimeParseException e) {
                throw new ConfigurationException(errorMessage,
                        "Enter a long value (ex: 123) or a standard ISO 8601 duration (ex: PT10H)");
            }
        }
    }

    /**
     * Returns true is a string is a number.
     *
     * @param string string to test
     * @return true if numeric
     */
    private static boolean isNumeric(final String string) {
        // null or empty
        if (string == null || string.length() == 0) {
            return false;
        }
        for (char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Getter exchangeService.
     *
     * @return exchangeService
     */
    @Bean
    public ExchangeService getExchangeService() {
        return exchangeService;
    }

    /**
     * Getter userService.
     *
     * @return userService
     */
    @Bean
    public UserService getUserService() {
        return userService;
    }

    /**
     * Getter marketService.
     *
     * @return marketService
     */
    @Bean
    public MarketService getMarketService() {
        return marketService;
    }

    /**
     * Getter tradeService.
     *
     * @return tradeService
     */
    @Bean
    public TradeService getTradeService() {
        return tradeService;
    }

    /**
     * Getter accountFlux.
     *
     * @return accountFlux
     */
    @Bean
    public AccountFlux getAccountFlux() {
        return accountFlux;
    }

    /**
     * Getter tickerFlux.
     *
     * @return tickerFlux
     */
    @Bean
    public TickerFlux getTickerFlux() {
        return tickerFlux;
    }

    /**
     * Getter orderFlux.
     *
     * @return orderFlux
     */
    @Bean
    public OrderFlux getOrderFlux() {
        return orderFlux;
    }

}
