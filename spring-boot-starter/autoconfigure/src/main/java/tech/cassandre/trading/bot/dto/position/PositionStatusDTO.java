package tech.cassandre.trading.bot.dto.position;

/**
 * Position status for {@link PositionDTO}.
 */
public enum PositionStatusDTO {

    /**
     * Opening - A position has been created, a buy order has been made but not yet completed.
     */
    OPENING,

    /**
     * Opening failure - A position has been created, but the buy order did not succeed.
     */
    OPENING_FAILURE,

    /**
     * Opened - The buy order has been accepted.
     */
    OPENED,

    /**
     * Closing - A sell order has been made but not yet completed.
     */
    CLOSING,

    /**
     * Closing failure - The sell order did not succeed.
     */
    CLOSING_FAILURE,

    /**
     * Closed - The sell order has been accepted.
     */
    CLOSED

}
