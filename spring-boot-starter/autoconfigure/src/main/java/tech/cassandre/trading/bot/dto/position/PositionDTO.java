package tech.cassandre.trading.bot.dto.position;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.util.exception.PositionException;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_UP;
import static lombok.AccessLevel.PRIVATE;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING_FAILURE;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING_FAILURE;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.SHORT;
import static tech.cassandre.trading.bot.util.math.MathConstants.BIGINTEGER_SCALE;
import static tech.cassandre.trading.bot.util.math.MathConstants.ONE_HUNDRED_BIG_DECIMAL;

/**
 * DTO representing a position.
 * A position is the amount of a security, commodity or currency which is owned by an individual, dealer, institution, or other fiscal entity.
 */
@Getter
@Builder
@ToString
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings({"checkstyle:VisibilityModifier", "DuplicatedCode"})
public class PositionDTO {

    /** Technical ID. */
    private final long uid;

    /** An identifier that uniquely identifies the position. */
    private final long positionId;

    /** Position type (Long or Short). */
    private final PositionTypeDTO type;

    /** The strategy that created the position. */
    private final StrategyDTO strategy;

    /** Currency pair. */
    private final CurrencyPairDTO currencyPair;

    /** Position amount. */
    private final CurrencyAmountDTO amount;

    /** Position rules. */
    private final PositionRulesDTO rules;

    /** Indicates if the position should be closed automatically by Cassandre. */
    private final boolean autoClose;

    /** Indicates that the position must be closed no matter the rules. */
    private final boolean forceClosing;

    /** The order created to open the position. */
    private OrderDTO openingOrder;

    /** The order created to close the position. */
    private OrderDTO closingOrder;

    /** Price of the lowest gain reached by this position. */
    private CurrencyAmountDTO lowestGainPrice;

    /** Price of the highest gain reached by this position. */
    private CurrencyAmountDTO highestGainPrice;

    /** Price of the latest gain price for this position. */
    private CurrencyAmountDTO latestGainPrice;

    /**
     * Constructor.
     *
     * @param newId           position id
     * @param newType         position type
     * @param newStrategy     strategy
     * @param newCurrencyPair currency pair
     * @param newAmount       amount
     * @param newOpenOrder    open order
     * @param newRules        position rules
     */
    public PositionDTO(final long newId,
                       final PositionTypeDTO newType,
                       final StrategyDTO newStrategy,
                       final CurrencyPairDTO newCurrencyPair,
                       final BigDecimal newAmount,
                       final OrderDTO newOpenOrder,
                       final PositionRulesDTO newRules) {
        this.uid = newId;
        this.type = newType;
        this.positionId = newStrategy.getNextPositionId();
        this.strategy = newStrategy;
        this.currencyPair = newCurrencyPair;
        this.amount = CurrencyAmountDTO.builder()
                .value(newAmount)
                .currency(newCurrencyPair.getBaseCurrency())
                .build();
        this.openingOrder = newOpenOrder;
        this.rules = newRules;
        this.forceClosing = false;
        this.autoClose = true;
    }

    /**
     * Returns position status.
     *
     * @return status
     */
    @ToString.Include(name = "status")
    public final PositionStatusDTO getStatus() {
        if (closingOrder == null) {
            // No closing order is present.

            // An error occurred with the opening order.
            if (openingOrder.getStatus().isInError()) {
                return OPENING_FAILURE;
            }
            // Checking if the opening order is fulfilled or not.
            if (openingOrder.isFulfilled()) {
                return OPENED;
            } else {
                return OPENING;
            }
        } else {
            // Closing order present.

            // An error occurred with the closing order.
            if (closingOrder.getStatus().isInError()) {
                return CLOSING_FAILURE;
            }
            // Checking if the closing order is fulfilled or not.
            if (closingOrder.isFulfilled()) {
                return CLOSED;
            } else {
                return CLOSING;
            }
        }
    }

