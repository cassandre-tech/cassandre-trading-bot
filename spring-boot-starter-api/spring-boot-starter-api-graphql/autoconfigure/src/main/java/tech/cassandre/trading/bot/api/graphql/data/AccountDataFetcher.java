package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Account data fetcher.
 */
@DgsComponent
@RequiredArgsConstructor
public class AccountDataFetcher extends BaseDataFetcher {

    /** User service. */
    private final UserService userService;

    /**
     * Returns all the accounts.
     *
     * @return all accounts
     */
    @DgsQuery
    public final List<AccountDTO> accounts() {
        return new ArrayList<>(userService.getAccountsFromCache().values());
    }

    /**
     * Returns the account with the corresponding account id value.
     *
     * @param accountId account id
     * @return account
     */
    @DgsQuery
    public final AccountDTO accountByAccountId(@InputArgument final String accountId) {
        return userService.getAccountsFromCache()
                .values()
                .stream()
                .filter(accountDTO -> Objects.equals(accountDTO.getAccountId(), accountId))
                .findFirst()
                .orElse(null);
    }

}
