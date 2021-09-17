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
        if (key != null && !key.equals("")) {   // If we have a key set, we make the check.
            if (!key.equals(principal)) {
                throw new BadCredentialsException("The API key was not found or not the expected value.");
            }
        }
        authentication.setAuthenticated(true);
        return authentication;
    }

}