    /**
     * Calculate the gain from a price.
     *
     * @param price price
     * @return gain
     */
    public Optional<GainDTO> calculateGainFromPrice(final BigDecimal price) {
        if (price != null && ZERO.compareTo(price) != 0) {
            // How gain calculation works for a long positions:
            //  - Bought 10 ETH with a price of 5 -> Amount of 50 USDT.
            //  - Sold 10 ETH with a price of 6 -> Amount of 60 USDT.
            // Gain value: 10 USDT
            // Gain percentage: ((60 - 50) / 50) * 100 = 20 %
            if (this.type == LONG) {
                // Amounts.
                final BigDecimal valueIBought;
                if (openingOrder.isFulfilled()) {
                    // If we received all the trades, I can calculate exactly the amount I bought.
                    valueIBought = openingOrder.getTrades()
                            .stream()
                            .map(t -> t.getAmountValue().multiply(t.getPriceValue()))
                            .reduce(ZERO, BigDecimal::add);
                } else {
                    // If we did not receive all trades, I use order information.
                    valueIBought = openingOrder.getAmountValue().multiply(openingOrder.getAveragePriceValue());
                }
                final BigDecimal valueICanSell = amount.getValue().multiply(price);

                // Percentage.
                final BigDecimal gainPercentage = ((valueICanSell.subtract(valueIBought))
                        .divide(valueIBought, BIGINTEGER_SCALE, FLOOR))
                        .multiply(ONE_HUNDRED_BIG_DECIMAL);

                return Optional.of(GainDTO.builder()
                        .percentage(gainPercentage.floatValue())
                        .amount(CurrencyAmountDTO.builder()
                                .value(valueICanSell.subtract(valueIBought))
                                .currency(currencyPair.getQuoteCurrency())
                                .build())
                        .build());
            }

            // How gain calculation works for a short positions:
            //  - Sold 10 ETH with a price of 5 USDT -> I now have 50 USDT.
            //  - Bought 5 ETH with my 50 USDT as the price raised to 10 USDT.
            //  Gain = ((5 - 10) / 10) * 100 = -50 % (I calculate evolution backward, from bought price to sold price).
            // --
            // When sold: Ticker ETH/USDT: 1 ETH costs 5 USDT.
            // The amount of USDT I can spend (amountGained) = amount * price in trade.
            // When bought: Ticker ETH/USDT: 1 ETH costs 10 USDT.
            // The amount of ETH I can buy (amountICanBuy) = amountIOwnInQuoteCurrency / price.
            // Gain = amountICanBuy - amount.
            if (this.type == SHORT) {
                // Amounts.
                final BigDecimal amountGained;
                if (openingOrder.isFulfilled()) {
                    // If we received all the trades, I can calculate exactly the amount I sold.
                    amountGained = openingOrder.getTrades()
                            .stream()
                            .map(t -> t.getAmountValue().multiply(t.getPriceValue()))
                            .reduce(ZERO, BigDecimal::add);
                } else {
                    // If we did not receive all trades, I use order information.
                    amountGained = openingOrder.getAmountValue().multiply(openingOrder.getAveragePriceValue());
                }
                final BigDecimal amountICanBuy = amountGained.divide(price, BIGINTEGER_SCALE, FLOOR);
                // Percentage.
                final BigDecimal gainPercentage = ((amountICanBuy.subtract(amount.getValue()))
                        .divide(amount.getValue(), BIGINTEGER_SCALE, FLOOR))
                        .multiply(ONE_HUNDRED_BIG_DECIMAL);

                return Optional.of(GainDTO.builder()
                        .percentage(gainPercentage.floatValue())
                        .amount(CurrencyAmountDTO.builder()
                                .value(amountICanBuy.subtract(amount.getValue()))
                                .currency(currencyPair.getBaseCurrency())
                                .build())
                        .build());
            }
        }
        return Optional.empty();
    }

    /**
     * Method called by Cassandre on every order update.
     *
     * @param updatedOrder order
     * @return true if the order is linked to the position.
     */
    public final boolean orderUpdate(final OrderDTO updatedOrder) {
        // Check if it's for the opening order.
        if (openingOrder.getOrderId().equals(updatedOrder.getOrderId())) {
            this.openingOrder = updatedOrder;
            return true;
        }
        // Check if it's for the closing order.
        if (closingOrder != null && closingOrder.getOrderId().equals(updatedOrder.getOrderId())) {
            this.closingOrder = updatedOrder;
            return true;
        }
        return false;
    }

    /**
     * Method called by Cassandre on every trade update.
     *
     * @param trade trade
     * @return true if the trade is linked to the position.
     */
    public boolean tradeUpdate(final TradeDTO trade) {
        // Return true to indicate that the trade was for this position.
        return trade.getOrderId().equals(openingOrder.getOrderId())
                || (closingOrder != null && trade.getOrderId().equals(closingOrder.getOrderId()));
    }

