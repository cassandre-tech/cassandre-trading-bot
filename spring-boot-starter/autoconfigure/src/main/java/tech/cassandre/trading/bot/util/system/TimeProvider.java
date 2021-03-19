package tech.cassandre.trading.bot.util.system;

import java.util.Date;

/**
 * Time provider.
 */
public abstract class TimeProvider {

    /**
     * Returns now.
     * @return date.
     */
    public static Date now() {
        return new Date();
    }

}
