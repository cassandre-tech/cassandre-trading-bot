package tech.cassandre.trading.bot.integration.gemini;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.service.UserService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.user.AccountFeatureDTO.FUNDING;
import static tech.cassandre.trading.bot.dto.user.AccountFeatureDTO.TRADING;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ANC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.driver-class-name=${GEMINI_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${GEMINI_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${GEMINI_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${GEMINI_KEY}",
        "cassandre.trading.bot.exchange.secret=${GEMINI_SECRET}",
        "cassandre.trading.bot.exchange.rates.account=100",
        "cassandre.trading.bot.exchange.rates.ticker=101",
        "cassandre.trading.bot.exchange.rates.trade=102",
        "cassandre.trading.bot.database.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver",
        "cassandre.trading.bot.database.datasource.url=jdbc:hsqldb:mem:cassandre-database;shutdown=true",
        "cassandre.trading.bot.database.datasource.username=sa",
        "cassandre.trading.bot.database.datasource.password=",
        "testableStrategy.enabled=true",
        "invalidStrategy.enabled=false"
})
@DisplayName("Gemini - User service")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Tag("integration")
    @DisplayName("Check get user, accounts and balances")
    public void checkGetUser() {
        // Expected values.
        final int expectedAccounts = 1;

        // =============================================================================================================
        // Retrieve the account.
        Optional<UserDTO> user = userService.getUser();

        // =============================================================================================================
        // Testing Account.
        assertTrue(user.isPresent());

        // =============================================================================================================
        // Testing wallets.
        assertEquals(expectedAccounts, user.get().getAccounts().size());
        Map<String, AccountDTO> wallets = user.get().getAccounts();
        AccountDTO tradeWallet = wallets.values().iterator().next();
        assertNotNull(tradeWallet);
        assertEquals(2, tradeWallet.getFeatures().size());
        assertTrue(tradeWallet.getFeatures().contains(TRADING));
        assertTrue(tradeWallet.getFeatures().contains(FUNDING));

        // =============================================================================================================
        // Testing balances.
        // Existing balances.
        assertTrue(tradeWallet.getBalance("BTC").isPresent());
        assertTrue(tradeWallet.getBalance(BTC).isPresent());
        // Non existing balances.
        assertTrue(tradeWallet.getBalance("ANC").isEmpty());
        assertTrue(tradeWallet.getBalance(ANC).isEmpty());
        // Values.
        assertEquals(0, tradeWallet.getBalance("BTC").get().getTotal().compareTo(new BigDecimal("2000")));
    }

}
