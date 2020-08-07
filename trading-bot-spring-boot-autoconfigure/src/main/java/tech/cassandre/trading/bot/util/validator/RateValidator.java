package tech.cassandre.trading.bot.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeParseException;

/**
 * Rate validator.
 */
public class RateValidator implements ConstraintValidator<Rate, String> {

    @Override
    public final boolean isValid(final String value, final ConstraintValidatorContext constraintValidatorContext) {
        if (isNumeric(value)) {
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

    /**
     * Returns true is a string is a number.
     *
     * @param string string to test
     * @return true if numeric
     */
    private static boolean isNumeric(final String string) {
        // null or empty
        if (string == null || string.length() == 0) {
            return false;
        }
        for (char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
