package tech.cassandre.trading.bot.strategy;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cassandre strategy annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface CassandreStrategy {

    /**
     * Strategy name.
     *
     * @return strategy name
     */
    String name() default "My strategy";

}
