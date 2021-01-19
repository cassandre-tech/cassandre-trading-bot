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
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.util.exception.PositionException;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
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
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

/**
 * DTO representing a position.
 * A position is the amount of a security, commodity or currency which is owned by an individual, dealer, institution, or other fiscal entity.
 */
@Getter
@Builder
@ToString
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
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

    /** The order created to open the position. */
    private OrderDTO openingOrder;

    /** The order created to close the position. */
    private OrderDTO closingOrder;

    /** Lowest price reached by tis position. */
    private CurrencyAmountDTO lowestPrice;

    /** Highest price reached by tis position. */
    private CurrencyAmountDTO highestPrice;

    /** Latest price for this position. */
    private CurrencyAmountDTO latestPrice;

    /** Percentage. */
    private static final int ONE_HUNDRED = 100;

    /** Big integer scale. */
    private static final int BIGINTEGER_SCALE = 4;

    /**
     * Constructor.
     *
     * @param newId           position id
     * @param newStrategy     strategy
     * @param newCurrencyPair currency pair
     * @param newAmount       amount
     * @param newOpenOrder    open order
     * @param newRules        position rules
     */
    public PositionDTO(final long newId,
                       final StrategyDTO newStrategy,
                       final CurrencyPairDTO newCurrencyPair,
                       final BigDecimal newAmount,
                       final OrderDTO newOpenOrder,
                       final PositionRulesDTO newRules) {
        this.status = OPENING;
        this.type = LONG;
        this.id = newId;
        this.positionId = newId;
        this.strategy = newStrategy;
        this.currencyPair = newCurrencyPair;
        this.amount = CurrencyAmountDTO.builder()
                .value(newAmount)
                .currency(newCurrencyPair.getBaseCurrency())
                .build();
        this.openingOrder = newOpenOrder;
        this.rules = newRules;
    }

    /**
     * Constructor (Use only if you don't have the order).
     *
     * @param newId           position id
     * @param newStrategy     strategy
     * @param newCurrencyPair currency pair
     * @param newAmount       amount
     * @param newOpenOrderId  open order id
     * @param newRules        position rules
     */
    public PositionDTO(final long newId,
                       final StrategyDTO newStrategy,
                       final CurrencyPairDTO newCurrencyPair,
                       final BigDecimal newAmount,
                       final String newOpenOrderId,
                       final PositionRulesDTO newRules) {
        this.status = OPENING;
        this.type = LONG;
        this.id = newId;
        this.positionId = newId;
        this.strategy = newStrategy;
        this.currencyPair = newCurrencyPair;
        this.amount = CurrencyAmountDTO.builder()
                .value(newAmount)
                .currency(newCurrencyPair.getBaseCurrency())
                .build();
        // We create a temporary opening order.
        openingOrder = OrderDTO.builder()
                .orderId(newOpenOrderId)
                .type(BID)
                .currencyPair(currencyPair)
                .status(PENDING_NEW)
                .timestamp(ZonedDateTime.now())
                .build();
        this.rules = newRules;
    }

    /**
     * Returns opening order id.
     *
     * @return opening order id
     */
    private String getOpeningOrderId() {
        if (openingOrder != null) {
            return openingOrder.getOrderId();
        } else {
            return null;
        }
    }

    /**
     * Returns closing order id.
     *
     * @return closing order id
     */
    private String getClosingOrderId() {
        if (closingOrder != null) {
            return closingOrder.getOrderId();
        } else {
            return null;
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
            // We take the price from the first trade received.
            // TODO Use order price and then, then mean of all trades.
            final TradeDTO openTrade = openingOrder.getTrades().iterator().next();

            // How gain calculation works ?
            //  - Bought 10 ETH with a price of 5 -> Amount of 50.
            //  - Sold 10 ETH with a price of 6 -> Amount of 60.
            //  Gain = (6-5)/5 = 20%.
            float gainPercentage = (price.subtract(openTrade.getPrice().getValue()))
                    .divide(openTrade.getPrice().getValue(), BIGINTEGER_SCALE, FLOOR)
                    .floatValue() * ONE_HUNDRED;
            BigDecimal gainAmount = ((openTrade.getAmount().getValue().multiply(price))
                    .subtract((openTrade.getAmount().getValue()).multiply(openTrade.getPrice().getValue())));

            return Optional.of(GainDTO.builder()
                    .percentage(gainPercentage)
                    .amount(CurrencyAmountDTO.builder()
                            .value(gainAmount)
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .fees(CurrencyAmountDTO.builder()
                            .value(ZERO)
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .build());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Method called by on every order update.
     *
     * @param updatedOrder order
     * @return true if the the order updated the position.
     */
    public final boolean orderUpdate(final OrderDTO updatedOrder) {
        if (openingOrder != null && openingOrder.getOrderId().equals(updatedOrder.getOrderId())) {
            this.openingOrder = updatedOrder;
            if (updatedOrder.getStatus().isInError()) {
                this.status = OPENING_FAILURE;

            }
            return true;
        }
        if (closingOrder != null && closingOrder.getOrderId().equals(updatedOrder.getOrderId())) {
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
        if (trade.getOrderId().equals(getOpeningOrderId()) && status == OPENING) {

            // We calculate the sum of amount in the all the trades.
            // If it reaches the original amount we order, we consider the trade opened.
            final BigDecimal total = openingOrder.getTrades()
                    .stream()
                    .filter(t -> !t.getTradeId().equals(trade.getTradeId()))
                    .map(t -> t.getAmount().getValue())
                    .reduce(trade.getAmount().getValue(), BigDecimal::add);
            if (amount.getValue().compareTo(total) == 0) {
                status = OPENED;
            }
        }

        // If status is CLOSING and the trades for the close order arrives for the whole amount ==> status = CLOSED.
        if (trade.getOrderId().equals(getClosingOrderId()) && status == CLOSING) {

            // We calculate the sum of amount in the all the trades.
            // If it reaches the original amount we order, we consider the trade opened.
            final BigDecimal total = closingOrder.getTrades()
                    .stream()
                    .filter(t -> !t.getTradeId().equals(trade.getTradeId()))
                    .map(t -> t.getAmount().getValue())
                    .reduce(trade.getAmount().getValue(), BigDecimal::add);
            if (amount.getValue().compareTo(total) == 0) {
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
                latestPrice = price;

                // If we don't close now, we update lowest and latest.
                if (!shouldBeClosed()) {
                    // If we don't have a lowest gain or if it's a lowest gain.
                    if (lowestCalculatedGain.isEmpty() || calculatedGain.get().isInferiorTo(lowestCalculatedGain.get())) {
                        lowestPrice = price;
                    }
                    // If we don't have a highest gain or if it's a highest gain.
                    if (highestCalculatedGain.isEmpty() || calculatedGain.get().isSuperiorTo(highestCalculatedGain.get())) {
                        highestPrice = price;
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the position should be closed.
     *
     * @return true if the rules says the position should be closed.
     */
    public boolean shouldBeClosed() {
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
        status = CLOSING;
        // We create a temporary closing order that will be saved in database.
        closingOrder = OrderDTO.builder()
                .orderId(newCloseOrderId)
                .timestamp(ZonedDateTime.now())
                .type(ASK)
                .currencyPair(currencyPair)
                .status(PENDING_NEW)
                .build();
    }

    /**
     * Getter lowestCalculatedGain.
     *
     * @return lowestCalculatedGain
     */
    public final Optional<GainDTO> getLowestCalculatedGain() {
        if (lowestPrice != null) {
            return calculateGainFromPrice(lowestPrice.getValue());
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
        if (highestPrice != null) {
            return calculateGainFromPrice(highestPrice.getValue());
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
        if (latestPrice != null) {
            return calculateGainFromPrice(latestPrice.getValue());
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
            BigDecimal gainPercentage = ((sold.subtract(bought)).divide(bought, HALF_UP)).multiply(new BigDecimal("100"));

            // Calculate fees.
            BigDecimal fees = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream())
                    .map(t -> t.getFee().getValue())
                    .reduce(ZERO, BigDecimal::add);

            // Return position gain.
            return GainDTO.builder()
                    .percentage(gainPercentage.setScale(2, HALF_UP).doubleValue())
                    .amount(CurrencyAmountDTO.builder()
                            .value(gainAmount)
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .fees(CurrencyAmountDTO.builder()
                            .value(fees)
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .build();
        } else {
            // No gain for the moment !
            return GainDTO.ZERO;
        }
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
                .append(this.status, that.status)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.rules, that.rules)
                .append(this.openingOrder, that.closingOrder)
                .append(this.lowestPrice, that.lowestPrice)
                .append(this.highestPrice, that.highestPrice)
                .append(this.latestPrice, that.latestPrice)
                .append(this.strategy.getId(), that.strategy.getId())
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(strategy.getId())
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
            String value = "Position n°" + id + " (rules : ";
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
                case CLOSING:
                    value += " on " + getCurrencyPair() + " - Closing - Waiting for the trades of order " + closingOrder.getOrderId();
                    break;
                case CLOSING_FAILURE:
                    value = "Position " + getId() + " - Closing failure";
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

}
