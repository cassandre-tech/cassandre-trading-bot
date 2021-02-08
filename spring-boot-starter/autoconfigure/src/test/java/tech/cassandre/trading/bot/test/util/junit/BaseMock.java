package tech.cassandre.trading.bot.test.util.junit;

import lombok.extern.java.Log;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.repository.ExchangeAccountRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.xchange.MarketServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.xchange.UserServiceXChangeImplementation;

import java.io.IOException;
import java.util.Collections;

import static java.math.BigDecimal.ZERO;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_STRATEGY;

/**
 * Base mock.
 */
@Log
public class BaseMock {

    /** Service rate. */
    public static final int SERVICE_RATE = 100;

    /** Default strategy. */
    protected StrategyDTO strategy = StrategyDTO.builder()
            .id(1L)
            .strategyId("01")
            .type(BASIC_STRATEGY)
            .build();

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
    public UserService userService() {
        AccountService mock;
        try {
            mock = getXChangeAccountServiceMock();
        } catch (IOException e) {
            log.severe("Impossible to instantiate mocked account service");
            return null;
        }
        return new UserServiceXChangeImplementation(SERVICE_RATE, mock);
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
    public org.knowm.xchange.service.trade.TradeService getXChangeTradeServiceMock() {
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
