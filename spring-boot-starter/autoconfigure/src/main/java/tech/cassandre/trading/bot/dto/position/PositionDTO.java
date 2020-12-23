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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static lombok.AccessLevel.PRIVATE;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING_FAILURE;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING_FAILURE;
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
public class PositionDTO {

    /** An identifier that uniquely identifies the position. */
    private final long id;

    /** Position status. */
    private PositionStatusDTO status;

    /** Currency pair. */
    private final CurrencyPairDTO currencyPair;

    /** Amount ordered. */
    private final CurrencyAmountDTO amount;

    /** Position rules. */
    private final PositionRulesDTO rules;

    /** The order id that opened the position. */
    private OrderDTO openingOrder;

    /** The order id that closed the position. */
    private OrderDTO closingOrder;

    /** Lowest price for this position. */
    private CurrencyAmountDTO lowestPrice;

    /** Highest price for this position. */
    private CurrencyAmountDTO highestPrice;

    /** Latest price for this position. */
    private CurrencyAmountDTO latestPrice;

    /** Strategy. */
    private final StrategyDTO strategy;

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
        this.id = newId;
        this.strategy = newStrategy;
        this.currencyPair = newCurrencyPair;
        this.amount = new CurrencyAmountDTO(newAmount, newCurrencyPair.getBaseCurrency());
        this.openingOrder = newOpenOrder;
        this.rules = newRules;
    }

    /**
     * Constructor (prefers passing the order).
     * TODO Remove ?
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
        this.id = newId;
        this.strategy = newStrategy;
        this.currencyPair = newCurrencyPair;
        this.amount = new CurrencyAmountDTO(newAmount, newCurrencyPair.getBaseCurrency());
        // We create a temporary opening order.
        openingOrder = OrderDTO.builder()
                .id(newOpenOrderId)
                .type(BID)
                .currencyPair(currencyPair)
                .status(PENDING_NEW)
                .timestamp(ZonedDateTime.now())
                .build();
        this.rules = newRules;
    }

    /**
     * Update order.
     *
     * @param updatedOrder updated value
     * @return true if updated
     */
    public final boolean updateOrder(final OrderDTO updatedOrder) {
        if (openingOrder != null && openingOrder.getId().equals(updatedOrder.getId())) {
            this.openingOrder = updatedOrder;
            if (updatedOrder.getStatus().isInError()) {
                this.status = OPENING_FAILURE;

            }
            return true;
        }
        if (closingOrder != null && closingOrder.getId().equals(updatedOrder.getId())) {
            this.closingOrder = updatedOrder;
            if (updatedOrder.getStatus().isInError()) {
                this.status = CLOSING_FAILURE;
            }
            return true;
        }
        return false;
    }

    /**
     * Setter for closeOrderId.
     *
     * @param newCloseOrderId the closeOrderId to set
     */
    public final void setClosingOrderId(final String newCloseOrderId) {
        // This method should only be called when in status OPENED.
        if (status != OPENED) {
            throw new PositionException("Impossible to set close order id for position " + id);
        }
        status = CLOSING;
        // We create a temporary closing order.
        closingOrder = OrderDTO.builder()
                .id(newCloseOrderId)
                .timestamp(ZonedDateTime.now())
                .type(ASK)
                .currencyPair(currencyPair)
                .status(PENDING_NEW)
                .build();
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
            if (amount.getValue().compareTo(getTotalAmountFromOpeningTrades(trade)) == 0) {
                status = OPENED;
            }
        }

        // If status is CLOSING and the trades for the close order arrives for the whole amount ==> status = CLOSED.
        if (trade.getOrderId().equals(getClosingOrderId()) && status == CLOSING) {

            // We calculate the sum of amount in the all the trades.
            // If it reaches the original amount we order, we consider the trade opened.
            if (amount.getValue().compareTo(getTotalAmountFromClosingTrades(trade)) == 0) {
                status = CLOSED;
            }
        }

        // Return true signaling there is an update if this trade was for this position.
        return trade.getOrderId().equals(getOpeningOrderId()) || trade.getOrderId().equals(getClosingOrderId());
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
        if (getClosingOrderId() != null || !ticker.getCurrencyPair().equals(currencyPair)) {
            return false;
        } else {
            final Optional<GainDTO> gain = calculateGainFromPrice(ticker.getLast());
            if (gain.isPresent()) {
                // We save the last calculated gain.
                this.latestPrice = new CurrencyAmountDTO(ticker.getLast(), ticker.getCurrencyPair().getQuoteCurrency());
                if (rules.isStopGainPercentageSet() && gain.get().getPercentage() >= rules.getStopGainPercentage()
                        || rules.isStopLossPercentageSet() && gain.get().getPercentage() <= -rules.getStopLossPercentage()) {
                    // If the rules tells we should sell.
                    return true;
                } else {
                    // We check if this gain is at a new highest.
                    if (highestPrice == null) {
                        highestPrice = new CurrencyAmountDTO(ticker.getLast(), ticker.getCurrencyPair().getQuoteCurrency());
                    } else {
                        final Optional<GainDTO> highestGain = calculateGainFromPrice(highestPrice.getValue());
                        if (highestGain.isPresent() && highestGain.get().getPercentage() <= gain.get().getPercentage()) {
                            highestPrice = new CurrencyAmountDTO(ticker.getLast(), ticker.getCurrencyPair().getQuoteCurrency());
                        }
                    }
                    // We check if this gain is at a new lowest.
                    if (lowestPrice == null) {
                        lowestPrice = new CurrencyAmountDTO(ticker.getLast(), ticker.getCurrencyPair().getQuoteCurrency());
                    } else {
                        final Optional<GainDTO> lowestGain = calculateGainFromPrice(lowestPrice.getValue());
                        if (lowestGain.isPresent() && lowestGain.get().getPercentage() >= gain.get().getPercentage()) {
                            lowestPrice = new CurrencyAmountDTO(ticker.getLast(), ticker.getCurrencyPair().getQuoteCurrency());
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
            // We take the price from the first trade received.
            // TODO Use order price and then, then mean of all trades.
            final TradeDTO openTrade = openingOrder.getTrades().iterator().next();

            // How gain calculation works ?
            //  - Bought 10 ETH with a price of 5 -> Amount of 50.
            //  - Sold 10 ETH with a price of 6 -> Amount of 60.
            //  Gain = (6-5)/5 = 20%.
            float gainPercentage = (price.subtract(openTrade.getPrice().getValue()))
                    .divide(openTrade.getPrice().getValue(), BIGINTEGER_SCALE, RoundingMode.FLOOR)
                    .floatValue() * ONE_HUNDRED;
            BigDecimal gainAmount = ((openTrade.getAmount().getValue().multiply(price))
                    .subtract((openTrade.getAmount().getValue()).multiply(openTrade.getPrice().getValue())));

            GainDTO gain = new GainDTO(gainPercentage,
                    new CurrencyAmountDTO(gainAmount, currencyPair.getQuoteCurrency()),
                    new CurrencyAmountDTO(ZERO, currencyPair.getQuoteCurrency()));
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

            // - Bought 10 ETH with a price of 4 BTC -> costs 40 BTC.
            // - Bought 20 ETH with a price of 3 BTC -> costs 60 BTC.
            // - Sold 10 ETH with a price of 3 BTC -> earns 30 BTC.
            // - Sold 05 ETH with a price of 6 BTC -> earns 30 BTC
            // - Sold 15 ETH with a price of 6 BTC -> earns 90 BTC
            // ---
            // To start the position, I spent 100 BTC.
            // When I closed the position, I received 150 BTC
            // Gain  -> ((150 - 100) / 100) * 100 = 50 %
            BigDecimal bought = getOpeningTrades()
                    .stream()
                    .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                    .reduce(ZERO, BigDecimal::add);

            BigDecimal sold = getClosingTrades()
                    .stream()
                    .map(t -> t.getAmount().getValue().multiply(t.getPrice().getValue()))
                    .reduce(ZERO, BigDecimal::add);

            // Calculate gain.
            BigDecimal gainAmount = sold.subtract(bought);
            BigDecimal gainPercentage = ((sold.subtract(bought)).divide(bought, HALF_UP)).multiply(new BigDecimal("100"));

            // Return position gain.
            return new GainDTO(gainPercentage.setScale(2, HALF_UP).doubleValue(),
                    new CurrencyAmountDTO(gainAmount, currencyPair.getQuoteCurrency()),
                    new CurrencyAmountDTO(getTotalFees(), currencyPair.getQuoteCurrency()));
        } else {
            // No gain for the moment !
            return new GainDTO();
        }
    }

    /**
     * Get total fees from all trades.
     *
     * @return fees
     */
    private BigDecimal getTotalFees() {
        return Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream())
                .map(t -> t.getFee().getValue())
                .reduce(ZERO, BigDecimal::add);
    }

    /**
     * Returns the total amount from open trades.
     *
     * @param trade trade that just arrived
     * @return total
     */
    private BigDecimal getTotalAmountFromOpeningTrades(final TradeDTO trade) {
        return openingOrder.getTrades()
                .stream()
                .filter(t -> !t.getId().equals(trade.getId()))
                .map(t -> t.getAmount().getValue())
                .reduce(trade.getAmount().getValue(), BigDecimal::add);
    }

    /**
     * Returns the total amount from close trades.
     *
     * @param trade trade trade that just arrived
     * @return total
     */
    private BigDecimal getTotalAmountFromClosingTrades(final TradeDTO trade) {
        return closingOrder.getTrades()
                .stream()
                .filter(t -> !t.getId().equals(trade.getId()))
                .map(t -> t.getAmount().getValue())
                .reduce(trade.getAmount().getValue(), BigDecimal::add);
    }

    /**
     * Returns opening order id.
     *
     * @return opening order id
     */
    private String getOpeningOrderId() {
        if (openingOrder != null) {
            return openingOrder.getId();
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
            return closingOrder.getId();
        } else {
            return null;
        }
    }

    /**
     * Getter trades.
     *
     * @return trades
     */
    public final Set<TradeDTO> getTrades() {
        return Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Returns trade from its id.
     *
     * @param tradeId trade id
     * @return trade
     */
    public final Optional<TradeDTO> getTrade(final String tradeId) {
        if (tradeId == null) {
            return Optional.empty();
        } else {
            Stream<TradeDTO> trades = openingOrder.getTrades().stream();
            if (closingOrder != null) {
                trades = Stream.concat(openingOrder.getTrades().stream(), closingOrder.getTrades().stream());
            }
            return trades
                    .filter(t -> tradeId.equals(t.getId()))
                    .findFirst();
        }
    }

    /**
     * Getter openTrades.
     *
     * @return openTrades
     */
    public final Set<TradeDTO> getOpeningTrades() {
        // TODO Maybe we could only retrieve trades and not sorting them ?
        return openingOrder.getTrades()
                .stream()
                .sorted(Comparator.comparing(TradeDTO::getTimestamp, nullsLast(naturalOrder())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Getter closeTrades.
     *
     * @return closeTrades
     */
    public final Set<TradeDTO> getClosingTrades() {
        if (closingOrder != null) {
            // TODO Maybe we could only retrieve trades and not sorting them ?
            return closingOrder.getTrades()
                    .stream()
                    .sorted(Comparator.comparing(TradeDTO::getTimestamp, nullsLast(naturalOrder())))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            return Collections.emptySet();
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
    public final String description() {
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
                    value += " - Opening - Waiting for the trades of order " + openingOrder.getId();
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
                    value += " on " + getCurrencyPair() + " - Closing - Waiting for the trades of order " + closingOrder.getId();
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

}
