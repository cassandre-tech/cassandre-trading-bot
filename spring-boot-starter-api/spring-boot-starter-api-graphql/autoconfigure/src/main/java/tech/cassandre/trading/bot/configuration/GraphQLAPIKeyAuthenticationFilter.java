package tech.cassandre.trading.bot.configuration;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * GraphQL API authentication filter.
 */
public class GraphQLAPIKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    /** API Request header name. */
    private static final String PRINCIPAL_REQUEST_HEADER = "X-API-Key";

    @Override
    protected final Object getPreAuthenticatedPrincipal(final HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(PRINCIPAL_REQUEST_HEADER);
    }

    @Override
    protected final Object getPreAuthenticatedCredentials(final HttpServletRequest httpServletRequest) {
        return "N/A";
    }

}
