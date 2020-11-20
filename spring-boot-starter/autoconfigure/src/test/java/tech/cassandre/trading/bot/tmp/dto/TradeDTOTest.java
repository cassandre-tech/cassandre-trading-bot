package tech.cassandre.trading.bot.tmp.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("DTO - TradeDTO")
@Disabled
public class TradeDTOTest {

    @Test
    @Tag("notReviewed")
    @DisplayName("Check equalTo")
    public void checkEqualToForTrades() {
        // Test that only id is important when testing equality.
        TradeDTO t1 = TradeDTO.builder().id("0000001").create();
        TradeDTO t1Bis = TradeDTO.builder().id("0000001")
                .currencyPair(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC))
                .feeAmount(new BigDecimal(1))
                .feeCurrency(CurrencyDTO.BTC)
                .orderId("000002")
                .originalAmount(new BigDecimal(1))
                .price(new BigDecimal(1))
                .timestamp(ZonedDateTime.now())
                .type(OrderTypeDTO.BID)
                .create();
        assertEquals(t1, t1Bis);

        // Test that the id makes the trade different.
        TradeDTO t2 = TradeDTO.builder().id("0000002").create();
        assertNotEquals(t1, t2);
    }

}
