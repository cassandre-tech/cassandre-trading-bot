package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ORDER_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ORDER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

@SetSystemProperty(key = PARAMETER_NAME, value = PARAMETER_NAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SANDBOX, value = PARAMETER_SANDBOX_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_USERNAME, value = PARAMETER_USERNAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_PASSPHRASE, value = PARAMETER_PASSPHRASE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_KEY, value = PARAMETER_KEY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SECRET, value = PARAMETER_SECRET_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_ORDER_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("Order flux")
public class OrderFluxTest extends BaseTest {

    /** Cassandre strategy. */
    @Autowired
    private TestableCassandreStrategy testableStrategy;

    /** Trade service. */
    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Received data")
    public void testReceivedData() {
        final int numberOfOrdersExpected = 7;
        final int numberOfTradeServiceCalls = 3;

        // Waiting for the trade service to have been called with all the test data.
        await().untilAsserted(() -> verify(tradeService, atLeast(numberOfTradeServiceCalls)).getOpenOrders());

        // Checking that somme tickers have already been treated (to verify we work on a single thread).
        assertTrue(testableStrategy.getOrdersUpdateReceived().size() < numberOfOrdersExpected);
        assertTrue(testableStrategy.getOrdersUpdateReceived().size() > 0);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(testableStrategy.getOrdersUpdateReceived().size() >= numberOfOrdersExpected));

        // Test all values received.
        final Iterator<OrderDTO> iterator = testableStrategy.getOrdersUpdateReceived().iterator();

        // Value 1.
        OrderDTO order = iterator.next();
        assertEquals("000001", order.getId());

        // Value 2.
        order = iterator.next();
        assertEquals("000002", order.getId());

        // Value 3.
        order = iterator.next();
        assertEquals("000003", order.getId());

        // Value 3 : the original amount changed.
        order = iterator.next();
        assertEquals("000003", order.getId());
        assertEquals(new BigDecimal(2), order.getOriginalAmount());

        // Value 4 : new order.
        order = iterator.next();
        assertEquals("000004", order.getId());

        // Value 5 : average price changed.
        order = iterator.next();
        assertEquals("000002", order.getId());
        assertEquals(new BigDecimal(1), order.getAveragePrice());

        // Value 6 : fee changed.
        order = iterator.next();
        assertEquals("000004", order.getId());
        assertEquals(new BigDecimal(1), order.getFee());
    }

    /**
     * Change configuration to integrate mocks.
     */
    @TestConfiguration
    public static class TestConfig {

        /**
         * Replace ticker flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public TickerFlux tickerFlux() {
            return new TickerFlux(marketService());
        }

        /**
         * Replace account flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public AccountFlux accountFlux() {
            return new AccountFlux(userService());
        }

        /**
         * Replace order flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public OrderFlux orderFlux() {
            return new OrderFlux(tradeService());
        }

        /**
         * UserService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public UserService userService() {
            UserService service = mock(UserService.class);
            given(service.getUser()).willReturn(Optional.empty());
            return service;
        }

        /**
         * MarketService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public MarketService marketService() {
            MarketService service = mock(MarketService.class);
            given(service.getTicker(any())).willReturn(Optional.empty());
            return service;
        }

        /**
         * TradeService mock.
         *
         * @return mocked service
         */
        @SuppressWarnings("unchecked")
        @Bean
        @Primary
        public TradeService tradeService() {
            // Creates the mock.
            TradeService tradeService = mock(TradeService.class);
            final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);

            // =========================================================================================================
            // First reply : 3 orders.

            // Order 000001.
            OrderDTO order01 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000001")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000002.
            OrderDTO order02 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000002")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000003.
            OrderDTO order03 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000003")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            Set<OrderDTO> reply01 = new LinkedHashSet<>();
            reply01.add(order01);
            reply01.add(order02);
            reply01.add(order03);

            // =========================================================================================================
            // Second reply.
            // Order 000003 : the original amount changed.
            // Order 000004 : new order.

            // Order 000001.
            OrderDTO order04 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000001")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000002.
            OrderDTO order05 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000002")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000003 : the original amount changed.
            OrderDTO order06 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(2))
                    .currencyPair(cp1)
                    .id("000003")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000004 : new order.
            OrderDTO order07 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000004")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            Set<OrderDTO> reply02 = new LinkedHashSet<>();
            reply02.add(order04);
            reply02.add(order05);
            reply02.add(order06);
            reply02.add(order07);

            // =========================================================================================================
            // Second reply.
            // Order 000002 : average prince changed.
            // Order 000004 : fee changed.

            // Order 000001.
            OrderDTO order08 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000001")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000002 : average price changed.
            OrderDTO order09 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000002")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(1))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000003.
            OrderDTO order10 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(2))
                    .currencyPair(cp1)
                    .id("000003")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(4))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            // Order 000004 : fee changed.
            OrderDTO order11 = OrderDTO.builder()
                    .type(OrderTypeDTO.ASK)
                    .originalAmount(new BigDecimal(1))
                    .currencyPair(cp1)
                    .id("000004")
                    .userReference("MY_REF_1")
                    .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                    .status(OrderStatusDTO.NEW)
                    .cumulativeAmount(new BigDecimal(2))
                    .averagePrice(new BigDecimal(3))
                    .fee(new BigDecimal(1))
                    .leverage("leverage1")
                    .limitPrice(new BigDecimal(5))
                    .create();

            Set<OrderDTO> reply03 = new LinkedHashSet<>();
            reply03.add(order08);
            reply03.add(order09);
            reply03.add(order10);
            reply03.add(order11);

            // Creating the mock.
            given(tradeService.getOpenOrders())
                    .willReturn(reply01,
                            new LinkedHashSet<>(),
                            reply02,
                            reply03);
            return tradeService;
        }

    }

}
