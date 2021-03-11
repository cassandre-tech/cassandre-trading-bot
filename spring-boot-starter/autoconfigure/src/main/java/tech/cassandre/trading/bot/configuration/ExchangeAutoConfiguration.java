package tech.cassandre.trading.bot.configuration;

import org.apache.commons.lang3.math.NumberUtils;
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
import tech.cassandre.trading.bot.domain.ExchangeAccount;
import tech.cassandre.trading.bot.repository.ExchangeAccountRepository;
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
import tech.cassandre.trading.bot.util.base.configuration.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Optional;
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

    /** XChange. */
    private Exchange xChangeExchange;

    /** XChange account service. */
    private AccountService xChangeAccountService;

    /** XChange market data service. */
    private MarketDataService xChangeMarketDataService;

    /** XChange trade service. */
    private org.knowm.xchange.service.trade.TradeService xChangeTradeService;

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

    /** Exchange account repository. */
    private final ExchangeAccountRepository exchangeAccountRepository;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param newApplicationContext        application context
     * @param newExchangeParameters        exchange parameters
     * @param newExchangeAccountRepository exchange account repository
     * @param newOrderRepository           order repository
     * @param newTradeRepository           trade repository
     * @param newPositionRepository        position repository
     */
    public ExchangeAutoConfiguration(final ApplicationContext newApplicationContext,
                                     final ExchangeParameters newExchangeParameters,
                                     final ExchangeAccountRepository newExchangeAccountRepository,
                                     final OrderRepository newOrderRepository,
                                     final TradeRepository newTradeRepository,
                                     final PositionRepository newPositionRepository) {
        this.applicationContext = newApplicationContext;
        this.exchangeParameters = newExchangeParameters;
        this.exchangeAccountRepository = newExchangeAccountRepository;
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
            Class<? extends Exchange> exchangeClass = Class.forName(getExchangeClassName()).asSubclass(Exchange.class);
            ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClass);

            // Exchange configuration.
            exchangeSpecification.setExchangeSpecificParametersItem(USE_SANDBOX_PARAMETER, exchangeParameters.getModes().getSandbox());
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
            if (exchangeParameters.getSslUri() != null) {
                exchangeSpecification.setSslUri(exchangeParameters.getSslUri());
            }
            if (exchangeParameters.getPlainTextUri() != null) {
                exchangeSpecification.setPlainTextUri(exchangeParameters.getPlainTextUri());
            }
            if (exchangeParameters.getHost() != null) {
                exchangeSpecification.setHost(exchangeParameters.getHost());
            }
            if (exchangeParameters.getPort() != null) {
                exchangeSpecification.setPort(Integer.parseInt(exchangeParameters.getPort()));
            }

            // Creates XChange services.
            xChangeExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
            xChangeAccountService = xChangeExchange.getAccountService();
            xChangeMarketDataService = xChangeExchange.getMarketDataService();
            xChangeTradeService = xChangeExchange.getTradeService();

            // Retrieve rates.
            long accountRate = getRateValue(exchangeParameters.getRates().getAccount());
            long tickerRate = getRateValue(exchangeParameters.getRates().getTicker());
            long tradeRate = getRateValue(exchangeParameters.getRates().getTrade());

            // Creates Cassandre services.
            UserServiceDryModeImplementation userServiceDryMode;
            TradeServiceDryModeImplementation tradeServiceDryMode = null;
            if (!exchangeParameters.getModes().getDry()) {
                // Normal mode.
                logger.info("ExchangeConfiguration - Dry mode is OFF");
                this.exchangeService = new ExchangeServiceXChangeImplementation(xChangeExchange);
                this.userService = new UserServiceXChangeImplementation(accountRate, xChangeAccountService);
                this.marketService = new MarketServiceXChangeImplementation(tickerRate, xChangeMarketDataService);
                this.tradeService = new TradeServiceXChangeImplementation(tradeRate, orderRepository, xChangeTradeService);
            } else {
                // Dry mode.
                logger.info("ExchangeConfiguration - Dry mode is ON");
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
            tradeFlux = new TradeFlux(tradeService, orderRepository, tradeRepository);
            positionFlux = new PositionFlux(positionRepository, orderRepository);

            // Force login to check credentials.
            xChangeAccountService.getAccountInfo();
            logger.info("ExchangeConfiguration - Connection to {} successful", exchangeParameters.getName());

            // Prints all the supported currency pairs.
            StringJoiner currencyPairList = new StringJoiner(", ");
            exchangeService.getAvailableCurrencyPairs().forEach(currencyPairDTO -> currencyPairList.add(currencyPairDTO.toString()));
            logger.info("ExchangeConfiguration - Supported currency pairs : {} ", currencyPairList);

            // if in dry mode, we set dependencies.
            if (tradeService instanceof TradeServiceDryModeImplementation) {
                assert tradeServiceDryMode != null;
                tradeServiceDryMode.setDependencies(orderFlux, tradeFlux);
            }

            // Save the exchange account in database.
            Optional<ExchangeAccount> exchangeAccount = exchangeAccountRepository.findByExchangeAndAccount(exchangeParameters.getName(), exchangeParameters.getUsername());
            if (exchangeAccount.isEmpty()) {
                ExchangeAccount ea = new ExchangeAccount();
                ea.setExchange(exchangeParameters.getName());
                ea.setAccount(exchangeParameters.getUsername());
                ea = exchangeAccountRepository.save(ea);
                logger.info("ExchangeConfiguration - Exchange configuration saved in database {}", ea);
            }
        } catch (ClassNotFoundException e) {
            // If we can't find the exchange class.
            throw new ConfigurationException("Impossible to find the exchange you requested : " + exchangeParameters.getName(),
                    "Choose a valid exchange (https://github.com/knowm/XChange) and add the dependency to Cassandre");
        } catch (HttpStatusIOException e) {
            if (e.getHttpStatusCode() == UNAUTHORIZED_STATUS_CODE) {
                // Authorization failure.
                throw new ConfigurationException("Invalid credentials for " + exchangeParameters.getName(),
                        "Check your exchange credentials : " + e.getMessage() + " - login used : " + exchangeParameters.getUsername());
            } else {
                // Another HTTP failure.
                throw new ConfigurationException("Error while connecting to the exchange : " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ConfigurationException("Unknown configuration error : " + e.getMessage());
        }
    }

    /**
     * Returns the XChange class based on the exchange name.
     *
     * @return XChange class name
     */
    private String getExchangeClassName() {
        // If the name contains a dot, it means that it's the XChange class name.
        if (exchangeParameters.getName() != null && exchangeParameters.getName().contains(".")) {
            return exchangeParameters.getName();
        }

        // XChange class package name and suffix.
        final String xChangeClassPackage = "org.knowm.xchange.";
        final String xChangeCLassSuffix = "Exchange";

        // Returns the XChange package name.
        assert exchangeParameters.getName() != null;
        return xChangeClassPackage                                                      // Package (org.knowm.xchange.).
                .concat(exchangeParameters.getName().toLowerCase())                     // domain (kucoin).
                .concat(".")                                                            // A dot (.)
                .concat(exchangeParameters.getName().substring(0, 1).toUpperCase())     // First letter uppercase (K).
                .concat(exchangeParameters.getName().substring(1).toLowerCase())        // The rest of the exchange name (ucoin).
                .concat(xChangeCLassSuffix);                                            // Adding exchange (Exchange).
    }

    /**
     * Return rate value in ms.
     *
     * @param stringValue string value
     * @return long value (ms)
     */
    private static long getRateValue(final String stringValue) {
        if (NumberUtils.isCreatable(stringValue)) {
            return Long.parseLong(stringValue);
        } else {
            return Duration.parse(stringValue).toMillis();
        }
    }

    /**
     * Getter xChangeExchange.
     *
     * @return xChangeExchange
     */
    @Bean
    public Exchange getXChangeExchange() {
        return xChangeExchange;
    }

    /**
     * Getter xChangeAccountService.
     *
     * @return xChangeAccountService
     */
    @Bean
    public AccountService getXChangeAccountService() {
        return xChangeAccountService;
    }

    /**
     * Getter xChangeMarketDataService.
     *
     * @return xChangeMarketDataService
     */
    @Bean
    public MarketDataService getXChangeMarketDataService() {
        return xChangeMarketDataService;
    }

    /**
     * Getter xChangeTradeService.
     *
     * @return xChangeTradeService
     */
    @Bean
    public org.knowm.xchange.service.trade.TradeService getXChangeTradeService() {
        return xChangeTradeService;
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
