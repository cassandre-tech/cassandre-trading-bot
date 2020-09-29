package tech.cassandre.trading.bot.test.backup.mocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.intern.PositionServiceImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.backup.PositionBackupTest;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class PositionBackupMock {

    @Autowired
    private PositionRepository positionRepository;

    @Bean
    @Primary
    public PositionService positionService() {
        return new PositionServiceImplementation(tradeService(), positionRepository);
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        TradeService service = mock(TradeService.class);

        // Position 1 creation reply (order ORDER00010).
        given(service.createBuyMarketOrder(PositionBackupTest.cp, new BigDecimal("0.0001")))
                .willReturn(new OrderCreationResultDTO("ORDER00010"));

        // Position 2 creation reply (order ORDER00020).
        given(service.createBuyMarketOrder(PositionBackupTest.cp, new BigDecimal("0.0002")))
                .willReturn(new OrderCreationResultDTO("ORDER00020"));

        return service;
    }

}
