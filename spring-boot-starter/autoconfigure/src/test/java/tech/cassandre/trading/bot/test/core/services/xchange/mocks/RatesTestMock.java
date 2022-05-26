package tech.cassandre.trading.bot.test.core.services.xchange.mocks;

import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.MarketServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.TradeServiceXChangeImplementation;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.UserServiceXChangeImplementation;

import java.io.IOException;
import java.util.Collections;

import static java.math.BigDecimal.ZERO;
import static org.knowm.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class RatesTestMock {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected TradeRepository tradeRepository;

    @Autowired
    protected PositionRepository positionRepository;

    @Bean
    @Primary
    public AccountFlux accountFlux() {
        return new AccountFlux(userService());
    }

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(applicationContext, marketService());
    }

    @Bean
    @Primary
    public OrderFlux orderFlux() {
        return new OrderFlux(orderRepository, tradeService());
    }

    @Bean
    @Primary

    public TradeFlux tradeFlux() {
        return new TradeFlux(orderRepository, tradeRepository, tradeService());
    }

    @Bean
    @Primary
    public UserService userService() {
        AccountService mock;
        try {
            mock = getXChangeAccountServiceMock();
        } catch (IOException e) {
            return null;
        }
        return new UserServiceXChangeImplementation(10000, mock);
    }

    @Bean
    @Primary
    public MarketService marketService() {
        MarketDataService mock;
        mock = getXChangeMarketDataServiceMock();
        return new MarketServiceXChangeImplementation(15000, mock);
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        org.knowm.xchange.service.trade.TradeService mock;
        try {
            mock = getXChangeTradeServiceMock();
        } catch (IOException e) {
            return null;
        }
        return new TradeServiceXChangeImplementation(20000, orderRepository, mock);
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
    @Bean
    @Primary
    public MarketDataService getXChangeMarketDataServiceMock() {
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
        final org.knowm.xchange.service.trade.TradeService mock = mock(org.knowm.xchange.service.trade.TradeService.class);
        given(mock.getOpenOrders()).willReturn(new OpenOrders(Collections.emptyList()));
        given(mock.getTradeHistory(any())).willReturn(new UserTrades(Collections.emptyList(), SortByTimestamp));
        return mock;
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
