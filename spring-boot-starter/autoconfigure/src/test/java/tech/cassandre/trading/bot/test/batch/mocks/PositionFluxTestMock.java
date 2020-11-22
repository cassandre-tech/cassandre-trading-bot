package tech.cassandre.trading.bot.test.batch.mocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.intern.PositionServiceImplementation;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class PositionFluxTestMock extends BaseTest {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(marketService());
    }

    @Bean
    @Primary
    public TradeFlux tradeFlux() {
        return new TradeFlux(tradeService(), tradeRepository);
    }

    @Bean
    @Primary
    public PositionFlux positionFlux() {
        return new PositionFlux(positionRepository);
    }

    @Bean
    @Primary
    public PositionService positionService() {
        return new PositionServiceImplementation(tradeService(), positionRepository, positionFlux());
    }

    @Bean
    @Primary
    public MarketService marketService() {
        return mock(MarketService.class);
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        TradeService service = mock(TradeService.class);

        // Position 1 creation reply (ORDER00010) - used for max and min gain test.
        given(service.createBuyMarketOrder(cp1, new BigDecimal("10")))
                .willReturn(new OrderCreationResultDTO("ORDER00010"));
        // Position 1 closed reply (ORDER00011) - used for max and min gain test.
        given(service.createSellMarketOrder(cp1, new BigDecimal("10.00000000")))   // Was forced to do that as after going to database, we have a 10.00000000 value
                .willReturn(new OrderCreationResultDTO("ORDER00011"));

        // Position 1 creation reply (order ORDER00010).
        given(service.createBuyMarketOrder(cp2, new BigDecimal("0.0001")))
                .willReturn(new OrderCreationResultDTO("ORDER00010"));
        // Position 2 creation reply (order ORDER00020).
        given(service.createBuyMarketOrder(cp2, new BigDecimal("0.0002")))
                .willReturn(new OrderCreationResultDTO("ORDER00020"));

        return service;
    }

}
