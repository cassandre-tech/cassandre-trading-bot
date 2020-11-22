package tech.cassandre.trading.bot.test.batch.mocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
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
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@TestConfiguration
public class TradeFluxTestMock extends BaseTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(marketService());
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
        return new TradeFlux(tradeService(), tradeRepository);
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public UserService userService() {
        Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
        final Map<String, AccountDTO> accounts = new LinkedHashMap<>();
        UserService userService = mock(UserService.class);
        // Returns three updates.

        // Account 01.
        BalanceDTO account01Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).create();
        balances.put(BTC, account01Balance1);
        AccountDTO account01 = AccountDTO.builder().id("01").name("trade").balances(balances).create();
        accounts.put("01", account01);
        UserDTO user01 = UserDTO.builder().setAccounts(accounts).create();
        balances.clear();
        accounts.clear();

        // Account 02.
        BalanceDTO account02Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).create();
        balances.put(BTC, account02Balance1);
        AccountDTO account02 = AccountDTO.builder().id("02").name("trade").balances(balances).create();
        accounts.put("02", account02);
        UserDTO user02 = UserDTO.builder().setAccounts(accounts).create();
        balances.clear();
        accounts.clear();

        // Account 03.
        balances.put(BTC, BalanceDTO.builder().available(new BigDecimal("2")).create());
        balances.put(ETH, BalanceDTO.builder().available(new BigDecimal("10")).create());
        balances.put(USDT, BalanceDTO.builder().available(new BigDecimal("2000")).create());
        AccountDTO account03 = AccountDTO.builder().id("03").name("trade").balances(balances).create();
        accounts.put("03", account03);
        UserDTO user03 = UserDTO.builder().setAccounts(accounts).create();
        balances.clear();
        accounts.clear();

        // Mock replies.
        given(userService.getUser()).willReturn(Optional.of(user01), Optional.of(user02), Optional.of(user03));
        return userService;
    }

    @Bean
    @Primary
    public MarketService marketService() {
        MarketService service = mock(MarketService.class);
        given(service.getTicker(any())).willReturn(Optional.empty());
        return service;
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public TradeService tradeService() {
        // Creates the mock.
        TradeService tradeService = mock(TradeService.class);

        // =========================================================================================================
        // First reply : 2 trades.
        TradeDTO trade01 = TradeDTO.builder().id("0000001").type(BID).currencyPair(cp1).create();
        TradeDTO trade02 = TradeDTO.builder().id("0000002")
                .type(BID)
                .currencyPair(cp1)
                .orderId("EMPTY")
                .originalAmount(new BigDecimal("1.100001"))
                .price(new BigDecimal("2.200002"))
                .timestamp(createZonedDateTime("01-09-2020"))
                .feeAmount(new BigDecimal("3.300003"))
                .feeCurrency(BTC)
                .create();

        Set<TradeDTO> reply01 = new LinkedHashSet<>();
        reply01.add(trade01);
        reply01.add(trade02);

        // =========================================================================================================
        // First reply : 3 trades.
        TradeDTO trade03 = TradeDTO.builder().id("0000003")
                .type(BID)
                .currencyPair(cp2)
                .orderId("EMPTY")
                .originalAmount(new BigDecimal("1.100001"))
                .currencyPair(cp2)
                .price(new BigDecimal("2.200002"))
                .timestamp(createZonedDateTime("01-09-2020"))
                .feeAmount(new BigDecimal("3.300003"))
                .feeCurrency(BTC)
                .create();
        TradeDTO trade04 = TradeDTO.builder().id("0000004").type(BID).currencyPair(cp1).create();
        TradeDTO trade05 = TradeDTO.builder().id("0000005").type(BID).currencyPair(cp1).create();

        Set<TradeDTO> reply02 = new LinkedHashSet<>();
        reply02.add(trade03);
        reply02.add(trade04);
        reply02.add(trade05);

        // =========================================================================================================
        // First reply : 3 trades - Trade07 is trade 0000003 again.
        TradeDTO trade06 = TradeDTO.builder().id("0000006").type(BID).currencyPair(cp2).create();
        TradeDTO trade07 = TradeDTO.builder().id("0000002") // did nto change from the previous trade.
                .type(BID)
                .currencyPair(cp1)
                .orderId("EMPTY")
                .originalAmount(new BigDecimal("1.100001"))
                .price(new BigDecimal("2.200002"))
                .timestamp(createZonedDateTime("01-09-2020"))
                .feeAmount(new BigDecimal("3.300003"))
                .feeCurrency(BTC)
                .create();
        TradeDTO trade08 = TradeDTO.builder().id("0000003")
                .type(BID)
                .currencyPair(cp2)
                .orderId("EMPTY!")
                .originalAmount(new BigDecimal("1.110001"))
                .currencyPair(cp2)
                .price(new BigDecimal("2.220002"))
                .timestamp(createZonedDateTime("02-09-2020"))
                .feeAmount(new BigDecimal("3.330003"))
                .feeCurrency(BTC)
                .create();
        TradeDTO trade09 = TradeDTO.builder().id("0000008").type(BID).currencyPair(cp1).create();

        Set<TradeDTO> reply03 = new LinkedHashSet<>();
        reply03.add(trade06);
        reply03.add(trade07);
        reply03.add(trade08);
        reply03.add(trade09);

        // =========================================================================================================
        // Creating the mock.
        given(tradeService.getTrades())
                .willReturn(reply01,
                        new LinkedHashSet<>(),
                        new LinkedHashSet<>(),
                        reply02,
                        reply03);
        return tradeService;
    }
}
