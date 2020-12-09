package tech.cassandre.trading.bot.dto.position;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.util.exception.PositionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
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
public class PositionDTO {

    /** An identifier that uniquely identifies the position. */
    private final long id;

    /** Position status. */
    private PositionStatusDTO status = OPENING;

    /** Currency pair. */
    private final CurrencyPairDTO currencyPair;

    /** Amount ordered. */
    private final BigDecimal amount;

    /** Position rules. */
    private final PositionRulesDTO rules;

    /** The order id that opened the position. */
    private OrderDTO openingOrder;

    /** The order id that closed the position. */
    private OrderDTO closingOrder;

    /** Lowest price for this position. */
    private BigDecimal lowestPrice;

    /** Highest price for this position. */
    private BigDecimal highestPrice;

    /** Latest price for this position. */
    private BigDecimal latestPrice;

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
        this.id = newId;
        this.strategy = newStrategy;
        this.currencyPair = newCurrencyPair;
        this.amount = newAmount;
        this.openingOrder = newOpenOrder;
        this.rules = newRules;
    }

    /**
     * Constructor (prefers passing the order).
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
        this.id = newId;
        this.strategy = newStrategy;
        this.currencyPair = newCurrencyPair;
        this.amount = newAmount;
        // We create a temporary opening order.
        openingOrder = OrderDTO.builder()
                .id(newOpenOrderId)
                .timestamp(ZonedDateTime.now())
                .type(BID)
                .currencyPair(currencyPair)
                .status(PENDING_NEW)
                .create();
        this.rules = newRules;
    }

    /**
     * Builder used for database restore.
     *
     * @param builder builder
     */
    protected PositionDTO(final PositionDTO.Builder builder) {
        this.id = builder.id;
        this.status = builder.status;
        this.currencyPair = builder.currencyPair;
        this.amount = builder.amount;
        PositionRulesDTO newRules = PositionRulesDTO.builder().create();
        boolean stopGainRuleSet = builder.stopGainPercentageRule != null;
        boolean stopLossRuleSet = builder.stopLossPercentageRule != null;
        // Two rules set.
        if (stopGainRuleSet && stopLossRuleSet) {
            newRules = PositionRulesDTO.builder()
                    .stopGainPercentage(builder.stopGainPercentageRule)
                    .stopLossPercentage(builder.stopLossPercentageRule)
                    .create();
        }
        // Stop gain set.
        if (stopGainRuleSet && !stopLossRuleSet) {
            newRules = PositionRulesDTO.builder()
                    .stopGainPercentage(builder.stopGainPercentageRule)
                    .create();
        }
        // Stop loss set.
        if (!stopGainRuleSet && stopLossRuleSet) {
            newRules = PositionRulesDTO.builder()
                    .stopLossPercentage(builder.stopLossPercentageRule)
                    .create();
        }
        this.rules = newRules;
        this.openingOrder = builder.openingOrder;
        this.closingOrder = builder.closingOrder;
        this.lowestPrice = builder.lowestPrice;
        this.highestPrice = builder.highestPrice;
        this.latestPrice = builder.latestPrice;
        this.strategy = builder.strategy;
    }

    /**
     * Returns builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
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
                .create();
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
            if (amount.compareTo(getTotalAmountFromOpeningTrades(trade)) == 0) {
                status = OPENED;
            }
        }

        // If status is CLOSING and the trades for the close order arrives for the whole amount ==> status = CLOSED.
        if (trade.getOrderId().equals(getClosingOrderId()) && status == CLOSING) {

            // We calculate the sum of amount in the all the trades.
            // If it reaches the original amount we order, we consider the trade opened.
            if (amount.compareTo(getTotalAmountFromClosingTrades(trade)) == 0) {
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
                this.latestPrice = ticker.getLast();
                if (rules.isStopGainPercentageSet() && gain.get().getPercentage() >= rules.getStopGainPercentage()
                        || rules.isStopLossPercentageSet() && gain.get().getPercentage() <= -rules.getStopLossPercentage()) {
                    // If the rules tells we should sell.
                    return true;
                } else {
                    // We check if this gain is at a new highest.
                    if (highestPrice == null) {
                        highestPrice = ticker.getLast();
                    } else {
                        final Optional<GainDTO> highestGain = calculateGainFromPrice(highestPrice);
                        if (highestGain.isPresent() && highestGain.get().getPercentage() <= gain.get().getPercentage()) {
                            highestPrice = ticker.getLast();
                        }
                    }
                    // We check if this gain is at a new lowest.
                    if (lowestPrice == null) {
                        lowestPrice = ticker.getLast();
                    } else {
                        final Optional<GainDTO> lowestGain = calculateGainFromPrice(lowestPrice);
                        if (lowestGain.isPresent() && lowestGain.get().getPercentage() >= gain.get().getPercentage()) {
                            lowestPrice = ticker.getLast();
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
            float gainPercentage = (price.subtract(openTrade.getPrice()))
                    .divide(openTrade.getPrice(), BIGINTEGER_SCALE, RoundingMode.FLOOR)
                    .floatValue() * ONE_HUNDRED;
            BigDecimal gainAmount = ((openTrade.getOriginalAmount().multiply(price))
                    .subtract((openTrade.getOriginalAmount()).multiply(openTrade.getPrice())));

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
                    .map(t -> t.getOriginalAmount().multiply(t.getPrice()))
                    .reduce(ZERO, BigDecimal::add);

            BigDecimal sold = getClosingTrades()
                    .stream()
                    .map(t -> t.getOriginalAmount().multiply(t.getPrice()))
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
                .map(TradeDTO::getOriginalAmount)
                .reduce(trade.getOriginalAmount(), BigDecimal::add);
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
                .map(TradeDTO::getOriginalAmount)
                .reduce(trade.getOriginalAmount(), BigDecimal::add);
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
        return currencyPair;
    }

    /**
     * Getter amount.
     *
     * @return amount
     */
    public final BigDecimal getAmount() {
        return amount;
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
     * Getter openingOrder.
     *
     * @return openingOrder
     */
    public final OrderDTO getOpeningOrder() {
        return openingOrder;
    }

    /**
     * Getter closingOrder.
     *
     * @return closingOrder
     */
    public final OrderDTO getClosingOrder() {
        return closingOrder;
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
     * Getter latestPrice.
     *
     * @return latestPrice
     */
    public final BigDecimal getLatestPrice() {
        return latestPrice;
    }

    /**
     * Getter strategy.
     *
     * @return strategy
     */
    public final StrategyDTO getStrategy() {
        return strategy;
    }

    /**
     * Getter last calculated gain from the last ticker received.
     *
     * @return lastCalculatedGain
     */
    @Deprecated(since = "2.4", forRemoval = true)
    public final Optional<GainDTO> getLastCalculatedGain() {
        return calculateGainFromPrice(latestPrice);
    }

    /**
     * Getter latestCalculatedGain.
     *
     * @return latestCalculatedGain
     */
    public final Optional<GainDTO> getLatestCalculatedGain() {
        return calculateGainFromPrice(latestPrice);
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
            return "Position " + getId();
        }
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** An identifier that uniquely identifies the position. */
        private long id;

        /** Position status. */
        private PositionStatusDTO status = OPENING;

        /** Currency pair. */
        private CurrencyPairDTO currencyPair;

        /** Amount ordered. */
        private BigDecimal amount;

        /** Stop gain percentage rule. */
        private Float stopGainPercentageRule;

        /** Stop loss percentage rule. */
        private Float stopLossPercentageRule;

        /** The order that opened the position. */
        private OrderDTO openingOrder;

        /** The order that closed the position. */
        private OrderDTO closingOrder;

        /** Lowest price for this position. */
        private BigDecimal lowestPrice;

        /** Highest price for this position. */
        private BigDecimal highestPrice;

        /** Latest price for this position. */
        private BigDecimal latestPrice;

        /** Strategy. */
        private StrategyDTO strategy;

        /**
         * id.
         *
         * @param newId type
         * @return builder
         */
        public Builder id(final long newId) {
            this.id = newId;
            return this;
        }

        /**
         * status.
         *
         * @param newStatus status
         * @return builder
         */
        public Builder status(final PositionStatusDTO newStatus) {
            this.status = newStatus;
            return this;
        }

        /**
         * currency pair.
         *
         * @param newCurrencyPair currency pair
         * @return builder
         */
        public Builder currencyPair(final CurrencyPairDTO newCurrencyPair) {
            this.currencyPair = newCurrencyPair;
            return this;
        }

        /**
         * amount.
         *
         * @param newAmount amount
         * @return builder
         */
        public Builder amount(final BigDecimal newAmount) {
            this.amount = newAmount;
            return this;
        }

        /**
         * stopGainPercentageRule.
         *
         * @param newStopGainPercentageRule newStopGainPercentageRule
         * @return builder
         */
        public Builder stopGainPercentageRule(final Float newStopGainPercentageRule) {
            this.stopGainPercentageRule = newStopGainPercentageRule;
            return this;
        }

        /**
         * stopLossPercentageRule.
         *
         * @param newStopLossPercentageRule stopLossPercentageRule
         * @return builder
         */
        public Builder stopLossPercentageRule(final Float newStopLossPercentageRule) {
            this.stopLossPercentageRule = newStopLossPercentageRule;
            return this;
        }

        /**
         * openingOrder.
         *
         * @param newOpeningOrder openingOrder
         * @return builder
         */
        public Builder openingOrder(final OrderDTO newOpeningOrder) {
            this.openingOrder = newOpeningOrder;
            return this;
        }

        /**
         * closingOrder.
         *
         * @param newClosingOrder closingOrder
         * @return builder
         */
        public Builder closingOrder(final OrderDTO newClosingOrder) {
            this.closingOrder = newClosingOrder;
            return this;
        }

        /**
         * lowestPrice.
         *
         * @param newLowestPrice lowestPrice
         * @return builder
         */
        public Builder lowestPrice(final BigDecimal newLowestPrice) {
            this.lowestPrice = newLowestPrice;
            return this;
        }

        /**
         * highestPrice.
         *
         * @param newHighestPrice highestPrice
         * @return builder
         */
        public Builder highestPrice(final BigDecimal newHighestPrice) {
            this.highestPrice = newHighestPrice;
            return this;
        }

        /**
         * latestPrice.
         *
         * @param newLatestPrice latestPrice
         * @return builder
         */
        public Builder latestPrice(final BigDecimal newLatestPrice) {
            // TODO Why do i have a warning in here ???
            this.latestPrice = newLatestPrice;
            return this;
        }

        /**
         * Strategy.
         *
         * @param newStrategy strategy
         * @return builder
         */
        public Builder strategy(final StrategyDTO newStrategy) {
            this.strategy = newStrategy;
            return this;
        }

        /**
         * Creates order.
         *
         * @return order
         */
        public PositionDTO create() {
            return new PositionDTO(this);
        }

    }

}
