package tech.cassandre.trading.bot.test.services.dry.mocks;

import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TradeServiceDryModeTestMock {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(applicationContext, marketService());
    }

    @Bean
    @Primary
    public MarketService marketService() {
        // Creates the mock.
        MarketService marketService = mock(MarketService.class);

        // We don't use the getTickers method.
        given(marketService.getTickers(any())).willThrow(new NotAvailableFromExchangeException("Not available in test"));

        // Replies for ETH / BTC.
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
        given(marketService
                .getTicker(cp1))
                .willReturn(Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(ZonedDateTime.now()).last(new BigDecimal("0.2")).build())
                );
        return marketService;
    }

}
