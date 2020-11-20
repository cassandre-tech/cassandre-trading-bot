package tech.cassandre.trading.bot.test.batch.mocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@TestConfiguration
public class AccountFluxTestMock {

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

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public UserService userService() {
        // Creates the mock.
        Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
        final Map<String, AccountDTO> accounts = new LinkedHashMap<>();
        UserService userService = mock(UserService.class);

        // =============================================================================================================
        // Account retrieved by configuration at Cassandre startup.
        AccountDTO tempAccount = AccountDTO.builder().id("trade").name("trade").create();
        accounts.put("trade", tempAccount);
        UserDTO tempUser = UserDTO.builder().setAccounts(accounts).create();
        accounts.clear();

        // =============================================================================================================
        // Account 1 with 2 balances (1 BTC - 2 ETH).
        // Account 2 with 1 balance (1 BTC).
        BalanceDTO account01Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        BalanceDTO account01Balance2 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(ETH)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        balances.put(BTC, account01Balance1);
        balances.put(ETH, account01Balance2);
        AccountDTO account01 = AccountDTO.builder().id("01").name("01").balances(balances).create();
        BalanceDTO account02Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        balances.clear();
        balances.put(BTC, account02Balance1);
        AccountDTO account02 = AccountDTO.builder().id("02").name("02").balances(balances).create();
        accounts.put("01", account01);
        accounts.put("02", account02);
        UserDTO user01 = UserDTO.builder().setAccounts(accounts).create();

        // =========================================================================================================
        // Account 1 with 3 balances (1 BTC - 2 ETH - 2 USDT).
        // Account 2 with 1 balance.
        // Change : Account 1 has now 3 balances.
        BalanceDTO account03Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        BalanceDTO account03Balance2 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(ETH)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        BalanceDTO account03Balance3 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(USDT)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        balances.clear();
        balances.put(BTC, account03Balance1);
        balances.put(ETH, account03Balance2);
        balances.put(USDT, account03Balance3);
        AccountDTO account03 = AccountDTO.builder().id("01").name("01").balances(balances).create();
        BalanceDTO account04Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        balances.clear();
        balances.put(BTC, account04Balance1);
        AccountDTO account04 = AccountDTO.builder().id("02").name("02").balances(balances).create();
        accounts.clear();
        accounts.put("01", account03);
        accounts.put("02", account04);
        UserDTO user02 = UserDTO.builder().setAccounts(accounts).create();

        // =========================================================================================================
        // Account 1 with 3 balances.
        // Account 2 with 1 balance.
        // Change : No change.
        BalanceDTO account05Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        BalanceDTO account05Balance2 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(ETH)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        BalanceDTO account05Balance3 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(USDT)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        balances.clear();
        balances.put(BTC, account05Balance1);
        balances.put(ETH, account05Balance2);
        balances.put(USDT, account05Balance3);
        AccountDTO account05 = AccountDTO.builder().id("01").name("01").balances(balances).create();
        BalanceDTO account06Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        balances.clear();
        balances.put(BTC, account06Balance1);
        AccountDTO account06 = AccountDTO.builder().id("02").name("02").balances(balances).create();
        accounts.clear();
        accounts.put("01", account05);
        accounts.put("02", account06);
        UserDTO user03 = UserDTO.builder().setAccounts(accounts).create();

        // =========================================================================================================
        // Account 1 with 3 balances.
        // Account 2 with 1 balance.
        // Change : ETH balance of account 1 changed (borrowed value) & balance of account 2 (frozen).
        BalanceDTO account07Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        BalanceDTO account07Balance2 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("5"))
                .currency(ETH)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        BalanceDTO account07Balance3 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(USDT)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        balances.clear();
        balances.put(BTC, account07Balance1);
        balances.put(ETH, account07Balance2);
        balances.put(USDT, account07Balance3);
        AccountDTO account07 = AccountDTO.builder().id("01").name("01").balances(balances).create();
        BalanceDTO account08Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        balances.clear();
        balances.put(BTC, account08Balance1);
        AccountDTO account08 = AccountDTO.builder().id("02").name("02").balances(balances).create();
        accounts.clear();
        accounts.put("01", account07);
        accounts.put("02", account08);
        UserDTO user04 = UserDTO.builder().setAccounts(accounts).create();

        // =========================================================================================================
        // Account 1 with 3 balances.
        // Account 2 with 1 balance.
        // Change : no change.
        BalanceDTO account09Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        BalanceDTO account09Balance2 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("5"))
                .currency(ETH)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        BalanceDTO account09Balance3 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(USDT)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        balances.clear();
        balances.put(BTC, account09Balance1);
        balances.put(ETH, account09Balance2);
        balances.put(USDT, account09Balance3);
        AccountDTO account09 = AccountDTO.builder().id("01").name("01").balances(balances).create();
        BalanceDTO account10Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        balances.clear();
        balances.put(BTC, account10Balance1);
        AccountDTO account10 = AccountDTO.builder().id("02").name("02").balances(balances).create();
        accounts.clear();
        accounts.put("01", account09);
        accounts.put("02", account10);
        UserDTO user05 = UserDTO.builder().setAccounts(accounts).create();

        // =========================================================================================================
        // Account 1 with 2 balances.
        // Account 2 with 1 balance.
        // Change : one balance removed on account 1.
        BalanceDTO account11Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("1"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        BalanceDTO account11Balance2 = BalanceDTO.builder()
                .available(new BigDecimal("2"))
                .borrowed(new BigDecimal("2"))
                .currency(USDT)
                .depositing(new BigDecimal("2"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("2"))
                .total(new BigDecimal("2"))
                .withdrawing(new BigDecimal("2"))
                .create();
        balances.clear();
        balances.put(BTC, account11Balance1);
        balances.put(USDT, account11Balance2);
        AccountDTO account11 = AccountDTO.builder().id("01").name("01").balances(balances).create();
        BalanceDTO account12Balance1 = BalanceDTO.builder()
                .available(new BigDecimal("1"))
                .borrowed(new BigDecimal("1"))
                .currency(BTC)
                .depositing(new BigDecimal("1"))
                .frozen(new BigDecimal("2"))
                .loaned(new BigDecimal("1"))
                .total(new BigDecimal("1"))
                .withdrawing(new BigDecimal("1"))
                .create();
        balances.clear();
        balances.put(BTC, account12Balance1);
        AccountDTO account12 = AccountDTO.builder().id("02").name("02").balances(balances).create();
        accounts.clear();
        accounts.put("01", account11);
        accounts.put("02", account12);
        UserDTO user06 = UserDTO.builder().setAccounts(accounts).create();

        // Mock.
        given(userService.getUser())
                .willReturn(Optional.of(tempUser),  // Retrieved by cassandre at startup for configuration.
                        Optional.of(user01),
                        Optional.empty(),
                        Optional.of(user02),
                        Optional.of(user03),
                        Optional.of(user04),
                        Optional.empty(),
                        Optional.of(user05),
                        Optional.of(user06)
                );
        return userService;
    }

    @Bean
    @Primary
    public MarketService marketService() {
        MarketService service = mock(MarketService.class);
        given(service.getTicker(any())).willReturn(Optional.empty());
        return service;
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        TradeService service = mock(TradeService.class);
        given(service.getOpenOrders()).willReturn(new LinkedHashSet<>());
        return service;
    }

}
