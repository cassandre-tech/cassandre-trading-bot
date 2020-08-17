package tech.cassandre.trading.bot.dto.position;

import tech.cassandre.trading.bot.util.dto.CurrencyAmountDTO;

/**
 * Position gain for {@link PositionDTO}.
 */
@SuppressWarnings("unused")
public class PositionGainDTO {

    /** Gain made with this position (percentage). */
    private final double percentage;

    /** Gain made with this position (amount). */
    private final CurrencyAmountDTO amount;

    /** Fees of the position. */
    private final CurrencyAmountDTO fees;

    /**
     * Constructor (for no gain).
     */
    public PositionGainDTO() {
        percentage = 0;
        amount = new CurrencyAmountDTO();
        fees = new CurrencyAmountDTO();
    }

    /**
     * Constructor.
     *
     * @param newPercentage gain (percentage)
     * @param newAmount     gain (amount)
     * @param newFees       fees
     */
    public PositionGainDTO(final double newPercentage, final CurrencyAmountDTO newAmount, final CurrencyAmountDTO newFees) {
        this.amount = newAmount;
        this.percentage = newPercentage;
        this.fees = newFees;
    }

    /**
     * Getter percentage.
     *
     * @return percentage
     */
    public final double getPercentage() {
        return percentage;
    }

    /**
     * Getter amount.
     *
     * @return amount
     */
    public final CurrencyAmountDTO getAmount() {
        return amount;
    }

    /**
     * Getter fees.
     *
     * @return fees
     */
    public final CurrencyAmountDTO getFees() {
        return fees;
    }

}
