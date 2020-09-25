package tech.cassandre.trading.bot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Position (used to save data between restarts).
 */
@Entity
@Table(name = "POSITIONS")
public class Position {

    /** An identifier that uniquely identifies the position. */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** Position rules - stop gain percentage. */
    @Column(name = "RULES_STOP_GAIN_PERCENTAGE")
    private Float stopGainPercentageRule;

    /** Position rules - stop loss percentage. */
    @Column(name = "RULES_STOP_LOSS_PERCENTAGE")
    private Float stopLossPercentageRule;

    /** The order id that opened the position. */
    @Column(name = "OPEN_ORDER_ID")
    private String openOrderId;

    /** The order id that closed the position. */
    @Column(name = "CLOSE_ORDER_ID")
    private String closeOrderId;

    /**
     * Getter id.
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter id.
     *
     * @param newId the id to set
     */
    public void setId(final long newId) {
        id = newId;
    }

    /**
     * Getter rulesStopGainPercentage.
     *
     * @return rulesStopGainPercentage
     */
    public Float getStopGainPercentageRule() {
        return stopGainPercentageRule;
    }

    /**
     * Setter rulesStopGainPercentage.
     *
     * @param newRulesStopGainPercentage the rulesStopGainPercentage to set
     */
    public void setStopGainPercentageRule(final Float newRulesStopGainPercentage) {
        stopGainPercentageRule = newRulesStopGainPercentage;
    }

    /**
     * Getter rulesStopLossPercentage.
     *
     * @return rulesStopLossPercentage
     */
    public Float getStopLossPercentageRule() {
        return stopLossPercentageRule;
    }

    /**
     * Setter rulesStopLossPercentage.
     *
     * @param newRulesStopLossPercentage the rulesStopLossPercentage to set
     */
    public void setStopLossPercentageRule(final Float newRulesStopLossPercentage) {
        stopLossPercentageRule = newRulesStopLossPercentage;
    }

    /**
     * Getter openOrderId.
     *
     * @return openOrderId
     */
    public String getOpenOrderId() {
        return openOrderId;
    }

    /**
     * Setter openOrderId.
     *
     * @param newOpenOrderId the openOrderId to set
     */
    public void setOpenOrderId(final String newOpenOrderId) {
        openOrderId = newOpenOrderId;
    }

    /**
     * Getter closeOrderId.
     *
     * @return closeOrderId
     */
    public String getCloseOrderId() {
        return closeOrderId;
    }

    /**
     * Setter closeOrderId.
     *
     * @param newCloseOrderId the closeOrderId to set
     */
    public void setCloseOrderId(final String newCloseOrderId) {
        closeOrderId = newCloseOrderId;
    }

}
