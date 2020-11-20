package tech.cassandre.trading.bot.configuration;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import si.mazi.rescu.HttpStatusIOException;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.dry.ExchangeServiceDryModeImplementation;
import tech.cassandre.trading.bot.service.dry.TradeServiceDryModeImplementation;
import tech.cassandre.trading.bot.service.dry.UserServiceDryModeImplementation;
import tech.cassandre.trading.bot.service.xchange.ExchangeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.xchange.MarketServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.xchange.TradeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.xchange.UserServiceXChangeImplementation;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.StringJoiner;

/**
 * ExchangeConfiguration configures the exchange connection.
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

    /** Application context. */
    private final ApplicationContext applicationContext;

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

    /** Trade flux. */
    private TradeFlux tradeFlux;

    /** Position flux. */
    private PositionFlux positionFlux;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     * @param newExchangeParameters exchange parameters
     * @param newOrderRepository    order repository
     * @param newTradeRepository    trade repository
     * @param newPositionRepository position repository
     */
    public ExchangeAutoConfiguration(final ApplicationContext newApplicationContext,
                                     final ExchangeParameters newExchangeParameters,
                                     final OrderRepository newOrderRepository,
                                     final TradeRepository newTradeRepository,
                                     final PositionRepository newPositionRepository) {
        this.applicationContext = newApplicationContext;
        this.exchangeParameters = newExchangeParameters;
        this.orderRepository = newOrderRepository;
        this.tradeRepository = newTradeRepository;
        this.positionRepository = newPositionRepository;
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
            exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX_PARAMETER, exchangeParameters.getModes().isSandbox());
            exchangeSpecification.setUserName(exchangeParameters.getUsername());
            exchangeSpecification.setExchangeSpecificParametersItem(PASSPHRASE_PARAMETER, exchangeParameters.getPassphrase());
            exchangeSpecification.setApiKey(exchangeParameters.getKey());
            exchangeSpecification.setSecretKey(exchangeParameters.getSecret());

            // Specific parameters.
            if (exchangeParameters.getProxyHost() != null) {
                exchangeSpecification.setProxyHost(exchangeParameters.getProxyHost());
            }
            if (exchangeParameters.getProxyPort() != null) {
                exchangeSpecification.setProxyPort(exchangeParameters.getProxyPort());
            }

            // Creates XChange services.
            final Exchange xChangeExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
            final AccountService xChangeAccountService = xChangeExchange.getAccountService();
            final MarketDataService xChangeMarketDataService = xChangeExchange.getMarketDataService();
            final org.knowm.xchange.service.trade.TradeService xChangeTradeService = xChangeExchange.getTradeService();

            // Retrieve rates.
            long accountRate = getRateValue(exchangeParameters.getRates().getAccount());
            long tickerRate = getRateValue(exchangeParameters.getRates().getTicker());
            long tradeRate = getRateValue(exchangeParameters.getRates().getTrade());

            // Creates Cassandre services.
            UserServiceDryModeImplementation userServiceDryMode;
            TradeServiceDryModeImplementation tradeServiceDryMode = null;
            if (!exchangeParameters.getModes().isDry()) {
                // Normal mode.
                getLogger().info("Dry mode is off");
                this.exchangeService = new ExchangeServiceXChangeImplementation(xChangeExchange);
                this.userService = new UserServiceXChangeImplementation(accountRate, xChangeAccountService);
                this.marketService = new MarketServiceXChangeImplementation(tickerRate, xChangeMarketDataService);
                this.tradeService = new TradeServiceXChangeImplementation(tradeRate, xChangeTradeService, tradeRepository, orderRepository);
            } else {
                // Dry mode.
                getLogger().info("Dry mode is on");
                this.exchangeService = new ExchangeServiceDryModeImplementation(applicationContext);
                userServiceDryMode = new UserServiceDryModeImplementation();
                this.userService = userServiceDryMode;
                this.marketService = new MarketServiceXChangeImplementation(tickerRate, xChangeMarketDataService);
                tradeServiceDryMode = new TradeServiceDryModeImplementation(userServiceDryMode, tradeRepository, orderRepository);
                this.tradeService = tradeServiceDryMode;
            }

            // Creates Cassandre flux.
            accountFlux = new AccountFlux(userService);
            tickerFlux = new TickerFlux(marketService);
            orderFlux = new OrderFlux(tradeService, orderRepository);
            tradeFlux = new TradeFlux(tradeService, tradeRepository);
            positionFlux = new PositionFlux(positionRepository);

            // Force login to check credentials.
            xChangeAccountService.getAccountInfo();
            getLogger().info("ExchangeConfiguration - Connection to {} successful", exchangeParameters.getName());

            // Prints all the supported currency pairs.
            StringJoiner currencyPairList = new StringJoiner(", ");
            exchangeService.getAvailableCurrencyPairs()
                    .forEach(currencyPairDTO -> currencyPairList.add(currencyPairDTO.toString()));
            getLogger().info("ExchangeConfiguration - Supported currency pairs : " + currencyPairList);

            // if in dry mode, we set dependencies.
            if (tradeService instanceof TradeServiceDryModeImplementation) {
                assert tradeServiceDryMode != null;
                tradeServiceDryMode.setDependencies(orderFlux, tradeFlux);
            }
        } catch (ClassNotFoundException e) {
            // If we can't find the exchange class.
            throw new ConfigurationException("Impossible to find the exchange you requested : " + exchangeParameters.getName(),
                    "Choose a valid exchange (https://github.com/knowm/XChange) and/or add the dependency to Cassandre");
        } catch (HttpStatusIOException e) {
            if (e.getHttpStatusCode() == UNAUTHORIZED_STATUS_CODE) {
                // Authorization failure.
                e.printStackTrace();
                throw new ConfigurationException("Invalid credentials for " + exchangeParameters.getName(),
                        "Check your exchange credentials " + e.getMessage() + " - login used : " + exchangeParameters.getUsername());
            } else {
                // Another HTTP failure.
                e.printStackTrace();
                throw new ConfigurationException("Error while connecting to the exchange " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
     * @param stringValue string value
     * @return long value (ms)
     */
    private static long getRateValue(final String stringValue) {
        if (isNumeric(stringValue)) {
            return Long.parseLong(stringValue);
        } else {
            return Duration.parse(stringValue).toMillis();
        }
    }

    /**
     * Returns true if a string is a number.
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
     * Getter for exchangeService.
     *
     * @return exchangeService
     */
    @Bean
    public ExchangeService getExchangeService() {
        return exchangeService;
    }

    /**
     * Getter for userService.
     *
     * @return userService
     */
    @Bean
    public UserService getUserService() {
        return userService;
    }

    /**
     * Getter for marketService.
     *
     * @return marketService
     */
    @Bean
    public MarketService getMarketService() {
        return marketService;
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

    /**
     * Getter for accountFlux.
     *
     * @return accountFlux
     */
    @Bean
    public AccountFlux getAccountFlux() {
        return accountFlux;
    }

    /**
     * Getter for tickerFlux.
     *
     * @return tickerFlux
     */
    @Bean
    public TickerFlux getTickerFlux() {
        return tickerFlux;
    }

    /**
     * Getter for orderFlux.
     *
     * @return orderFlux
     */
    @Bean
    public OrderFlux getOrderFlux() {
        return orderFlux;
    }

    /**
     * Getter for tradeFlux.
     *
     * @return tradeFlux
     */
    @Bean
    public TradeFlux getTradeFlux() {
        return tradeFlux;
    }

    /**
     * Getter for positionFlux.
     *
     * @return positionFlux
     */
    @Bean
    public PositionFlux getPositionFlux() {
        return positionFlux;
    }

}
