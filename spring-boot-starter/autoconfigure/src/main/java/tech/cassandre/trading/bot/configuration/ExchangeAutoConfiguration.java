package tech.cassandre.trading.bot.configuration;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import si.mazi.rescu.HttpStatusIOException;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TickerStreamFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.ExchangeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.MarketServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.PositionServiceCassandreImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.TradeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.UserServiceXChangeImplementation;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.configuration.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * ExchangeConfiguration configures the exchange connection.
 */
@Configuration
@EnableConfigurationProperties(ExchangeParameters.class)
@RequiredArgsConstructor
public class ExchangeAutoConfiguration extends BaseConfiguration {

    /** Unauthorized http status code. */
    private static final int UNAUTHORIZED_STATUS_CODE = 401;

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Exchange parameters. */
    private final ExchangeParameters exchangeParameters;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Position repository. */
    private final PositionRepository positionRepository;

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

    /** Position service. */
    private PositionService positionService;

    /** Account flux. */
    private AccountFlux accountFlux;

    /** Ticker flux. */
    private TickerFlux tickerFlux;

    /** Ticker stream flux. */
    private TickerStreamFlux tickerStreamFlux;

    /** Order flux. */
    private OrderFlux orderFlux;

    /** Trade flux. */
    private TradeFlux tradeFlux;

    /** Position flux. */
    private PositionFlux positionFlux;

