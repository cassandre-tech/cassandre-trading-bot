package tech.cassandre.trading.bot.issues.v4_x.v4_1_1;

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
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Github issue 510")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/issue510.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue510Test extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Test
    @DisplayName("Fix empty openingOrder or closing order")
    public void checkEmptyOrderFix() {
        final Map<CurrencyDTO, GainDTO> gains = positionService.getGains();

        // Check fees in position 6 (they must be in USDT in Kucoin data).
        final Optional<PositionDTO> position = strategy.getPositionByPositionId(6);
        assertTrue(position.isPresent());
        assertEquals(USDT, position.get().getGain().getFees().getCurrency());

        // CHeck that we have no fees in BTC (as kucoin only takes us USDT).
        assertNotEquals(0, ZERO.compareTo(gains.get(USDT).getFees().getValue()));
        assertEquals(0, ZERO.compareTo(gains.get(BTC).getFees().getValue()));
    }

}
