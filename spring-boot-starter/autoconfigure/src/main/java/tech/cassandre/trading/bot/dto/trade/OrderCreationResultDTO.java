package tech.cassandre.trading.bot.dto.trade;

import lombok.Getter;

/**
 * Order creation result for {@link OrderDTO}.
 * If successful (isSuccessful() == true), you can get the order with getOrder().
 * if not successful (isSuccessful() == false), you can get:
 * - The error message with getErrorMessage().
 * - The exception causing the error with getException().
 */
@Getter
public final class OrderCreationResultDTO {

    /** Order (filled if order creation is successful). */
    private OrderDTO order;

    /** Error message (filled if order creation failed). */
    private String errorMessage;

    /** Exception (filled if order creation failed). */
    private Exception exception;

    /**
     * Constructor for successful order creation.
     *
     * @param newOrder order
     */
    public OrderCreationResultDTO(final OrderDTO newOrder) {
        this.order = newOrder;
    }

    /**
     * Constructor for unsuccessful order creation.
     *
     * @param newErrorMessage error message
     * @param newException    exception
     */
    public OrderCreationResultDTO(final String newErrorMessage, final Exception newException) {
        this.errorMessage = newErrorMessage;
        this.exception = newException;
    }

    /**
     * Getter successful.
     *
     * @return successful
     */
    public boolean isSuccessful() {
        return order != null;
    }

    /**
     * Returns order id.
     *
     * @return id
     */
    public String getOrderId() {
        if (getOrder() != null) {
            return getOrder().getOrderId();
        } else {
            return "No order";
        }
    }

    @Override
    public String toString() {
        if (isSuccessful()) {
            return "OrderCreationResultDTO{"
                    + " order='" + order + '\''
                    + '}';
        } else {
            return "OrderCreationResultDTO{"
                    + " errorMessage='" + errorMessage + '\''
                    + ", exception=" + exception
                    + '}';
        }
    }

}
