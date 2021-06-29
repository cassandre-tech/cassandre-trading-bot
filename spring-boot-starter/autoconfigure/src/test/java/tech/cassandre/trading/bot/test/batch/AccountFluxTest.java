package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.test.batch.mocks.AccountFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Batch - Account flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@Import(AccountFluxTestMock.class)
public class AccountFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // The mock will reply 7 times with data.
        final int numberOfRepliesExpected = 7;

        // Waiting for the service to have been called 7 times.
        await().untilAsserted(() -> verify(accountService, atLeast(numberOfRepliesExpected)).getAccountInfo());

        // Checking that some data have already been treated by strategy but not all !
        // The flux should be asynchronous and a single thread in strategy is treating updates.
        assertTrue(strategy.getAccountsUpdatesReceived().size() > 0);
        assertTrue(strategy.getAccountsUpdatesReceived().size() <= numberOfRepliesExpected);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertEquals(numberOfRepliesExpected, strategy.getAccountsUpdatesReceived().size()));

        // Test all values received by the strategy with update methods.
        final Iterator<AccountDTO> iterator = strategy.getAccountsUpdatesReceived().iterator();

        // =============================================================================================================
        // Test all values received by the strategy with update methods.

        // Check update 1.
        AccountDTO a = iterator.next();
        assertEquals("01", a.getAccountId());
        assertEquals("Account 01", a.getName());
        assertEquals(2, a.getBalances().size());
        final Optional<BalanceDTO> update1BTCBalance = a.getBalance(BTC);
        assertTrue(update1BTCBalance.isPresent());
        assertEquals(BTC, update1BTCBalance.get().getCurrency());
        assertEquals(0, 0, update1BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update1BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update1BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update1BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update1BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update1BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update1BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));
        final Optional<BalanceDTO> update1ETHBalance = a.getBalance(ETH);
        assertTrue(update1ETHBalance.isPresent());
        assertEquals(ETH, update1ETHBalance.get().getCurrency());
        assertEquals(0, 0, update1ETHBalance.get().getTotal().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update1ETHBalance.get().getAvailable().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update1ETHBalance.get().getFrozen().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update1ETHBalance.get().getLoaned().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update1ETHBalance.get().getBorrowed().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update1ETHBalance.get().getWithdrawing().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update1ETHBalance.get().getDepositing().compareTo(new BigDecimal("2")));

        // Check update 2.
        a = iterator.next();
        assertEquals("02", a.getAccountId());
        assertEquals("Account 02", a.getName());
        assertEquals(1, a.getBalances().size());
        final Optional<BalanceDTO> update2BTCBalance = a.getBalance(BTC);
        assertTrue(update2BTCBalance.isPresent());
        assertEquals(BTC, update2BTCBalance.get().getCurrency());
        assertEquals(0, 0, update2BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update2BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update2BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update2BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update2BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update2BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update2BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));

        // Check update 3.
        a = iterator.next();
        assertEquals("01", a.getAccountId());
        assertEquals("Account 01", a.getName());
        assertEquals(3, a.getBalances().size());
        final Optional<BalanceDTO> update3BTCBalance = a.getBalance(BTC);
        assertTrue(update3BTCBalance.isPresent());
        assertEquals(BTC, update3BTCBalance.get().getCurrency());
        assertEquals(0, 0, update3BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update3BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update3BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update3BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update3BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update3BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update3BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));
        final Optional<BalanceDTO> update3ETHBalance = a.getBalance(ETH);
        assertTrue(update3ETHBalance.isPresent());
        assertEquals(ETH, update3ETHBalance.get().getCurrency());
        assertEquals(0, 0, update3ETHBalance.get().getTotal().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3ETHBalance.get().getAvailable().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3ETHBalance.get().getFrozen().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3ETHBalance.get().getLoaned().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3ETHBalance.get().getBorrowed().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3ETHBalance.get().getWithdrawing().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3ETHBalance.get().getDepositing().compareTo(new BigDecimal("2")));
        final Optional<BalanceDTO> update3USDTBalance = a.getBalance(USDT);
        assertTrue(update3USDTBalance.isPresent());
        assertEquals(USDT, update3USDTBalance.get().getCurrency());
        assertEquals(0, 0, update3USDTBalance.get().getTotal().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3USDTBalance.get().getAvailable().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3USDTBalance.get().getFrozen().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3USDTBalance.get().getLoaned().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3USDTBalance.get().getBorrowed().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3USDTBalance.get().getWithdrawing().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update3USDTBalance.get().getDepositing().compareTo(new BigDecimal("2")));

        // Check update 4  - ETH balance of account 01 changed (borrowed value).
        a = iterator.next();
        assertEquals("01", a.getAccountId());
        assertEquals("Account 01", a.getName());
        final Optional<BalanceDTO> update4BTCBalance = a.getBalance(BTC);
        assertTrue(update4BTCBalance.isPresent());
        assertEquals(BTC, update4BTCBalance.get().getCurrency());
        assertEquals(0, 0, update4BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update4BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update4BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update4BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update4BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update4BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update4BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));
        final Optional<BalanceDTO> update4ETHBalance = a.getBalance(ETH);
        assertTrue(update4ETHBalance.isPresent());
        assertEquals(ETH, update4ETHBalance.get().getCurrency());
        assertEquals(0, 0, update4ETHBalance.get().getTotal().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4ETHBalance.get().getAvailable().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4ETHBalance.get().getFrozen().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4ETHBalance.get().getLoaned().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4ETHBalance.get().getBorrowed().compareTo(new BigDecimal("5")));
        assertEquals(0, 0, update4ETHBalance.get().getWithdrawing().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4ETHBalance.get().getDepositing().compareTo(new BigDecimal("2")));
        final Optional<BalanceDTO> update4USDTBalance = a.getBalance(USDT);
        assertTrue(update4USDTBalance.isPresent());
        assertEquals(USDT, update4USDTBalance.get().getCurrency());
        assertEquals(0, 0, update4USDTBalance.get().getTotal().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4USDTBalance.get().getAvailable().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4USDTBalance.get().getFrozen().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4USDTBalance.get().getLoaned().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4USDTBalance.get().getBorrowed().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4USDTBalance.get().getWithdrawing().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update4USDTBalance.get().getDepositing().compareTo(new BigDecimal("2")));

        // Check update 5 - BTC balance of account 02 changed (all values).
        a = iterator.next();
        assertEquals("02", a.getAccountId());
        assertEquals("Account 02", a.getName());
        final Optional<BalanceDTO> update5BTCBalance = a.getBalance(BTC);
        assertTrue(update5BTCBalance.isPresent());
        assertEquals(BTC, update5BTCBalance.get().getCurrency());
        assertEquals(0, 0, update5BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update5BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update5BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update5BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update5BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update5BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update5BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));

        // Check update 6 - ETH balance removed on account 01.
        a = iterator.next();
        assertEquals("01", a.getAccountId());
        assertEquals("Account 01", a.getName());
        assertEquals(2, a.getBalances().size());
        final Optional<BalanceDTO> update6BTCBalance = a.getBalance(BTC);
        assertTrue(update6BTCBalance.isPresent());
        assertEquals(BTC, update6BTCBalance.get().getCurrency());
        assertEquals(0, 0, update6BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update6BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update6BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update6BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update6BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update6BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, update6BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));
        final Optional<BalanceDTO> update6ETHBalance = a.getBalance(ETH);
        assertFalse(update6ETHBalance.isPresent());
        final Optional<BalanceDTO> update6USDTBalance = a.getBalance(USDT);
        assertTrue(update6USDTBalance.isPresent());
        assertEquals(USDT, update6USDTBalance.get().getCurrency());
        assertEquals(0, 0, update6USDTBalance.get().getTotal().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update6USDTBalance.get().getAvailable().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update6USDTBalance.get().getFrozen().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update6USDTBalance.get().getLoaned().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update6USDTBalance.get().getBorrowed().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update6USDTBalance.get().getWithdrawing().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, update6USDTBalance.get().getDepositing().compareTo(new BigDecimal("2")));

        // Check update 7 - New account 03.
        a = iterator.next();
        assertEquals("03", a.getAccountId());
        assertEquals("Account 03", a.getName());
        assertEquals(1, a.getBalances().size());
        final Optional<BalanceDTO> update7BTCBalance = a.getBalance(BTC);
        assertTrue(update7BTCBalance.isPresent());
        assertEquals(BTC, update7BTCBalance.get().getCurrency());
        assertEquals(0, 0, update7BTCBalance.get().getTotal().compareTo(new BigDecimal("11")));
        assertEquals(0, 0, update7BTCBalance.get().getAvailable().compareTo(new BigDecimal("12")));
        assertEquals(0, 0, update7BTCBalance.get().getFrozen().compareTo(new BigDecimal("13")));
        assertEquals(0, 0, update7BTCBalance.get().getLoaned().compareTo(new BigDecimal("15")));
        assertEquals(0, 0, update7BTCBalance.get().getBorrowed().compareTo(new BigDecimal("14")));
        assertEquals(0, 0, update7BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("16")));
        assertEquals(0, 0, update7BTCBalance.get().getDepositing().compareTo(new BigDecimal("17")));

        // =============================================================================================================
        // Check data we have in strategy.
        final Map<String, AccountDTO> strategyAccounts = strategy.getAccounts();
        assertEquals(4, strategyAccounts.size());
        assertNotNull(strategyAccounts.get("trade"));   // First reply of the mock when Cassandre starts.
        assertNotNull(strategyAccounts.get("01"));
        assertNotNull(strategyAccounts.get("02"));
        assertNotNull(strategyAccounts.get("03"));

        // Check account 01.
        Optional<AccountDTO> a1 = strategy.getAccountByAccountId("01");
        assertTrue(a1.isPresent());
        assertEquals("01", a1.get().getAccountId());
        assertEquals("Account 01", a1.get().getName());
        assertEquals(2, a1.get().getBalances().size());
        final Optional<BalanceDTO> account01BTCBalance = a1.get().getBalance(BTC);
        assertTrue(account01BTCBalance.isPresent());
        assertEquals(BTC, account01BTCBalance.get().getCurrency());
        assertEquals(0, 0, account01BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account01BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account01BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account01BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account01BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account01BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account01BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));
        final Optional<BalanceDTO> account01ETHBalance = a1.get().getBalance(ETH);
        assertFalse(account01ETHBalance.isPresent());
        final Optional<BalanceDTO> account01USDTBalance = a1.get().getBalance(USDT);
        assertTrue(account01USDTBalance.isPresent());
        assertEquals(USDT, account01USDTBalance.get().getCurrency());
        assertEquals(0, 0, account01USDTBalance.get().getTotal().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, account01USDTBalance.get().getAvailable().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, account01USDTBalance.get().getFrozen().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, account01USDTBalance.get().getLoaned().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, account01USDTBalance.get().getBorrowed().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, account01USDTBalance.get().getWithdrawing().compareTo(new BigDecimal("2")));
        assertEquals(0, 0, account01USDTBalance.get().getDepositing().compareTo(new BigDecimal("2")));

        // Check account 02.
        Optional<AccountDTO> a2 = strategy.getAccountByAccountId("02");
        assertTrue(a2.isPresent());
        assertEquals("02", a2.get().getAccountId());
        assertEquals("Account 02", a2.get().getName());
        assertEquals(1, a2.get().getBalances().size());
        final Optional<BalanceDTO> account02BTCBalance = a2.get().getBalance(BTC);
        assertTrue(account02BTCBalance.isPresent());
        assertEquals(BTC, account02BTCBalance.get().getCurrency());
        assertEquals(0, 0, account02BTCBalance.get().getTotal().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account02BTCBalance.get().getAvailable().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account02BTCBalance.get().getFrozen().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account02BTCBalance.get().getLoaned().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account02BTCBalance.get().getBorrowed().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account02BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("1")));
        assertEquals(0, 0, account02BTCBalance.get().getDepositing().compareTo(new BigDecimal("1")));

        // Check account 03.
        Optional<AccountDTO> a3 = strategy.getAccountByAccountId("03");
        assertTrue(a3.isPresent());
        assertEquals("03", a3.get().getAccountId());
        assertEquals("Account 03", a3.get().getName());
        assertEquals(1, a3.get().getBalances().size());
        final Optional<BalanceDTO> account03BTCBalance = a3.get().getBalance(BTC);
        assertTrue(account03BTCBalance.isPresent());
        assertEquals(BTC, account03BTCBalance.get().getCurrency());
        assertEquals(0, 0, account03BTCBalance.get().getTotal().compareTo(new BigDecimal("11")));
        assertEquals(0, 0, account03BTCBalance.get().getAvailable().compareTo(new BigDecimal("12")));
        assertEquals(0, 0, account03BTCBalance.get().getFrozen().compareTo(new BigDecimal("13")));
        assertEquals(0, 0, account03BTCBalance.get().getLoaned().compareTo(new BigDecimal("15")));
        assertEquals(0, 0, account03BTCBalance.get().getBorrowed().compareTo(new BigDecimal("14")));
        assertEquals(0, 0, account03BTCBalance.get().getWithdrawing().compareTo(new BigDecimal("16")));
        assertEquals(0, 0, account03BTCBalance.get().getDepositing().compareTo(new BigDecimal("17")));
    }

}
