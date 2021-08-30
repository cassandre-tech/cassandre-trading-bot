package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * User service - XChange implementation.
 */
public class UserServiceXChangeImplementation extends BaseService implements UserService {

    /** XChange service. */
    private final org.knowm.xchange.service.account.AccountService xChangeAccountService;

    /**
     * Constructor.
     *
     * @param rate                     rate in ms
     * @param newXChangeAccountService xchange account service
     */
    public UserServiceXChangeImplementation(final long rate, final org.knowm.xchange.service.account.AccountService newXChangeAccountService) {
        super(rate);
        this.xChangeAccountService = newXChangeAccountService;
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Optional<UserDTO> getUser() {
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            bucket.asScheduler().consume(1);

            logger.debug("Retrieving account information");
            final UserDTO user = accountMapper.mapToUserDTO(xChangeAccountService.getAccountInfo());
            logger.debug("Account information retrieved " + user);
            return Optional.ofNullable(user);
        } catch (IOException e) {
            logger.error("Error retrieving account information: {}", e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Map<String, AccountDTO> getAccounts() {
        final Optional<UserDTO> user = getUser();
        if (user.isPresent()) {
            return user.get().getAccounts();
        } else {
            return Collections.emptyMap();
        }
    }

}
