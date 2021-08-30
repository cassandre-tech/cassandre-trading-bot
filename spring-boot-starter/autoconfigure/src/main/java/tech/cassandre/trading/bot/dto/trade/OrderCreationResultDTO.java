package tech.cassandre.trading.bot.dto.trade;

import lombok.Getter;

/**
 * Order creation result for {@link OrderDTO}.
 */
@Getter
public final class OrderCreationResultDTO {

    /** Order (filled if order creation is successful). */
    private OrderDTO order;

    /** Error message (filled if order creation failed). */
    private final String errorMessage;

    /** Exception (filled if order creation failed). */
    private final Exception exception;

    /**
     * Constructor for successful order creation.
     *
     * @param newOrder order
     */
    public OrderCreationResultDTO(final OrderDTO newOrder) {
        this.order = newOrder;
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
     * Getter orderId.
     *
     * @return orderId
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
