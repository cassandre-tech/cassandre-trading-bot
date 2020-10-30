package tech.cassandre.trading.bot.dto.position;

/**
 * Position creation result for {@link PositionDTO}.
 */
public final class PositionCreationResultDTO {

    /** Position ID (filled if order creation is successful). */
    private final Long positionId;

    /** Order ID (filled if order creation is successful). */
    private final String orderId;

    /** Error message (filled if position creation failed). */
    private final String errorMessage;

    /** Exception (filled if position creation failed). */
    private final Exception exception;

    /** Indicates if the position creation was successful or not. */
    private final boolean successful;

    /**
     * Constructor for successful position creation.
     *
     * @param newPositionId position id.
     * @param newOrderId    order id.
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
     * @param newException    exception
     */
    public PositionCreationResultDTO(final String newErrorMessage, final Exception newException) {
        successful = false;
        this.positionId = null;
        this.orderId = null;
        this.errorMessage = newErrorMessage;
        this.exception = newException;
    }

    /**
     * Getter for positionId.
     *
     * @return positionId
     */
    public Long getPositionId() {
        return positionId;
    }

    /**
     * Getter for orderId.
     *
     * @return orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Getter for errorMessage.
     *
     * @return errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Getter for exception.
     *
     * @return exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns successful.
     *
     * @return true if order creation was successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        if (successful) {
            return "PositionCreationResultDTO{"
                    + " positionId=" + positionId
                    + ", orderId='" + orderId + '\''
                    + '}';
        } else {
            return "PositionCreationResultDTO{"
                    + " errorMessage='" + errorMessage + '\''
                    + ", exception=" + exception
                    + '}';
        }
    }

}
