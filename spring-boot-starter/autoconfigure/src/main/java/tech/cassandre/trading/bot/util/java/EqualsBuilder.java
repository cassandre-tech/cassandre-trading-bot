package tech.cassandre.trading.bot.util.java;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Equals builder dealing correctly with BigDecimal and ZonedDateTime.
 */
public class EqualsBuilder {

    /** Equals indicator. By default, it's equals. */
    private boolean equals = true;

    /**
     * Add a test with two objects (dealing with BigDecimal and ZonedDateTime).
     *
     * @param object1 object 1
     * @param object2 object 2
     * @return builder
     */
    public EqualsBuilder append(final Object object1, final Object object2) {
        // If object1 is null.
        // --> equals if object2 is also null.
        // --> not equals if object2 is not null.
        if (equals && object1 == null) {
            equals = (object2 == null);
        }
        if (equals && object2 == null) {
            equals = (object1 == null);
        }
        if (equals && object1 != null) {
            if (object1 instanceof BigDecimal) {
                // Big Decimal.
                equals = ((BigDecimal) object1).compareTo((BigDecimal) object2) == 0;
            } else if (object1 instanceof ZonedDateTime) {
                // ZonedDateTime.
                equals = ((ZonedDateTime) object1).isEqual((ZonedDateTime) object2);
            } else {
                equals = object1.equals(object2);
            }
        }
        return this;
    }

    /**
     * Returns the result of all equals.
     *
     * @return true;
     */
    public boolean isEquals() {
        return equals;
    }

}
