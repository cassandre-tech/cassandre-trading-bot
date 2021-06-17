package tech.cassandre.trading.bot.test.strategy.basic;

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
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
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
        return new OrderFlux(tradeService(), orderRepository);
    }

    @Bean
    @Primary
    public TradeFlux tradeFlux() {
        return new TradeFlux(tradeService(), orderRepository, tradeRepository);
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
        Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
        final Map<String, AccountDTO> accounts = new LinkedHashMap<>();
        UserService userService = mock(UserService.class);
        // Returns three updates.

        // =============================================================================================================
        // Account retrieved by configuration.
        AccountDTO tempAccount = AccountDTO.builder().accountId("03").name("trade").build();
        accounts.put("trade", tempAccount);
        UserDTO tempUser = UserDTO.builder().accounts(accounts).build();
        accounts.clear();

        // Account 01.
        BalanceDTO account01Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).build();
        balances.put(BTC, account01Balance1);
        AccountDTO account01 = AccountDTO.builder().accountId("01").balances(balances).build();
        accounts.put("01", account01);
        UserDTO user01 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // Account 02.
        BalanceDTO account02Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).build();
        balances.put(BTC, account02Balance1);
        AccountDTO account02 = AccountDTO.builder().accountId("02").balances(balances).build();
        accounts.put("02", account02);
        UserDTO user02 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // Account 03.
        balances.put(BTC, BalanceDTO.builder().available(new BigDecimal("2")).build());
        balances.put(ETH, BalanceDTO.builder().available(new BigDecimal("10")).build());
        balances.put(USDT, BalanceDTO.builder().available(new BigDecimal("2000")).build());
        AccountDTO account03 = AccountDTO.builder().accountId("03").name("trade").balances(balances).build();
        accounts.put("03", account03);
        UserDTO user03 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // Mock replies.
        given(userService.getUser()).willReturn(Optional.of(tempUser), Optional.of(user01), Optional.of(user02), Optional.of(user03));
        return userService;
    }

    @Bean
    @Primary
    public MarketService marketService() {
        MarketService service = mock(MarketService.class);

        // We don't use the getTickers method.
        given(service.getTickers(any())).willThrow(new NotAvailableFromExchangeException("Not available in test"));

        // Returns three values.
        given(service.getTicker(ETH_BTC)).willReturn(
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("1")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("2")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("3")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("4")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("5")),
                BaseTest.getFakeTicker(ETH_BTC, new BigDecimal("6")),
                Optional.empty()
        );
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

        // Returns three values for getOpenOrders.
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

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public PositionService positionService() {
        // =============================================================================================================
        // Loading strategy.
        StrategyDTO strategy = StrategyDTO.builder().strategyId("1").build();

        // Creates the mock.
        final PositionRulesDTO noRules = PositionRulesDTO.builder().build();
        PositionService positionService = mock(PositionService.class);
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);
        final BigDecimal amount = new BigDecimal("1");

        // Reply 1 : 2 positions.
        // TODO
//        PositionDTO p1 = new PositionDTO(1, LONG, strategy, cp1, amount, "O000001", noRules);
//        PositionDTO p2 = new PositionDTO(2, LONG, strategy, cp1, amount,"O000002", noRules);
//        Set<PositionDTO> reply01 = new LinkedHashSet<>();
//        reply01.add(p1);
//        reply01.add(p2);
//
//        // Reply 2 : 3 positions.
//        Set<PositionDTO> reply02 = new LinkedHashSet<>();
//        PositionDTO p3 = new PositionDTO(1, LONG, strategy, cp1, amount,"O000001", noRules);
//        PositionDTO p4 = new PositionDTO(2, LONG, strategy, cp1, amount,"O000002", noRules);
//        PositionDTO p5 = new PositionDTO(3, LONG, strategy, cp1, amount,"O000003", noRules);
//        reply02.add(p3);
//        reply02.add(p4);
//        reply02.add(p5);
//
//        // Reply 2 : 2 positions.
//        Set<PositionDTO> reply03 = new LinkedHashSet<>();
//        PositionDTO p6 = new PositionDTO(1, LONG, strategy, cp1, amount,"O000001", noRules);
//        PositionDTO p7 = new PositionDTO(2, LONG, strategy, cp1, amount,"O000001", noRules);
//        reply03.add(p6);
//        reply03.add(p7);

//        given(positionService.getPositions())
//                .willReturn(reply01,
//                        new LinkedHashSet<>(),
//                        reply02,
//                        reply03);
//        return positionService;
        return null;
    }

}
