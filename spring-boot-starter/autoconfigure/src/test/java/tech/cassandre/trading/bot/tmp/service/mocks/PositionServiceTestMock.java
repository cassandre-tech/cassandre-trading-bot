package tech.cassandre.trading.bot.tmp.service.mocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.intern.PositionServiceImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.tmp.service.PositionServiceTest;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@TestConfiguration
public class PositionServiceTestMock {

    @Autowired
    private PositionRepository positionRepository;

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
    public PositionFlux positionFlux() {
        return new PositionFlux(positionRepository);
    }

    @Bean
    @Primary
    public PositionService positionService() {
        return new PositionServiceImplementation(tradeService(), positionRepository, positionFlux());
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
        return mock(MarketService.class);
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        TradeService service = mock(TradeService.class);

        // Position 1 creation reply (order ORDER00010).
        given(service.createBuyMarketOrder(PositionServiceTest.cp1, new BigDecimal("0.0001")))
                .willReturn(new OrderCreationResultDTO("ORDER00010"));

        // Position 2 creation reply (order ORDER00020).
        given(service.createBuyMarketOrder(PositionServiceTest.cp2, new BigDecimal("0.0002")))
                .willReturn(new OrderCreationResultDTO("ORDER00020"));

        // Position 3 creation reply (order ORDER00030).
        given(service.createBuyMarketOrder(PositionServiceTest.cp1, new BigDecimal("0.0003")))
                .willReturn(new OrderCreationResultDTO("Error message", new RuntimeException("Error exception")));

        // Position 1 closed reply (ORDER00011).
        given(service.createSellMarketOrder(PositionServiceTest.cp1, new BigDecimal("0.00010000")))
                .willReturn(new OrderCreationResultDTO("ORDER00011"));

        // Position 1 closed reply (ORDER00011) - used for max and min gain test.
        given(service.createBuyMarketOrder(PositionServiceTest.cp1, new BigDecimal("10")))
                .willReturn(new OrderCreationResultDTO("ORDER00010"));
        // Position 1 closed reply (ORDER00011) - used for max and min gain test.
        given(service.createSellMarketOrder(PositionServiceTest.cp1, new BigDecimal("10.00000000")))
                .willReturn(new OrderCreationResultDTO("ORDER00011"));

        return service;
    }

}
