package tech.cassandre.trading.bot.util.dry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * AOP for user service in dry mode.
 */
@Aspect
@Configuration
@ConditionalOnExpression("${cassandre.trading.bot.exchange.modes.dry:true}")
public class UserServiceDryModeAOP extends BaseService {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** User file prefix. */
    private static final String USER_FILE_PREFIX = "user-";

    /** User file suffix. */
    private static final String USER_FILE_SUFFIX = ".*sv";

    /** User ID. */
    private static final String USER_ID = "user";

    /** Trade account ID. */
    private static final String TRADE_ACCOUNT_ID = "trade";

    /** Simulated user information. */
    private UserDTO user;

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     */
    public UserServiceDryModeAOP(final ApplicationContext newApplicationContext) {
        this.applicationContext = newApplicationContext;
        Map<String, AccountDTO> accounts = new LinkedHashMap<>();

        getFilesToLoad().forEach(file -> {
            if (file.getFilename() != null) {

                // Account.
                final int accountIndexStart = file.getFilename().indexOf(USER_FILE_PREFIX) + USER_FILE_PREFIX.length();
                final int accountIndexStop = file.getFilename().indexOf("sv") - 2;
                final String accountName = file.getFilename().substring(accountIndexStart, accountIndexStop);
                logger.info("Adding account '" + accountName + "'");

                // Balances.
                HashMap<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
                try (Scanner scanner = new Scanner(file.getFile())) {
                    while (scanner.hasNextLine()) {
                        try (Scanner rowScanner = new Scanner(scanner.nextLine())) {
                            if (file.getFilename().endsWith("tsv")) {
                                rowScanner.useDelimiter("\t");
                            } else {
                                rowScanner.useDelimiter(",");
                            }
                            // Data retrieved from file.
                            final String currency = rowScanner.next().replaceAll("\"", "");
                            final String amount = rowScanner.next().replaceAll("\"", "");
                            // Creating balance.
                            logger.info("- Adding balance " + amount + " " + currency);
                            BalanceDTO balance = BalanceDTO.builder()
                                    .currency(new CurrencyDTO(currency))
                                    .available(new BigDecimal(amount))
                                    .build();
                            balances.put(new CurrencyDTO(currency), balance);
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.error("{} not found !", file.getFilename());
                } catch (IOException e) {
                    logger.error("IOException : " + e);
                }

                // Creating account.
                accounts.put(accountName,
                        AccountDTO.builder()
                                .accountId(accountName)
                                .name(accountName)
                                .balances(balances)
                                .build());
            }
        });

        // Creates the user.
        user = UserDTO.builder()
                .id(USER_ID)
                .accounts(accounts)
                .build();
    }

    @Around("execution(* tech.cassandre.trading.bot.service.UserService.getUser())")
    public final Optional<UserDTO> getUser(final ProceedingJoinPoint pjp) {
        return Optional.of(user);
    }

    public final Optional<UserDTO> getUser() {
        return Optional.of(user);
    }

    /**
     * Update balance of trade account (method called by trade service).
     *
     * @param currency currency
     * @param amount   amount
     */
    public void addToBalance(final CurrencyDTO currency, final BigDecimal amount) {
        // TODO Retrieve the trade account specified in each strategy.
        Optional<BalanceDTO> balance = user.getAccounts().get(TRADE_ACCOUNT_ID).getBalance(currency);
        final Map<String, AccountDTO> accounts = new LinkedHashMap<>();

        // For each account.
        user.getAccounts().forEach((s, a) -> {
            HashMap<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();

            // For each balance.
            a.getBalances().forEach((c, b) -> {
                BalanceDTO newBalance;
                if (a.getAccountId().equals(TRADE_ACCOUNT_ID) && b.getCurrency().equals(currency)) {
                    // If we are on the account to update, we calculate the new value.
                    newBalance = BalanceDTO.builder()
                            .currency(b.getCurrency())
                            .available(b.getAvailable().add(amount))
                            .build();
                } else {
                    // Else we keep the same value.
                    newBalance = BalanceDTO.builder()
                            .currency(b.getCurrency())
                            .available(b.getAvailable())
                            .build();
                }
                balances.put(newBalance.getCurrency(), newBalance);
            });
            // If the balance does not exists, we add it.
            if (balance.isEmpty()) {
                balances.put(currency, BalanceDTO.builder()
                        .currency(currency)
                        .available(amount)
                        .build());
            }

            // Creating account
            AccountDTO account = AccountDTO.builder()
                    .accountId(a.getAccountId())
                    .name(a.getName())
                    .balances(balances)
                    .build();
            accounts.put(account.getAccountId(), account);
        });

        user = UserDTO.builder()
                .id(USER_ID)
                .accounts(accounts)
                .build();
    }

    /**
     * Returns the list of files to import.
     *
     * @return files to import.
     */
    private List<Resource> getFilesToLoad() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] resources = resolver.getResources("classpath*:" + USER_FILE_PREFIX + "*" + USER_FILE_SUFFIX);
            return Arrays.asList(resources);
        } catch (IOException e) {
            logger.error("UserServiceDryModeAOP encountered an error : " + e.getMessage());
        }
        return Collections.emptyList();
    }

}
