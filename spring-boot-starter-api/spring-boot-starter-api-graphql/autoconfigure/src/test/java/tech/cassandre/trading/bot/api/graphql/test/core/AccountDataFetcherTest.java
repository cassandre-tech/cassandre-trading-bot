package tech.cassandre.trading.bot.api.graphql.test.core;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.api.graphql.client.generated.DgsConstants;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.AccountByAccountIdGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.AccountByAccountIdProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.AccountsGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.AccountsProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Account;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Balance;
import tech.cassandre.trading.bot.api.graphql.data.AccountDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;
import tech.cassandre.trading.bot.api.graphql.test.util.base.BaseDataFetcherTest;
import tech.cassandre.trading.bot.api.graphql.test.util.mock.BaseMock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

/**
 * Account data fetcher test. Using a mock to simulate account data.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Account data fetcher test")
@SpringBootTest(classes = {DgsAutoConfiguration.class, CassandreTradingBot.class, DgsAutoConfiguration.class, AccountDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(BaseMock.class)
public class AccountDataFetcherTest extends BaseDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("accounts: [Account]")
    public void accounts() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new AccountsGraphQLQuery.Builder().build(),
                new AccountsProjectionRoot().accountId()
                                            .name());
        // Query execution.
        List<Account> accounts = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Accounts + "[*]",
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(2, accounts.size());

        // Trade account tests.
        final Optional<Account> tradeAccount = accounts.stream().filter(account -> "trade".equals(account.getAccountId())).findAny();
        assertTrue(tradeAccount.isPresent());
        assertEquals("trade", tradeAccount.get().getAccountId());
        assertEquals("trade account name", tradeAccount.get().getName());

        // Savings account tests.
        final Optional<Account> savingsAccount = accounts.stream().filter(account -> "savings".equals(account.getAccountId())).findAny();
        assertTrue(savingsAccount.isPresent());
        assertEquals("savings", savingsAccount.get().getAccountId());
        assertEquals("savings account name", savingsAccount.get().getName());
    }

    @Test
    @DisplayName("accountByAccountId(accountId: String): Account")
    void accountByAccountId() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new AccountByAccountIdGraphQLQuery.Builder().accountId("trade").build(),
                new AccountByAccountIdProjectionRoot().accountId()
                        .name()
                        .balances()
                            .currency().code()
                            .getParent()
                            .total()
                            .available()
                            .frozen()
                            .loaned()
                            .borrowed()
                            .withdrawing()
                            .depositing());
        // Query execution.
        Account account = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.AccountByAccountId,
                new TypeRef<>() {
                });
        // Tests
        assertNotNull(account);
        assertEquals("trade", account.getAccountId());
        assertEquals("trade account name", account.getName());
        assertEquals(2, account.getBalances().size());

        // Testing BTC balance.
        final Optional<Balance> btcBalance = account.getBalances().stream()
                .filter(balance -> balance.getCurrency().equals(BTC))
                .findAny();
        assertTrue(btcBalance.isPresent());
        assertEquals(BTC, btcBalance.get().getCurrency());
        assertEquals(0, new BigDecimal("1").compareTo(btcBalance.get().getTotal()));
        assertEquals(0, new BigDecimal("2").compareTo(btcBalance.get().getAvailable()));
        assertEquals(0, new BigDecimal("3").compareTo(btcBalance.get().getFrozen()));
        assertEquals(0, new BigDecimal("4").compareTo(btcBalance.get().getBorrowed()));
        assertEquals(0, new BigDecimal("5").compareTo(btcBalance.get().getLoaned()));
        assertEquals(0, new BigDecimal("6").compareTo(btcBalance.get().getWithdrawing()));
        assertEquals(0, new BigDecimal("7").compareTo(btcBalance.get().getDepositing()));

        // Testing ETH balance.
        final Optional<Balance> ethBalance = account.getBalances().stream()
                .filter(balance -> balance.getCurrency().equals(ETH))
                .findAny();
        assertTrue(ethBalance.isPresent());
        assertEquals(ETH, ethBalance.get().getCurrency());
        assertEquals(0, new BigDecimal("11").compareTo(ethBalance.get().getTotal()));
        assertEquals(0, new BigDecimal("22").compareTo(ethBalance.get().getAvailable()));
        assertEquals(0, new BigDecimal("33").compareTo(ethBalance.get().getFrozen()));
        assertEquals(0, new BigDecimal("44").compareTo(ethBalance.get().getBorrowed()));
        assertEquals(0, new BigDecimal("55").compareTo(ethBalance.get().getLoaned()));
        assertEquals(0, new BigDecimal("66").compareTo(ethBalance.get().getWithdrawing()));
        assertEquals(0, new BigDecimal("77").compareTo(ethBalance.get().getDepositing()));
    }

}
