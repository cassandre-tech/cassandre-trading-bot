package tech.cassandre.trading.bot.util.math;

import java.math.BigDecimal;

/**
 * Math constants.
 */
public final class MathConstants {

    /** 100 (usually used for percentage). */
    public static final BigDecimal ONE_HUNDRED_BIG_DECIMAL = new BigDecimal("100");

    /** Big integer scale (used for divisions). */
    public static final int BIGINTEGER_SCALE = 8;

    /**
     * Private constructor - Utility class.
     */
    private MathConstants() {
    }

}
