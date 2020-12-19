package tech.cassandre.trading.bot.mock.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.intern.PositionServiceImplementation;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

@TestConfiguration
public class PositionFluxTestMock extends BaseTest {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private OrderRepository orderRepository;

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
        return new TradeFlux(tradeService(), orderRepository, tradeRepository);
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

        StrategyDTO strategy = StrategyDTO.builder().id("1").build();

        // Position 1 creation reply (ORDER00010) - used for max and min gain test.
        given(service.createBuyMarketOrder(strategy, cp1, new BigDecimal("10")))
                .willReturn(new OrderCreationResultDTO(getPendingOrder("ORDER00010", BID, new BigDecimal("10"), cp1)));
        // Position 1 closed reply (ORDER00011) - used for max and min gain test.
        given(service.createSellMarketOrder(strategy, cp1, new BigDecimal("10.00000000")))   // Was forced to do that as after going to database, we have a 10.00000000 value
                .willReturn(new OrderCreationResultDTO(getPendingOrder("ORDER00011", ASK, new BigDecimal("10.00000000"), cp1)));

        // Position 1 creation reply (order ORDER00010).
        given(service.createBuyMarketOrder(strategy, cp2, new BigDecimal("0.0001")))
                .willReturn(new OrderCreationResultDTO(getPendingOrder("ORDER00010", BID, new BigDecimal("0.0001"), cp2)));
        // Position 2 creation reply (order ORDER00020).
        given(service.createBuyMarketOrder(strategy, cp2, new BigDecimal("0.0002")))
                .willReturn(new OrderCreationResultDTO(getPendingOrder("ORDER00020", BID, new BigDecimal("0.0002"), cp2)));

        return service;
    }

}
