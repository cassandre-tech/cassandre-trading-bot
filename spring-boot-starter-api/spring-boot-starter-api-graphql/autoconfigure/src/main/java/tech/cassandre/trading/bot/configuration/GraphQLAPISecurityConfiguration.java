package tech.cassandre.trading.bot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * GraphQL API security configuration.
 */
@Configuration
@EnableWebSecurity
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 1)
public class GraphQLAPISecurityConfiguration extends WebSecurityConfigurerAdapter {

    /** API Key. */
    @Value("${cassandre.trading.bot.api.graphql.key:}")
    private String key;

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        if (key == null || key.isBlank()) {
            // if key is not set, no security at all. Everything is accessible.
            http.antMatcher("/graphql/**")
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(STATELESS)
                    .and()
                    .authorizeRequests().anyRequest().permitAll();
        } else {
            // If a key is set, we ask for key on every request (X-API-Key header).
            GraphQLAPIKeyAuthenticationFilter filter = new GraphQLAPIKeyAuthenticationFilter();
            filter.setAuthenticationManager(new GraphQLAPIKeyAuthenticationManager(key));
            http.antMatcher("/graphql/**")
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(STATELESS)
                    .and()
                    .addFilter(filter)
                    .authorizeRequests().anyRequest().authenticated();
        }
    }

}
