package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;

@DisplayName("DTO - GainDTO")
public class GainDTOTest {

    @Test
    @DisplayName("Check toString() method")
    public void checkToString() {
        final GainDTO gain1 = GainDTO.ZERO;
        assertEquals("No gain", gain1.toString());

        final GainDTO gain2 = GainDTO.builder()
                .percentage(1)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();
        assertEquals("Gains: 2 BTC (1.0 %) / Fees: 3 BTC", gain2.toString());
    }

    @Test
    @DisplayName("Check isInferiorTo() method")
    public void checkIsInferiorTO() {
        GainDTO gain = GainDTO.builder()
                .percentage(2)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO inferiorGain = GainDTO.builder()
                .percentage(1)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO superiorGain = GainDTO.builder()
                .percentage(3)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        assertFalse(gain.isInferiorTo(inferiorGain));
        assertTrue(gain.isInferiorTo(superiorGain));
    }

    @Test
    @DisplayName("Check isSuperiorTo() method")
    public void isSuperiorTo() {
        GainDTO gain = GainDTO.builder()
                .percentage(2)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO inferiorGain = GainDTO.builder()
                .percentage(1)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        GainDTO superiorGain = GainDTO.builder()
                .percentage(3)
                .amount(new CurrencyAmountDTO(new BigDecimal("2"), BTC))
                .fees(new CurrencyAmountDTO(new BigDecimal("3"), BTC))
                .build();

        assertTrue(gain.isSuperiorTo(inferiorGain));
        assertFalse(gain.isSuperiorTo(superiorGain));
    }

}
