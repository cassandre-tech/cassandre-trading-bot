package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.ETH_BTC;

@DisplayName("DTO - TradeDTO")
public class TradeDTOTest {

    @Test
    @DisplayName("Check equals()")
    public void checkEqualToForTrades() {
        // Test that only id is important when testing equality.
        TradeDTO t1 = TradeDTO.builder()
                .tradeId("0000001")
                .build();
        TradeDTO t1Bis = TradeDTO.builder()
                .tradeId("0000001")
                .type(BID)
                .orderId("000002")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1", ETH))
                .price(new CurrencyAmountDTO("1", BTC))
                .fee(new CurrencyAmountDTO("1", BTC))
                .userReference("Reference 0000001")
                .timestamp(ZonedDateTime.now())
                .build();
        assertNotEquals(t1, t1Bis);

        // Test that the id makes the trade different.
        TradeDTO t2 = TradeDTO.builder()
                .tradeId("0000002")
                .build();
        assertNotEquals(t1, t2);
    }

    @Test
    @DisplayName("Check null trades")
    public void checkNullTrades() {
        TradeDTO t1 = TradeDTO.builder().tradeId("0000001").build();
        TradeDTO t2 = TradeDTO.builder().tradeId("0000002").build();
        assertNotEquals(t1, t2);
        assertNotEquals(t2, t1);
    }

}