    /**
     * Instantiating the exchange services based on user parameters.
     */
    @PostConstruct
    public void configure() {
        try {
            // Instantiate exchange class.
            Class<? extends Exchange> exchangeClass = Class.forName(getExchangeClassName()).asSubclass(Exchange.class);
            ExchangeSpecification exchangeSpecification = new ExchangeSpecification(exchangeClass);

            // Exchange configuration.
            exchangeSpecification.setUserName(exchangeParameters.getUsername());
            exchangeSpecification.setApiKey(exchangeParameters.getKey());
            exchangeSpecification.setSecretKey(exchangeParameters.getSecret());
            exchangeSpecification.getResilience().setRateLimiterEnabled(true);
            exchangeSpecification.setExchangeSpecificParametersItem("Use_Sandbox", exchangeParameters.getModes().getSandbox());
            exchangeSpecification.setExchangeSpecificParametersItem("passphrase", exchangeParameters.getPassphrase());
            exchangeSpecification.setProxyHost(exchangeParameters.getProxyHost());
            exchangeSpecification.setProxyPort(exchangeParameters.getProxyPort());
            exchangeSpecification.setSslUri(exchangeParameters.getSslUri());
            exchangeSpecification.setPlainTextUri(exchangeParameters.getPlainTextUri());
            exchangeSpecification.setHost(exchangeParameters.getHost());
            if (exchangeParameters.getPort() != null) {
                exchangeSpecification.setPort(Integer.parseInt(exchangeParameters.getPort()));
            }

            // Creates XChange services.
            if (exchangeParameters.isTickerStreamEnabled()) {
                exchangeSpecification.setShouldLoadRemoteMetaData(true); // this must be set or Streaming will not download currencies by default
                // Create Streaming XChange services
                xChangeExchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
            } else {
                xChangeExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
            }
            xChangeAccountService = xChangeExchange.getAccountService();
            xChangeMarketDataService = xChangeExchange.getMarketDataService();
            xChangeTradeService = xChangeExchange.getTradeService();

            if (exchangeParameters.isTickerStreamEnabled()) {
                ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
                applicationContext
                        .getBeansWithAnnotation(CassandreStrategy.class)
                        .values()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(object -> (CassandreStrategyInterface) object)
                        .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                        .flatMap(Set::stream)
                        .forEach(pair -> builder.addTicker(new CurrencyPair(pair.getBaseCurrency().getCurrencyCode(), pair.getQuoteCurrency().getCode())));

                // Connect to the Exchange WebSocket API. Here we use a blocking wait.
                ((StreamingExchange) xChangeExchange).connect(builder.build()).blockingAwait();
            }

            // Force login to check credentials.
            logger.info("Exchange connection with driver {}", exchangeParameters.getDriverClassName());
            xChangeAccountService.getAccountInfo();
            logger.info("Exchange connection successful with username {} (Dry mode: {} / Sandbox: {})",
                    exchangeParameters.getUsername(),
                    exchangeParameters.getModes().getDry(),
                    exchangeParameters.getModes().getSandbox());
        } catch (ClassNotFoundException e) {
            // If we can't find the exchange class.
            throw new ConfigurationException("Impossible to find the exchange driver class you requested: " + exchangeParameters.getDriverClassName(),
                    "Choose and configure a valid exchange (https://trading-bot.cassandre.tech/learn/exchange-connection-configuration.html#how-does-it-works)");
        } catch (HttpStatusIOException e) {
            if (e.getHttpStatusCode() == UNAUTHORIZED_STATUS_CODE) {
                // Authorization failure.
                throw new ConfigurationException("Invalid credentials for " + exchangeParameters.getDriverClassName(),
                        "Check your exchange credentials: " + e.getMessage() + " - login used: " + exchangeParameters.getUsername());
            } else {
                // Another HTTP failure.
                throw new ConfigurationException("Error while connecting to the exchange: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new ConfigurationException("IO error: " + e.getMessage());
        }
    }

    /**
     * Returns the XChange class based on the exchange name.
     * This is used in case the full driver class with package is not given in the parameters.
     *
     * @return XChange class name
     */
    private String getExchangeClassName() {
        // If the name contains a dot, it means that the user set the complete XChange class name in the configuration.
        if (exchangeParameters.getDriverClassName() != null && exchangeParameters.getDriverClassName().contains(".")) {
            return exchangeParameters.getDriverClassName();
        } else {
            // Try to guess the XChange class package name from the exchange name parameter.
            return "org.knowm.xchange."                                                                     // Package (org.knowm.xchange.).
                    .concat(exchangeParameters.getDriverClassName().toLowerCase())                          // domain (kucoin).
                    .concat(".")                                                                        // A dot (.)
                    .concat(exchangeParameters.getDriverClassName().substring(0, 1).toUpperCase())          // First letter uppercase (K).
                    .concat(exchangeParameters.getDriverClassName().substring(1).toLowerCase())   // The rest of the exchange name (ucoin).
                    .concat("Exchange");                                                                // Adding exchange (Exchange).
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
    @DependsOn("getXChangeExchange")
    public ExchangeService getExchangeService() {
        if (exchangeService == null) {
            exchangeService = new ExchangeServiceXChangeImplementation(getXChangeExchange());
        }
        return exchangeService;
    }

    /**
     * Getter for userService.
     *
     * @return userService
     */
    @Bean
    @DependsOn("getXChangeAccountService")
    public UserService getUserService() {
        if (userService == null) {
            userService = new UserServiceXChangeImplementation(
                    exchangeParameters.getRates().getAccountValueInMs(),
                    getXChangeAccountService());
        }
        return userService;
    }

    /**
     * Getter for marketService.
     *
     * @return marketService
     */
    @Bean
    @DependsOn("getXChangeMarketDataService")
    public MarketService getMarketService() {
        if (marketService == null && !exchangeParameters.isTickerStreamEnabled()) {
            marketService = new MarketServiceXChangeImplementation(
                    exchangeParameters.getRates().getTickerValueInMs(),
                    getXChangeMarketDataService());
        }
        return marketService;
    }

    /**
     * Getter for tradeService.
     *
     * @return tradeService
     */
    @Bean
    @DependsOn("getXChangeTradeService")
    public TradeService getTradeService() {
        if (tradeService == null) {
            tradeService = new TradeServiceXChangeImplementation(
                    exchangeParameters.getRates().getTradeValueInMs(),
                    orderRepository,
                    getXChangeTradeService());
        }
        return tradeService;
    }

    /**
     * Getter for accountFlux.
     *
     * @return accountFlux
     */
    @Bean
    @DependsOn("getXChangeTradeService")
    public AccountFlux getAccountFlux() {
        if (accountFlux == null) {
            accountFlux = new AccountFlux(getUserService());
        }
        return accountFlux;
    }

    /**
     * Getter for tickerFlux.
     *
     * @return tickerFlux
     */
    @Bean
    @DependsOn("getMarketService")
    public TickerFlux getTickerFlux() {
        if (tickerFlux == null && !exchangeParameters.isTickerStreamEnabled()) {
            tickerFlux = new TickerFlux(applicationContext, getMarketService());
        }
        return tickerFlux;
    }

    /**
     * Getter for tickerStreamFlux.
     *
     * @return tickerStreamFlux
     */
    @Bean
    public TickerStreamFlux getTickerStreamFlux() {
        if (tickerStreamFlux == null && exchangeParameters.isTickerStreamEnabled()) {
            tickerStreamFlux = new TickerStreamFlux(applicationContext, (StreamingExchange) getXChangeExchange());
        }
        return tickerStreamFlux;
    }

    /**
     * Getter for orderFlux.
     *
     * @return orderFlux
     */
    @Bean
    @DependsOn("getTradeService")
    public OrderFlux getOrderFlux() {
        if (orderFlux == null) {
            orderFlux = new OrderFlux(orderRepository, getTradeService());
        }
        return orderFlux;
    }

    /**
     * Getter for tradeFlux.
     *
     * @return tradeFlux
     */
    @Bean
    @DependsOn("getTradeService")
    public TradeFlux getTradeFlux() {
        if (tradeFlux == null) {
            tradeFlux = new TradeFlux(orderRepository, tradeRepository, getTradeService());
        }
        return tradeFlux;
    }

    /**
     * Getter for positionFlux.
     *
     * @return positionFlux
     */
    @Bean
    @DependsOn("getTradeService")
    public PositionFlux getPositionFlux() {
        if (positionFlux == null) {
            positionFlux = new PositionFlux(positionRepository);
        }
        return positionFlux;
    }

    /**
     * Getter for positionService.
     *
     * @return positionService
     */
    @Bean
    @DependsOn("getTradeService")
    public PositionService getPositionService() {
        if (positionService == null) {
            positionService = new PositionServiceCassandreImplementation(positionRepository, getTradeService(), positionFlux);
        }
        return positionService;
    }

}
