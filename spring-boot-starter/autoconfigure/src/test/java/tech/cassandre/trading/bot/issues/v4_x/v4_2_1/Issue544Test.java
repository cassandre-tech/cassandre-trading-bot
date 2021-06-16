package tech.cassandre.trading.bot.issues.v4_x.v4_2_1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.EUR;

@DisplayName("Github issue 544")
public class Issue544Test {

    @Test
    public void whenTimestampIsNullTest() throws InterruptedException {
        final Map<CurrencyPairDTO, TickerDTO> previousValues = new LinkedHashMap<>();
        int count = 0;
        for (int i = 0; i < 100; i++) {
            TickerDTO newTicker = TickerDTO.builder().currencyPair(new CurrencyPairDTO(BTC, EUR)).build();
            TickerDTO oldTicker = previousValues.get(newTicker.getCurrencyPair());
            if (!newTicker.equals(oldTicker)) {
                previousValues.put(newTicker.getCurrencyPair(), newTicker);
            } else {
                count++;
            }
            Thread.sleep(100L);
        }
        assertEquals(0, count);
    }

}
