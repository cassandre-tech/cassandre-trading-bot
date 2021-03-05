package tech.cassandre.trading.bot.dto.position;

/**
 * Position type for {@link PositionDTO}.
 */
public enum PositionTypeDTO {

    /**
     * Long position is nothing but buying share.
     * If you are bullish (means you think that price of X share will rise) at that time you buy some amount of Share is called taking Long Position in share.
     */
    LONG,

    /**
     * Short position is nothing but selling share.
     * If you are bearish (means you think that price of xyz share are going to fall) at that time you sell some amount of share is called taking Short Position in share.
     */
    SHORT

}
