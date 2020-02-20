package tech.cassandre.trading.bot.dto.trade;

import java.util.Optional;

/**
 * Order creation result.
 */
@SuppressWarnings("unused")
public final class OrderCreationResultDTO {

	/** Order ID (filled if order creation is successful). */
	private final String orderId;

	/** Error message (filled if order creation failed). */
	private final String errorMessage;

	/** Exception (filled if order creation failed). */
	private final Exception exception;

	/**
	 * Constructor for successful order creation.
	 *
	 * @param newOrderId order id.
	 */
	public OrderCreationResultDTO(final String newOrderId) {
		this.orderId = newOrderId;
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
		this.orderId = null;
		this.errorMessage = newErrorMessage;
		this.exception = newException;
	}

	/**
	 * Getter for orderId.
	 *
	 * @return orderId
	 */
	public Optional<String> getOrderId() {
		return Optional.ofNullable(orderId);
	}

	/**
	 * Getter for errorMessage.
	 *
	 * @return errorMessage
	 */
	public Optional<String> getErrorMessage() {
		return Optional.ofNullable(errorMessage);
	}

	/**
	 * Getter for exception.
	 *
	 * @return exception
	 */
	public Optional<Exception> getException() {
		return Optional.ofNullable(exception);
	}

	@Override
	public String toString() {
		return "OrderCreationResultDTO{"
				+ " orderId='" + orderId + '\''
				+ ", errorMessage='" + errorMessage + '\''
				+ ", exception=" + exception
				+ '}';
	}

}