    /**
     * Method called by Cassandre on every ticker update.
     *
     * @param ticker ticker
     * @return true if the ticker is linked to the position.
     */
    public final boolean tickerUpdate(final TickerDTO ticker) {
        // If the position is not closing and the ticker is the one expected.
        if (getStatus() == OPENED && ticker.getCurrencyPair().equals(currencyPair)) {

            // We calculate the gain, and we update fields price fields.
            // LastGain for sure / lowest and highest if it needs to be changed.
            calculateGainFromPrice(ticker.getLast()).ifPresent(gain -> {

                // We always update the last calculated gain.
                latestGainPrice = CurrencyAmountDTO.builder()
                        .value(ticker.getLast())
                        .currency(ticker.getQuoteCurrency())
                        .build();

                // If we don't close now, we update lowest and latest gain of the position.
                if (!shouldBeClosed()) {
                    // If the latest gain price is inferior to the lowest gain price (or if it doesn't exist), update it.
                    final Optional<GainDTO> lowestCalculatedGain = getLowestCalculatedGain();
                    if (lowestCalculatedGain.isEmpty() || gain.isInferiorTo(lowestCalculatedGain.get())) {
                        lowestGainPrice = latestGainPrice;
                    }
                    // If the latest gain price is superior to the highest gain price (or if it doesn't exist), update it.
                    final Optional<GainDTO> highestCalculatedGain = getHighestCalculatedGain();
                    if (highestCalculatedGain.isEmpty() || gain.isSuperiorTo(highestCalculatedGain.get())) {
                        highestGainPrice = latestGainPrice;
                    }
                }
            });
            return true;
        } else {
            // Not a ticker for this position or the position is no more opened.
            return false;
        }
    }

    /**
     * Returns amount locked by this position.
     *
     * @return amount
     */
    public CurrencyAmountDTO getAmountToLock() {
        if (getStatus() == CLOSED) {
            return CurrencyAmountDTO.ZERO;
        }

        if (type == LONG) {
            // We need to lock the amount we bought.
            if (openingOrder != null) {
                // We calculate the amount we bought from opening order trades.
                final BigDecimal amountBought = openingOrder.getTrades()
                        .stream()
                        .map(TradeDTO::getAmountValue)
                        .reduce(ZERO, BigDecimal::add);
                // If we have a closing order, we calculate how much we sold.
                BigDecimal amountSold = ZERO;
                if (closingOrder != null) {
                    amountSold = closingOrder.getTrades()
                            .stream()
                            .map(TradeDTO::getAmountValue)
                            .reduce(ZERO, BigDecimal::add);
                }
                return new CurrencyAmountDTO(amountBought.subtract(amountSold), currencyPair.getBaseCurrency());
            }
        } else {
            if (openingOrder != null) {
                // We calculate the amount we sold from opening order trades.
                final BigDecimal amountSold = openingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmountValue().multiply(t.getPriceValue()))
                        .reduce(ZERO, BigDecimal::add);
                // If we have a closing order, we calculate how much we bought.
                BigDecimal amountBought = ZERO;
                if (closingOrder != null) {
                    amountBought = closingOrder.getTrades()
                            .stream()
                            .map(t -> t.getAmountValue().multiply(t.getPriceValue()))
                            .reduce(ZERO, BigDecimal::add);
                }
                return new CurrencyAmountDTO(amountSold.subtract(amountBought), currencyPair.getQuoteCurrency());
            }
        }

