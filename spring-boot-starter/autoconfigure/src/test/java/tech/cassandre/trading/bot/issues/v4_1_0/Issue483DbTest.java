package tech.cassandre.trading.bot.issues.v4_1_0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.test.util.junit.BaseDbTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.strategy.basic.TestableCassandreStrategy;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DisplayName("Github issue 483")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/issue483.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(Issue483DbTestMock.class)
@Testcontainers
public class Issue483DbTest extends BaseDbTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check onPositionStatusUpdate not called after restart")
    public void checkGainsCalculation() {
        // In the bug, position short n°24 had no gain.
        final Optional<PositionDTO> position = strategy.getPositionByPositionId(24);
        assertTrue(position.isPresent());
        assertTrue(position.get().getLatestCalculatedGain().isPresent());
        assertNotEquals(0, position.get().getLatestCalculatedGain().get().getPercentage());
    }

}
