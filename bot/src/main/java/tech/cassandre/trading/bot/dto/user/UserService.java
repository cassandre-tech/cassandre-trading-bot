package tech.cassandre.trading.bot.dto.user;

import java.util.Optional;

/**
 * Service giving information about user, accounts and balances.
 */
public interface UserService {

	/**
	 * Retrieve user information from exchange (user, accounts and balances).
	 *
	 * @return account from exchange
	 */
	Optional<UserDTO> getUser();

}
