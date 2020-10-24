package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;

@DisplayName("DTO - GainDTO")
public class GainDTOTest {

    @Test
    @DisplayName("Check toString method")
    public void checkToString() {
        final GainDTO gain1 = new GainDTO();
        assertEquals("No gain", gain1.toString());

        final GainDTO gain2 = new GainDTO(1,
                new CurrencyAmountDTO(new BigDecimal("2"), BTC),
                new CurrencyAmountDTO(new BigDecimal("3"), BTC));
        assertEquals("Gain : 2 BTC (1.0 %) - Fees: 3 BTC", gain2.toString());
    }

}
