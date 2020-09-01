package tech.cassandre.trading.bot.integration.kucoin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.user.AccountFeatureDTO.FUNDING;
import static tech.cassandre.trading.bot.dto.user.AccountFeatureDTO.TRADING;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.name=${KUCOIN_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${KUCOIN_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${KUCOIN_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${KUCOIN_KEY}",
        "cassandre.trading.bot.exchange.secret=${KUCOIN_SECRET}",
        "cassandre.trading.bot.exchange.rates.account=100",
        "cassandre.trading.bot.exchange.rates.ticker=101",
        "cassandre.trading.bot.exchange.rates.trade=102",
        "testableStrategy.enabled=true",
        "invalidStrategy.enabled=false"
})
@DisplayName("Kucoin - User service")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Get user, accounts and balances")
    public void testGetUser() {
        // Expected values.
        final int expectedAccounts = 2;
        final int expectedWalletsInTradingAccount = 2;
        final BigDecimal expectedAmountInKCS = BigDecimal.valueOf(1000);

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
        // Testing Wallet.
        assertEquals(expectedAccounts, user.get().getAccounts().size());
        Map<String, AccountDTO> wallets = user.get().getAccounts();
        AccountDTO mainWallet = wallets.get("main");
        assertNotNull(mainWallet);
        assertEquals("main", mainWallet.getId());
        assertEquals("main", mainWallet.getName());
        assertEquals(2, mainWallet.getFeatures().size());
        assertTrue(mainWallet.getFeatures().contains(TRADING));
        assertTrue(mainWallet.getFeatures().contains(FUNDING));
        AccountDTO tradeWallet = wallets.get("trade");
        assertNotNull(tradeWallet);
        assertEquals("trade", tradeWallet.getId());
        assertEquals("trade", tradeWallet.getName());
        assertEquals(2, tradeWallet.getFeatures().size());
        assertTrue(tradeWallet.getFeatures().contains(TRADING));
        assertTrue(tradeWallet.getFeatures().contains(FUNDING));

        // =============================================================================================================
        // Testing balances.
        assertEquals(expectedWalletsInTradingAccount, tradeWallet.getBalances().size());
        // Existing balances.
        assertTrue(tradeWallet.getBalance("BTC").isPresent());
        assertTrue(tradeWallet.getBalance(CurrencyDTO.BTC).isPresent());
        assertTrue(tradeWallet.getBalance("ETH").isPresent());
        assertTrue(tradeWallet.getBalance(CurrencyDTO.ETH).isPresent());
        assertTrue(mainWallet.getBalance("KCS").isPresent());
        // Non existing balances.
        assertTrue(tradeWallet.getBalance("ANC").isEmpty());
        assertTrue(tradeWallet.getBalance(CurrencyDTO.ANC).isEmpty());
        // Values.
        assertEquals(1, tradeWallet.getBalance("BTC").get().getTotal().compareTo(BigDecimal.ZERO));
        assertEquals(1, tradeWallet.getBalance("ETH").get().getTotal().compareTo(BigDecimal.ZERO));
        assertEquals(0, mainWallet.getBalance("KCS").get().getTotal().compareTo(expectedAmountInKCS));
        assertEquals(0, mainWallet.getBalance("KCS").get().getAvailable().compareTo(expectedAmountInKCS));
    }

}
