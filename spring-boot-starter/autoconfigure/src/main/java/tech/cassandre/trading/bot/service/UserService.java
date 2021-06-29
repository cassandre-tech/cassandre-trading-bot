package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.user.UserDTO;

import java.util.Optional;

/**
 * Service getting information about user, accounts and balances.
 */
public interface UserService {

    /**
     * Retrieve user information from exchange (user, accounts and balances).
     *
     * @return user from exchange
     */
    Optional<UserDTO> getUser();

}
