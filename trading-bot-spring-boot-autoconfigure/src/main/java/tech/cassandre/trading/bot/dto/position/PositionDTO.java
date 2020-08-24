package tech.cassandre.trading.bot.dto.position;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyAmountDTO;
import tech.cassandre.trading.bot.util.exception.PositionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

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

    /** Position status. */
    private PositionStatusDTO status = OPENING;

    /** Position rules. */
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
     * Setter for closeOrderId.
     *
     * @param newCloseOrderId the closeOrderId to set
     */
    public final void setCloseOrderId(final String newCloseOrderId) {
        // This method should only be called when in status OPENED.
        if (status != OPENED) {
            throw new PositionException("Impossible to set close order id for position " + id);
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
     * Returns the gain of the position.
     * Of course the position should be closed to have a gain.
     *
     * @return gain
     */
    public GainDTO getGain() {
        if (status == CLOSED) {
            // Gain calculation for currency pair : ETH-BTC
            // The first listed currency of a currency pair is called the base currency.
            // The second currency is called the quote currency.

            // Price is 0.035547 means 1 Ether equals 0.035547 Bitcoin
            // If you buy a currency pair, you buy the base currency and implicitly sell the quoted currency.

            // - Bought 10 ETH with a price of 5 BTC -> costs 50 BTC.
            // - Sold 10 ETH with a price of 6 BTC -> earns 60 BTC.
            // Gain in percentage = (6-5)/5 = 20 %.
            double percentage = (closeTrade.getPrice().subtract(openTrade.getPrice()))
                    .divide(openTrade.getPrice(), BIGINTEGER_SCALE, RoundingMode.FLOOR)
                    .floatValue() * ONE_HUNDRED;
            // Gain in amount = (10*6)-(10*5)= 10 BTC.
            BigDecimal amount = ((closeTrade.getOriginalAmount().multiply(closeTrade.getPrice()))
                    .subtract((openTrade.getOriginalAmount()).multiply(openTrade.getPrice())));
            // Fees : open trade fees + close trade fees.
            BigDecimal fees = openTrade.getFee().getValue().add(closeTrade.getFee().getValue());

            // Return position gain.
            return new GainDTO(percentage,
                    new CurrencyAmountDTO(amount, openTrade.getCurrencyPair().getQuoteCurrency()),
                    new CurrencyAmountDTO(fees, openTrade.getCurrencyPair().getQuoteCurrency()));
        } else {
            // No gain for the moment !
            return new GainDTO();
        }
    }

    /**
     * Getter for id.
     *
     * @return id
     */
    public final long getId() {
        return id;
    }

    /**
     * Getter for status.
     *
     * @return status
     */
    public final PositionStatusDTO getStatus() {
        return status;
    }

    /**
     * Getter for openTrade.
     *
     * @return openTrade
     */
    public final TradeDTO getOpenTrade() {
        return openTrade;
    }

    /**
     * Getter for closeTrade.
     *
     * @return closeTrade
     */
    public final TradeDTO getCloseTrade() {
        return closeTrade;
    }

    /**
     * Getter openOrderId.
     *
     * @return openOrderId
     */
    public final String getOpenOrderId() {
        return openOrderId;
    }

    /**
     * Getter closeOrderId.
     *
     * @return closeOrderId
     */
    public final String getCloseOrderId() {
        return closeOrderId;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositionDTO that = (PositionDTO) o;
        return id == that.id && status == that.status;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public final String toString() {
        return "PositionDTO{"
                + " id=" + id
                + ", status=" + status
                + ", openOrderId='" + openOrderId + '\''
                + ", openTrade=" + openTrade
                + ", closeOrderId='" + closeOrderId + '\''
                + ", closeTrade=" + closeTrade
                + '}';
    }

}
