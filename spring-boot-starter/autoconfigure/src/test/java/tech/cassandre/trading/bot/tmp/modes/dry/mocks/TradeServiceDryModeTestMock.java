package tech.cassandre.trading.bot.tmp.modes.dry.mocks;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TradeServiceDryModeTestMock {

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(marketService());
    }

    @Bean
    @Primary
    public MarketService marketService() {
        // Creates the mock.
        MarketService marketService = mock(MarketService.class);

        // Replies for ETH / BTC.
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
        given(marketService
                .getTicker(cp1))
                .willReturn(Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(Calendar.getInstance().getTime()).last(new BigDecimal("0.2")).create())
                );
        return marketService;
    }

}
