package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_5;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 761")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue761Test extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Creating a position with a very small amount")
    public void testPositionCreationWithAVerySmallAmount() {
        // Creates position 1 (Amount of zero) - should fail.
        final PositionCreationResultDTO position1Result = strategy.createShortPosition(ETH_BTC,
                BigDecimal.ZERO,
                PositionRulesDTO.builder().build());
        assertFalse(position1Result.isSuccessful());
        assertEquals("Impossible to create a position for such a small amount: 0", position1Result.getErrorMessage());

        // Creates position 1 (Amount of 0.0000000001) - should fail.
        final PositionCreationResultDTO position2Result = strategy.createShortPosition(ETH_BTC,
                new BigDecimal("0.0000000001"),
                PositionRulesDTO.builder().build());
        assertFalse(position2Result.isSuccessful());
        assertEquals("Impossible to create a position for such a small amount: 1E-10", position2Result.getErrorMessage());
    }

}
