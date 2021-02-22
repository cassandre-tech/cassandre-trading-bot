package tech.cassandre.trading.bot.strategy;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Cassandre strategy annotation.
 */
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface CassandreStrategy {

    /**
     * Strategy id.
     *
     * @return strategy id
     */
    String strategyId() default "1";

    /**
     * Strategy name.
     *
     * @return strategy name
     */
    String strategyName() default "My strategy";

}
