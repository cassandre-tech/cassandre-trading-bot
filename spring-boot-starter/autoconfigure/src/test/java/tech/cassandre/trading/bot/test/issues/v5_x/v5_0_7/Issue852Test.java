package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_7;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 852")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false"),
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/issues/issue852.yaml")
})
@ActiveProfiles("schedule-disabled")
@Import(Issue852TestMock.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue852Test {

    @Autowired
    private PositionService positionService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Base and quote precisions are not saved in database")
    public void baseAndQuotePrecisionManagementInPosition() {
        // Position 1 test (existing in database).
        final Optional<PositionDTO> position1 = positionService.getPositionByUid(1);
        assertTrue(position1.isPresent());
        assertEquals(new CurrencyDTO("SHIB"), position1.get().getCurrencyPair().getBaseCurrency());
        assertEquals(new CurrencyDTO("USDT"), position1.get().getCurrencyPair().getQuoteCurrency());
        assertEquals(1, position1.get().getCurrencyPair().getBaseCurrencyPrecision());
        assertEquals(2, position1.get().getCurrencyPair().getQuoteCurrencyPrecision());

        // Position 2 test (existing in database).
        final Optional<PositionDTO> position2 = positionService.getPositionByUid(2);
        assertTrue(position2.isPresent());
        assertEquals(new CurrencyDTO("ETH"), position2.get().getCurrencyPair().getBaseCurrency());
        assertEquals(new CurrencyDTO("BTC"), position2.get().getCurrencyPair().getQuoteCurrency());
        assertEquals(3, position2.get().getCurrencyPair().getBaseCurrencyPrecision());
        assertEquals(4, position2.get().getCurrencyPair().getQuoteCurrencyPrecision());

        // Position 3 test (manual creation).
        final CurrencyPairDTO newCurrencyPair = new CurrencyPairDTO("EUR",
                "SHIB",
                6,
                9);
        strategy.createLongPosition(newCurrencyPair,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .build());
        final Optional<PositionDTO> position3 = positionService.getPositionByUid(3);
        assertTrue(position3.isPresent());
        assertEquals(new CurrencyDTO("EUR"), position3.get().getCurrencyPair().getBaseCurrency());
        assertEquals(new CurrencyDTO("SHIB"), position3.get().getCurrencyPair().getQuoteCurrency());
        assertEquals(6, position3.get().getCurrencyPair().getBaseCurrencyPrecision());
        assertEquals(9, position3.get().getCurrencyPair().getQuoteCurrencyPrecision());
    }

}
