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
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.util.exception.PositionException;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

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
    private final long id;

    /** An identifier that uniquely identifies the position. */
    private final long positionId;

    /** Position type (Long or Short). */
    private final PositionTypeDTO type;

    /** The strategy that created the position. */
    private final StrategyDTO strategy;

    /** Currency pair. */
    private final CurrencyPairDTO currencyPair;

    /** Amount that was ordered. */
    private final CurrencyAmountDTO amount;

    /** Position rules. */
    private final PositionRulesDTO rules;

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

    /** 100%. */
    private static final int ONE_HUNDRED_INTEGER = 100;

    /** 100%. */
    private static final BigDecimal ONE_HUNDRED_BIG_DECIMAL = new BigDecimal("100");

    /** Big integer scale. */
    private static final int BIGINTEGER_SCALE = 8;

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
        this.id = newId;
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

            // An error occurred with the order.
            if (openingOrder.getStatus().isInError()) {
                return OPENING_FAILURE;
            }
            // Checking if fulfilled or not.
            if (openingOrder.isFulfilled()) {
                return OPENED;
            } else {
                return OPENING;
            }
        } else {
            // Closing order present.

            // An error occurred with the order.
            if (closingOrder.getStatus().isInError()) {
                return CLOSING_FAILURE;
            }
            // Checking if fulfilled or not.
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
                        .fees(CurrencyAmountDTO.builder()
                                .value(ZERO)
                                .currency(currencyPair.getQuoteCurrency())
                                .build())
                        .build());
            }

            // How gain calculation works for a short positions:
            //  - Sold 10 ETH with a price of 5 USDT -> I now have 50 USDT.
            //  - Bought 5 ETH with my 50 USDT as the price raised to 10 USDT.
            //  Gain = ((5 - 10) / 10) * 100 = -50 % (I calculate evolution backward, from bought price to sold price).
            // --
            // When sold : Ticker ETH/USDT : 1 ETH costs 5 USDT.
            // The amount of USDT I can spend (amountGained) = amount * price in trade.
            // When bought : Ticker ETH/USDT : 1 ETH costs 10 USDT.
            // The amount of ETH I can buy (amountICanBuy) = amountIOwnInQuoteCurrency / price.
            // Gain = amountICanBuy - amount.
            if (this.type == SHORT) {
                // Amounts.
                final BigDecimal amountGained;
                if (openingOrder.isFulfilled()) {
                    // If we received all the trades, I can calculate exactly the amount I bought.
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
                        .fees(CurrencyAmountDTO.builder()
                                .value(ZERO)
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
     * @return true if the order updated the position.
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
     * @return true if the trade updated the position.
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
     * @return true if the ticker updated the position.
     */
    public final boolean tickerUpdate(final TickerDTO ticker) {
        // If the position is not closing and the ticker is the one expected.
        if (getStatus() == OPENED && ticker.getCurrencyPair().equals(currencyPair)) {

            // We calculate the gain, and we update fields price fields.
            // LastGain for sure.
            // Lowest and highest if it changes.
            calculateGainFromPrice(ticker.getLast()).ifPresent(gain -> {

                // We update the last calculated gain.
                latestGainPrice = CurrencyAmountDTO.builder()
                        .value(ticker.getLast())
                        .currency(ticker.getQuoteCurrency())
                        .build();

                // If we don't close now, we update lowest and latest.
                if (!shouldBeClosed()) {
                    // If the latest gain price is inferior to the lowest gain price, we update it.
                    final Optional<GainDTO> lowestCalculatedGain = getLowestCalculatedGain();
                    if (lowestCalculatedGain.isEmpty() || gain.isInferiorTo(lowestCalculatedGain.get())) {
                        lowestGainPrice = latestGainPrice;
                    }
                    // If the latest gain price is superior to the highest gain price, we update it.
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
        // If the position is set to "force closing", we return yes.
        if (forceClosing) {
            return true;
        }

        // Returns true if one of the rule is triggered.
        final Optional<GainDTO> latestCalculatedGain = getLatestCalculatedGain();
        return latestCalculatedGain.filter(gainDTO -> rules.isStopGainPercentageSet() && gainDTO.getPercentage() >= rules.getStopGainPercentage()
                || rules.isStopLossPercentageSet() && gainDTO.getPercentage() <= -rules.getStopLossPercentage())
                .isPresent();
    }

    /**
     * Close position with order id.
     *
     * @param newCloseOrder the closeOrderId to set
     */
    public final void closePositionWithOrder(final OrderDTO newCloseOrder) {
        // This method should only be called when in status OPENED.
        if (getStatus() != OPENED) {
            throw new PositionException("Impossible to close position " + id + " because of its status " + getStatus());
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
                // Gain calculation for currency pair : ETH-BTC
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

                // Calculate fees.
                BigDecimal fees = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream())
                        .filter(tradeDTO -> tradeDTO.getFee() != null)
                        .map(TradeDTO::getFeeValue)
                        .reduce(ZERO, BigDecimal::add);
                CurrencyDTO feeCurrency;
                final Optional<TradeDTO> firstTrade = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream()).findFirst();
                if (firstTrade.isPresent() && firstTrade.get().getFee() != null) {
                    feeCurrency = firstTrade.get().getFee().getCurrency();
                } else {
                    feeCurrency = currencyPair.getQuoteCurrency();
                }

                // Return position gain.
                return GainDTO.builder()
                        .percentage(gainPercentage.setScale(2, HALF_UP).doubleValue())
                        .amount(CurrencyAmountDTO.builder()
                                .value(gainAmount)
                                .currency(currencyPair.getQuoteCurrency())
                                .build())
                        .fees(CurrencyAmountDTO.builder()
                                .value(fees)
                                .currency(feeCurrency)
                                .build())
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

                // Calculate fees.
                BigDecimal fees = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream())
                        .filter(tradeDTO -> tradeDTO.getFee() != null)
                        .map(TradeDTO::getFeeValue)
                        .reduce(ZERO, BigDecimal::add);
                CurrencyDTO feeCurrency;
                final Optional<TradeDTO> firstTrade = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream()).findFirst();
                if (firstTrade.isPresent() && firstTrade.get().getFee() != null) {
                    feeCurrency = firstTrade.get().getFee().getCurrency();
                } else {
                    feeCurrency = currencyPair.getQuoteCurrency();
                }

                // Return position gain.
                return GainDTO.builder()
                        .percentage(gainPercentage.setScale(2, HALF_UP).doubleValue())
                        .amount(CurrencyAmountDTO.builder()
                                .value(gainAmount)
                                .currency(currencyPair.getBaseCurrency())
                                .build())
                        .fees(CurrencyAmountDTO.builder()
                                .value(fees)
                                .currency(feeCurrency)
                                .build())
                        .build();
            }
        }
        // If the position is not closed: we gain zero.
        return GainDTO.ZERO;
    }

    /**
     * Get position description.
     *
     * @return description
     */
    @SuppressWarnings("unused")
    public final String getDescription() {
        try {
            String value = StringUtils.capitalize(type.toString().toLowerCase(Locale.ROOT)) + " position n°" + positionId;
            // Rules.
            value += " (rules : ";
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
                case OPENING:
                    value += " - Opening - Waiting for the trades of order " + openingOrder.getOrderId();
                    break;
                case OPENED:
                    value += " on " + getCurrencyPair() + " - Opened";
                    final Optional<GainDTO> lastGain = getLatestCalculatedGain();
                    if (lastGain.isPresent() && getLatestCalculatedGain().isPresent()) {
                        value += " - Last gain calculated " + getFormattedValue(getLatestCalculatedGain().get().getPercentage()) + " %";
                    }
                    break;
                case OPENING_FAILURE:
                    value = "Position " + getId() + " - Opening failure";
                    break;
                case CLOSING:
                    value += " on " + getCurrencyPair() + " - Closing - Waiting for the trades of order " + closingOrder.getOrderId();
                    break;
                case CLOSING_FAILURE:
                    value = "Position " + getId() + " - Closing failure";
                    break;
                case CLOSED:
                    final GainDTO gain = getGain();
                    value += " on " + getCurrencyPair() + " - Closed - " + gain;
                    break;
                default:
                    value = "Incorrect state for position " + getId();
                    break;
            }
            return value;
        } catch (Exception e) {
            return "Position " + getId() + " (error in getDescription() method)";
        }
    }

    /**
     * Returns formatted value.
     *
     * @param value value
     * @return formatted value
     */
    private String getFormattedValue(final double value) {
        return new DecimalFormat("#0.##").format(value);
    }


    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PositionDTO that = (PositionDTO) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
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
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
