package tech.cassandre.trading.bot.integration.coinbasepro;

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
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.user.AccountFeatureDTO.FUNDING;
import static tech.cassandre.trading.bot.dto.user.AccountFeatureDTO.TRADING;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.driver-class-name=${COINBASE_PRO_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${COINBASE_PRO_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${COINBASE_PRO_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${COINBASE_PRO_KEY}",
        "cassandre.trading.bot.exchange.secret=${COINBASE_PRO_SECRET}",
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
@DisplayName("Coinbase pro - User service")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Tag("integration")
    @DisplayName("Check get user, accounts and balances")
    public void checkGetUser() {
        // Expected values.
        final int expectedAccounts = 1;
        final int expectedWalletsInTradingAccount = 8;
        final BigDecimal expectedAmountInBAT = BigDecimal.valueOf(993);

        // =============================================================================================================
        // Retrieve the account.
        Optional<UserDTO> user = userService.getUser();

        // =============================================================================================================
        // Testing Account.
        assertTrue(user.isPresent());
        assertNotNull(user.get().getTimestamp());
        assertTrue(user.get().getTimestamp().isAfter(ZonedDateTime.now().minusSeconds(1)));
        assertTrue(user.get().getTimestamp().isBefore(ZonedDateTime.now().plusSeconds(1)));

        // =============================================================================================================
        // Testing wallets.
        assertEquals(expectedAccounts, user.get().getAccounts().size());
        Map<String, AccountDTO> wallets = user.get().getAccounts();
        AccountDTO tradeWallet = wallets.get("9872694c-8135-4513-a9c5-e884374abc2f");
        assertNotNull(tradeWallet);
        assertTrue(tradeWallet.getFeatures().contains(TRADING));
        assertTrue(tradeWallet.getFeatures().contains(FUNDING));

        // =============================================================================================================
        // Testing balances.
        assertEquals(expectedWalletsInTradingAccount, tradeWallet.getBalances().size());
        // Existing balances.
        assertTrue(tradeWallet.getBalance("BTC").isPresent());
        assertTrue(tradeWallet.getBalance("USD").isPresent());
        assertTrue(tradeWallet.getBalance("BAT").isPresent());
        // Non existing balances.
        assertTrue(tradeWallet.getBalance("ANC").isEmpty());
        // Values.
        assertEquals(1, tradeWallet.getBalance("BTC").get().getTotal().compareTo(ZERO));
        assertEquals(1, tradeWallet.getBalance("USD").get().getTotal().compareTo(ZERO));
        assertEquals(0, tradeWallet.getBalance("BAT").get().getTotal().compareTo(expectedAmountInBAT));
    }

}
