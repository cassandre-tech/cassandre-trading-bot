package tech.cassandre.trading.bot.util.dry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static java.math.BigDecimal.ZERO;

/**
 * AOP for user service in dry mode.
 */
@Aspect
@Component
@ConditionalOnExpression("${cassandre.trading.bot.exchange.modes.dry:true}")
public class UserServiceDryModeAOP extends BaseService {

    /** User file prefix. */
    private static final String USER_FILE_PREFIX = "user-";

    /** User file suffix. */
    private static final String USER_FILE_SUFFIX = ".*sv";

    /** User ID. */
    private static final String USER_ID = "user";

    /** Account information. */
    private AccountInfo accountInfo;

    /** Application context. */
    private final ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     */
    public UserServiceDryModeAOP(final ApplicationContext newApplicationContext) {
        applicationContext = newApplicationContext;

        Collection<Wallet> wallets = new LinkedHashSet<>();
        getFilesToLoad().forEach(file -> {
            if (file.getFilename() != null) {

                // Account.
                final int accountIndexStart = file.getFilename().indexOf(USER_FILE_PREFIX) + USER_FILE_PREFIX.length();
                final int accountIndexStop = file.getFilename().indexOf("sv") - 2;
                final String account = file.getFilename().substring(accountIndexStart, accountIndexStop);
                logger.info("Adding account '" + account + "'");

                // Balances.
                Collection<Balance> balances = new LinkedHashSet<>();
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
                            balances.add(new Balance(new Currency(currency), new BigDecimal(amount)));
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.error("{} not found !", file.getFilename());
                } catch (IOException e) {
                    logger.error("IOException : " + e);
                }

                // Creating wallet.
                wallets.add(new Wallet(account, account, balances, Collections.emptySet(), ZERO, ZERO));
            }
        });

        // Creates the account info.
        accountInfo = new AccountInfo(USER_ID, wallets);
    }

    @Around("execution(* org.knowm.xchange.service.account.AccountService.getAccountInfo())")
    public final AccountInfo getAccountInfo(final ProceedingJoinPoint pjp) {
        return accountInfo;
    }

    /**
     * Update balance of trade account (method called by trade service).
     *
     * @param strategy strategy
     * @param currency currency
     * @param amount   amount
     */
    public void addToBalance(final GenericCassandreStrategy strategy, final Currency currency, final BigDecimal amount) {
        final Optional<AccountDTO> tradeAccount = strategy.getTradeAccount();
        if (tradeAccount.isEmpty()) {
            logger.error("Trading account not found!");
        } else {

            // We build a new account information from what we saved.
            Collection<Wallet> wallets = new LinkedHashSet<>();

            // We retreat all the wallets we have.
            accountInfo.getWallets()
                    .forEach((name, wallet) -> {
                        HashMap<Currency, Balance> balances = new LinkedHashMap<>();

                        // For each balance, we add it if nothing changed or, if on trading account and we need to change the amount,
                        // Then we do it.
                        wallet.getBalances().forEach((balanceCurrency, balance) -> {
                            if (name.equals(tradeAccount.get().getName()) && balanceCurrency.equals(currency)) {
                                // If we are on the account and currency to update, we calculate the new value.
                                balances.put(balanceCurrency, new Balance(balanceCurrency, balance.getTotal().add(amount)));
                            } else {
                                // Else we keep the same value.
                                balances.put(balanceCurrency, balance);
                            }
                        });

                        // if for the trading account, we don't have a balance for the currency we are trying to add/remove
                        // amounts, then we create a new balance.
                        if (name.equals(tradeAccount.get().getName()) && balances.get(currency) == null) {
                            balances.put(currency, new Balance(currency, amount));
                        }

                        // We add the wallet.
                        wallets.add(new Wallet(name, name, balances.values(), Collections.emptySet(), ZERO, ZERO));
                    });

            // Creates the account info.
            accountInfo = new AccountInfo(USER_ID, wallets);
            // Updates all strategies.
            final UserDTO userDTO = accountMapper.mapToUserDTO(accountInfo);
            applicationContext
                    .getBeansWithAnnotation(CassandreStrategy.class)
                    .values()  // We get the list of all required cp of all strategies.
                    .stream()
                    .map(o -> ((CassandreStrategyInterface) o))
                    .forEach(cassandreStrategyInterface -> cassandreStrategyInterface.initializeAccounts(userDTO.getAccounts()));
        }
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
