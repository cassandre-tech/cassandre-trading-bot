package tech.cassandre.trading.bot.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.cassandre.trading.bot.util.APIParameters;

/**
 * API Configuration.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "tech.cassandre.trading.bot.web.api")
@EnableConfigurationProperties(APIParameters.class)
@ConditionalOnClass({ExchangeAutoConfiguration.class, StrategiesAutoConfiguration.class})
public class APIConfiguration implements WebMvcConfigurer {

    /** API parameters. */
    private final APIParameters apiParameters;

    /**
     * Constructor.
     *
     * @param newAPIParameters api parameters
     */
    public APIConfiguration(final APIParameters newAPIParameters) {
        this.apiParameters = newAPIParameters;
    }

}
