package tech.cassandre.trading.bot.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * GraphQL API authentication manager.
 */
@RequiredArgsConstructor
public class GraphQLAPIKeyAuthenticationManager implements AuthenticationManager {

    /** API key. */
    private final String key;

    @Override
    public final Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        if (key != null && !key.equals(principal)) {
            throw new BadCredentialsException("Incorrect API key");
        }
        authentication.setAuthenticated(true);
        return authentication;
    }

}
