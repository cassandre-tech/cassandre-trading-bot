package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Account flux - push {@link AccountDTO}.
 * Two methods override from super class:
 * - getNewValues(): calling user service to retrieve accounts values from exchange.
 * - saveValues(): not implemented as we don't store accounts data in database.
 * To get a deep understanding of how it works, read the documentation of {@link BaseFlux}.
 */
@RequiredArgsConstructor
public class AccountFlux extends BaseFlux<AccountDTO> {

    /** User service. */
    private final UserService userService;

    /** Previous values. */
    private final Map<String, AccountDTO> previousValues = new ConcurrentHashMap<>();

    @Override
    protected final Set<AccountDTO> getNewValues() {
        return userService.getAccounts()
                .values()
                .stream()
                .peek(accountDTO -> logger.debug("Retrieved account from exchange: {}", accountDTO))
                // We consider that we have a new value to send to strategies in two cases:
                // - New value (AccountDTO) is already in previous values but balances are different.
                // - New value (AccountDTO) doesn't exist at all in previous values.
                .filter(accountDTO -> !Objects.equals(accountDTO, previousValues.get(accountDTO.getAccountId())))
                .peek(accountDTO -> logger.debug("Updated account: {}", accountDTO))
                // We add or replace the new value in the previous values.
                .peek(accountDTO -> previousValues.put(accountDTO.getAccountId(), accountDTO))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
