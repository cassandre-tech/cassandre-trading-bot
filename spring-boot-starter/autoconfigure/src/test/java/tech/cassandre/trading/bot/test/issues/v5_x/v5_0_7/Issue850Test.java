package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_7;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 850")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false"),
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/issues/issue850.yaml")
})
@ActiveProfiles("schedule-disabled")
@Import(Issue850TestMock.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue850Test {

    @Autowired
    private PositionService positionService;

    @Test
    @DisplayName("Fees can be in different currencies on a single position")
    public void badFeesManagement() {
        // Position 1 - Data description is here: src/test/resources/db/test/issues/issue850.sql.
        final Optional<PositionDTO> position1 = positionService.getPositionByUid(1);
        assertTrue(position1.isPresent());
        final Map<CurrencyDTO, CurrencyAmountDTO> ordersFees = position1.get().getGain().getFeesByCurrency();

        // ETH: 0.00002000+0.00003000+1.50000000+0.50002000 = 2.00007
        // BTC: 0.00004000+1.00002000 = 1.00006
        // KCS: 0.00005000
        assertEquals(3, ordersFees.size());
        assertEquals(0, new BigDecimal("2.00007000").compareTo(ordersFees.get(ETH).getValue()));
        assertEquals(0, new BigDecimal("1.00006000").compareTo(ordersFees.get(BTC).getValue()));
        assertEquals(0, new BigDecimal("0.00005000").compareTo(ordersFees.get(KCS).getValue()));
    }

}
