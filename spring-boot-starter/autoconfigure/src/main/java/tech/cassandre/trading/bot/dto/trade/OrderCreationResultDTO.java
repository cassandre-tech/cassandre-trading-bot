package tech.cassandre.trading.bot.dto.trade;

import java.time.ZonedDateTime;

import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;

/**
 * Order creation result for {@link OrderDTO}.
 */
public final class OrderCreationResultDTO {

    /** Order ID (filled if order creation is successful). */
    private final String orderId;

    /** Order (filled if order creation is successful). */
    private OrderDTO order;

    /** Error message (filled if order creation failed). */
    private final String errorMessage;

    /** Exception (filled if order creation failed). */
    private final Exception exception;

    /** Indicates if the position creation was successful or not. */
    private final boolean successful;

    /**
     * Constructor for successful order creation.
     *
     * @param newOrder order
     */
    public OrderCreationResultDTO(final OrderDTO newOrder) {
        successful = true;
        this.orderId = newOrder.getId();
        this.order = newOrder;
        this.errorMessage = null;
        this.exception = null;
    }

    /**
     * Constructor for successful order creation.
     *
     * @param newOrderId order id
     * @Deprecated Use OrderCreationResultDTO()
     */
    @Deprecated(since = "4.0.0")
    public OrderCreationResultDTO(final String newOrderId) {
        successful = true;
        this.orderId = newOrderId;
        this.order = OrderDTO.builder()
                .id(newOrderId)
                .timestamp(ZonedDateTime.now())
                .status(PENDING_NEW)
                .create();
        this.errorMessage = null;
        this.exception = null;
    }

    /**
     * Constructor for unsuccessful order creation.
     *
     * @param newErrorMessage error message
     * @param newException    exception
     */
    public OrderCreationResultDTO(final String newErrorMessage, final Exception newException) {
        successful = false;
        this.orderId = null;
        this.errorMessage = newErrorMessage;
        this.exception = newException;
    }

    /**
     * Getter for orderId.
     *
     * @return orderId
     * @Deprecated Use getOrder().getId() instead
     */
    @Deprecated(since = "4.0.0")
    public String getOrderId() {
        return orderId;
    }

    /**
     * Getter order.
     *
     * @return order
     */
    public OrderDTO getOrder() {
        return order;
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
     * Getter for successful.
     *
     * @return successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        if (successful) {
            return "OrderCreationResultDTO{"
                    + " orderId='" + orderId + '\''
                    + '}';
        } else {
            return "OrderCreationResultDTO{"
                    + " errorMessage='" + errorMessage + '\''
                    + ", exception=" + exception
                    + '}';
        }

    }

}
