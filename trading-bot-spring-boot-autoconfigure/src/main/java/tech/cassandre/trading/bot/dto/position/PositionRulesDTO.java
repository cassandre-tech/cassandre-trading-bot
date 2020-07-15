package tech.cassandre.trading.bot.dto.position;

/**
 * Position rules used to know if cassandre should close a position.
 */
public class PositionRulesDTO {

    /** Stop gain percentage has been set. */
    private final boolean stopGainPercentageSet;

    /** Stop gain percentage. */
    private final float stopGainPercentage;

    /** Stop loss percentage has been set. */
    private final boolean stopLossPercentageSet;

    /** Stop loss percentage. */
    private final float stopLossPercentage;

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
     * Getter stopGainPercentageSet.
     *
     * @return stopGainPercentageSet
     */
    public final boolean isStopGainPercentageSet() {
        return stopGainPercentageSet;
    }

    /**
     * Getter stopGainPercentage.
     *
     * @return stopGainPercentage
     */
    public final float getStopGainPercentage() {
        return stopGainPercentage;
    }

    /**
     * Getter stopLossPercentageSet.
     *
     * @return stopLossPercentageSet
     */
    public final boolean isStopLossPercentageSet() {
        return stopLossPercentageSet;
    }

    /**
     * Getter stopLossPercentage.
     *
     * @return stopLossPercentage
     */
    public final float getStopLossPercentage() {
        return stopLossPercentage;
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** Stop gain percentage has been set. */
        private boolean stopGainPercentageSet = false;

        /** Stop gain percentage. */
        private float stopGainPercentage;

        /** Stop loss percentage has been set. */
        private boolean stopLossPercentageSet = false;

        /** Stop loss percentage. */
        private float stopLossPercentage;

        /**
         * Stop gain percentage.
         *
         * @param newStopGainPercentage stop gain percentage
         * @return builder
         */
        public Builder stopGainPercentage(final float newStopGainPercentage) {
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
        public Builder stopLossPercentage(final float newStopLossPercentage) {
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
