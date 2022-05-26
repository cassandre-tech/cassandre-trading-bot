package tech.cassandre.trading.bot.dto.trade;

/**
 * Order status for {@link OrderDTO}.
 */
@SuppressWarnings("unused")
public enum OrderStatusDTO {

    /** Initial order when instantiated. */
    PENDING_NEW,

    /** Initial order when placed on the order book at exchange. */
    NEW,

    /** Partially match against opposite order on order book at exchange. */
    PARTIALLY_FILLED,

    /** Fully match against opposite order on order book at exchange. */
    FILLED,

    /** Waiting to be removed from order book at exchange. */
    PENDING_CANCEL,

    /** Order partially canceled at exchange. */
    PARTIALLY_CANCELED,

    /** Removed from order book at exchange. */
    CANCELED,

    /** Waiting to be replaced by another order on order book at exchange. */
    PENDING_REPLACE,

    /** Order has been replaced by another order on order book at exchange. */
    REPLACED,

    /** Order has been triggered at stop price. */
    STOPPED,

    /** Order has been rejected by exchange and not place on order book. */
    REJECTED,

    /** Order has expired it's time to live or trading session and been removed from order book. */
    EXPIRED,

    /** Order is open and waiting to be filled. */
    OPEN,

    /** Order has been either filled or cancelled. */
    CLOSED,

    /** The exchange returned a state which is not in the exchange's API documentation. The state of the order cannot be confirmed. */
    UNKNOWN;

    /**
     * Returns true when open.
     *
     * @return Returns true when open
     */
    public final boolean isOpen() {
        return switch (this) {
            case PENDING_NEW, NEW, PARTIALLY_FILLED -> true;
            default -> false;
        };
    }

    /**
     * Returns true if the status indicates an error.
     *
     * @return Returns true for final
     */
    public final boolean isInError() {
        return switch (this) {
            case CANCELED, REPLACED, STOPPED, REJECTED, EXPIRED -> true;
            default -> false;
        };
    }

    /**
     * Returns true for final.
     *
     * @return Returns true for final
     */
    public final boolean isFinal() {
        return switch (this) { // Cancelled, partially-executed order is final status.
            case FILLED, PARTIALLY_CANCELED, CANCELED, REPLACED, STOPPED, REJECTED, EXPIRED -> true;
            default -> false;
        };
    }

}
