package tech.cassandre.trading.bot.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Database parameters from application.properties.
 */
@Validated
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "cassandre.trading.bot.api")
public class APIParameters {

    /** API key. */
    @NotEmpty(message = "API key required")
    private String key;

    /** API secret. */
    @NotEmpty(message = "API secret required")
    private String secret;

}
