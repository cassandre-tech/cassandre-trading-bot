package tech.cassandre.trading.bot.test.batch;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Mocks used by tests.
 */
@TestConfiguration
public class TradeFluxTestMock {

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(marketService());
    }

    @Bean
    @Primary
    public AccountFlux accountFlux() {
        return new AccountFlux(userService());
    }

    @Bean
    @Primary
    public OrderFlux orderFlux() {
        return new OrderFlux(tradeService());
    }

    @Bean
    @Primary
    public TradeFlux tradeFlux() {
        return new TradeFlux(tradeService());
    }

    @Bean
    @Primary
    public UserService userService() {
        UserService service = mock(UserService.class);
        given(service.getUser()).willReturn(Optional.empty());
        return service;
    }

    @Bean
    @Primary
    public MarketService marketService() {
        MarketService service = mock(MarketService.class);
        given(service.getTicker(any())).willReturn(Optional.empty());
        return service;
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public TradeService tradeService() {
        // Creates the mock.
        TradeService tradeService = mock(TradeService.class);

        // =========================================================================================================
        // First reply : 2 trades.
        TradeDTO trade01 = TradeDTO.builder().id("0000001").create();
        TradeDTO trade02 = TradeDTO.builder().id("0000002").create();

        Set<TradeDTO> reply01 = new LinkedHashSet<>();
        reply01.add(trade01);
        reply01.add(trade02);

        // =========================================================================================================
        // First reply : 3 trades.
        TradeDTO trade03 = TradeDTO.builder().id("0000003").create();
        TradeDTO trade04 = TradeDTO.builder().id("0000004").create();
        TradeDTO trade05 = TradeDTO.builder().id("0000005").create();

        Set<TradeDTO> reply02 = new LinkedHashSet<>();
        reply02.add(trade03);
        reply02.add(trade04);
        reply02.add(trade05);

        // =========================================================================================================
        // First reply : 3 trades - Trade07 is again trade 0000003.
        TradeDTO trade06 = TradeDTO.builder().id("0000006").create();
        TradeDTO trade07 = TradeDTO.builder().id("0000003").create();
        TradeDTO trade08 = TradeDTO.builder().id("0000008").create();

        Set<TradeDTO> reply03 = new LinkedHashSet<>();
        reply02.add(trade06);
        reply02.add(trade07);
        reply02.add(trade08);

        // =========================================================================================================
        // Creating the mock.
        given(tradeService.getTrades())
                .willReturn(reply01,
                        new LinkedHashSet<>(),
                        reply02,
                        reply03);
        return tradeService;
    }
}
