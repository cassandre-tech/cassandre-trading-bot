package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.batch.mocks.AccountFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_ACCOUNT;

@SpringBootTest
@DisplayName("Batch - Account flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_ACCOUNT, value = "100")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Import(AccountFluxTestMock.class)
public class AccountFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // We will call the service 8 times and we will have two empty answers.
        final int numberOfUpdatesExpected = 6;
        final int numberOfServiceCallsExpected = 8;

        // Waiting for the user service to have been called with all the test data.
        await().untilAsserted(() -> verify(userService, atLeast(numberOfServiceCallsExpected)).getUser());

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getAccountsUpdateReceived().size() > 0);
        assertTrue(strategy.getAccountsUpdateReceived().size() < numberOfUpdatesExpected);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertEquals(numberOfUpdatesExpected, strategy.getAccountsUpdatesReceived().size()));
        // Test all values received by the strategy with update methods.
        final Iterator<AccountDTO> iterator = strategy.getAccountsUpdatesReceived().iterator();

        // Check update 1.
        AccountDTO a = iterator.next();
        assertEquals("01", a.getId());
        assertEquals(2, a.getBalances().size());

        // Check update 2.
        a = iterator.next();
        assertEquals("02", a.getId());
        assertEquals(1, a.getBalances().size());

        // Check update 3.
        a = iterator.next();
        assertEquals("01", a.getId());
        assertEquals(3, a.getBalances().size());

        // Check update 4.
        a = iterator.next();
        assertEquals("01", a.getId());
        assertTrue(a.getBalance(ETH).isPresent());
        assertEquals(0, new BigDecimal("5").compareTo(a.getBalance(ETH).get().getBorrowed()));

        // Check update 5.
        a = iterator.next();
        assertEquals("02", a.getId());
        assertTrue(a.getBalance(BTC).isPresent());
        assertEquals(0, new BigDecimal("2").compareTo(a.getBalance(BTC).get().getFrozen()));

        // Check update 6.
        a = iterator.next();
        assertEquals("01", a.getId());
        assertEquals(2, a.getBalances().size());

        // TODO Improve the test with a last update adding a new account (03) with a new balance (3 ETH).

        // Check data we have in strategy.
        AccountDTO account1 = strategy.getAccounts().get("01");
        assertNotNull(account1);
        assertEquals(2, account1.getBalances().size());
        assertTrue(account1.getBalance(BTC).isPresent());
        assertEquals(0, new BigDecimal("1").compareTo(account1.getBalance(BTC).get().getAvailable()));
        assertTrue(account1.getBalance(USDT).isPresent());
        assertEquals(0, new BigDecimal("2").compareTo(account1.getBalance(USDT).get().getAvailable()));
        AccountDTO account2 = strategy.getAccounts().get("02");
        assertNotNull(account2);
        assertEquals(1, account2.getBalances().size());
        assertTrue(account2.getBalance(BTC).isPresent());
        assertEquals(0, new BigDecimal("2").compareTo(account2.getBalance(BTC).get().getFrozen()));
        assertEquals(0, new BigDecimal("1").compareTo(account2.getBalance(BTC).get().getAvailable()));
    }

}
