package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@DisplayName("DTO - TradeDTO")
public class TradeDTOTest {

    @Test
    @DisplayName("Check equalTo")
    public void checkEqualToForTrades() {
        // Test that only id is important when testing equality.
        TradeDTO t1 = TradeDTO.builder().id("0000001").build();
        TradeDTO t1Bis = TradeDTO.builder().id("0000001")
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .fee(new CurrencyAmountDTO("1", BTC))
                .orderId("000002")
                .amount(new CurrencyAmountDTO("1", ETH))
                .price(new CurrencyAmountDTO("1", BTC))
                .timestamp(ZonedDateTime.now())
                .type(BID)
                .build();
        assertNotEquals(t1, t1Bis);

        // Test that the id makes the trade different.
        TradeDTO t2 = TradeDTO.builder().id("0000002").build();
        assertNotEquals(t1, t2);
    }

    @Test
    @DisplayName("Check null trades")
    public void checkNullTrades() {
        TradeDTO t1 = TradeDTO.builder().id("0000001").build();
        TradeDTO t2 = TradeDTO.builder().id("0000002").build();
        assertNotEquals(t1, t2);
        assertNotEquals(t2, t1);
    }

}
