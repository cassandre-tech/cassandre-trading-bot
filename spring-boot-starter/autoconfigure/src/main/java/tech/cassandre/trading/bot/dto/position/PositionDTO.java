package tech.cassandre.trading.bot.dto.position;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
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
@SuppressWarnings({"checkstyle:VisibilityModifier"})
public class PositionDTO {

    /** Technical ID. */
    private final long id;

    /** An identifier that uniquely identifies the position. */
    private final Long positionId;

    /** Position type. */
    private final PositionTypeDTO type;

    /** The strategy that created the position. */
    private final StrategyDTO strategy;

    /** Currency pair. */
    private final CurrencyPairDTO currencyPair;

    /** Amount that was ordered. */
    private final CurrencyAmountDTO amount;

    /** Position rules. */
    private final PositionRulesDTO rules;

    /** Position status. */
    private PositionStatusDTO status;

    /** Indicates that the position must be closed no matter the rules. */
    private boolean forceClosing;

    /** The order id created to open the position. */
    private final String openingOrderId;

    /** The order created to open the position. */
    private OrderDTO openingOrder;

    /** The order id created to open the position. */
    private String closingOrderId;

    /** The order created to close the position. */
    private OrderDTO closingOrder;

    /** Price of lowest gain reached by this position. */
    private CurrencyAmountDTO lowestGainPrice;

    /** Price of highest gain reached by this position. */
    private CurrencyAmountDTO highestGainPrice;

    /** Price of latest gain price for this position. */
    private CurrencyAmountDTO latestGainPrice;

    /** 100%. */
    private static final int ONE_HUNDRED_FLOAT = 100;

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
     * @param newOpenOrderId  open order id
     * @param newRules        position rules
     */
    public PositionDTO(final long newId,
                       final PositionTypeDTO newType,
                       final StrategyDTO newStrategy,
                       final CurrencyPairDTO newCurrencyPair,
                       final BigDecimal newAmount,
                       final String newOpenOrderId,
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
        this.openingOrderId = newOpenOrderId;
        this.rules = newRules;
        this.status = OPENING;
        this.forceClosing = false;
    }

