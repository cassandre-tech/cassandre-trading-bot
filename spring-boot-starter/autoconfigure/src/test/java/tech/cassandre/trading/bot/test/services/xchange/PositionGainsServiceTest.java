package tech.cassandre.trading.bot.test.services.xchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Service - XChange - Positions gains service")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/gains-test.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
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
            TRADE_14 - Sold 1 for 14 = 14.
            TRADE_15 - Sold 8 for 15 = 120.
            We bought 10 BTC for 113 USDT and sold them for 147 USDT.
            Amount gain : 34 USDT.
            Amount percentage : 30.08 % - ((147 - 113) / 113) * 100.
            Fees : 15 USDT.
        */
        final Optional<PositionDTO> p1 = positionService.getPositionById(1L);
        assertTrue(p1.isPresent());
        final GainDTO gain1 = p1.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("34").compareTo(gain1.getAmount().getValue()));
        assertEquals(USDT, gain1.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(30.09, gain1.getPercentage());
        // Gain (fees).
        assertEquals(0, new BigDecimal("15").compareTo(gain1.getFees().getValue()));
        assertEquals(USDT, gain1.getFees().getCurrency());
        // Net gain.
        assertEquals(0, new BigDecimal("19").compareTo(gain1.getNetAmount().getValue()));
        assertEquals(USDT, gain1.getNetAmount().getCurrency());

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
        assertEquals(0, new BigDecimal("10").compareTo(gain2.getFees().getValue()));
        assertEquals(BTC, gain2.getFees().getCurrency());
        // Net gain.
        assertEquals(0, new BigDecimal("-1010").compareTo(gain2.getNetAmount().getValue()));
        assertEquals(BTC, gain2.getNetAmount().getCurrency());

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
        assertEquals(0, new BigDecimal("11").compareTo(gain3.getFees().getValue()));
        assertEquals(USDT, gain3.getFees().getCurrency());
        // Net gain.
        assertEquals(0, new BigDecimal("139").compareTo(gain3.getNetAmount().getValue()));
        assertEquals(USDT, gain3.getNetAmount().getCurrency());

        // The should not not gain for positions 4,5 & 6.
        final Optional<PositionDTO> p4 = positionService.getPositionById(4L);
        assertTrue(p4.isPresent());
        assertEquals(0, p4.get().getGain().getPercentage());
        assertEquals(0, ZERO.compareTo(p4.get().getGain().getAmount().getValue()));
        assertEquals(0, ZERO.compareTo(p4.get().getGain().getNetAmount().getValue()));
        final Optional<PositionDTO> p5 = positionService.getPositionById(5L);
        assertTrue(p5.isPresent());
        assertEquals(0, p5.get().getGain().getPercentage());
        assertEquals(0, ZERO.compareTo(p5.get().getGain().getAmount().getValue()));
        assertEquals(0, ZERO.compareTo(p5.get().getGain().getNetAmount().getValue()));
        final Optional<PositionDTO> p6 = positionService.getPositionById(6L);
        assertTrue(p6.isPresent());
        assertEquals(0, p6.get().getGain().getPercentage());
        assertEquals(0, ZERO.compareTo(p6.get().getGain().getAmount().getValue()));
        assertEquals(0, ZERO.compareTo(p6.get().getGain().getNetAmount().getValue()));

        /*
            Position 7 (SHORT) - Sold 10 ETH for USDT.
            TRADE_63 - Sold 10 ETH with a price of 5 = 50 USDT.
            TRADE_64 - Bought ETH with 50 USDT with a price of 10 = 5 ETH
            Amount gain : -5 ETH.
            Amount percentage : -50%.
            Fees : 4 USDT.
         */
        final Optional<PositionDTO> p7 = positionService.getPositionById(7L);
        assertTrue(p7.isPresent());
        final GainDTO gain7 = p7.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("-5").compareTo(gain7.getAmount().getValue()));
        assertEquals(ETH, gain7.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(-50, gain7.getPercentage());
        // Gain (fees).
        assertEquals(0, new BigDecimal("4").compareTo(gain7.getFees().getValue()));
        assertEquals(ETH, gain7.getFees().getCurrency());
        // Net gain.
        assertEquals(0, new BigDecimal("-9").compareTo(gain7.getNetAmount().getValue()));
        assertEquals(ETH, gain7.getNetAmount().getCurrency());

        // Check all gains.
        final Map<CurrencyDTO, GainDTO> gains = positionService.getGains();
        assertEquals(3, gains.size());

        // Gains USDT.
        final GainDTO usdtGain = gains.get(USDT);
        assertNotNull(usdtGain);
        assertEquals(25.81, usdtGain.getPercentage());
        assertEquals(0, new BigDecimal("184").compareTo(usdtGain.getAmount().getValue()));
        assertEquals(USDT, usdtGain.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("26").compareTo(usdtGain.getFees().getValue()));
        assertEquals(USDT, usdtGain.getFees().getCurrency());
        // Net gain.
        assertEquals(0, new BigDecimal("158").compareTo(usdtGain.getNetAmount().getValue()));
        assertEquals(USDT, usdtGain.getNetAmount().getCurrency());

        // Gains BTC.
        final GainDTO btcGain = gains.get(BTC);
        assertNotNull(btcGain);
        assertEquals(-50, btcGain.getPercentage());
        assertEquals(0, new BigDecimal("-1000").compareTo(btcGain.getAmount().getValue()));
        assertEquals(BTC, btcGain.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("10").compareTo(btcGain.getFees().getValue()));
        assertEquals(BTC, btcGain.getFees().getCurrency());
        // Net gain.
        assertEquals(0, new BigDecimal("-1010").compareTo(btcGain.getNetAmount().getValue()));
        assertEquals(BTC, btcGain.getNetAmount().getCurrency());

        // Gains ETH.
        final GainDTO ethGain = gains.get(ETH);
        assertNotNull(ethGain);
        assertEquals(-50, ethGain.getPercentage());
        assertEquals(0, new BigDecimal("-5").compareTo(ethGain.getAmount().getValue()));
        assertEquals(ETH, ethGain.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("4").compareTo(ethGain.getFees().getValue()));
        assertEquals(ETH, ethGain.getFees().getCurrency());
    }

}
