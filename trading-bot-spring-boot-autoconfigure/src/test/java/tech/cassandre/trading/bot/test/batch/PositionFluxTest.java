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
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableCassandreStrategy;

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
@DisplayName("Position flux")
public class PositionFluxTest extends BaseTest {

    /** Cassandre strategy. */
    @Autowired
    private TestableCassandreStrategy testableStrategy;

    /** Position service. */
    @Autowired
    private PositionService positionService;

    @Test
    @DisplayName("Received data")
    public void testReceivedData() {
        final int numberOfPositionExpected = 3;
        final int numberOfPositionServiceCalls = 4;

        // Waiting for the trade service to have been called with all the test data.
        await().untilAsserted(() -> verify(positionService, atLeast(numberOfPositionServiceCalls)).getPositions());

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(testableStrategy.getPositionsUpdateReceived().size() >= numberOfPositionExpected));

        // Test all values received.
        final Iterator<PositionDTO> iterator = testableStrategy.getPositionsUpdateReceived().iterator();
        assertEquals(1, iterator.next().getId());
        assertEquals(2, iterator.next().getId());
        assertEquals(3, iterator.next().getId());
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
         * Replace trade flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public TradeFlux tradeFlux() {
            return new TradeFlux(tradeService());
        }

        /**
         * Replace the flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public PositionFlux positionFlux() {
            return new PositionFlux(positionService());
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
        @Bean
        @Primary
        public TradeService tradeService() {
            // Creates the mock.
            return mock(TradeService.class);
        }

        /**
         * PositionService mock.
         *
         * @return mocked service
         */
        @SuppressWarnings("unchecked")
        @Bean
        @Primary
        public PositionService positionService() {
            // Creates the mock.
            final PositionRulesDTO noRules = PositionRulesDTO.builder().create();
            PositionService positionService = mock(PositionService.class);

            // Reply 1 : 2 positions.
            PositionDTO p1 = new PositionDTO(1, "O000001", noRules);
            PositionDTO p2 = new PositionDTO(2, "O000002", noRules);
            Set<PositionDTO> reply01 = new LinkedHashSet<>();
            reply01.add(p1);
            reply01.add(p2);

            // Reply 2 : 3 positions.
            Set<PositionDTO> reply02 = new LinkedHashSet<>();
            PositionDTO p3 = new PositionDTO(1, "O000001", noRules);
            PositionDTO p4 = new PositionDTO(2, "O000002", noRules);
            PositionDTO p5 = new PositionDTO(3, "O000003", noRules);
            reply02.add(p3);
            reply02.add(p4);
            reply02.add(p5);

            // Reply 2 : 2 positions.
            Set<PositionDTO> reply03 = new LinkedHashSet<>();
            PositionDTO p6 = new PositionDTO(1, "O000001", noRules);
            PositionDTO p7 = new PositionDTO(2, "O000001", noRules);
            reply03.add(p6);
            reply03.add(p7);

            given(positionService.getPositions())
                    .willReturn(reply01,
                            new LinkedHashSet<>(),
                            reply02,
                            reply03);
            return positionService;
        }

    }

}
