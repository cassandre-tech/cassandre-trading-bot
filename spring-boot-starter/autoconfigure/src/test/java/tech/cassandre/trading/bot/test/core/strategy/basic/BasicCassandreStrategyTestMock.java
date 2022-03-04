package tech.cassandre.trading.bot.test.core.strategy.basic;

import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.EUR;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SuppressWarnings("unchecked")
@TestConfiguration
public class BasicCassandreStrategyTestMock extends BaseTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(applicationContext, marketService());
    }

    @Bean
    @Primary
    public AccountFlux accountFlux() {
        return new AccountFlux(userService());
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
    public PositionFlux positionFlux() {
        return new PositionFlux(positionRepository);
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public UserService userService() {
        Set<BalanceDTO> balances = new LinkedHashSet<>();
        final Map<String, AccountDTO> accounts = new LinkedHashMap<>();
        UserService userService = mock(UserService.class);

        // =============================================================================================================
        // Account retrieved by configuration - Empty, just allowing the getTradeAccount() in configuration to work.
        AccountDTO tempAccount = AccountDTO.builder().accountId("03").name("trade").build();
        accounts.put("03", tempAccount);
        UserDTO tempUser = UserDTO.builder().accounts(accounts).build();
        accounts.clear();

        // =============================================================================================================
        // Creating the response to the first call of the AccountFlux.
        // User has three accounts:
        // - Account 01 (No name with 1 BTC).
        // - Account 02 (No name with 1 ETH).
        // - Account 03 (Trade account with 2 BTC, 10 ETH, 2 000 USDT).

        // User service response 01.
        BalanceDTO account01Balance1 = BalanceDTO.builder().currency(BTC).available(new BigDecimal("1")).build();
        balances.add(account01Balance1);
        AccountDTO account01 = AccountDTO.builder().accountId("01").balances(balances).build();
        accounts.put("01", account01);
        UserDTO userResponse01 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // User service response 02.
        BalanceDTO account02Balance1 = BalanceDTO.builder().currency(ETH).available(new BigDecimal("1")).build();
        balances.add(account02Balance1);
        AccountDTO account02 = AccountDTO.builder().accountId("02").balances(balances).build();
        accounts.put("02", account02);
        UserDTO userResponse02 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // User service response 03.
        balances.add(BalanceDTO.builder().currency(BTC).available(new BigDecimal("2")).build());
        balances.add(BalanceDTO.builder().currency(ETH).available(new BigDecimal("10")).build());
        balances.add(BalanceDTO.builder().currency(EUR).available(new BigDecimal("2000")).build());
        AccountDTO account03 = AccountDTO.builder().accountId("03").name("trade").balances(balances).build();
        accounts.put("03", account03);
        UserDTO userResponse03 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // User service response 04.
        balances.add(BalanceDTO.builder().currency(BTC).available(new BigDecimal("1")).build());
        balances.add(BalanceDTO.builder().currency(ETH).available(new BigDecimal("10")).build());
        balances.add(BalanceDTO.builder().currency(USDT).available(new BigDecimal("100")).build());
        AccountDTO account04 = AccountDTO.builder().accountId("03").name("trade").balances(balances).build();
        accounts.put("03", account04);
        UserDTO userResponse04 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // We have two different mock replies.
        // StrategiesAutoConfiguration calls userService.getUser().
        given(userService.getUser()).willReturn(Optional.of(tempUser));
        // AccountFlux calls userService.getAccounts()
        given(userService.getAccounts()).willReturn(userResponse01.getAccounts(),
                                                    userResponse02.getAccounts(),
                                                    userResponse03.getAccounts(),
                                                    userResponse04.getAccounts());
        return userService;
    }

    @Bean
    @Primary
    public MarketService marketService() {
        MarketService service = mock(MarketService.class);

        // We don't use the getTickers method.
        given(service.getTickers(any())).willThrow(new NotAvailableFromExchangeException("Not available during tests"));

        // Returns three values for ETH/BTC.
        given(service.getTicker(ETH_BTC)).willReturn(
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("1")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("2")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("3")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("4")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("5")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("6")),
                Optional.empty()
        );
        // Returns one value for ETH/USDT.
        given(service.getTicker(ETH_USDT)).willReturn(
                BaseTest.getFakeTicker(ETH_USDT, new BigDecimal("10000")),
                Optional.empty()
        );
        return service;
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        TradeService service = mock(TradeService.class);

        // Returns three values for getOpenOrders().
        Set<OrderDTO> replyGetOpenOrders = new LinkedHashSet<>();
        replyGetOpenOrders.add(OrderDTO.builder().orderId("000001").type(BID).strategy(strategyDTO).currencyPair(ETH_BTC).timestamp(createZonedDateTime("01-01-2020")).build());   // Order 01.
        replyGetOpenOrders.add(OrderDTO.builder().orderId("000002").type(BID).strategy(strategyDTO).currencyPair(ETH_BTC).timestamp(createZonedDateTime("01-02-2020")).build());   // Order 02.
        replyGetOpenOrders.add(OrderDTO.builder().orderId("000003").type(BID).strategy(strategyDTO).currencyPair(ETH_BTC).timestamp(createZonedDateTime("01-03-2020")).build());   // Order 03.
        given(service.getOrders()).willReturn(replyGetOpenOrders);

        // Returns three values for getTrades().
        Set<TradeDTO> replyGetTrades = new LinkedHashSet<>();
        replyGetTrades.add(TradeDTO.builder().tradeId("0000001").orderId("000001").type(BID).currencyPair(ETH_BTC).timestamp(createZonedDateTime("01-01-2020")).build());      // Trade 01.
        replyGetTrades.add(TradeDTO.builder().tradeId("0000002").orderId("000001").type(BID).currencyPair(ETH_BTC).timestamp(createZonedDateTime("01-02-2020")).build());      // Trade 02.
        replyGetTrades.add(TradeDTO.builder().tradeId("0000003").orderId("000001").type(BID).currencyPair(ETH_BTC).timestamp(createZonedDateTime("01-03-2020")).build());      // Trade 03.
        given(service.getTrades()).willReturn(replyGetTrades);

        return service;
    }

}
