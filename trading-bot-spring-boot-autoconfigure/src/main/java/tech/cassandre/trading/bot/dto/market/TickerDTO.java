package tech.cassandre.trading.bot.dto.market;

import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * DTO representing a stock ticker.
 * A ticker is a report of the price of certain securities, updated continuously throughout the trading session.
 */
public final class TickerDTO {

    /** Currency pair. */
    private final CurrencyPairDTO currencyPair;

    /** The opening price is the first trade price that was recorded during the day’s trading. */
    private final BigDecimal open;

    /** Last trade field is the price set during the last trade. */
    private final BigDecimal last;

    /** The bid price shown represents the highest bid price. */
    private final BigDecimal bid;

    /** The ask price shown represents the lowest bid price. */
    private final BigDecimal ask;

    /** The day’s high price. */
    private final BigDecimal high;

    /** The day’s low price. */
    private final BigDecimal low;

    /** Volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day). */
    private final BigDecimal vwap;

    /** Volume is the number of shares or contracts traded. */
    private final BigDecimal volume;

    /** Quote volume. */
    private final BigDecimal quoteVolume;

    /** The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price. */
    private final BigDecimal bidSize;

    /** The ask size represents the quantity of a security that investors are willing to sell at a specified selling price. */
    private final BigDecimal askSize;

    /** Information timestamp. */
    private final ZonedDateTime timestamp;

    /**
     * Builder constructor.
     *
     * @param builder Builder.
     */
    protected TickerDTO(final TickerDTO.Builder builder) {
        this.currencyPair = builder.currencyPair;
        this.bidSize = builder.bidSize;
        this.ask = builder.ask;
        this.low = builder.low;
        this.volume = builder.volume;
        this.askSize = builder.askSize;
        this.bid = builder.bid;
        this.vwap = builder.vwap;
        this.open = builder.open;
        this.last = builder.last;
        this.high = builder.high;
        this.quoteVolume = builder.quoteVolume;
        if (builder.timestamp != null) {
            timestamp = ZonedDateTime.ofInstant(builder.timestamp.toInstant(), ZoneId.systemDefault());
        } else {
            timestamp = ZonedDateTime.now();
        }
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
     * Getter for currencyPair.
     *
     * @return currencyPair
     */
    public CurrencyPairDTO getCurrencyPair() {
        return currencyPair;
    }

    /**
     * Getter for open.
     *
     * @return open
     */
    public BigDecimal getOpen() {
        return open;
    }

    /**
     * Getter for last.
     *
     * @return last
     */
    public BigDecimal getLast() {
        return last;
    }

    /**
     * Getter for bid.
     *
     * @return bid
     */
    public BigDecimal getBid() {
        return bid;
    }

    /**
     * Getter for ask.
     *
     * @return ask
     */
    public BigDecimal getAsk() {
        return ask;
    }

    /**
     * Getter for high.
     *
     * @return high
     */
    public BigDecimal getHigh() {
        return high;
    }

    /**
     * Getter for low.
     *
     * @return low
     */
    public BigDecimal getLow() {
        return low;
    }

    /**
     * Getter for vwap.
     *
     * @return vwap
     */
    public BigDecimal getVwap() {
        return vwap;
    }

    /**
     * Getter for volume.
     *
     * @return volume
     */
    public BigDecimal getVolume() {
        return volume;
    }

    /**
     * Getter for quoteVolume.
     *
     * @return quoteVolume
     */
    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    /**
     * Getter for bidSize.
     *
     * @return bidSize
     */
    public BigDecimal getBidSize() {
        return bidSize;
    }

    /**
     * Getter for askSize.
     *
     * @return askSize
     */
    public BigDecimal getAskSize() {
        return askSize;
    }

    /**
     * Getter for timestamp.
     *
     * @return timestamp
     */
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TickerDTO tickerDTO = (TickerDTO) o;
        return Objects.equals(getCurrencyPair(), tickerDTO.getCurrencyPair())
                && getTimestamp().equals(tickerDTO.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrencyPair(), getTimestamp());
    }

