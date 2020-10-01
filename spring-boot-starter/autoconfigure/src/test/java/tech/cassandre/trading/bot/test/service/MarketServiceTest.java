package tech.cassandre.trading.bot.test.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Services - Market service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = "TEST_NAME", value = "Configuration parameters - Valid configuration")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MarketServiceTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private MarketService marketService;

    @Test
    @DisplayName("Check get estimated buying cost")
    public void checkGetEstimatedBuyingCost() {
        final CurrencyPairDTO anotherCurrencyPair = new CurrencyPairDTO(BTC, USDT);
        final CurrencyPairDTO currencyPair = new CurrencyPairDTO(ETH, BTC);
        final BigDecimal amount = new BigDecimal("3");

        // When the ticker doesn't exists.
        assertTrue(marketService.getEstimatedBuyingCost(anotherCurrencyPair, amount).isEmpty());

        // When the ticker is not the one expected.
        marketService.getTicker(anotherCurrencyPair);
        assertTrue(marketService.getEstimatedBuyingCost(currencyPair, amount).isEmpty());

        // When there s a ticker with a price.
        Optional<TickerDTO> ticker = marketService.getTicker(currencyPair);
        assertTrue(ticker.isPresent());
        final Optional<CurrencyAmountDTO> cost = marketService.getEstimatedBuyingCost(currencyPair, amount);
        assertTrue(cost.isPresent());
        assertEquals(BTC, cost.get().getCurrency());
        assertEquals(0, (ticker.get().getLast().multiply(amount)).compareTo(cost.get().getValue()));
    }

}
