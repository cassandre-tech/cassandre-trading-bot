package tech.cassandre.trading.bot.beta.dto;

import io.qase.api.annotation.CaseId;
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
    @CaseId(47)
    @DisplayName("Check successful position creation")
    public void checkSuccessfulPositionCreation() {
        OrderDTO o = OrderDTO.builder().orderId("2").build();
        PositionDTO p = PositionDTO.builder().id(1).openingOrder(o).build();
        final PositionCreationResultDTO result = new PositionCreationResultDTO(p);
        assertEquals(1, result.getPosition().getId());
        assertEquals("2", result.getPosition().getOpeningOrder().getOrderId());
        assertTrue(result.isSuccessful());
    }

    @Test
    @CaseId(48)
    @DisplayName("Check unsuccessful position creation")
    public void checkUnsuccessfulPositionCreation() {
        final PositionCreationResultDTO p = new PositionCreationResultDTO("Error message", new RuntimeException("Exception"));
        assertEquals("Error message", p.getErrorMessage());
        assertEquals(RuntimeException.class, p.getException().getClass());
        assertEquals("Exception", p.getException().getMessage());
        assertFalse(p.isSuccessful());
    }

}
