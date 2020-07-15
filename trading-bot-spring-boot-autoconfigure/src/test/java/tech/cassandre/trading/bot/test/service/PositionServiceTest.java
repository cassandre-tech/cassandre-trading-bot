package tech.cassandre.trading.bot.test.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.PositionServiceImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ORDER_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USD;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ORDER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

/**
 * Position test.
 */
@SetSystemProperty(key = PARAMETER_NAME, value = PARAMETER_NAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SANDBOX, value = PARAMETER_SANDBOX_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_USERNAME, value = PARAMETER_USERNAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_PASSPHRASE, value = PARAMETER_PASSPHRASE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_KEY, value = PARAMETER_KEY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SECRET, value = PARAMETER_SECRET_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_ORDER_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
@DisplayName("Position tests")
public class PositionServiceTest extends BaseTest {

    /** Position service. */
    @Autowired
    private PositionService positionService;

    /** Trade flux. */
    @Autowired
    private TradeFlux tradeFlux;

    /** Ticker flux. */
    @Autowired
    private TickerFlux tickerFlux;

    /** Currency pair 1 used for test. */
    private static final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    /** Currency pair 1 used for test. */
    private static final CurrencyPairDTO cp2 = new CurrencyPairDTO(USD, BTC);

    @Test
    @DisplayName("Position creation")
    public void createPositionTest() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10).create());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPositionId());
        assertEquals("ORDER00010", p1.getOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20).create());
        assertTrue(p2.isSuccessful());
        assertEquals(2, p2.getPositionId());
        assertEquals("ORDER00020", p2.getOrderId());
        assertNull(p2.getErrorMessage());
        assertNull(p2.getException());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        final PositionCreationResultDTO p3 = positionService.createPosition(cp1,
                new BigDecimal("0.0003"),
                PositionRulesDTO.builder().stopGainPercentage(30).stopLossPercentage(30).create());
        assertFalse(p3.isSuccessful());
        assertNull(p3.getPositionId());
        assertNull(p3.getOrderId());
        assertEquals("Error message", p3.getErrorMessage());
        assertEquals("Error exception", p3.getException().getMessage());
        assertEquals(2, positionService.getPositions().size());
    }

    @Test
    @DisplayName("get positions and get positions by id")
    public void getPositionTest() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10).create());
        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20).create());
        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        positionService.createPosition(cp1,
                new BigDecimal("0.0003"),
                PositionRulesDTO.builder().stopGainPercentage(30).stopLossPercentage(30).create());

        // Tests
        assertEquals(2, positionService.getPositions().size());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(1, positionService.getPositionById(1).get().getId());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(2, positionService.getPositionById(2).get().getId());
        assertFalse(positionService.getPositionById(3).isPresent());
    }

    @Test
    @DisplayName("Trade update")
    public void tradeUpdateTest() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10).create());
        assertEquals("ORDER00010", p1.getOrderId());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20).create());
        assertEquals("ORDER00020", p2.getOrderId());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Trade 1 - should not change anything.
        tradeFlux.emitValue(TradeDTO.builder().id("000001").orderId("ORDER00001").create());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // Trade 2 - should change status of position 1.
        tradeFlux.emitValue(TradeDTO.builder().id("000002").orderId("ORDER00010").create());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(1).get().getStatus()));
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Trade 3 - should change status of position 2.
        tradeFlux.emitValue(TradeDTO.builder().id("000002").orderId("ORDER00020").create());
        assertEquals(OPENED, positionService.getPositionById(1).get().getStatus());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(2).get().getStatus()));
    }

    @Test
    @DisplayName("Close position")
    public void closePositionTest() throws InterruptedException {
        // Creates position 1 (ETH/BTC, 0.0001, 100% stop gain).
        positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100).create());

        // The open trade arrives, change the status and set the price.
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("ORDER00010")
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("0.0001"))
                .price(new BigDecimal("0.2"))
                .create());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(1).get().getStatus()));

        // A first ticker arrives with a gain of 100% but for the wrong CP.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp2).ask(new BigDecimal("0.5")).create());
        Thread.sleep(FIVE_SECOND);
        assertEquals(OPENED, positionService.getPositionById(1).get().getStatus());

        // A second ticker arrives with a gain of 25%.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).ask(new BigDecimal("0.3")).create());
        Thread.sleep(FIVE_SECOND);
        assertEquals(OPENED, positionService.getPositionById(1).get().getStatus());

        // A third ticker arrives with a gain of 100%.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).ask(new BigDecimal("0.5")).create());
        Thread.sleep(FIVE_SECOND);
        assertEquals(CLOSING, positionService.getPositionById(1).get().getStatus());

        // The close trade arrives, change the status and set the price.
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("ORDER00011")
                .currencyPair(cp1)
                .create());
        await().untilAsserted(() -> assertEquals(CLOSED, positionService.getPositionById(1).get().getStatus()));
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
         * Replace position service with a
         * @return mock
         */
        @Bean
        @Primary
        public PositionService positionService() {
            return new PositionServiceImplementation(tradeService());
        }

        /**
         * UserService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public UserService userService() {
            return mock(UserService.class);
        }

        /**
         * MarketService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public MarketService marketService() {
            return mock(MarketService.class);
        }

        /**
         * TradeService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public TradeService tradeService() {
            TradeService service = mock(TradeService.class);

            // Position 1 creation reply (order ORDER00010).
            given(service.createBuyMarketOrder(cp1, new BigDecimal("0.0001")))
                    .willReturn(new OrderCreationResultDTO("ORDER00010"));

            // Position 2 creation reply (order ORDER00020).
            given(service.createBuyMarketOrder(cp2, new BigDecimal("0.0002")))
                    .willReturn(new OrderCreationResultDTO("ORDER00020"));

            // Position 3 creation reply (order ORDER00030).
            given(service.createBuyMarketOrder(cp1, new BigDecimal("0.0003")))
                    .willReturn(new OrderCreationResultDTO("Error message", new RuntimeException("Error exception")));

            // Position 1 closed reply (ORDER00011).
            given(service.createSellMarketOrder(cp1, new BigDecimal("0.0001")))
                    .willReturn(new OrderCreationResultDTO("ORDER00011"));

            return service;
        }

    }

}
