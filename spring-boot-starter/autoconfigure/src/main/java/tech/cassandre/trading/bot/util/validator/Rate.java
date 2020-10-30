package tech.cassandre.trading.bot.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validator for rate.
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = RateValidator.class)
@Documented
@SuppressWarnings({"checkstyle:WhitespaceAround"})
public @interface Rate {

    /**
     * Message.
     *
     * @return message
     */
    String message();

    /**
     * Group of rates.
     *
     * @return rates
     */
    Class<?>[] groups() default {};

    /**
     * Payload.
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};

}
