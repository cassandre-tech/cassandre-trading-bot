package tech.cassandre.trading.bot.configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * GraphQL API authentication manager.
 */
public class GraphQLAPIKeyAuthenticationManager implements AuthenticationManager {

    /** API key. */
    private final String key;

    /**
     * Constructor.
     *
     * @param newKey API key
     */
    public GraphQLAPIKeyAuthenticationManager(final String newKey) {
        this.key = newKey;
    }

    @Override
    public final Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        if (key != null && !key.equals(principal)) {
            throw new BadCredentialsException("The API key was not found or not the expected valueUnknownExchangeTest.checkErrorMessages");
        }
        authentication.setAuthenticated(true);
        return authentication;
    }

}
