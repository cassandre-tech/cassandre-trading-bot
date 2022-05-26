package tech.cassandre.trading.bot.dto.position;

import lombok.Getter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.text.DecimalFormat;

/**
 * Position rules for {@link PositionDTO}.
 * This class is used to tell cassandre on which rules it should close a position.
 * Supported rules:
 * - Stop gain in percentage.
 * - Stop loss in percentage.
 */
@Getter
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

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PositionRulesDTO that = (PositionRulesDTO) o;
        return new EqualsBuilder()
                .append(this.stopGainPercentageSet, that.stopGainPercentageSet)
                .append(this.stopLossPercentageSet, that.stopGainPercentageSet)
                .append(this.stopGainPercentage, that.stopGainPercentage)
                .append(this.stopLossPercentage, that.stopLossPercentage)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(stopGainPercentageSet)
                .append(stopLossPercentageSet)
                .append(stopGainPercentage)
                .append(stopLossPercentage)
                .toHashCode();
    }

    @Override
    public final String toString() {
        // Defines the decimal format to display gains.
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);

        // Returns the gain depending on what has been selected.
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
        public PositionRulesDTO build() {
            return new PositionRulesDTO(this);
        }

    }

}
