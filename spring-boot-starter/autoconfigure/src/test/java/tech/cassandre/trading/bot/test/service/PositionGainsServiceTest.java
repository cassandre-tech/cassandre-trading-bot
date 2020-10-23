package tech.cassandre.trading.bot.test.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Services - Positions gains service")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/gains-test.sql"),
        @Property(key = "spring.jpa.hibernate.ddl-auto", value = "create-drop")
})
public class PositionGainsServiceTest {

    @Autowired
    private PositionService positionService;

    @Test
    @DisplayName("Check gains calculation")
    public void checkGainsCalculation() {
        /*
            Position 1 - Bought 10 BTC with USDT.
            TRADE_11 - Bought 7 for 11 = 77.
            TRADE_12 - Bought 3 for 12 = 36.
            TRADE_13 - Sold 1 for 13 = 13.
            TRADE_14 - Sold 2 for 14 = 28.
            TRADE_15 - Sold 8 for 15 = 120.
            We bought 10 BTC for 113 USDT and sold them for 161 USDT.
            Amount gain : 48 USDT.
            Amount percentage : 42.48 % - ((161 - 113) / 113) * 100.
            Fees : 15 USDT.
        */
        final Optional<PositionDTO> p1 = positionService.getPositionById(1L);
        assertTrue(p1.isPresent());
        final GainDTO gain1 = p1.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("48").compareTo(gain1.getAmount().getValue()));
        assertEquals(USDT, gain1.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(42.48, gain1.getPercentage());
        // Gain (fees).
        assertEquals(0, new BigDecimal(15).compareTo(gain1.getFees().getValue()));
        assertEquals(USDT, gain1.getFees().getCurrency());

        /*
            Position 2 - Bought 20 ETH with BTC.
            TRADE_21 - Bought 20 ETH for 100 = 2000.
            TRADE_22 - Sold 20 ETH for 50 = 1000.
            We bought 20 BTC for 2 000 BTC and sold them for 1 000 BTC.
            Amount gain : -1 000 BTC.
            Amount percentage : -50 % - ((1 000 - 2 000) / 2 000) * 100.
            Fees : 10 BTC.
         */
        final Optional<PositionDTO> p2 = positionService.getPositionById(2L);
        assertTrue(p2.isPresent());
        final GainDTO gain2 = p2.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("-1000").compareTo(gain2.getAmount().getValue()));
        assertEquals(BTC, gain2.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(-50, gain2.getPercentage());
        // Gain (fees).
        assertEquals(0, new BigDecimal(10).compareTo(gain2.getFees().getValue()));
        assertEquals(BTC, gain2.getFees().getCurrency());

        /*
            Position 3 - Bought 30 BTC with USDT.
            TRADE_31 - Bought 30 BTC for 20 = 600.
            TRADE_32 - Bought 30 BTC for 25 = 750.
            We bought 30 BTC for 600 USDT and sold them for 750 USDT.
            Amount gain : 150 USDT.
            Amount percentage : 25% - ((750 - 600) / 600) * 100.
            Fees : 11 USDT.
         */
        final Optional<PositionDTO> p3 = positionService.getPositionById(3L);
        assertTrue(p3.isPresent());
        final GainDTO gain3 = p3.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("150").compareTo(gain3.getAmount().getValue()));
        assertEquals(USDT, gain3.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(25, gain3.getPercentage());
        // Gain (fees).
        assertEquals(0, new BigDecimal(11).compareTo(gain3.getFees().getValue()));
        assertEquals(USDT, gain3.getFees().getCurrency());

        // The should not not gain for positions 4, 5 & 6.
        final Optional<PositionDTO> p4 = positionService.getPositionById(4L);
        assertTrue(p4.isPresent());
        assertEquals(0, p4.get().getGain().getPercentage());
        final Optional<PositionDTO> p5 = positionService.getPositionById(5L);
        assertTrue(p5.isPresent());
        assertEquals(0, p5.get().getGain().getPercentage());
        final Optional<PositionDTO> p6 = positionService.getPositionById(6L);
        assertTrue(p6.isPresent());
        assertEquals(0, p6.get().getGain().getPercentage());

        // Check all gains.
        final HashMap<CurrencyDTO, GainDTO> gains = positionService.getGains();
        assertEquals(2, gains.size());
        // Gains USDT.
        final GainDTO usdtGain = gains.get(USDT);
        assertNotNull(usdtGain);
        assertEquals(27.77, usdtGain.getPercentage());
        assertEquals(0, new BigDecimal("198").compareTo(usdtGain.getAmount().getValue()));
        assertEquals(USDT, usdtGain.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("26").compareTo(usdtGain.getFees().getValue()));
        assertEquals(USDT, usdtGain.getFees().getCurrency());

        // Gains BTC.
        final GainDTO btcGain = gains.get(BTC);
        assertNotNull(btcGain);
        assertEquals(-50, btcGain.getPercentage());
        assertEquals(0, new BigDecimal("-1000").compareTo(btcGain.getAmount().getValue()));
        assertEquals(BTC, btcGain.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("10").compareTo(btcGain.getFees().getValue()));
        assertEquals(BTC, btcGain.getFees().getCurrency());
    }

}
