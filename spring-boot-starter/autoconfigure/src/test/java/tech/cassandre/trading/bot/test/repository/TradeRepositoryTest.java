package tech.cassandre.trading.bot.test.repository;

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
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/backup.yaml")
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
        Trade t = trades.next();
        assertEquals(1, t.getId());
        assertEquals("BACKUP_TRADE_01", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("BACKUP_OPENING_ORDER_02", t.getOrder().getOrderId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getAmount().getValue().compareTo(new BigDecimal("20")));
        assertEquals("BTC", t.getAmount().getCurrency());
        assertEquals(0, t.getPrice().getValue().compareTo(new BigDecimal("10")));
        assertEquals("USDT", t.getPrice().getCurrency());
        assertEquals(0, t.getFee().getValue().compareTo(new BigDecimal("1")));
        assertEquals("USDT", t.getFee().getCurrency());
        assertEquals("Trade 01", t.getUserReference());
        assertEquals(createZonedDateTime("01-08-2020"), t.getTimestamp());

        // Retrieving order 1 with findByOrderId().
        Optional<Trade> tBis = tradeRepository.findByTradeId("BACKUP_TRADE_01");
        assertTrue(tBis.isPresent());
        assertEquals(t, tBis.get());

        // Trade 02.
        t = trades.next();
        assertEquals(2, t.getId());
        assertEquals("BACKUP_TRADE_02", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("BACKUP_OPENING_ORDER_03", t.getOrder().getOrderId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getAmount().getValue().compareTo(new BigDecimal("20")));
        assertEquals("BTC", t.getAmount().getCurrency());
        assertEquals(0, t.getPrice().getValue().compareTo(new BigDecimal("20")));
        assertEquals("USDT", t.getPrice().getCurrency());
        assertEquals(0, t.getFee().getValue().compareTo(new BigDecimal("2")));
        assertEquals("USDT", t.getFee().getCurrency());
        assertEquals("Trade 02", t.getUserReference());
        assertEquals(createZonedDateTime("02-08-2020"), t.getTimestamp());

        // Retrieving order 2 with findByOrderId().
        tBis = tradeRepository.findByTradeId("BACKUP_TRADE_02");
        assertTrue(tBis.isPresent());
        assertEquals(t, tBis.get());

        // Trade 03.
        t = trades.next();
        assertEquals(3, t.getId());
        assertEquals("BACKUP_TRADE_03", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("BACKUP_OPENING_ORDER_04", t.getOrder().getOrderId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getAmount().getValue().compareTo(new BigDecimal("40")));
        assertEquals("BTC", t.getAmount().getCurrency());
        assertEquals(0, t.getPrice().getValue().compareTo(new BigDecimal("30")));
        assertEquals("USDT", t.getPrice().getCurrency());
        assertEquals(0, t.getFee().getValue().compareTo(new BigDecimal("3")));
        assertEquals("USDT", t.getFee().getCurrency());
        assertEquals("Trade 03", t.getUserReference());
        assertEquals(createZonedDateTime("03-08-2020"), t.getTimestamp());

        // Retrieving order 3 with findByOrderId().
        tBis = tradeRepository.findByTradeId("BACKUP_TRADE_03");
        assertTrue(tBis.isPresent());
        assertEquals(t, tBis.get());

        // Trade 04.
        t = trades.next();
        assertEquals(4, t.getId());
        assertEquals("BACKUP_TRADE_04", t.getTradeId());
        assertEquals(ASK, t.getType());
        assertEquals("BACKUP_CLOSING_ORDER_01", t.getOrder().getOrderId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getAmount().getValue().compareTo(new BigDecimal("20")));
        assertEquals("BTC", t.getAmount().getCurrency());
        assertEquals(0, t.getPrice().getValue().compareTo(new BigDecimal("40")));
        assertEquals("USDT", t.getPrice().getCurrency());
        assertEquals(0, t.getFee().getValue().compareTo(new BigDecimal("4")));
        assertEquals("USDT", t.getFee().getCurrency());
        assertEquals("Trade 04", t.getUserReference());
        assertEquals(createZonedDateTime("04-08-2020"), t.getTimestamp());

        // Retrieving order 4 with findByOrderId().
        tBis = tradeRepository.findByTradeId("BACKUP_TRADE_04");
        assertTrue(tBis.isPresent());
        assertEquals(t, tBis.get());

        // Trade 05.
        t = trades.next();
        assertEquals(5, t.getId());
        assertEquals("BACKUP_TRADE_05", t.getTradeId());
        assertEquals(ASK, t.getType());
        assertEquals("BACKUP_CLOSING_ORDER_02", t.getOrder().getOrderId());
        assertEquals("ETH/USD", t.getCurrencyPair());
        assertEquals(0, t.getAmount().getValue().compareTo(new BigDecimal("40")));
        assertEquals("ETH", t.getAmount().getCurrency());
        assertEquals(0, t.getPrice().getValue().compareTo(new BigDecimal("40")));
        assertEquals("USD", t.getPrice().getCurrency());
        assertEquals(0, t.getFee().getValue().compareTo(new BigDecimal("5")));
        assertEquals("USD", t.getFee().getCurrency());
        assertEquals("Trade 05", t.getUserReference());
        assertEquals(createZonedDateTime("05-08-2020"), t.getTimestamp());

        // Retrieving order 5 with findByOrderId().
        tBis = tradeRepository.findByTradeId("BACKUP_TRADE_05");
        assertTrue(tBis.isPresent());
        assertEquals(t, tBis.get());
    }

}
