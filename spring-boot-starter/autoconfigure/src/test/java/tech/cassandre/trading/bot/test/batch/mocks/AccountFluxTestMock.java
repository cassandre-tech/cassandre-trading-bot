package tech.cassandre.trading.bot.test.batch.mocks;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static java.math.BigDecimal.ZERO;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class AccountFluxTestMock extends BaseMock {

    @Override
    public AccountService getXChangeAccountServiceMock() throws IOException {
        final AccountService mockAccountService = mock(AccountService.class);

        // =============================================================================================================
        // Returns the mock.
        given(mockAccountService.getAccountInfo()).willReturn(
                getAccountInfoReplyForExchangeConfiguration(),
                // =====================================================================================================
                // Reply 01.
                // Account 01 with 2 balances (1 BTC - 2 ETH).
                // Account 02 with 1 balance (1 BTC).
                getReply01(),
                // =====================================================================================================
                // Reply 02.
                // Account 01 with 3 balances (1 BTC - 2 ETH - 2 USDT).
                // Account 02 with 1 balance.
                // Change : Account 1 has now 3 balances.
                getReply02(),
                // =====================================================================================================
                // Reply 03.
                // Account 01 with 3 balances (1 BTC - 2 ETH - 2 USDT).
                // Account 02 with 1 balance.
                // Change : No change.
                getReply03(),
                // =====================================================================================================
                // Reply 04.
                // Account 01 with 3 balances.
                // Account 02 with 1 balance.
                // Change : ETH balance of account 01 changed (borrowed value) & balance of account 02 (all values).
                getReply04(),
                // =====================================================================================================
                // Reply 05.
                // Account 01 with 3 balances.
                // Account 02 with 1 balance.
                // Change : No change.
                getReply05(),
                // =====================================================================================================
                // Reply 06.
                // Account 01 with 2 balances.
                // Account 02 with 1 balance.
                getReply06(),
                // =====================================================================================================
                // Reply 07.
                // Account 01 with 3 balances.
                // Account 02 with 1 balance.
                // Account 03 with 1 balance.
                // Change : New account (03).
                getReply07()
        );
        return mockAccountService;
    }

    /**
     * Reply 01.
     * Account 01 with 2 balances (1 BTC - 2 ETH).
     * Account 02 with 1 balance (1 BTC).
     *
     * @return reply
     */
    private AccountInfo getReply01() {
        Balance account01Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance2 = new Balance(
                Currency.ETH,               // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account01Wallet = new Wallet(
                "01",
                "Account 01",
                Arrays.asList(account01Balance1, account01Balance2),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account02Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account02Wallet = new Wallet(
                "02",
                "Account 02",
                Collections.singletonList(account02Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        return new AccountInfo(
                account01Wallet,
                account02Wallet
        );
    }

    /**
     * Reply 02.
     * Account 01 with 3 balances (1 BTC - 2 ETH - 2 USDT).
     * Account 02 with 1 balance.
     * Change : Account 1 has now 3 balances.
     *
     * @return reply
     */
    private AccountInfo getReply02() {
        Balance account01Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance2 = new Balance(
                Currency.ETH,               // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance3 = new Balance(
                Currency.USDT,              // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account01Wallet = new Wallet(
                "01",
                "Account 01",
                Arrays.asList(account01Balance1, account01Balance2, account01Balance3),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account02Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account02Wallet = new Wallet(
                "02",
                "Account 02",
                Collections.singletonList(account02Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        return new AccountInfo(
                account01Wallet,
                account02Wallet
        );
    }

    /**
     * Reply 03.
     * Account 01 with 3 balances (1 BTC - 2 ETH - 2 USDT).
     * Account 02 with 1 balance.
     * Change : No change.
     *
     * @return reply
     */
    private AccountInfo getReply03() {
        Balance account01Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance2 = new Balance(
                Currency.ETH,               // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance3 = new Balance(
                Currency.USDT,              // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account01Wallet = new Wallet(
                "01",
                "Account 01",
                Arrays.asList(account01Balance1, account01Balance2, account01Balance3),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account02Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account02Wallet = new Wallet(
                "02",
                "Account 02",
                Collections.singletonList(account02Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        return new AccountInfo(
                account01Wallet,
                account02Wallet
        );
    }

    /**
     * Reply 04.
     * Account 01 with 3 balances (1 BTC - 2 ETH - 2 USDT).
     * Account 02 with 1 balance.
     * Change : ETH balance of account 01 changed (borrowed value).
     *
     * @return reply
     */
    private AccountInfo getReply04() {
        Balance account01Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance2 = new Balance(
                Currency.ETH,               // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("5"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance3 = new Balance(
                Currency.USDT,              // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account01Wallet = new Wallet(
                "01",
                "Account 01",
                Arrays.asList(account01Balance1, account01Balance2, account01Balance3),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account02Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account02Wallet = new Wallet(
                "02",
                "Account 02",
                Collections.singletonList(account02Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        return new AccountInfo(
                account01Wallet,
                account02Wallet
        );
    }

    /**
     * Reply 05.
     * Account 01 with 3 balances (1 BTC - 2 ETH - 2 USDT).
     * Account 02 with 1 balance.
     * Change : No change.
     *
     * @return reply
     */
    private AccountInfo getReply05() {
        Balance account01Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance2 = new Balance(
                Currency.ETH,               // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("5"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance3 = new Balance(
                Currency.USDT,              // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account01Wallet = new Wallet(
                "01",
                "Account 01",
                Arrays.asList(account01Balance1, account01Balance2, account01Balance3),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account02Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("3"),    // Total.
                new BigDecimal("3"),    // Available.
                new BigDecimal("3"),    // Frozen.
                new BigDecimal("3"),    // Borrowed
                new BigDecimal("3"),    // Loaned.
                new BigDecimal("3"),    // Withdrawing.
                new BigDecimal("3"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account02Wallet = new Wallet(
                "02",
                "Account 02",
                Collections.singletonList(account02Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        return new AccountInfo(
                account01Wallet,
                account02Wallet
        );
    }

    /**
     * Reply 06.
     * Account 01 with 2 balances.
     * Account 02 with 1 balance.
     * Change : ETH balance removed on account 01.
     *
     * @return reply
     */
    private AccountInfo getReply06() {
        Balance account01Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance3 = new Balance(
                Currency.USDT,              // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account01Wallet = new Wallet(
                "01",
                "Account 01",
                Arrays.asList(account01Balance1, account01Balance3),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account02Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("3"),    // Total.
                new BigDecimal("3"),    // Available.
                new BigDecimal("3"),    // Frozen.
                new BigDecimal("3"),    // Borrowed
                new BigDecimal("3"),    // Loaned.
                new BigDecimal("3"),    // Withdrawing.
                new BigDecimal("3"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account02Wallet = new Wallet(
                "02",
                "Account 02",
                Collections.singletonList(account02Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        return new AccountInfo(
                account01Wallet,
                account02Wallet
        );
    }

    /**
     * Reply 07.
     * Account 01 with 3 balances.
     * Account 02 with 1 balance.
     * Account 03 with 1 balance.
     * Change : New account (03).
     *
     * @return reply
     */
    private AccountInfo getReply07() {
        Balance account01Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("1"),    // Total.
                new BigDecimal("1"),    // Available.
                new BigDecimal("1"),    // Frozen.
                new BigDecimal("1"),    // Borrowed
                new BigDecimal("1"),    // Loaned.
                new BigDecimal("1"),    // Withdrawing.
                new BigDecimal("1"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Balance account01Balance3 = new Balance(
                Currency.USDT,              // Currency.
                new BigDecimal("2"),    // Total.
                new BigDecimal("2"),    // Available.
                new BigDecimal("2"),    // Frozen.
                new BigDecimal("2"),    // Borrowed
                new BigDecimal("2"),    // Loaned.
                new BigDecimal("2"),    // Withdrawing.
                new BigDecimal("2"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account01Wallet = new Wallet(
                "01",
                "Account 01",
                Arrays.asList(account01Balance1, account01Balance3),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account02Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("3"),    // Total.
                new BigDecimal("3"),    // Available.
                new BigDecimal("3"),    // Frozen.
                new BigDecimal("3"),    // Borrowed
                new BigDecimal("3"),    // Loaned.
                new BigDecimal("3"),    // Withdrawing.
                new BigDecimal("3"),    // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account02Wallet = new Wallet(
                "02",
                "Account 02",
                Collections.singletonList(account02Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        Balance account03Balance1 = new Balance(
                Currency.BTC,               // Currency.
                new BigDecimal("11"),   // Total.
                new BigDecimal("12"),   // Available.
                new BigDecimal("13"),   // Frozen.
                new BigDecimal("14"),   // Borrowed
                new BigDecimal("15"),   // Loaned.
                new BigDecimal("16"),   // Withdrawing.
                new BigDecimal("17"),   // Depositing.
                new Date()                  // Timestamp.
        );

        Wallet account03Wallet = new Wallet(
                "03",
                "Account 03",
                Collections.singletonList(account03Balance1),
                Collections.emptySet(),
                ZERO,
                ZERO);

        return new AccountInfo(
                account01Wallet,
                account02Wallet,
                account03Wallet
        );
    }

}
