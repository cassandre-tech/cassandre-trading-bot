package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Account flux - push {@link AccountDTO}.
 */
@RequiredArgsConstructor
public class AccountFlux extends BaseFlux<AccountDTO> {

    /** User service. */
    private final UserService userService;

    /** Previous values. */
    private Map<String, AccountDTO> previousValues = new LinkedHashMap<>();

    @Override
    protected final Set<AccountDTO> getNewValues() {
        logger.debug("AccountFlux - Retrieving accounts information from exchange");
        Set<AccountDTO> newValues = new LinkedHashSet<>();

        // Calling the service and treating results.
        userService.getUser().ifPresent(user -> {
            // For each account, we check if there is something new.
            user.getAccounts().forEach((accountId, account) -> {
                logger.debug("AccountFlux - Treating account: {}", accountId);
                if (previousValues.containsKey(accountId)) {
                    // If in the previous values, check the balances.
                    if (!account.equals(previousValues.get(accountId))) {
                        logger.debug("AccountFlux - Account {} has changed: {}", accountId, account);
                        newValues.add(account);
                    }
                } else {
                    // Send if it does not exist.
                    logger.debug("AccountFlux - New account: {}", account);
                    newValues.add(account);
                }
            });
            previousValues = user.getAccounts();
        });

        return newValues;
    }

    @Override
    protected final Set<AccountDTO> saveValues(final Set<AccountDTO> newValues) {
        // We don't save accounts in database.
        return newValues;
    }

}
