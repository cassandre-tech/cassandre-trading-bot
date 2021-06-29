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

    /** Indicates if the position creation was successful or not. */
    boolean successful;

    /**
     * Constructor for successful position creation.
     *
     * @param newPosition position
     */
    public PositionCreationResultDTO(final PositionDTO newPosition) {
        successful = true;
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
        successful = false;
        this.position = null;
        this.errorMessage = newErrorMessage;
        this.exception = newException;
    }

}
