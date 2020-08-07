package tech.cassandre.trading.bot.dto.position;

/**
 * Position status for {@link PositionDTO}.
 */
@SuppressWarnings("unused")
public enum PositionStatusDTO {

    /**
     * Opening - a position has been created, a buy order has been made but not yet completed.
     */
    OPENING,

    /**
     * Opening failure - a position has been created, but the buy order did not succeed.
     */
    OPENING_FAILURE,

    /**
     * Opened - the buy order has been accepted.
     */
    OPENED,

    /**
     * Closing - a sell order has been made but not yet completed.
     */
    CLOSING,

    /**
     * Closing failure - the sell order did not succeed.
     */
    CLOSING_FAILURE,

    /**
     * Closed - the sell order has been accepted.
     */
    CLOSED

}
