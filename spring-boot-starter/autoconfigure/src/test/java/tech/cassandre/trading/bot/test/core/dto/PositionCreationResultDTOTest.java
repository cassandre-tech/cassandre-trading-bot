package tech.cassandre.trading.bot.test.core.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DTO - PositionCreationResultDTO")
public class PositionCreationResultDTOTest {

    @Test
    @DisplayName("Check successful position creation")
    public void checkSuccessfulPositionCreation() {
        OrderDTO o = OrderDTO.builder().orderId("2").build();
        PositionDTO p = PositionDTO.builder().uid(1).openingOrder(o).build();
        final PositionCreationResultDTO result = new PositionCreationResultDTO(p);
        // Testing result values.
        assertEquals(1, result.getPosition().getUid());
        assertEquals("2", result.getPosition().getOpeningOrder().getOrderId());
        assertTrue(result.isSuccessful());
    }

    @Test
    @DisplayName("Check unsuccessful position creation")
    public void checkUnsuccessfulPositionCreation() {
        final PositionCreationResultDTO result = new PositionCreationResultDTO("Error message", new RuntimeException("Exception"));
        // Testing result values.
        assertEquals("Error message", result.getErrorMessage());
        assertEquals(RuntimeException.class, result.getException().getClass());
        assertEquals("Exception", result.getException().getMessage());
        assertFalse(result.isSuccessful());
    }

}
