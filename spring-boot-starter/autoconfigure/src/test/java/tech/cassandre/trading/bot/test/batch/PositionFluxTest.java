package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.batch.mocks.PositionFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName("Batch - Position flux")
@Configuration({
        @Property(key = "TEST_NAME", value = "Batch - Position flux")
})
@Import(PositionFluxTestMock.class)
public class PositionFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        final int numberOfPositionExpected = 3;
        final int numberOfPositionServiceCalls = 4;

        // Waiting for the trade service to have been called with all the test data.
        await().untilAsserted(() -> verify(positionService, atLeast(numberOfPositionServiceCalls)).getPositions());

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getPositionsUpdateReceived().size() >= numberOfPositionExpected));

        // Test all values received.
        final Iterator<PositionDTO> iterator = strategy.getPositionsUpdateReceived().iterator();
        assertEquals(1, iterator.next().getId());
        assertEquals(2, iterator.next().getId());
        assertEquals(3, iterator.next().getId());
    }

}
