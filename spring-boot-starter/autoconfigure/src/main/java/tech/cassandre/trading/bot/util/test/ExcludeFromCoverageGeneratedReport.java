package tech.cassandre.trading.bot.util.test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation tells Jacoco to not take into account the annotated method.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ExcludeFromCoverageGeneratedReport {

}
