package tech.cassandre.trading.bot.tmp.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DTO - PositionCreationResultDTO")
@Disabled
public class PositionCreationResultDTOTest {

    @Test
    @Tag("notReviewed")
    @DisplayName("Check successful position creation")
    public void checkSuccessfulPositionCreation() {
        final PositionCreationResultDTO p = new PositionCreationResultDTO(1, "2");
        assertEquals(1, p.getPositionId());
        assertEquals("2", p.getOrderId());
        assertTrue(p.isSuccessful());
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check unsuccessful position creation")
    public void checkUnsuccessfulPositionCreation() {
        final PositionCreationResultDTO p = new PositionCreationResultDTO("Error message", new RuntimeException("Exception"));
        assertEquals("Error message", p.getErrorMessage());
        assertEquals(RuntimeException.class, p.getException().getClass());
        assertEquals("Exception", p.getException().getMessage());
        assertFalse(p.isSuccessful());
    }

}
