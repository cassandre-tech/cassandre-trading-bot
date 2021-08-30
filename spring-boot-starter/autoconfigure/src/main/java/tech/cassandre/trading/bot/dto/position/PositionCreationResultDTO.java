package tech.cassandre.trading.bot.dto.position;

import lombok.Value;

/**
 * Position creation result for {@link PositionDTO}.
 */
@Value
@SuppressWarnings("checkstyle:VisibilityModifier")
public class PositionCreationResultDTO {

    /** Position (filled if position is successful). */
    PositionDTO position;

    /** Error message (filled if position creation failed). */
    String errorMessage;

    /** Exception (filled if position creation failed). */
    Exception exception;

    /**
     * Constructor for successful position creation.
     *
     * @param newPosition position
     */
    public PositionCreationResultDTO(final PositionDTO newPosition) {
        this.position = newPosition;
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
        this.position = null;
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

}
