package tech.cassandre.trading.bot.test.modes.dry;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;

/**
 * Mocks used by tests.
 */
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
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDay(1)).bid(new BigDecimal("0.2")).ask(new BigDecimal("0.2")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDay(2)).bid(new BigDecimal("0.2")).ask(new BigDecimal("0.3")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDay(3)).bid(new BigDecimal("0.2")).ask(new BigDecimal("0.4")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createDay(4)).bid(new BigDecimal("0.2")).ask(new BigDecimal("0.4")).create())
                );
        // Replies for ETH / USDT.
        final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);
        given(marketService
                .getTicker(cp2))
                .willReturn(
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDay(5)).bid(new BigDecimal("0.3")).ask(new BigDecimal("0.3")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDay(6)).bid(new BigDecimal("0.3")).ask(new BigDecimal("0.3")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDay(7)).bid(new BigDecimal("0.3")).ask(new BigDecimal("0.6")).create()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createDay(8)).bid(new BigDecimal("0.3")).ask(new BigDecimal("0.1")).create())
                );
        return marketService;
    }

}
