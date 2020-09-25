package tech.cassandre.trading.bot.test.util.junit.configuration;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Property (from application.properties).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Configuration.class)
@ExtendWith(ConfigurationExtension.class)
public @interface Property {

    /** Key. */
    String key();

    /** Value. */
    String value() default "";

}
