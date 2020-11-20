package tech.cassandre.trading.bot.tmp.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;

@DisplayName("DTO - GainDTO")
@Disabled
public class GainDTOTest {

    @Test
    @Tag("notReviewed")
    @DisplayName("Check toString method")
    public void checkToString() {
        final GainDTO gain1 = new GainDTO();
        assertEquals("No gain", gain1.toString());

        final GainDTO gain2 = new GainDTO(1,
                new CurrencyAmountDTO(new BigDecimal("2"), BTC),
                new CurrencyAmountDTO(new BigDecimal("3"), BTC));
        assertEquals("Gains: 2 BTC (1.0 %) / Fees: 3 BTC", gain2.toString());
    }

}
