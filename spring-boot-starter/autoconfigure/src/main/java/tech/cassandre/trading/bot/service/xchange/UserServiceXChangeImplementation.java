package tech.cassandre.trading.bot.service.xchange;

import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.io.IOException;
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
    public final Optional<UserDTO> getUser() {
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            getBucket().asScheduler().consume(1);

            logger.debug("UserService - Retrieving account information");
            final UserDTO user = accountMapper.mapToUserDTO(xChangeAccountService.getAccountInfo());
            logger.debug("UserService - Account information retrieved " + user);
            return Optional.ofNullable(user);
        } catch (IOException e) {
            logger.error("UserService - Error retrieving account information : {}", e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            logger.error("UserService - InterruptedException : {}", e.getMessage());
            return Optional.empty();
        }
    }

}