    /**
     * Calculate the gain from a price.
     *
     * @param price price
     * @return gain
     */
    private Optional<GainDTO> calculateGainFromPrice(final BigDecimal price) {
        if (this.status != OPENING && price != null) {
            // How gain calculation works for long positions ?
            //  - Bought 10 ETH with a price of 5 -> Amount of 50 USDT.
            //  - Sold 10 ETH with a price of 6 -> Amount of 60 USDT.
            // Gain value: 10 USDT
            // Gain percentage: ((60 - 50) / 50) * 100 = 20 %
            if (this.type == LONG) {
                // Amounts.
                final BigDecimal valueIBought = openingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                        .reduce(ZERO, BigDecimal::add);
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

            // How gain calculation works for short positions ?
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
                final BigDecimal amountGained = openingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                        .reduce(ZERO, BigDecimal::add);
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
     * Method called by on every order update.
     *
     * @param updatedOrder order
     * @return true if the the order updated the position.
     */
    public final boolean orderUpdate(final OrderDTO updatedOrder) {
        if (openingOrderId.equals(updatedOrder.getOrderId())) {
            this.openingOrder = updatedOrder;
            if (updatedOrder.getStatus().isInError()) {
                this.status = OPENING_FAILURE;
            }
            return true;
        }
        if (closingOrderId != null && closingOrderId.equals(updatedOrder.getOrderId())) {
            this.closingOrder = updatedOrder;
            if (updatedOrder.getStatus().isInError()) {
                this.status = CLOSING_FAILURE;
            }
            return true;
        }
        return false;
    }

    /**
     * Method called by on every trade update.
     *
     * @param trade trade
     * @return true if the the trade updated the position.
     */
    public boolean tradeUpdate(final TradeDTO trade) {
        // If status is OPENING and the trades for the open order arrives for the whole amount ==> status = OPENED.
        if (trade.getOrderId().equals(openingOrderId) && status == OPENING) {

            // We calculate the sum of amount in the all the trades.
            // If it reaches the original amount we order, we consider the trade opened.
            System.out.println("=>>>" + openingOrder);
            final BigDecimal tradesTotal = openingOrder.getTrades()
                    .stream()
                    .filter(t -> !t.getTradeId().equals(trade.getTradeId()))
                    .map(t -> t.getAmount().getValue())
                    .reduce(trade.getAmount().getValue(), BigDecimal::add);
            if (openingOrder.getAmount().getValue().compareTo(tradesTotal) == 0) {
                status = OPENED;
            }
        }

        // If status is CLOSING and the trades for the close order arrives for the whole amount ==> status = CLOSED.
        if (trade.getOrderId().equals(closingOrderId) && status == CLOSING) {

            // We calculate the sum of amount in the all the trades.
            // If it reaches the original amount we order, we consider the trade opened.
            final BigDecimal tradesTotal = closingOrder.getTrades()
                    .stream()
                    .filter(t -> !t.getTradeId().equals(trade.getTradeId()))
                    .map(t -> t.getAmount().getValue())
                    .reduce(trade.getAmount().getValue(), BigDecimal::add);
            if (closingOrder.getAmount().getValue().compareTo(tradesTotal) == 0) {
                status = CLOSED;
            }
        }

        // Return true signaling there is an update if this trade was for this position.
        return trade.getOrderId().equals(getOpeningOrderId()) || trade.getOrderId().equals(getClosingOrderId());
    }

    /**
     * Method called by on every ticker update.
     *
     * @param ticker ticker
     * @return true if the the ticker updated the position.
     */
    public final boolean tickerUpdate(final TickerDTO ticker) {
        // If the position is not closing and the ticker is the one expected.
        if (getClosingOrder() == null && ticker.getCurrencyPair().equals(currencyPair)) {

            // We retrieve the gains.
            final Optional<GainDTO> calculatedGain = calculateGainFromPrice(ticker.getLast());
            final Optional<GainDTO> lowestCalculatedGain = getLowestCalculatedGain();
            final Optional<GainDTO> highestCalculatedGain = getHighestCalculatedGain();

            // We set the new values.
            calculatedGain.ifPresent(gain -> {
                final CurrencyAmountDTO price = CurrencyAmountDTO.builder()
                        .value(ticker.getLast())
                        .currency(ticker.getQuoteCurrency())
                        .build();

                // We save the last calculated gain.
                latestGainPrice = price;

                // If we don't close now, we update lowest and latest.
                if (!shouldBeClosed()) {
                    // If we don't have a lowest gain or if it's a lowest gain.
                    if (lowestCalculatedGain.isEmpty() || calculatedGain.get().isInferiorTo(lowestCalculatedGain.get())) {
                        lowestGainPrice = price;
                    }
                    // If we don't have a highest gain or if it's a highest gain.
                    if (highestCalculatedGain.isEmpty() || calculatedGain.get().isSuperiorTo(highestCalculatedGain.get())) {
                        highestGainPrice = price;
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns amount locked by this position.
     *
     * @return amount
     */
    public CurrencyAmountDTO getAmountToLock() {
        if (status == CLOSED) {
            return CurrencyAmountDTO.ZERO;
        }

        if (type == LONG) {
            // We need to lock the amount we bought.
            if (openingOrder != null) {
                // We calculate the amount we bought from opening order trades.
                final BigDecimal amountBought = openingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmount().getValue())
                        .reduce(ZERO, BigDecimal::add);
                // If we have a closing order, we calculate how much we sold.
                BigDecimal amountSold = ZERO;
                if (closingOrder != null) {
                    amountSold = closingOrder.getTrades()
                            .stream()
                            .map(t -> t.getAmount().getValue())
                            .reduce(ZERO, BigDecimal::add);
                }
                return new CurrencyAmountDTO(amountBought.subtract(amountSold), currencyPair.getBaseCurrency());
            }
        } else {
            if (openingOrder != null) {
                // We calculate the amount we sold from opening order trades.
                final BigDecimal amountSold = openingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                        .reduce(ZERO, BigDecimal::add);
                // If we have a closing order, we calculate how much we bought.
                BigDecimal amountBought = ZERO;
                if (closingOrder != null) {
                    amountBought = closingOrder.getTrades()
                            .stream()
                            .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                            .reduce(ZERO, BigDecimal::add);
                }
                return new CurrencyAmountDTO(amountSold.subtract(amountBought), currencyPair.getQuoteCurrency());
            }
        }

        return CurrencyAmountDTO.ZERO;
    }

    /**
     * Setter forceClosing.
     *
     * @param newForceClosing the forceClosing to set
     */
    public final void setForceClosing(final boolean newForceClosing) {
        forceClosing = newForceClosing;
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

        final Optional<GainDTO> latestCalculatedGain = getLatestCalculatedGain();
        // Returns true if one of the rule is triggered.
        return latestCalculatedGain.filter(gainDTO -> rules.isStopGainPercentageSet() && gainDTO.getPercentage() >= rules.getStopGainPercentage()
                || rules.isStopLossPercentageSet() && gainDTO.getPercentage() <= -rules.getStopLossPercentage())
                .isPresent();
    }

    /**
     * Close position with order id.
     *
     * @param newCloseOrderId the closeOrderId to set
     */
    public final void closePositionWithOrderId(final String newCloseOrderId) {
        // This method should only be called when in status OPENED.
        if (status != OPENED) {
            throw new PositionException("Impossible to set close order id for position " + id);
        }
        closingOrderId = newCloseOrderId;
        status = CLOSING;
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
        if (status == CLOSED) {
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
                        .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                        .reduce(ZERO, BigDecimal::add);

                BigDecimal sold = closingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                        .reduce(ZERO, BigDecimal::add);

                // Calculate gain.
                BigDecimal gainAmount = sold.subtract(bought);
                BigDecimal gainPercentage = ((sold.subtract(bought)).divide(bought, HALF_UP)).multiply(ONE_HUNDRED_BIG_DECIMAL);

                // Calculate fees.
                BigDecimal fees = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream())
                        .map(t -> t.getFee().getValue())
                        .reduce(ZERO, BigDecimal::add);
                CurrencyDTO feeCurrency;
                final Optional<TradeDTO> firstTrade = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream()).findFirst();
                if (firstTrade.isPresent()) {
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
                        .map(t -> t.getAmount().getValue())
                        .reduce(ZERO, BigDecimal::add);

                BigDecimal bought = closingOrder.getTrades()
                        .stream()
                        .map(t -> t.getAmount().getValue())
                        .reduce(ZERO, BigDecimal::add);

                // Calculate gain.
                BigDecimal gainAmount = bought.subtract(sold);
                BigDecimal gainPercentage = ((bought.subtract(sold)).divide(sold, HALF_UP)).multiply(ONE_HUNDRED_BIG_DECIMAL);

                // Calculate fees.
                BigDecimal fees = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream())
                        .map(t -> t.getFee().getValue())
                        .reduce(ZERO, BigDecimal::add);
                CurrencyDTO feeCurrency;
                final Optional<TradeDTO> firstTrade = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream()).findFirst();
                if (firstTrade.isPresent()) {
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
        return GainDTO.ZERO;
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
                .append(this.status, that.status)
                .append(this.openingOrder, that.openingOrder)
                .append(this.openingOrderId, that.openingOrderId)
                .append(this.closingOrder, that.closingOrder)
                .append(this.closingOrderId, that.closingOrderId)
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

    /**
     * Get position description.
     *
     * @return description
     */
    @SuppressWarnings("unused")
    public final String getDescription() {
        try {
            String value = type.toString().toLowerCase(Locale.ROOT) + " position n°" + positionId + " (rules : ";
            // Rules.
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
            switch (status) {
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
                    value += " on " + getCurrencyPair() + " - Closed - Gain : " + getFormattedValue(gain.getPercentage()) + " %";
                    break;
                default:
                    value = "Incorrect state for position " + getId();
                    break;
            }
            return value;
        } catch (Exception e) {
            return "Position " + getId() + " (error in description generation)";
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


    /**
     * Getter lowestPrice.
     *
     * @return lowestPrice
     */
    @Deprecated(since = "4.1.0", forRemoval = true)
    public final CurrencyAmountDTO getLowestPrice() {
        return lowestGainPrice;
    }

    /**
     * Getter highestPrice.
     *
     * @return highestPrice
     */
    @Deprecated(since = "4.1.0", forRemoval = true)
    public final CurrencyAmountDTO getHighestPrice() {
        return highestGainPrice;
    }

    /**
     * Getter latestPrice.
     *
     * @return latestPrice
     */
    @Deprecated(since = "4.1.0", forRemoval = true)
    public final CurrencyAmountDTO getLatestPrice() {
        return latestGainPrice;
    }

}