    @Override
    public String toString() {
        return "TickerDTO{"
                + " currencyPair=" + currencyPair
                + ", open=" + open
                + ", last=" + last
                + ", bid=" + bid
                + ", ask=" + ask
                + ", high=" + high
                + ", low=" + low
                + ", vwap=" + vwap
                + ", volume=" + volume
                + ", quoteVolume=" + quoteVolume
                + ", bidSize=" + bidSize
                + ", askSize=" + askSize
                + ", timestamp=" + timestamp
                + '}';
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** To milliseconds. */
        public static final int MILLISECONDS = 1000;

        /** Currency pair. */
        private CurrencyPairDTO currencyPair;

        /** The opening price is the first trade price that was recorded during the day’s trading. */
        private BigDecimal open;

        /** Last trade field is the price set during the last trade. */
        private BigDecimal last;

        /** The bid price shown represents the highest bid price. */
        private BigDecimal bid;

        /** The ask price shown represents the lowest bid price. */
        private BigDecimal ask;

        /** The day’s high price. */
        private BigDecimal high;

        /** The day’s low price. */
        private BigDecimal low;

        /** Volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day). */
        private BigDecimal vwap;

        /** Volume is the number of shares or contracts traded. */
        private BigDecimal volume;

        /** Quote volume. */
        private BigDecimal quoteVolume;

        /** The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price. */
        private BigDecimal bidSize;

        /** The ask size represents the quantity of a security that investors are willing to sell at a specified selling price. */
        private BigDecimal askSize;

        /** Information timestamp. */
        private Date timestamp;

        /**
         * Currency pair.
         *
         * @param newCurrencyPair currency pair
         * @return builder
         */
        public Builder currencyPair(final CurrencyPairDTO newCurrencyPair) {
            this.currencyPair = newCurrencyPair;
            return this;
        }

        /**
         * open.
         *
         * @param newOpen open
         * @return builder
         */
        public Builder open(final BigDecimal newOpen) {
            this.open = newOpen;
            return this;
        }

        /**
         * open (with string).
         *
         * @param newOpen open
         * @return builder
         */
        public Builder open(final String newOpen) {
            this.open = toBigDecimal(newOpen);
            return this;
        }

        /**
         * last.
         *
         * @param newLast newLast
         * @return builder
         */
        public Builder last(final BigDecimal newLast) {
            this.last = newLast;
            return this;
        }

        /**
         * last (with string).
         *
         * @param newLast newLast
         * @return builder
         */
        public Builder last(final String newLast) {
            this.last = toBigDecimal(newLast);
            return this;
        }

        /**
         * bid.
         *
         * @param newBid newBid
         * @return builder
         */
        public Builder bid(final BigDecimal newBid) {
            this.bid = newBid;
            return this;
        }

        /**
         * bid (with string).
         *
         * @param newBid newBid
         * @return builder
         */
        public Builder bid(final String newBid) {
            this.bid = toBigDecimal(newBid);
            return this;
        }

        /**
         * ask.
         *
         * @param newAsk newAsk
         * @return builder
         */
        public Builder ask(final BigDecimal newAsk) {
            this.ask = newAsk;
            return this;
        }

        /**
         * ask (with string).
         *
         * @param newAsk newAsk
         * @return builder
         */
        public Builder ask(final String newAsk) {
            this.ask = toBigDecimal(newAsk);
            return this;
        }

        /**
         * high.
         *
         * @param newHigh newHigh
         * @return builder
         */
        public Builder high(final BigDecimal newHigh) {
            this.high = newHigh;
            return this;
        }

        /**
         * high (with string).
         *
         * @param newHigh newHigh
         * @return builder
         */
        public Builder high(final String newHigh) {
            this.high = toBigDecimal(newHigh);
            return this;
        }

        /**
         * low.
         *
         * @param newLow newLow
         * @return builder
         */
        public Builder low(final BigDecimal newLow) {
            this.low = newLow;
            return this;
        }

        /**
         * low (with string).
         *
         * @param newLow newLow
         * @return builder
         */
        public Builder low(final String newLow) {
            this.low = toBigDecimal(newLow);
            return this;
        }

        /**
         * vwap.
         *
         * @param newVwap newWwap
         * @return builder
         */
        public Builder vwap(final BigDecimal newVwap) {
            this.vwap = newVwap;
            return this;
        }

        /**
         * vwap (with string).
         *
         * @param newVwap newVwap
         * @return builder
         */
        public Builder vwap(final String newVwap) {
            this.vwap = toBigDecimal(newVwap);
            return this;
        }

        /**
         * volume.
         *
         * @param newVolume newVolume
         * @return builder
         */
        public Builder volume(final BigDecimal newVolume) {
            this.volume = newVolume;
            return this;
        }

        /**
         * volume (with string).
         *
         * @param newVolume newVolume
         * @return builder
         */
        public Builder volume(final String newVolume) {
            this.volume = toBigDecimal(newVolume);
            return this;
        }

        /**
         * quoteVolume.
         *
         * @param newQuoteVolume quoteVolume
         * @return builder
         */
        public Builder quoteVolume(final BigDecimal newQuoteVolume) {
            this.quoteVolume = newQuoteVolume;
            return this;
        }

        /**
         * quoteVolume (with string).
         *
         * @param newQuoteVolume quoteVolume
         * @return builder
         */
        public Builder quoteVolume(final String newQuoteVolume) {
            this.quoteVolume = toBigDecimal(newQuoteVolume);
            return this;
        }

        /**
         * bidSize.
         *
         * @param newBidSize bidSize
         * @return builder
         */
        public Builder bidSize(final BigDecimal newBidSize) {
            this.bidSize = newBidSize;
            return this;
        }


        /**
         * bidSize (with string).
         *
         * @param newBidSize bidSize
         * @return builder
         */
        public Builder bidSize(final String newBidSize) {
            this.bidSize = toBigDecimal(newBidSize);
            return this;
        }

        /**
         * askSize.
         *
         * @param newAskSize askSize
         * @return builder
         */
        public Builder askSize(final BigDecimal newAskSize) {
            this.askSize = newAskSize;
            return this;
        }

        /**
         * askSize (with string).
         *
         * @param newAskSize askSize
         * @return builder
         */
        public Builder askSize(final String newAskSize) {
            this.askSize = toBigDecimal(newAskSize);
            return this;
        }

        /**
         * timestamp (with Date).
         *
         * @param newTimestamp timestamp
         * @return builder
         */
        public Builder timestamp(final Date newTimestamp) {
            this.timestamp = newTimestamp;
            return this;
        }

        /**
         * timestamp (with epoch in seconds).
         *
         * @param newTimestamp timestamp
         * @return builder
         */
        public Builder timestampAsEpochInSeconds(final long newTimestamp) {
            this.timestamp = new Date(newTimestamp * MILLISECONDS);
            return this;
        }

        /**
         * Creates ticker.
         *
         * @return ticker
         */
        public TickerDTO create() {
            return new TickerDTO(this);
        }

        /**
         * Transforms a String in a BigDecimal.
         *
         * @param stringValue float value
         * @return big decimal value
         */
        private BigDecimal toBigDecimal(final String stringValue) {
            return new BigDecimal(stringValue);
        }

    }

}
