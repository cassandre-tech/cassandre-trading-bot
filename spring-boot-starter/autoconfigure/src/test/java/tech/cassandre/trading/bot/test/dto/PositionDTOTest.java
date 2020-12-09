package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@DisplayName("DTO - PositionDTO")
public class PositionDTOTest {

    private final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    private final BigDecimal amount = new BigDecimal("0.0001");

    private final PositionRulesDTO noRules = PositionRulesDTO.builder().create();

    @Test
    @DisplayName("Check that close order update is limited to OPENED position")
    public void checkCloseOrderIdUpdate() {
        StrategyDTO strategy = new StrategyDTO();
        strategy.setId("1");

        // We create a position that was opened with the order O000001.
        PositionDTO p = new PositionDTO(1, strategy, cp1, amount, "O000001", noRules);

        // We are in OPENING status and we try to call setCloseOrderId.
        assertEquals(OPENING, p.getStatus());
        assertThrows(RuntimeException.class, () -> p.setClosingOrderId("O000002"));

        // We move to OPENED status and we try to call setCloseOrderId.
        p.tradeUpdate(TradeDTO.builder().id("T000001").type(BID).orderId("O000001").originalAmount(amount).create());
        assertEquals(OPENED, p.getStatus());

        // We are in OPENED, we should now be able to setCloseOrderId.
        p.setClosingOrderId("O000002");
        assertEquals(CLOSING, p.getStatus());

        // We are in CLOSING, we should not be able to setCloseOrderId.
        assertThrows(RuntimeException.class, () -> p.setClosingOrderId("O000002"));

        // We move to CLOSED.
        p.tradeUpdate(TradeDTO.builder().id("T000001").type(ASK).orderId("O000002").originalAmount(amount).create());
        assertEquals(CLOSED, p.getStatus());
        assertThrows(RuntimeException.class, () -> p.setClosingOrderId("O000002"));
    }

    @Test
    @DisplayName("Check equalTo")
    public void checkEqualTo() {
        StrategyDTO strategy = new StrategyDTO();
        strategy.setId("1");

        PositionDTO p1 = new PositionDTO(1, strategy, cp1, amount, "O000001", noRules);
        PositionDTO p1Bis = new PositionDTO(1, strategy, cp1, amount, "O000001", noRules);
        PositionDTO p2 = new PositionDTO(2, strategy, cp1, amount, "O000002", noRules);

        // Same position.
        assertEquals(p1, p1);
        assertEquals(p1, p1Bis);

        // Two different positions.
        assertNotEquals(p1, p2);

        // Status changed - for P1.
        p1.tradeUpdate(TradeDTO.builder().id("T000001").type(BID).currencyPair(cp1).originalAmount(amount).orderId("O000001").create());
        assertNotEquals(p1, p1Bis);
    }

}