        return CurrencyAmountDTO.ZERO;
    }

    /**
     * Returns true if the position should be closed.
     *
     * @return true if the rules says the position should be closed.
     */
    public boolean shouldBeClosed() {
        // If the position is set to "force closing", we return yes no matter the rules and gains.
        if (forceClosing) {
            return true;
        }

        // Returns true if one of the rule is triggered.
        final Optional<GainDTO> latestCalculatedGain = getLatestCalculatedGain();
        return latestCalculatedGain
                .filter(gainDTO -> rules.isStopGainPercentageSet() && gainDTO.getPercentage() >= rules.getStopGainPercentage()
                        || rules.isStopLossPercentageSet() && gainDTO.getPercentage() <= -rules.getStopLossPercentage())
                .isPresent();
    }

    /**
     * Close position with order.
     *
     * @param newCloseOrder the order closing the position
     */
    public final void closePositionWithOrder(final OrderDTO newCloseOrder) {
        // This method should only be called when the position is in the OPENED status.
        if (getStatus() != OPENED) {
            throw new PositionException("Impossible to close position " + uid + " because of its status " + getStatus());
        }
        closingOrder = newCloseOrder;
    }

    /**
     * Getter lowestCalculatedGain.
     *
     * @return lowestCalculatedGain
     */
    public final Optional<GainDTO> getLowestCalculatedGain() {
        if (lowestGainPrice != null) {
            return calculateGainFromPrice(lowestGainPrice.getValue());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Getter highestCalculatedGain.
     *
     * @return highestCalculatedGain
     */
    public final Optional<GainDTO> getHighestCalculatedGain() {
        if (highestGainPrice != null) {
            return calculateGainFromPrice(highestGainPrice.getValue());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Getter latestCalculatedGain.
     *
     * @return latestCalculatedGain
     */
    public final Optional<GainDTO> getLatestCalculatedGain() {
        if (latestGainPrice != null) {
            return calculateGainFromPrice(latestGainPrice.getValue());
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
        if (getStatus() == CLOSED) {
            if (this.type == LONG) {
                // Gain calculation for currency pair: ETH-BTC
                // The first listed currency of a currency pair is called the base currency.
                // The second currency is called the quote currency.

                // Price is 0.035547 means 1 Ether equals 0.035547 Bitcoin
                // If you buy a currency pair, you buy the base currency and implicitly sell the quoted currency.

                // - Bought 10 ETH with a price of 4 BTC -> costs 40 BTC.
                // - Bought 20 ETH with a price of 3 BTC -> costs 60 BTC.
                // - Sold 10 ETH with a price of 3 BTC -> earns 30 BTC.
                // - Sold 05 ETH with a price of 6 BTC -> earns 30 BTC
                // - Sold 15 ETH with a price of 6 BTC -> earns 90 BTC
                // ---
                // To start the position, I spent 100 BTC.
                // When I closed the position, I received 150 BTC
                // Gain  -> ((150 - 100) / 100) * 100 = 50 %
                BigDecimal bought = openingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmountValue().multiply(t.getPriceValue()))
                        .reduce(ZERO, BigDecimal::add);

                BigDecimal sold = closingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmountValue().multiply(t.getPriceValue()))
                        .reduce(ZERO, BigDecimal::add);

                // Calculate gain.
                BigDecimal gainAmount = sold.subtract(bought);
                BigDecimal gainPercentage = ((sold.subtract(bought)).divide(bought, HALF_UP)).multiply(ONE_HUNDRED_BIG_DECIMAL);

                // Opening & closing order fees.
                final List<CurrencyAmountDTO> openingOrderFees = openingOrder.getTrades()
                        .stream()
                        .filter(tradeDTO -> tradeDTO.getFee() != null)
                        .map(tradeDTO -> new CurrencyAmountDTO(tradeDTO.getFee().getValue(), tradeDTO.getFee().getCurrency()))
                        .toList();
                final List<CurrencyAmountDTO> closingOrderFees = closingOrder.getTrades()
                        .stream()
                        .filter(tradeDTO -> tradeDTO.getFee() != null)
                        .map(tradeDTO -> new CurrencyAmountDTO(tradeDTO.getFee().getValue(), tradeDTO.getFee().getCurrency()))
                        .toList();

                // Return position gain.
                return GainDTO.builder()
                        .percentage(gainPercentage.setScale(2, HALF_UP).doubleValue())
                        .amount(CurrencyAmountDTO.builder()
                                .value(gainAmount)
                                .currency(currencyPair.getQuoteCurrency())
                                .build())
                        .openingOrderFees(openingOrderFees)
                        .closingOrderFees(closingOrderFees)
                        .build();
            }

            if (this.type == SHORT) {
                BigDecimal sold = openingOrder.getTrades()
                        .stream()
                        .map(TradeDTO::getAmountValue)
                        .reduce(ZERO, BigDecimal::add);

                BigDecimal bought = closingOrder.getTrades()
                        .stream()
                        .map(TradeDTO::getAmountValue)
                        .reduce(ZERO, BigDecimal::add);

                // Calculate gain.
                BigDecimal gainAmount = bought.subtract(sold);
                BigDecimal gainPercentage = ((bought.subtract(sold)).divide(sold, HALF_UP)).multiply(ONE_HUNDRED_BIG_DECIMAL);

                // Opening & closing order fees.
                final List<CurrencyAmountDTO> openingOrderFees = openingOrder.getTrades()
                        .stream()
                        .filter(tradeDTO -> tradeDTO.getFee() != null)
                        .map(tradeDTO -> new CurrencyAmountDTO(tradeDTO.getFee().getValue(), tradeDTO.getFee().getCurrency()))
                        .toList();

                final List<CurrencyAmountDTO> closingOrderFees = closingOrder.getTrades()
                        .stream()
                        .filter(tradeDTO -> tradeDTO.getFee() != null)
                        .map(tradeDTO -> new CurrencyAmountDTO(tradeDTO.getFee().getValue(), tradeDTO.getFee().getCurrency()))
                        .toList();

                // Return position gain.
                return GainDTO.builder()
                        .percentage(gainPercentage.setScale(2, HALF_UP).doubleValue())
                        .amount(CurrencyAmountDTO.builder()
                                .value(gainAmount)
                                .currency(currencyPair.getBaseCurrency())
                                .build())
                        .openingOrderFees(openingOrderFees)
                        .closingOrderFees(closingOrderFees)
                        .build();
            }
        }
        // If the position is not closed: Zero gain.
        return GainDTO.ZERO;
    }

    /**
     * Get position description.
     *
     * @return description
     */
    @SuppressWarnings({"unused", "checkstyle:InnerAssignment"})
    public final String getDescription() {
        try {
            String value = StringUtils.capitalize(type.toString().toLowerCase(Locale.ROOT)) + " position n°" + positionId + " of " + amount;
            // Rules.
            value += " (rules: ";
            if (!rules.isStopGainPercentageSet() && !rules.isStopLossPercentageSet()) {
                value += "no rules";
            }
            if (rules.isStopGainPercentageSet() && !rules.isStopLossPercentageSet()) {
                value += rules.getStopGainPercentage() + " % gain";
            }
            if (rules.isStopLossPercentageSet() && !rules.isStopGainPercentageSet()) {
                value += rules.getStopLossPercentage() + " % loss";
            }
            if (rules.isStopGainPercentageSet() && rules.isStopLossPercentageSet()) {
                value += rules.getStopGainPercentage() + " % gain / ";
                value += rules.getStopLossPercentage() + " % loss";
            }
            value += ")";
            switch (getStatus()) {
                case OPENING -> value += " - Opening - Waiting for the trades of order " + openingOrder.getOrderId();
                case OPENED -> {
                    value += " - Opened";
                    final Optional<GainDTO> lastGain = getLatestCalculatedGain();
                    if (lastGain.isPresent() && getLatestCalculatedGain().isPresent()) {
                        value += " - Last gain calculated " + new DecimalFormat("#0.##").format(getLatestCalculatedGain().get().getPercentage()) + " %";
                    }
                }
                case OPENING_FAILURE -> value = "Position " + this.getUid() + " - Opening failure";
                case CLOSING -> value += " - Closing - Waiting for the trades of order " + closingOrder.getOrderId();
                case CLOSING_FAILURE -> value = "Position " + this.getUid() + " - Closing failure";
                case CLOSED -> {
                    final GainDTO gain = getGain();
                    if (gain != null) {
                        value += " - Closed - " + gain;
                    } else {
                        value += " - Closed - Error during gain calculation";
                    }
                }
                default -> value = "Incorrect state for position " + this.getUid();
            }
            return value;
        } catch (Exception e) {
            return "Position " + this.getUid() + " (error in getDescription() method)";
        }
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
        final PositionDTO that = (PositionDTO) o;
        return new EqualsBuilder()
                .append(this.uid, that.uid)
                .append(this.positionId, that.positionId)
                .append(this.type, that.type)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.rules, that.rules)
                .append(this.getStatus(), that.getStatus())
                .append(this.openingOrder, that.openingOrder)
                .append(this.closingOrder, that.closingOrder)
                .append(this.lowestGainPrice, that.lowestGainPrice)
                .append(this.highestGainPrice, that.highestGainPrice)
                .append(this.latestGainPrice, that.latestGainPrice)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(uid)
                .toHashCode();
    }

}
