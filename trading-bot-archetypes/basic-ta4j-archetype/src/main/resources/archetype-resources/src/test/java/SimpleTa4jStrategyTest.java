#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
package ${package};

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.test.mock.TickerFluxMock;

import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;

/**
 * Simple Ta4J strategy test.
 */
@SpringBootTest
@Import(TickerFluxMock.class)
@DisplayName("Simple Ta4J strategy test")
public class SimpleTa4jStrategyTest {

    @Autowired
    private SimpleTa4jStrategy strategy;

    @Autowired
    private TickerFluxMock tickerFluxMock;

    @Test
    @DisplayName("Check gains")
    public void gainTest() {
        await().forever().until(() -> tickerFluxMock.isFluxDone());

        final Map<CurrencyDTO, GainDTO> gains = strategy.getGains();

        System.out.println("Cumulated gains:");
        gains.forEach((currency, gain) -> System.out.println(currency + " : " + gain.getAmount()));

        System.out.println("Position closed:");
        strategy.getPositions()
                .values()
                .stream()
                .filter(p -> p.getStatus().equals(CLOSED))
                .forEach(p -> System.out.println(" - " + p.getDescription()));

        System.out.println("Position not closed:");
        strategy.getPositions()
                .values()
                .stream()
                .filter(p -> !p.getStatus().equals(CLOSED))
                .forEach(p -> System.out.println(" - " + p.getDescription()));

        assertTrue(gains.get(strategy.getRequestedCurrencyPair().getQuoteCurrency()).getPercentage() > 0);
    }

}
