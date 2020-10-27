package tech.cassandre.trading.bot.dto.util;

/**
 * Gain.
 */
public class GainDTO {

    /** Gain made (percentage). */
    private final double percentage;

    /** Gain made (amount). */
    private final CurrencyAmountDTO amount;

    /** Fees. */
    private final CurrencyAmountDTO fees;

    /**
     * Constructor (for no gain).
     */
    public GainDTO() {
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
    public GainDTO(final double newPercentage, final CurrencyAmountDTO newAmount, final CurrencyAmountDTO newFees) {
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
     * Getter netAmount.
     *
     * @return netAmount
     */
    public final CurrencyAmountDTO getNetAmount() {
        return new CurrencyAmountDTO(amount.getValue().subtract(fees.getValue()), amount.getCurrency());
    }

    /**
     * Getter fees.
     *
     * @return fees
     */
    public final CurrencyAmountDTO getFees() {
        return fees;
    }

    @Override
    public final String toString() {
        if (percentage == 0) {
            return "No gain";
        } else {
            return "Gains: " + amount + " (" + percentage + " %) / Fees: " + fees;
        }

    }

}
