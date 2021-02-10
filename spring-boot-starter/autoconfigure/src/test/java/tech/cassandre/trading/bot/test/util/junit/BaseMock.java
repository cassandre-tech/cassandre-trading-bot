package tech.cassandre.trading.bot.test.util.junit;

import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.repository.ExchangeAccountRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.xchange.MarketServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.xchange.TradeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.xchange.UserServiceXChangeImplementation;

import java.io.IOException;
import java.util.Collections;

import static java.math.BigDecimal.ZERO;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Base mock.
 */
public class BaseMock extends BaseTest {

    /** Service rate. */
    public static final int SERVICE_RATE = 900;

    @Autowired
    private ExchangeAccountRepository exchangeAccountRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected TradeRepository tradeRepository;

    @Autowired
    protected PositionRepository positionRepository;

    @Bean
    @Primary
    public MarketService marketService() {
        return new MarketServiceXChangeImplementation(SERVICE_RATE, getXChangeMarketDataServiceMock());
    }

    @Bean
    @Primary
    public AccountFlux accountFlux() {
        return new AccountFlux(userService());
    }

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(marketService());
    }

    @Bean
    @Primary
    public OrderFlux orderFlux() {
        return new OrderFlux(tradeService(), orderRepository);
    }

    @Bean
    @Primary
    public UserService userService() {
        AccountService mock;
        try {
            mock = getXChangeAccountServiceMock();
        } catch (IOException e) {
            logger.error("Impossible to instantiate mocked account service");
            return null;
        }
        return new UserServiceXChangeImplementation(SERVICE_RATE, mock);
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        org.knowm.xchange.service.trade.TradeService mock;
        try {
            mock = getXChangeTradeServiceMock();
        } catch (IOException e) {
            logger.error("Impossible to instantiate mocked account service");
            return null;
        }
        return new TradeServiceXChangeImplementation(SERVICE_RATE, orderRepository, mock);
    }

    /**
     * Returns mocked XChange account service.
     *
     * @return mocked XChange account service
     */
    @Bean
    @Primary
    public AccountService getXChangeAccountServiceMock() throws IOException {
        final AccountService accountServiceMock = mock(AccountService.class);
        given(accountServiceMock.getAccountInfo()).willReturn(
                getAccountInfoReplyForExchangeConfiguration()
        );
        return accountServiceMock;
    }

    /**
     * Returns mocked XChange market data service.
     *
     * @return mocked XChange market data service.
     */
    private MarketDataService getXChangeMarketDataServiceMock() {
        return mock(MarketDataService.class);
    }

    /**
     * Returns mocked XChange trade service.
     *
     * @return mocked XChange trade service
     */
    @Bean
    @Primary
    public org.knowm.xchange.service.trade.TradeService getXChangeTradeServiceMock() throws IOException {
        return mock(org.knowm.xchange.service.trade.TradeService.class);
    }

    /**
     * Returns account information for exchange configuration.
     *
     * @return exchange configuration
     */
    protected final AccountInfo getAccountInfoReplyForExchangeConfiguration() {
        return new AccountInfo(
                new Wallet("trade",
                        "trade",
                        Collections.emptySet(),
                        Collections.emptySet(),
                        ZERO,
                        ZERO));
    }

}
