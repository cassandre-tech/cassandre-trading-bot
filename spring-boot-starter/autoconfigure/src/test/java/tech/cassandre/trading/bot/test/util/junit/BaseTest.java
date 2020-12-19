package tech.cassandre.trading.bot.test.util.junit;

import org.awaitility.Awaitility;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.mapper.CassandreMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for tests.
 */
public class BaseTest {

    /** cp1 for tests. */
    protected final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    /** cp2 for tests. */
    protected final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);

    /** cp3 for tests. */
    protected final CurrencyPairDTO cp3 = new CurrencyPairDTO(BTC, USDT);

    /** Ten seconds wait. */
    protected static final long WAITING_TIME_IN_SECONDS = 5L;

    /** How much we should wait for tests until it is declared as failed. */
    protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 30;

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Constructor.
     */
    public BaseTest() {
        // Default Configuration for Awaitility.
        Awaitility.setDefaultPollInterval(fibonacci(SECONDS));
        Awaitility.setDefaultTimeout(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS);
    }

    /**
     * Getter logger.
     *
     * @return logger
     */
    protected final Logger getLogger() {
        return logger;
    }

    /**
     * Get pending order.
     *
     * @param orderId      orderId
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @return order
     */
    protected OrderDTO getPendingOrder(final String orderId,
                                       final OrderTypeDTO orderTypeDTO,
                                       final BigDecimal amount,
                                       final CurrencyPairDTO currencyPair) {
        return OrderDTO.builder()
                .id(orderId)
                .timestamp(ZonedDateTime.now())
                .type(orderTypeDTO)
                .originalAmount(amount)
                .currencyPair(currencyPair)
                .status(PENDING_NEW)
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
                .bid(bid)
                .last(bid)
                .build());
    }

    /**
     * Util method to return a fake ticker with date.
     *
     * @param date date
     * @param cp   currency pair
     * @param bid  bid price
     * @return ticket
     */
    protected static Optional<TickerDTO> getFakeTicker(final ZonedDateTime date, final CurrencyPairDTO cp, final BigDecimal bid) {
        return Optional.of(TickerDTO.builder()
                .currencyPair(cp)
                .timestamp(date)
                .bid(bid)
                .last(bid)
                .build());
    }

    /**
     * Util method to return a fake ticker with date.
     *
     * @param date date with format dd-MM-yyyy
     * @param cp   currency pair
     * @param bid  bid price
     * @return ticket
     */
    protected static Optional<TickerDTO> getFakeTicker(final String date, final CurrencyPairDTO cp, final BigDecimal bid) {
        return Optional.of(TickerDTO.builder()
                .currencyPair(cp)
                .timestamp(createZonedDateTime(date))
                .bid(bid)
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
     * Get exception message from parameter exception.
     *
     * @param e exception
     * @return message
     */
    protected String getParametersExceptionMessage(Exception e) {
        return e.getCause().getCause().getCause().getMessage();
    }


    /**
     * Generate a date in 2020 with a day.
     *
     * @param day day
     * @return date
     */
    protected static ZonedDateTime createDate(final int day) {
        return ZonedDateTime.of(2020, 1, day, 9, 0, 0, 0, ZoneId.systemDefault());
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
