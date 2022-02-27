package tech.cassandre.trading.bot.test.core.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

@SpringBootTest
@DisplayName("Repository - Trade")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/core/backup.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class TradeRepositoryTest extends BaseTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Test
    @DisplayName("Check imported data")
    public void checkImportedTrades() {
        // Trades.
        final Iterator<Trade> trades = tradeRepository.findByOrderByTimestampAsc().iterator();
        assertEquals(10, tradeRepository.count());

        // Trade 01.
        Trade trade1 = trades.next();
        assertEquals(1, trade1.getUid());
        assertEquals("BACKUP_TRADE_01", trade1.getTradeId());
        assertEquals(BID, trade1.getType());
        assertEquals("BACKUP_OPENING_ORDER_02", trade1.getOrder().getOrderId());
        assertEquals("BTC/USDT", trade1.getCurrencyPair());
        assertEquals(0, trade1.getAmount().getValue().compareTo(new BigDecimal("20")));
        assertEquals("BTC", trade1.getAmount().getCurrency());
        assertEquals(0, trade1.getPrice().getValue().compareTo(new BigDecimal("10")));
        assertEquals("USDT", trade1.getPrice().getCurrency());
        assertEquals(0, trade1.getFee().getValue().compareTo(new BigDecimal("1")));
        assertEquals("USDT", trade1.getFee().getCurrency());
        assertEquals("Trade 01", trade1.getUserReference());
        assertTrue(createZonedDateTime("01-08-2020").isEqual(trade1.getTimestamp()));

        // Retrieving order 1 with findByOrderId().
        Optional<Trade> trade1Bis = tradeRepository.findByTradeId("BACKUP_TRADE_01");
        assertTrue(trade1Bis.isPresent());
        assertEquals(trade1, trade1Bis.get());

        // Trade 02.
        Trade trade2 = trades.next();
        assertEquals(2, trade2.getUid());
        assertEquals("BACKUP_TRADE_02", trade2.getTradeId());
        assertEquals(BID, trade2.getType());
        assertEquals("BACKUP_OPENING_ORDER_03", trade2.getOrder().getOrderId());
        assertEquals("BTC/USDT", trade2.getCurrencyPair());
        assertEquals(0, trade2.getAmount().getValue().compareTo(new BigDecimal("20")));
        assertEquals("BTC", trade2.getAmount().getCurrency());
        assertEquals(0, trade2.getPrice().getValue().compareTo(new BigDecimal("20")));
        assertEquals("USDT", trade2.getPrice().getCurrency());
        assertEquals(0, trade2.getFee().getValue().compareTo(new BigDecimal("2")));
        assertEquals("USDT", trade2.getFee().getCurrency());
        assertEquals("Trade 02", trade2.getUserReference());
        assertTrue(createZonedDateTime("02-08-2020").isEqual(trade2.getTimestamp()));

        // Retrieving order 2 with findByOrderId().
        Optional<Trade> trade2Bis = tradeRepository.findByTradeId("BACKUP_TRADE_02");
        assertTrue(trade2Bis.isPresent());
        assertEquals(trade2, trade2Bis.get());

        // Trade 03.
        Trade trade3 = trades.next();
        assertEquals(3, trade3.getUid());
        assertEquals("BACKUP_TRADE_03", trade3.getTradeId());
        assertEquals(BID, trade3.getType());
        assertEquals("BACKUP_OPENING_ORDER_04", trade3.getOrder().getOrderId());
        assertEquals("BTC/USDT", trade3.getCurrencyPair());
        assertEquals(0, trade3.getAmount().getValue().compareTo(new BigDecimal("40")));
        assertEquals("BTC", trade3.getAmount().getCurrency());
        assertEquals(0, trade3.getPrice().getValue().compareTo(new BigDecimal("30")));
        assertEquals("USDT", trade3.getPrice().getCurrency());
        assertEquals(0, trade3.getFee().getValue().compareTo(new BigDecimal("3")));
        assertEquals("USDT", trade3.getFee().getCurrency());
        assertEquals("Trade 03", trade3.getUserReference());
        assertTrue(createZonedDateTime("03-08-2020").isEqual(trade3.getTimestamp()));

        // Retrieving order 3 with findByOrderId().
        Optional<Trade> trade3Bis = tradeRepository.findByTradeId("BACKUP_TRADE_03");
        assertTrue(trade3Bis.isPresent());
        assertEquals(trade3, trade3Bis.get());

        // Trade 04.
        Trade trade4 = trades.next();
        assertEquals(4, trade4.getUid());
        assertEquals("BACKUP_TRADE_04", trade4.getTradeId());
        assertEquals(ASK, trade4.getType());
        assertEquals("BACKUP_CLOSING_ORDER_01", trade4.getOrder().getOrderId());
        assertEquals("BTC/USDT", trade4.getCurrencyPair());
        assertEquals(0, trade4.getAmount().getValue().compareTo(new BigDecimal("20")));
        assertEquals("BTC", trade4.getAmount().getCurrency());
        assertEquals(0, trade4.getPrice().getValue().compareTo(new BigDecimal("40")));
        assertEquals("USDT", trade4.getPrice().getCurrency());
        assertEquals(0, trade4.getFee().getValue().compareTo(new BigDecimal("4")));
        assertEquals("USDT", trade4.getFee().getCurrency());
        assertEquals("Trade 04", trade4.getUserReference());
        assertTrue(createZonedDateTime("04-08-2020").isEqual(trade4.getTimestamp()));

        // Retrieving order 4 with findByOrderId().
        Optional<Trade> trade4Bis = tradeRepository.findByTradeId("BACKUP_TRADE_04");
        assertTrue(trade4Bis.isPresent());
        assertEquals(trade4, trade4Bis.get());

        // Trade 05.
        Trade trade5 = trades.next();
        assertEquals(5, trade5.getUid());
        assertEquals("BACKUP_TRADE_05", trade5.getTradeId());
        assertEquals(ASK, trade5.getType());
        assertEquals("BACKUP_CLOSING_ORDER_02", trade5.getOrder().getOrderId());
        assertEquals("ETH/USD", trade5.getCurrencyPair());
        assertEquals(0, trade5.getAmount().getValue().compareTo(new BigDecimal("40")));
        assertEquals("ETH", trade5.getAmount().getCurrency());
        assertEquals(0, trade5.getPrice().getValue().compareTo(new BigDecimal("40")));
        assertEquals("USD", trade5.getPrice().getCurrency());
        assertEquals(0, trade5.getFee().getValue().compareTo(new BigDecimal("5")));
        assertEquals("USD", trade5.getFee().getCurrency());
        assertEquals("Trade 05", trade5.getUserReference());
        assertTrue(createZonedDateTime("05-08-2020").isEqual(trade5.getTimestamp()));

        // Retrieving order 5 with findByOrderId().
        Optional<Trade> trade5Bis = tradeRepository.findByTradeId("BACKUP_TRADE_05");
        assertTrue(trade5Bis.isPresent());
        assertEquals(trade5, trade5Bis.get());
    }

}
