package tech.cassandre.trading.bot.test.batch;

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

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Flux and services mocks.
 */
@TestConfiguration
public class PositionFluxTestMock {

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
                        reply02,
                        reply03);
        return positionService;
    }

}
