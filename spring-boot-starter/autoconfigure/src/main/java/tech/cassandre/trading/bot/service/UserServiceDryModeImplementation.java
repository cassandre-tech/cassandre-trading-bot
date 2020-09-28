package tech.cassandre.trading.bot.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.BaseService;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;

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
 * User service in dry mode.
 */
public class UserServiceDryModeImplementation extends BaseService implements UserService {

    /** User file prefix. */
    private static final String USER_FILE_PREFIX = "user-";

    /** User file suffix. */
    private static final String USER_FILE_SUFFIX = ".tsv";

    /** User ID. */
    public static final String USER_ID = "user";

    /** Trade account ID. */
    public static final String TRADE_ACCOUNT_ID = "trade";

    /** Simulated user information. */
    private UserDTO user;

    /** strategy. */
    private GenericCassandreStrategy strategy;

    /**
     * Constructor.
     */
    public UserServiceDryModeImplementation() {
        user = UserDTO.builder().setId(USER_ID).create();
        getFilesToLoad().forEach(file -> {
            if (file.getFilename() != null) {

                // Account.
                final int accountIndexStart = file.getFilename().indexOf(USER_FILE_PREFIX) + USER_FILE_PREFIX.length();
                final int accountIndexStop = file.getFilename().indexOf(USER_FILE_SUFFIX);
                final String accountName = file.getFilename().substring(accountIndexStart, accountIndexStop);
                getLogger().info("Adding account '" + accountName + "'");

                // Balances.
                HashMap<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
                try (Scanner scanner = new Scanner(file.getFile())) {
                    while (scanner.hasNextLine()) {
                        try (Scanner rowScanner = new Scanner(scanner.nextLine())) {
                            rowScanner.useDelimiter("\t");
                            // Data retrieved from file.
                            final String currency = rowScanner.next();
                            final String amount = rowScanner.next();
                            // Creating balance.
                            getLogger().info("- Adding balance " + amount + " " + currency);
                            BalanceDTO balance = BalanceDTO.builder()
                                    .currency(new CurrencyDTO(currency))
                                    .available(new BigDecimal(amount))
                                    .create();
                            balances.put(new CurrencyDTO(currency), balance);
                        }
                    }
                } catch (FileNotFoundException e) {
                    getLogger().error("{} not found !", file.getFilename());
                } catch (IOException e) {
                    getLogger().error("IOException : " + e);
                }

                // Creating account.
                AccountDTO account = AccountDTO.builder()
                        .id(accountName)
                        .name(accountName)
                        .balances(balances)
                        .create();
                user.getAccounts().put(account.getId(), account);
            }
        });
    }

    /**
     * Set dependencies.
     *
     * @param newStrategy strategy
     */
    public void setDependencies(final GenericCassandreStrategy newStrategy) {
        this.strategy = newStrategy;
    }

    @Override
    public final Optional<UserDTO> getUser() {
        return Optional.of(user);
    }

    /**
     * Returns the list of files to import.
     *
     * @return files to import.
     */
    public List<Resource> getFilesToLoad() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] resources = resolver.getResources("classpath:" + USER_FILE_PREFIX + "*" + USER_FILE_SUFFIX);
            return Arrays.asList(resources);
        } catch (IOException e) {
            getLogger().error("TickerFluxMock encountered an error : " + e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Update balance of trade account.
     *
     * @param currency currency
     * @param amount   amount
     */
    public void addToBalance(final CurrencyDTO currency, final BigDecimal amount) {
        Optional<BalanceDTO> balance = user.getAccounts().get(TRADE_ACCOUNT_ID).getBalance(currency);
        if (balance.isPresent()) {
            final Map<String, AccountDTO> accounts = new LinkedHashMap<>();

            // For each account.
            user.getAccounts().forEach((s, a) -> {
                HashMap<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();

                // For each balance.
                a.getBalances().forEach(b -> {
                    BalanceDTO newBalance;
                    if (a.getId().equals(TRADE_ACCOUNT_ID) && b.getCurrency().equals(currency)) {
                        // If we are on the account to update, we calculate the new value.
                        newBalance = BalanceDTO.builder()
                                .currency(b.getCurrency())
                                .available(b.getAvailable().add(amount))
                                .create();
                    } else {
                        // Else we keep the same value.
                        newBalance = BalanceDTO.builder()
                                .currency(b.getCurrency())
                                .available(b.getAvailable())
                                .create();
                    }
                    balances.put(newBalance.getCurrency(), newBalance);
                });

                // Creating account
                AccountDTO account = AccountDTO.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .balances(balances)
                        .create();
                accounts.put(account.getId(), account);
            });
            // Change the user value and the account in the strategy.
            strategy.getAccounts().clear();
            strategy.getAccounts().putAll(accounts);
            this.user = UserDTO.builder().setId(USER_ID).setAccounts(accounts).create();
        }
    }

}
