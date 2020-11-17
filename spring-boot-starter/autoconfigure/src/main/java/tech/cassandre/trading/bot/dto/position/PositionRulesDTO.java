package tech.cassandre.trading.bot.dto.position;

import java.text.DecimalFormat;

/**
 * Position rules for {@link PositionDTO}.
 * It is used to know if cassandre should close a position.
 * Supported rules :
 * - Stop gain with percentage.
 * - Stop loss with percentage.
 */
public class PositionRulesDTO {

    /** Stop gain percentage has been set. */
    private final boolean stopGainPercentageSet;

    /** Stop gain percentage. */
    private final Float stopGainPercentage;

    /** Stop loss percentage has been set. */
    private final boolean stopLossPercentageSet;

    /** Stop loss percentage. */
    private final Float stopLossPercentage;

    /**
     * Builder constructor.
     *
     * @param builder Builder.
     */
    protected PositionRulesDTO(final Builder builder) {
        this.stopGainPercentageSet = builder.stopGainPercentageSet;
        this.stopGainPercentage = builder.stopGainPercentage;
        this.stopLossPercentageSet = builder.stopLossPercentageSet;
        this.stopLossPercentage = builder.stopLossPercentage;
    }

    /**
     * Returns builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Getter for stopGainPercentageSet.
     *
     * @return stopGainPercentageSet
     */
    public final boolean isStopGainPercentageSet() {
        return stopGainPercentageSet;
    }

    /**
     * Getter for stopGainPercentage.
     *
     * @return stopGainPercentage
     */
    public final Float getStopGainPercentage() {
        return stopGainPercentage;
    }

    /**
     * Getter for stopLossPercentageSet.
     *
     * @return stopLossPercentageSet
     */
    public final boolean isStopLossPercentageSet() {
        return stopLossPercentageSet;
    }

    /**
     * Getter for stopLossPercentage.
     *
     * @return stopLossPercentage
     */
    public final Float getStopLossPercentage() {
        return stopLossPercentage;
    }

    @Override
    public final String toString() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        if (isStopGainPercentageSet() && isStopLossPercentageSet()) {
            return "Stop gain at " + df.format(getStopGainPercentage()) + " % / Stop loss at " + df.format(getStopLossPercentage()) + " %";
        }
        if (isStopGainPercentageSet()) {
            return "Stop gain at " + df.format(getStopGainPercentage()) + " %";
        }
        if (isStopLossPercentageSet()) {
            return "Stop loss at " + df.format(getStopLossPercentage()) + " %";
        }
        // No rules.
        return "No rules";
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** Stop gain percentage has been set. */
        private boolean stopGainPercentageSet = false;

        /** Stop gain percentage. */
        private Float stopGainPercentage;

        /** Stop loss percentage has been set. */
        private boolean stopLossPercentageSet = false;

        /** Stop loss percentage. */
        private Float stopLossPercentage;

        /**
         * Stop gain percentage.
         *
         * @param newStopGainPercentage stop gain percentage
         * @return builder
         */
        public Builder stopGainPercentage(final Float newStopGainPercentage) {
            this.stopGainPercentageSet = true;
            this.stopGainPercentage = newStopGainPercentage;
            return this;
        }

        /**
         * Stop loss percentage.
         *
         * @param newStopLossPercentage stop loss percentage
         * @return builder
         */
        public Builder stopLossPercentage(final Float newStopLossPercentage) {
            this.stopLossPercentageSet = true;
            this.stopLossPercentage = newStopLossPercentage;
            return this;
        }

        /**
         * Creates position rules.
         *
         * @return position rules
         */
        public PositionRulesDTO create() {
            return new PositionRulesDTO(this);
        }

    }

}
