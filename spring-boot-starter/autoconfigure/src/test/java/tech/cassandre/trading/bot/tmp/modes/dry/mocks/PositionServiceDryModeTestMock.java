package tech.cassandre.trading.bot.tmp.modes.dry.mocks;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SuppressWarnings("unchecked")
@TestConfiguration
public class PositionServiceDryModeTestMock extends BaseTest {

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
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);
        given(marketService
                .getTicker(cp1))
                .willReturn(
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(1)).last(new BigDecimal("0.2")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(2)).last(new BigDecimal("0.3")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(3)).last(new BigDecimal("0.4")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(4)).last(new BigDecimal("0.4")).create())
                );
        // Replies for ETH / USDT.
        final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);
        given(marketService
                .getTicker(cp2))
                .willReturn(
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDate(5)).last(new BigDecimal("0.3")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDate(6)).last(new BigDecimal("0.3")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDate(7)).last(new BigDecimal("0.6")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDate(8)).last(new BigDecimal("0.1")).create())
                );
        return marketService;
    }

}
