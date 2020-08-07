package tech.cassandre.trading.bot.test.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.PositionServiceImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Flux and services mocks.
 */
@TestConfiguration
public class PositionServiceTestMock {

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
        given(service.createBuyMarketOrder(PositionServiceTest.cp1, new BigDecimal("0.0001")))
                .willReturn(new OrderCreationResultDTO("ORDER00010"));

        // Position 2 creation reply (order ORDER00020).
        given(service.createBuyMarketOrder(PositionServiceTest.cp2, new BigDecimal("0.0002")))
                .willReturn(new OrderCreationResultDTO("ORDER00020"));

        // Position 3 creation reply (order ORDER00030).
        given(service.createBuyMarketOrder(PositionServiceTest.cp1, new BigDecimal("0.0003")))
                .willReturn(new OrderCreationResultDTO("Error message", new RuntimeException("Error exception")));

        // Position 1 closed reply (ORDER00011).
        given(service.createSellMarketOrder(PositionServiceTest.cp1, new BigDecimal("0.0001")))
                .willReturn(new OrderCreationResultDTO("ORDER00011"));

        return service;
    }

}
