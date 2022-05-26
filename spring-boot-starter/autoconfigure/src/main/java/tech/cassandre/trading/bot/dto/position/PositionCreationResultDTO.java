package tech.cassandre.trading.bot.dto.position;

import lombok.Getter;

/**
 * Position creation result for {@link PositionDTO}.
 * If successful (isSuccessful() == true), you can get the position with getPosition().
 * if not successful (isSuccessful() == false), you can get:
 * - The error message with getErrorMessage().
 * - The exception causing the error with getException().
 */
@Getter
public final class PositionCreationResultDTO {

    /** Position (filled if position is successful). */
    private PositionDTO position;

    /** Error message (filled if position creation failed). */
    private String errorMessage;

    /** Exception (filled if position creation failed). */
    private Exception exception;

    /**
     * Constructor for successful position creation.
     *
     * @param newPosition position
     */
    public PositionCreationResultDTO(final PositionDTO newPosition) {
        this.position = newPosition;
    }

    /**
     * Constructor for unsuccessful position creation.
     *
     * @param newErrorMessage error message
     * @param newException    exception
     */
    public PositionCreationResultDTO(final String newErrorMessage, final Exception newException) {
        this.errorMessage = newErrorMessage;
        this.exception = newException;
    }

    /**
     * Getter successful.
     *
     * @return successful
     */
    public boolean isSuccessful() {
        return position != null;
    }

    @Override
    public String toString() {
        if (isSuccessful()) {
            return "PositionCreationResultDTO{"
                    + " position='" + position + '\''
                    + '}';
        } else {
            return "PositionCreationResultDTO{"
                    + " errorMessage='" + errorMessage + '\''
                    + ", exception=" + exception
                    + '}';
        }
    }

}
