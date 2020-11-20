package tech.cassandre.trading.bot.tmp.modes.dry;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Dry mode - Exchange service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = BEFORE_CLASS)
@Disabled
public class ExchangeServiceDryModeTest {

    @Autowired
    private ExchangeService exchangeService;

    @Test
    @Tag("notReviewed")
    @DisplayName("Check the list of available")
    public void checkGetAvailableCurrencyPairs() {
        // The available currencies should be the same than the strategy.
        final Set<CurrencyPairDTO> availableCurrencyPairs = exchangeService.getAvailableCurrencyPairs();
        assertEquals(2, availableCurrencyPairs.size());
        assertTrue(availableCurrencyPairs.contains(new CurrencyPairDTO(ETH, BTC)));
        assertTrue(availableCurrencyPairs.contains(new CurrencyPairDTO(ETH, USDT)));
    }

}
