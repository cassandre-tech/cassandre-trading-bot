package tech.cassandre.trading.bot.test.util.junit;

import org.awaitility.Awaitility;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.mapper.AccountMapper;
import tech.cassandre.trading.bot.util.mapper.CurrencyMapper;
import tech.cassandre.trading.bot.util.mapper.OrderMapper;
import tech.cassandre.trading.bot.util.mapper.PositionMapper;
import tech.cassandre.trading.bot.util.mapper.StrategyMapper;
import tech.cassandre.trading.bot.util.mapper.TickerMapper;
import tech.cassandre.trading.bot.util.mapper.TradeMapper;
import tech.cassandre.trading.bot.util.mapper.UtilMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_STRATEGY;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for tests.
 */
public class BaseTest {

    /** Default strategy. */
    protected final StrategyDTO strategyDTO = StrategyDTO.builder()
            .id(1L)
            .strategyId("01")
            .type(BASIC_STRATEGY)
            .build();

    /** Logger. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Type mapper. */
    protected final UtilMapper utilMapper = Mappers.getMapper(UtilMapper.class);

    /** Currency mapper. */
    protected final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

    /** Strategy mapper. */
    protected final StrategyMapper strategyMapper = Mappers.getMapper(StrategyMapper.class);

    /** Account mapper. */
    protected final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    /** Ticker mapper. */
    protected final TickerMapper tickerMapper = Mappers.getMapper(TickerMapper.class);

    /** Order mapper. */
    protected final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    /** Trade mapper. */
    protected final TradeMapper tradeMapper = Mappers.getMapper(TradeMapper.class);

    /** Position mapper. */
    protected final PositionMapper positionMapper = Mappers.getMapper(PositionMapper.class);

    /** cp1 for tests. */
    protected final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    /** XChange cp1 for tests. */
    protected final CurrencyPair xChangeCP1 = new CurrencyPair(Currency.ETH, Currency.BTC);

    /** cp2 for tests. */
    protected final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);

    /** XChange cp2 for tests. */
    protected final CurrencyPair xChangeCP2 = new CurrencyPair(Currency.ETH, Currency.USDT);

    /** cp3 for tests. */
    protected final CurrencyPairDTO cp3 = new CurrencyPairDTO(BTC, USDT);

    /** XChange cp2 for tests. */
    protected final CurrencyPair xChangeCP3 = new CurrencyPair(Currency.BTC, Currency.USDT);

    /** cp4 for tests. */
    protected final CurrencyPairDTO cp4 = new CurrencyPairDTO(KCS, USDT);

    /** XChange cp4 for tests. */
    protected final CurrencyPair xChangeCP4 = new CurrencyPair(Currency.KCS, Currency.USDT);

    /** cp5 for tests. */
    protected final CurrencyPairDTO cp5 = new CurrencyPairDTO(BTC, ETH);

    /** XChange cp5 for tests. */
    protected final CurrencyPair xChangeCP5 = new CurrencyPair(Currency.BTC, Currency.ETH);

    /** Ten seconds wait. */
    protected static final long WAITING_TIME_IN_SECONDS = 5L;

    /** How much we should wait for tests until it is declared as failed. */
    protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 60;

    /**
     * Constructor.
     */
    public BaseTest() {
        // Default Configuration for Awaitility.
        Awaitility.setDefaultPollInterval(fibonacci(SECONDS));
        Awaitility.setDefaultTimeout(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS);
    }

    /**
     * Get pending order.
     *
     * @param strategy     strategy
     * @param orderId      orderId
     * @param orderTypeDTO order type
     * @param amount       amount
     * @param currencyPair currency pair
     * @return order
     */
    protected OrderDTO getPendingOrder(final StrategyDTO strategy,
                                       final String orderId,
                                       final OrderTypeDTO orderTypeDTO,
                                       final BigDecimal amount,
                                       final CurrencyPairDTO currencyPair) {
        return OrderDTO.builder()
                .orderId(orderId)
                .type(orderTypeDTO)
                .strategy(strategy)
                .currencyPair(currencyPair)
                .amount(new CurrencyAmountDTO(amount, currencyPair.getBaseCurrency()))
                .status(PENDING_NEW)
                .timestamp(ZonedDateTime.now())
                .build();
    }

    /**
     * Util method to return a fake ticker.
     *
     * @param cp  currency pair
     * @param bid bid price
     * @return ticket
     */
    protected static Optional<TickerDTO> getFakeTicker(final CurrencyPairDTO cp, final BigDecimal bid) {
        return Optional.of(TickerDTO.builder()
                .currencyPair(cp)
                .timestamp(getRandomDate())
                .last(bid)
                .build());
    }

    /**
     * Get random date.
     *
     * @return random date
     */
    public static ZonedDateTime getRandomDate() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long now = new Date().getTime();
        Date hundredYearsAgo = new Date(now - aDay * 365 * 100);
        Date tenDaysAgo = new Date(now - aDay * 10);
        long startMillis = hundredYearsAgo.getTime();
        long endMillis = tenDaysAgo.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(randomMillisSinceEpoch), ZoneId.systemDefault());
    }

    /**
     * Generate a date in 2020 with a day.
     *
     * @param day day
     * @return date
     */
    protected static Date createDate(final int day) {
        return Date.from(ZonedDateTime.of(2020, 1, day, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant());
    }

    /**
     * Generate a date in 2020 with a day.
     *
     * @param day day
     * @return date
     */
    protected static ZonedDateTime createZonedDateTime(final int day) {
        return ZonedDateTime.of(2020, 1, day, 0, 0, 0, 0, ZoneId.systemDefault());
    }

    /**
     * Generates a ZonedDateTime.
     *
     * @param date date with format dd-MM-yyyy
     * @return ZonedDateTime
     */
    protected static ZonedDateTime createZonedDateTime(final String date) {
        LocalDateTime ldt = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        return ldt.atZone(ZoneId.systemDefault());
    }

}
