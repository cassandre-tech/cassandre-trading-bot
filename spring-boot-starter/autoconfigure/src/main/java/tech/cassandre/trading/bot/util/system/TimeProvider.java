package tech.cassandre.trading.bot.util.system;

import java.util.Date;

public abstract class TimeProvider {

    public static Date now() {
        return new Date();
    }
}
