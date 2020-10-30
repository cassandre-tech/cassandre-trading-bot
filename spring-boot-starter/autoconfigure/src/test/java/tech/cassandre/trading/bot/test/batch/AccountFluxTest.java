package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@SpringBootTest
@DisplayName("Batch - Account flux")
@Configuration({
        @Property(key = "TEST_NAME", value = "Batch - Account flux")
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
        final int numberOfAccountsUpdateExpected = 6;
        final int numberOfUserServiceCallsExpected = 6;

        // Waiting for the user service to have been called with all the test data.
        await().untilAsserted(() -> verify(userService, atLeast(numberOfUserServiceCallsExpected)).getUser());

        // Checking that somme accounts update have already been treated (to verify we work on a single thread).
        assertTrue(strategy.getAccountsUpdatesReceived().size() <= numberOfAccountsUpdateExpected);
        assertTrue(strategy.getAccountsUpdatesReceived().size() > 0);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertEquals(numberOfAccountsUpdateExpected, strategy.getAccountsUpdatesReceived().size()));

        // Checking values.
        final Iterator<AccountDTO> iterator = strategy.getAccountsUpdatesReceived().iterator();

        // Check update 1.
        AccountDTO accountUpdate = iterator.next();
        assertEquals("01", accountUpdate.getId());
        assertEquals(2, accountUpdate.getBalances().size());

        // Check update 2.
        accountUpdate = iterator.next();
        assertEquals("02", accountUpdate.getId());
        assertEquals(1, accountUpdate.getBalances().size());

        // Check update 3.
        accountUpdate = iterator.next();
        assertEquals("01", accountUpdate.getId());
        assertEquals(3, accountUpdate.getBalances().size());

        // Check update 4.
        accountUpdate = iterator.next();
        assertEquals("01", accountUpdate.getId());
        assertTrue(accountUpdate.getBalance(ETH).isPresent());
        assertEquals(0, new BigDecimal("5").compareTo(accountUpdate.getBalance(ETH).get().getBorrowed()));

        // Check update 5.
        accountUpdate = iterator.next();
        assertEquals("02", accountUpdate.getId());
        assertTrue(accountUpdate.getBalance(CurrencyDTO.BTC).isPresent());
        assertEquals(0, new BigDecimal("2").compareTo(accountUpdate.getBalance(CurrencyDTO.BTC).get().getFrozen()));

        // Check update 6.
        accountUpdate = iterator.next();
        assertEquals("01", accountUpdate.getId());
        assertEquals(2, accountUpdate.getBalances().size());
    }

}
