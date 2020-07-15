package tech.cassandre.trading.bot.dto.position;

/**
 * Position creation result.
 */
public class PositionCreationResultDTO {

    /** Position ID (filled if order creation is successful). */
    private final Long positionId;

    /** Order ID (filled if order creation is successful). */
    private final String orderId;

    /** Error message (filled if order creation failed). */
    private final String errorMessage;

    /** Exception (filled if order creation failed). */
    private final Exception exception;

    /** Indicates if the transaction was successful or not. */
    private final boolean successful;

    /**
     * Constructor for successful position creation.
     *
     * @param newPositionId position id.
     * @param newOrderId order id.
     */
    public PositionCreationResultDTO(final long newPositionId, final String newOrderId) {
        successful = true;
        this.positionId = newPositionId;
        this.orderId = newOrderId;
        this.errorMessage = null;
        this.exception = null;
    }

    /**
     * Constructor for unsuccessful position creation.
     *
     * @param newErrorMessage error message
     * @param newException exception
     */
    public PositionCreationResultDTO(final String newErrorMessage, final Exception newException) {
        successful = false;
        this.positionId = null;
        this.orderId = null;
        this.errorMessage = newErrorMessage;
        this.exception = newException;
    }

    /**
     * Getter positionId.
     *
     * @return positionId
     */
    public final Long getPositionId() {
        return positionId;
    }

    /**
     * Getter orderId.
     *
     * @return orderId
     */
    public final String getOrderId() {
        return orderId;
    }

    /**
     * Getter errorMessage.
     *
     * @return errorMessage
     */
    public final String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Getter exception.
     *
     * @return exception
     */
    public final Exception getException() {
        return exception;
    }

    /**
     * Returns successful.

     * @return true if order creation was successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public final String toString() {
        return "PositionCreationResultDTO{"
                + " positionId='" + positionId + '\''
                + ", orderId='" + orderId + '\''
                + ", errorMessage='" + errorMessage + '\''
                + ", exception=" + exception
                + '}';
    }

}
