package tech.cassandre.trading.bot.test.core.services.xchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
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
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/core/gains-test.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionGainsServiceTest {

    @Autowired
    private PositionService positionService;

    @Test
    @DisplayName("Check gains calculation")
    public void checkGainsCalculation() {
        // =============================================================================================================
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
        final Optional<PositionDTO> p1 = positionService.getPositionByUid(1L);
        assertTrue(p1.isPresent());
        final GainDTO gain1 = p1.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("34").compareTo(gain1.getAmount().getValue()));
        assertEquals(USDT, gain1.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(30.09, gain1.getPercentage());
        // Gain (fees).
        final Map<CurrencyDTO, CurrencyAmountDTO> gain1Fees = gain1.getFeesByCurrency();
        assertEquals(1, gain1Fees.size());
        assertNotNull(gain1Fees.get(USDT));
        assertEquals(0, new BigDecimal("15").compareTo(gain1Fees.get(USDT).getValue()));
        assertEquals(USDT, gain1Fees.get(USDT).getCurrency());

        // =============================================================================================================
        /*
            Position 2 - Bought 20 ETH with BTC.
            TRADE_21 - Bought 20 ETH for 100 = 2000.
            TRADE_22 - Sold 20 ETH for 50 = 1000.
            We bought 20 BTC for 2 000 BTC and sold them for 1 000 BTC.
            Amount gain : -1 000 BTC.
            Amount percentage : -50 % - ((1 000 - 2 000) / 2 000) * 100.
            Fees : 10 BTC.
         */
        final Optional<PositionDTO> p2 = positionService.getPositionByUid(2L);
        assertTrue(p2.isPresent());
        final GainDTO gain2 = p2.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("-1000").compareTo(gain2.getAmount().getValue()));
        assertEquals(BTC, gain2.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(-50, gain2.getPercentage());
        // Gain (fees).
        final Map<CurrencyDTO, CurrencyAmountDTO> gain2Fees = gain2.getFeesByCurrency();
        assertEquals(1, gain2Fees.size());
        assertNotNull(gain2Fees.get(BTC));
        assertEquals(0, new BigDecimal("10").compareTo(gain2Fees.get(BTC).getValue()));
        assertEquals(BTC, gain2Fees.get(BTC).getCurrency());

        // =============================================================================================================
        /*
            Position 3 - Bought 30 BTC with USDT.
            TRADE_31 - Bought 30 BTC for 20 = 600.
            TRADE_32 - Bought 30 BTC for 25 = 750.
            We bought 30 BTC for 600 USDT and sold them for 750 USDT.
            Amount gain : 150 USDT.
            Amount percentage : 25% - ((750 - 600) / 600) * 100.
            Fees : 11 USDT.
         */
        final Optional<PositionDTO> p3 = positionService.getPositionByUid(3L);
        assertTrue(p3.isPresent());
        final GainDTO gain3 = p3.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("150").compareTo(gain3.getAmount().getValue()));
        assertEquals(USDT, gain3.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(25, gain3.getPercentage());
        // Gain (fees).
        final Map<CurrencyDTO, CurrencyAmountDTO> gain3Fees = gain3.getFeesByCurrency();
        assertEquals(1, gain3Fees.size());
        assertNotNull(gain3Fees.get(USDT));
        assertEquals(0, new BigDecimal("11").compareTo(gain3Fees.get(USDT).getValue()));
        assertEquals(USDT, gain3Fees.get(USDT).getCurrency());

        // There should be no gain for positions 4,5 & 6.
        final Optional<PositionDTO> p4 = positionService.getPositionByUid(4L);
        assertTrue(p4.isPresent());
        assertEquals(0, p4.get().getGain().getPercentage());
        assertEquals(0, ZERO.compareTo(p4.get().getGain().getAmount().getValue()));
        final Optional<PositionDTO> p5 = positionService.getPositionByUid(5L);
        assertTrue(p5.isPresent());
        assertEquals(0, p5.get().getGain().getPercentage());
        assertEquals(0, ZERO.compareTo(p5.get().getGain().getAmount().getValue()));
        final Optional<PositionDTO> p6 = positionService.getPositionByUid(6L);
        assertTrue(p6.isPresent());
        assertEquals(0, p6.get().getGain().getPercentage());
        assertEquals(0, ZERO.compareTo(p6.get().getGain().getAmount().getValue()));

        // =============================================================================================================
        /*
            Position 7 (SHORT) - Sold 10 ETH for USDT.
            TRADE_63 - Sold 10 ETH with a price of 5 = 50 USDT.
            TRADE_64 - Bought ETH with 50 USDT with a price of 10 = 5 ETH
            Amount gain : -5 ETH.
            Amount percentage : -50%.
            Fees : 4 USDT.
         */
        final Optional<PositionDTO> p7 = positionService.getPositionByUid(7L);
        assertTrue(p7.isPresent());
        final GainDTO gain7 = p7.get().getGain();
        // Gain (amount).
        assertEquals(0, new BigDecimal("-5").compareTo(gain7.getAmount().getValue()));
        assertEquals(ETH, gain7.getAmount().getCurrency());
        // Gain (percentage).
        assertEquals(-50, gain7.getPercentage());
        // Gain (fees).
        final Map<CurrencyDTO, CurrencyAmountDTO> gain7Fees = gain7.getFeesByCurrency();
        assertEquals(1, gain7Fees.size());
        assertNotNull(gain7Fees.get(ETH));
        assertEquals(0, new BigDecimal("4").compareTo(gain7Fees.get(ETH).getValue()));
        assertEquals(ETH, gain7Fees.get(ETH).getCurrency());

        // =============================================================================================================
        // Check all gains.
        final Map<CurrencyDTO, GainDTO> gains = positionService.getGains();
        assertEquals(3, gains.size());

        // USDT Gains.
        final GainDTO usdtGain = gains.get(USDT);
        assertNotNull(usdtGain);
        assertEquals(25.81, usdtGain.getPercentage());
        assertEquals(0, new BigDecimal("184").compareTo(usdtGain.getAmount().getValue()));
        assertEquals(USDT, usdtGain.getAmount().getCurrency());

        // BTC Gains.
        final GainDTO btcGain = gains.get(BTC);
        assertNotNull(btcGain);
        assertEquals(-50, btcGain.getPercentage());
        assertEquals(0, new BigDecimal("-1000").compareTo(btcGain.getAmount().getValue()));
        assertEquals(BTC, btcGain.getAmount().getCurrency());

        // ETH Gains.
        final GainDTO ethGain = gains.get(ETH);
        assertNotNull(ethGain);
        assertEquals(-50, ethGain.getPercentage());
        assertEquals(0, new BigDecimal("-5").compareTo(ethGain.getAmount().getValue()));
        assertEquals(ETH, ethGain.getAmount().getCurrency());

        // ALl fees with getOrdersFees().
        final Map<CurrencyDTO, CurrencyAmountDTO> fees = ethGain.getFeesByCurrency();
        assertEquals(3, fees.size());
        // USDT.
        assertNotNull(fees.get(USDT));
        assertEquals(0, new BigDecimal("26").compareTo(fees.get(USDT).getValue()));
        assertEquals(USDT, fees.get(USDT).getCurrency());
        // BTC.
        assertNotNull(fees.get(BTC));
        assertEquals(0, new BigDecimal("10").compareTo(fees.get(BTC).getValue()));
        assertEquals(BTC, fees.get(BTC).getCurrency());
        // ETH.
        assertNotNull(fees.get(ETH));
        assertEquals(0, new BigDecimal("4").compareTo(fees.get(ETH).getValue()));
        assertEquals(ETH, fees.get(ETH).getCurrency());

        // ALl fees with getOrdersFees().
        assertEquals(3, ethGain.getFees().size());
        // USDT.
        Optional<CurrencyAmountDTO> usdtFees = ethGain.getFees()
                .stream()
                .filter(currencyAmountDTO -> currencyAmountDTO.getCurrency().equals(USDT))
                .findAny();
        assertTrue(usdtFees.isPresent());
        assertEquals(0, new BigDecimal("26").compareTo(usdtFees.get().getValue()));
        assertEquals(USDT, usdtFees.get().getCurrency());
        // BTC.
        Optional<CurrencyAmountDTO> btcFees = ethGain.getFees()
                .stream()
                .filter(currencyAmountDTO -> currencyAmountDTO.getCurrency().equals(BTC))
                .findAny();
        assertTrue(btcFees.isPresent());
        assertEquals(0, new BigDecimal("10").compareTo(btcFees.get().getValue()));
        assertEquals(BTC, btcFees.get().getCurrency());
        // ETH.
        Optional<CurrencyAmountDTO> ethFees = ethGain.getFees()
                .stream()
                .filter(currencyAmountDTO -> currencyAmountDTO.getCurrency().equals(ETH))
                .findAny();
        assertTrue(ethFees.isPresent());
        assertEquals(0, new BigDecimal("4").compareTo(ethFees.get().getValue()));
        assertEquals(ETH, ethFees.get().getCurrency());
    }

}
