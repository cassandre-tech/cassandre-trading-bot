package tech.cassandre.trading.bot.dto.trade;

/**
 * Order status for {@link OrderDTO}.
 */
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

    /** Order has been replace by another order on order book at exchange. */
    REPLACED,

    /** Order has been triggered at stop price. */
    STOPPED,

    /** Order has been rejected by exchange and not place on order book. */
    REJECTED,

    /** Order has expired it's time to live or trading session and been removed from order book. */
    EXPIRED,

    /** The exchange returned a state which is not in the exchange's API documentation. The state of the order cannot be confirmed. */
    UNKNOWN;

    /**
     * Returns true for final.
     *
     * @return Returns true for final
     */
    public final boolean isFinal() {
        switch (this) {
            case FILLED:
            case PARTIALLY_CANCELED: // Cancelled, partially-executed order is final status.
            case CANCELED:
            case REPLACED:
            case STOPPED:
            case REJECTED:
            case EXPIRED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns true when open.
     *
     * @return Returns true when open
     */
    public final boolean isOpen() {
        switch (this) {
            case PENDING_NEW:
            case NEW:
            case PARTIALLY_FILLED:
                return true;
            default:
                return false;
        }
    }

}
