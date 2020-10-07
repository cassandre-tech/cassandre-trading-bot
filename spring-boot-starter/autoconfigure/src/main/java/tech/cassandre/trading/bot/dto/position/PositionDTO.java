package tech.cassandre.trading.bot.dto.position;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.util.exception.PositionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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

    /** Position version (used for database backup). */
    private final AtomicLong version = new AtomicLong(0L);

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

    /** Last calculated gain from the last ticker received. */
    private GainDTO lastCalculatedGain;

    /** Lowest price for this position. */
    private BigDecimal lowestPrice;

    /** Highest price for this position. */
    private BigDecimal highestPrice;

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
    public PositionDTO(final long newId,
                       final String newOpenOrderId,
                       final PositionRulesDTO newRules) {
        this.id = newId;
        this.openOrderId = newOpenOrderId;
        this.rules = newRules;
        version.incrementAndGet();
    }

    /**
     * Constructor (only used when restoring from database).
     *
     * @param newId           position id
     * @param newStatus       status
     * @param newRules        position rules
     * @param newOpenOrderId  open order id
     * @param newOpenTrade    open trade
     * @param newCloseOrderId close order id
     * @param newCloseTrade   close trade
     * @param newLowestPrice  lowest price
     * @param newHighestPrice highest price
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public PositionDTO(final long newId,
                       final PositionStatusDTO newStatus,
                       final PositionRulesDTO newRules,
                       final String newOpenOrderId,
                       final TradeDTO newOpenTrade,
                       final String newCloseOrderId,
                       final TradeDTO newCloseTrade,
                       final BigDecimal newLowestPrice,
                       final BigDecimal newHighestPrice) {
        this.id = newId;
        this.status = newStatus;
        this.rules = newRules;
        this.openOrderId = newOpenOrderId;
        this.openTrade = newOpenTrade;
        this.closeOrderId = newCloseOrderId;
        this.closeTrade = newCloseTrade;
        this.lowestPrice = newLowestPrice;
        this.highestPrice = newHighestPrice;
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
        version.incrementAndGet();
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
            version.incrementAndGet();
        }
        // If status is CLOSING and the trade for the close order arrives ==> status = CLOSED.
        if (trade.getOrderId().equals(closeOrderId) && status == CLOSING) {
            closeTrade = trade;
            status = CLOSED;
            version.incrementAndGet();
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
            final Optional<GainDTO> gain = calculateGainFromPrice(ticker.getLast());
            if (gain.isPresent()) {
                // We save the last calculated gain.
                this.lastCalculatedGain = gain.get();

                if (rules.isStopGainPercentageSet() && gain.get().getPercentage() >= rules.getStopGainPercentage()
                        || rules.isStopLossPercentageSet() && gain.get().getPercentage() <= -rules.getStopLossPercentage()) {
                    version.incrementAndGet();
                    // If the rules tells we should sell.
                    return true;
                } else {
                    // We check if this gain is at a new highest.
                    if (highestPrice == null) {
                        highestPrice = ticker.getLast();
                        version.incrementAndGet();
                    } else {
                        final Optional<GainDTO> highestGain = calculateGainFromPrice(highestPrice);
                        if (highestGain.isPresent() && highestGain.get().getPercentage() <= gain.get().getPercentage()) {
                            highestPrice = ticker.getLast();
                            version.incrementAndGet();
                        }
                    }
                    // We check if this gain is at a new lowest.
                    if (lowestPrice == null) {
                        lowestPrice = ticker.getLast();
                        version.incrementAndGet();
                    } else {
                        final Optional<GainDTO> lowestGain = calculateGainFromPrice(lowestPrice);
                        if (lowestGain.isPresent() && lowestGain.get().getPercentage() >= gain.get().getPercentage()) {
                            lowestPrice = ticker.getLast();
                            version.incrementAndGet();
                        }
                    }
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Calculate the gain from a price.
     *
     * @param price price
     * @return gain
     */
    private Optional<GainDTO> calculateGainFromPrice(final BigDecimal price) {
        if ((status == OPENED || status == CLOSED) && price != null) {
            // How gain calculation works ?
            //  - Bought 10 ETH with a price of 5 -> Amount of 50.
            //  - Sold 10 ETH with a price of 6 -> Amount of 60.
            //  Gain = (6-5)/5 = 20%.
            float percentage = (price.subtract(openTrade.getPrice()))
                    .divide(openTrade.getPrice(), BIGINTEGER_SCALE, RoundingMode.FLOOR)
                    .floatValue() * ONE_HUNDRED;
            BigDecimal amount = ((openTrade.getOriginalAmount().multiply(price))
                    .subtract((openTrade.getOriginalAmount()).multiply(openTrade.getPrice())));

            GainDTO gain = new GainDTO(percentage,
                    new CurrencyAmountDTO(amount, openTrade.getCurrencyPair().getQuoteCurrency()),
                    new CurrencyAmountDTO(BigDecimal.ZERO, openTrade.getCurrencyPair().getQuoteCurrency()));
            return Optional.of(gain);
        } else {
            return Optional.empty();
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
     * Get currency pair.
     *
     * @return currency pair
     */
    public final CurrencyPairDTO getCurrencyPair() {
        if (openTrade != null) {
            return openTrade.getCurrencyPair();
        } else {
            return null;
        }
    }

    /**
     * Getter rules.
     *
     * @return rules
     */
    public final PositionRulesDTO getRules() {
        return rules;
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
     * Getter last calculated gain from the last ticker received.
     *
     * @return lastCalculatedGain
     */
    public final Optional<GainDTO> getLastCalculatedGain() {
        return Optional.ofNullable(lastCalculatedGain);
    }

    /**
     * Getter closeOrderId.
     *
     * @return closeOrderId
     */
    public final String getCloseOrderId() {
        return closeOrderId;
    }

    /**
     * Getter lowestPrice.
     *
     * @return lowestPrice
     */
    public final BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    /**
     * Getter highestPrice.
     *
     * @return highestPrice
     */
    public final BigDecimal getHighestPrice() {
        return highestPrice;
    }

    /**
     * Getter lowestCalculatedGain.
     *
     * @return lowestCalculatedGain
     */
    public final Optional<GainDTO> getLowestCalculatedGain() {
        return calculateGainFromPrice(lowestPrice);
    }

    /**
     * Getter highestCalculatedGain.
     *
     * @return highestCalculatedGain
     */
    public final Optional<GainDTO> getHighestCalculatedGain() {
        return calculateGainFromPrice(highestPrice);
    }

    /**
     * Getter version.
     *
     * @return version
     */
    public final Long getVersion() {
        return version.longValue();
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
        try {
            String value = "Position n°" + id + " (";
            // Rules.
            if (!rules.isStopGainPercentageSet() && !rules.isStopLossPercentageSet()) {
                value += "no rules";
            }
            if (rules.isStopGainPercentageSet() && !rules.isStopLossPercentageSet()) {
                value += rules.getStopGainPercentage() + " % gain rule";
            }
            if (rules.isStopLossPercentageSet() && !rules.isStopGainPercentageSet()) {
                value += rules.getStopLossPercentage() + " % loss rule";
            }
            if (rules.isStopGainPercentageSet() && rules.isStopLossPercentageSet()) {
                value += rules.getStopGainPercentage() + " % gain rule / ";
                value += rules.getStopLossPercentage() + " % loss rule";
            }
            value += ")";
            switch (status) {
                case OPENING:
                    value += " - Opening - Waiting for the trade of order " + getOpenOrderId();
                    break;
                case OPENED:
                    value += " on " + getCurrencyPair() + " - Opened";
                    final Optional<GainDTO> lastGain = getLastCalculatedGain();
                    if (lastGain.isPresent()) {
                        value += " - Last gain calculated " + getLastCalculatedGain().get().getPercentage() + " %";
                    }
                    break;
                case CLOSING:
                    value += " on " + getCurrencyPair() + " - Closing - Waiting for the trade of order " + getCloseOrderId();
                    break;
                case CLOSED:
                    final GainDTO gain = getGain();
                    value += " on " + getCurrencyPair() + " - Closed - Gain : " + gain.getPercentage() + " %";
                    break;
                default:
                    value = "Incorrect state for position " + getId();
                    break;
            }
            return value;
        } catch (NullPointerException e) {
            return "Position " + getId();
        }
    }

}
