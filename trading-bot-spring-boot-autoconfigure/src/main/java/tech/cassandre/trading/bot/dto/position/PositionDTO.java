package tech.cassandre.trading.bot.dto.position;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;

import java.math.RoundingMode;

import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;

/**
 * DTO representing a position.
 * A position is the amount of a security, commodity or currency which is owned by an individual, dealer, institution, or other fiscal entity.
 */
public class PositionDTO {

    /** An identifier that uniquely identifies the position. */
    private final long id;

    /** The position status. */
    private PositionStatusDTO status = OPENING;

    /** Position riles. */
    private final PositionRulesDTO rules;

    /** The order id that opened the position. */
    private final String openOrderId;

    /** The trade that opened the position. */
    private TradeDTO openTrade;

    /** The order id that closed the position. */
    private String closeOrderId;

    /** The trade that closed the position. */
    private TradeDTO closeTrade;

    /** Percentage. */
    private static final int ONE_HUNDRED = 100;

    /** Big integer scale. */
    private static final int BIGINTEGER_SCALE = 4;

    /**
     * Constructor.
     *
     * @param newId          position id
     * @param newOpenOrderId open order id
     * @param newRules       position rules
     */
    public PositionDTO(final long newId, final String newOpenOrderId, final PositionRulesDTO newRules) {
        this.id = newId;
        this.openOrderId = newOpenOrderId;
        this.rules = newRules;
    }

    /**
     * Setter closeOrderId.
     *
     * @param newCloseOrderId the closeOrderId to set
     */
    public final void setCloseOrderId(final String newCloseOrderId) {
        // This method should only be called when in status OPENED.
        if (status != OPENED) {
            throw new RuntimeException("Impossible to set close order id for position " + id);
        }
        status = CLOSING;
        closeOrderId = newCloseOrderId;
    }

    /**
     * Method called by on every trade update.
     *
     * @param trade trade
     */
    public void tradeUpdate(final TradeDTO trade) {
        // If status is OPENING and the trade for the open order arrives ==> status = OPENED.
        if (trade.getOrderId().equals(openOrderId) && status == OPENING) {
            openTrade = trade;
            status = OPENED;
        }
        // If status is CLOSING and the trade for the close order arrives ==> status = CLOSED.
        if (trade.getOrderId().equals(closeOrderId) && status == CLOSING) {
            closeTrade = trade;
            status = CLOSED;
        }
    }

    /**
     * Returns true if the position should be closed.
     *
     * @param ticker ticker
     * @return true if the rules says the position should be closed.
     */
    public boolean shouldBeClosed(final TickerDTO ticker) {
        // The status must be OPENED to be closed.
        // The currency pair of the ticker must be the same than the currency pair of the open trade.
        if (status != OPENED || !ticker.getCurrencyPair().equals(openTrade.getCurrencyPair())) {
            return false;
        } else {
            // How gain calculation works ?
            //  - Bought 10 ETH with a price of 5 -> Amount of 50.
            //  - Sold 10 ETH with a price of 6 -> Amount of 60.
            //  Gain = (6-5)/5 = 20%.
            float gain = (ticker.getAsk().subtract(openTrade.getPrice()))
                    .divide(openTrade.getPrice(), BIGINTEGER_SCALE, RoundingMode.FLOOR)
                    .floatValue() * ONE_HUNDRED;

            // Check with max gain and max lost rules.
            return rules.isStopGainPercentageSet() && gain >= rules.getStopGainPercentage()
                    || rules.isStopLossPercentageSet() && gain <= -rules.getStopLossPercentage();
        }
    }

    /**
     * Getter id.
     *
     * @return id
     */
    public final long getId() {
        return id;
    }

    /**
     * Getter status.
     *
     * @return status
     */
    public final PositionStatusDTO getStatus() {
        return status;
    }

    /**
     * Getter openTrade.
     *
     * @return openTrade
     */
    public final TradeDTO getOpenTrade() {
        return openTrade;
    }

    /**
     * Getter closeTrade.
     *
     * @return closeTrade
     */
    public final TradeDTO getCloseTrade() {
        return closeTrade;
    }

}
