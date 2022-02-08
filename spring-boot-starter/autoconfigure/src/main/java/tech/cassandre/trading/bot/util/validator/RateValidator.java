package tech.cassandre.trading.bot.util.validator;

import org.apache.commons.lang3.math.NumberUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeParseException;

/**
 * Rate validator.
 */
public class RateValidator implements ConstraintValidator<Rate, String> {

    @Override
    public final boolean isValid(final String value, final ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.length() == 0) {
            return false;
        }
        if (NumberUtils.isCreatable(value)) {
            return true;
        } else {
            try {
                java.time.Duration.parse(value);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
    }

}
