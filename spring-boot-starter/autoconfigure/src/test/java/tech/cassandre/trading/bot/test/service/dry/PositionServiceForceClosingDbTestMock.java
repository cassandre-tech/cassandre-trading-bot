package tech.cassandre.trading.bot.test.service.dry;

import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.util.junit.BaseDbTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SuppressWarnings("unchecked")
@TestConfiguration
public class PositionServiceForceClosingDbTestMock extends BaseDbTest {

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

        // We don't use the getTickers method.
        given(marketService.getTickers(any())).willThrow(new NotAvailableFromExchangeException("Not available in test"));

        // Replies for ETH / BTC.
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);
        given(marketService
                .getTicker(cp1))
                .willReturn(
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(1)).last(new BigDecimal("0.2")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(2)).last(new BigDecimal("0.2")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(3)).last(new BigDecimal("0.21")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(4)).last(new BigDecimal("0.21")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(5)).last(new BigDecimal("0.21")).build())
                );
        // Replies for ETH / USDT.
        final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);
        given(marketService
                .getTicker(cp2))
                .willReturn(
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(5)).last(new BigDecimal("0.3")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(6)).last(new BigDecimal("0.3")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(7)).last(new BigDecimal("0.31")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(8)).last(new BigDecimal("0.31")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(9)).last(new BigDecimal("0.31")).build())
                );
        return marketService;
    }

}
