package tech.cassandre.trading.bot.test.util.junit;

import org.awaitility.Awaitility;
import org.junit.ClassRule;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = BaseDbTest.DockerPostgreDataSourceInitializer.class)
public class BaseDbTest {

    @ClassRule
    @Container
    public static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:9.4");

    public static class DockerPostgreDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "cassandre.trading.bot.database.datasource.url=" + database.getJdbcUrl(),
                    "cassandre.trading.bot.database.datasource.driver-class-name=" + database.getDriverClassName(),
                    "cassandre.trading.bot.database.datasource.username=" + database.getUsername(),
                    "cassandre.trading.bot.database.datasource.password=" + database.getPassword()
            );
        }
    }

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

    /** ETH/BTC. */
    public static final CurrencyPairDTO ETH_BTC = new CurrencyPairDTO(ETH, BTC);

    /** XChange ETH/BTC. */
    public static final CurrencyPair XCHANGE_ETH_BTC = new CurrencyPair(Currency.ETH, Currency.BTC);

    /** ETH/USDT. */
    public static final CurrencyPairDTO ETH_USDT = new CurrencyPairDTO(ETH, USDT);

    /** XChange ETH/USDT. */
    public static final CurrencyPair XCHANGE_ETH_USDT = new CurrencyPair(Currency.ETH, Currency.USDT);

    /** BTC/USDT. */
    public static final CurrencyPairDTO BTC_USDT = new CurrencyPairDTO(BTC, USDT);

    /** XChange BTC/USDT. */
    public static final CurrencyPair XCHANGE_BTC_USDT = new CurrencyPair(Currency.BTC, Currency.USDT);

    /** KCS/USDT. */
    public static final CurrencyPairDTO KCS_USDT = new CurrencyPairDTO(KCS, USDT);

    /** XChange KCS/USDT. */
    public static final CurrencyPair XCHANGE_KCS_USDT = new CurrencyPair(Currency.KCS, Currency.USDT);

    /** BTC/ETH. */
    public static final CurrencyPairDTO BTC_ETH = new CurrencyPairDTO(BTC, ETH);

    /** XChange BTC/ETH. */
    public static final CurrencyPair XCHANGE_BTC_ETH = new CurrencyPair(Currency.BTC, Currency.ETH);

    /** Ten seconds wait. */
    protected static final long WAITING_TIME_IN_SECONDS = 5L;

    /** How much we should wait for tests until it is declared as failed. */
    protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 60;

    /**
     * Constructor.
     */
    public BaseDbTest() {
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
