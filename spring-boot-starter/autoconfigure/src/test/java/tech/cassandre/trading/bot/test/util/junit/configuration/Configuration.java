package tech.cassandre.trading.bot.test.util.junit.configuration;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration (Used to override applications.properties parameters during tests).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(ConfigurationExtension.class)
public @interface Configuration {

    /** Property values. */
    Property[] value();

}
