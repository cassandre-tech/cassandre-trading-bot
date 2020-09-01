package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.base.BaseFlux;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Account flux - push {@link AccountDTO}.
 */
public class AccountFlux extends BaseFlux<AccountDTO> {

    /** User service. */
    private final UserService userService;

    /** Previous values. */
    private Map<String, AccountDTO> previousValues = new LinkedHashMap<>();

    /**
     * Constructor.
     *
     * @param newUserService user service
     */
    public AccountFlux(final UserService newUserService) {
        this.userService = newUserService;
    }

    @Override
    protected final Set<AccountDTO> getNewValues() {
        getLogger().debug("AccountFlux - Retrieving new values");
        Set<AccountDTO> newValues = new LinkedHashSet<>();

        // Calling the service and treating results
        userService.getUser().ifPresent(user -> {
            // For each account, we check if there is something new.
            user.getAccounts().forEach((accountId, account) -> {
                getLogger().debug("AccountFlux - Treating account : {}", accountId);
                if (previousValues.containsKey(accountId)) {
                    // If in the previous values, heck the balances.
                    if (!account.equals(previousValues.get(accountId))) {
                        getLogger().debug("AccountFlux - Account {} has changed : {}", accountId, account);
                        newValues.add(account);
                    }
                } else {
                    // Send if it does not exist.
                    getLogger().debug("AccountFlux - New account : {}", account);
                    newValues.add(account);
                }
            });
            previousValues = user.getAccounts();
        });
        getLogger().debug("AccountFlux - {} account(s) updated", newValues.size());
        return newValues;
    }

}
