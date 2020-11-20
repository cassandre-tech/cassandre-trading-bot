package tech.cassandre.trading.bot.test.batch.mocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.xchange.TradeServiceXChangeImplementation;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@TestConfiguration
public class OrderFluxTestMock {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

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
        new TradeServiceXChangeImplementation(100l, null, tradeRepository, orderRepository);
        TradeService tradeService = mock(TradeService.class);
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

        // =========================================================================================================
        // First reply : 3 orders.

        // Order 000001.
        OrderDTO order01 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000001")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000002.
        OrderDTO order02 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000002")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000003.
        OrderDTO order03 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000003")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        Set<OrderDTO> reply01 = new LinkedHashSet<>();
        reply01.add(order01);
        reply01.add(order02);
        reply01.add(order03);

        // =========================================================================================================
        // Second reply.
        // Order 000003 : the original amount changed.
        // Order 000004 : new order.

        // Order 000001.
        OrderDTO order04 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000001")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000002.
        OrderDTO order05 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000002")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000003 : the original amount changed.
        OrderDTO order06 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("2"))
                .currencyPair(cp1)
                .id("000003")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000004 : new order.
        OrderDTO order07 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000004")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        Set<OrderDTO> reply02 = new LinkedHashSet<>();
        reply02.add(order04);
        reply02.add(order05);
        reply02.add(order06);
        reply02.add(order07);

        // =========================================================================================================
        // Second reply.
        // Order 000002 : average prince changed.
        // Order 000004 : fee changed.

        // Order 000001.
        OrderDTO order08 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000001")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000002 : average price changed.
        OrderDTO order09 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000002")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("1"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000003.
        OrderDTO order10 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("2"))
                .currencyPair(cp1)
                .id("000003")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("4"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        // Order 000004 : fee changed.
        OrderDTO order11 = OrderDTO.builder()
                .type(ASK)
                .originalAmount(new BigDecimal("1"))
                .currencyPair(cp1)
                .id("000004")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("2"))
                .averagePrice(new BigDecimal("3"))
                .fee(new BigDecimal("1"))
                .leverage("leverage1")
                .limitPrice(new BigDecimal("5"))
                .create();

        Set<OrderDTO> reply03 = new LinkedHashSet<>();
        reply03.add(order08);
        reply03.add(order09);
        reply03.add(order10);
        reply03.add(order11);

        // Creating the mock.
        given(tradeService.getOpenOrders())
                .willReturn(reply01,
                        new LinkedHashSet<>(),
                        reply02,
                        reply03);
        return tradeService;
    }

}
