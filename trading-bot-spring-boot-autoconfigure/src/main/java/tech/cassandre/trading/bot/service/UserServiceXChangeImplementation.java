package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.io.IOException;
import java.util.Optional;

/**
 * Account service - XChange implementation.
 */
public class UserServiceXChangeImplementation extends BaseService implements UserService {

	/** XChange service. */
	private final org.knowm.xchange.service.account.AccountService xChangeAccountService;

	/**
	 * Constructor.
	 *
	 * @param newXChangeAccountService xchange account service
	 */
	public UserServiceXChangeImplementation(final org.knowm.xchange.service.account.AccountService newXChangeAccountService) {
		this.xChangeAccountService = newXChangeAccountService;
	}

	@Override
	public final Optional<UserDTO> getUser() {
		try {
			getLogger().debug("UserServiceXChangeImplementation - Retrieving account information");
			final UserDTO user = getMapper().mapToUserDTO(xChangeAccountService.getAccountInfo());
			getLogger().debug("UserServiceXChangeImplementation - Account information retrieved " + user);
			return Optional.ofNullable(user);
		} catch (IOException e) {
			getLogger().error("Error retrieving account information : {}", e.getMessage());
			return Optional.empty();
		}
	}

}
