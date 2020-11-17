package tech.cassandre.trading.bot.test.backup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Backup - Trades")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql"),
        @Property(key = "spring.jpa.hibernate.ddl-auto", value = "create-drop"),
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class  TradeBackupTest extends BaseTest {

    @Autowired
    private PositionService positionService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @DisplayName("Check restored trades")
    public void checkRestoredTrades() {
        // =============================================================================================================
        // Check that positions and trades are restored in strategy & services.
        assertEquals(5, strategy.getPositions().size());
        assertEquals(5, positionService.getPositions().size());
        assertEquals(10, strategy.getTrades().size());
        assertEquals(10, tradeService.getTrades().size());
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());

        // Check trade 01.
        TradeDTO t = strategy.getTrades().get("BACKUP_TRADE_01");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_01", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_02", t.getOrderId());
        assertEquals(BID, t.getType());
        assertEquals(0, new BigDecimal("20").compareTo(t.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(t.getPrice()));
        assertEquals(createZonedDateTime("01-08-2020"), t.getTimestamp());
        assertEquals(0, new BigDecimal("1").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        // Check trade 02.
        t = strategy.getTrades().get("BACKUP_TRADE_02");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_02", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_03", t.getOrderId());
        assertEquals(BID, t.getType());
        assertEquals(0, new BigDecimal("30").compareTo(t.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.getPrice()));
        assertEquals(createZonedDateTime("02-08-2020"), t.getTimestamp());
        assertEquals(0, new BigDecimal("2").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        // Check trade 03.
        t = strategy.getTrades().get("BACKUP_TRADE_03");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_03", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_04", t.getOrderId());
        assertEquals(BID, t.getType());
        assertEquals(0, new BigDecimal("40").compareTo(t.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(t.getPrice()));
        assertEquals(createZonedDateTime("03-08-2020"), t.getTimestamp());
        assertEquals(0, new BigDecimal("3").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        // Check trade 04.
        t = strategy.getTrades().get("BACKUP_TRADE_04");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_04", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_05", t.getOrderId());
        assertEquals(ASK, t.getType());
        assertEquals(0, new BigDecimal("40").compareTo(t.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.getPrice()));
        assertEquals(createZonedDateTime("04-08-2020"), t.getTimestamp());
        assertEquals(0, new BigDecimal("4").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        // Check trade 05.
        t = strategy.getTrades().get("BACKUP_TRADE_05");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_05", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_06", t.getOrderId());
        assertEquals(ASK, t.getType());
        assertEquals(0, new BigDecimal("50").compareTo(t.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(ETH, USD), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(t.getPrice()));
        assertEquals(createZonedDateTime("05-08-2020"), t.getTimestamp());
        assertEquals(0, new BigDecimal("5").compareTo(t.getFee().getValue()));
        assertEquals(USD, t.getFee().getCurrency());
    }

    @Test
    @DisplayName("Check saved trades")
    public void checkSavedTrades() {
        // =============================================================================================================
        // Check that positions and trades are restored in strategy & services.
        assertEquals(5, strategy.getPositions().size());
        assertEquals(5, positionService.getPositions().size());
        assertEquals(10, strategy.getTrades().size());
        assertEquals(10, tradeService.getTrades().size());
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());

        // =============================================================================================================
        // Add a trade and check that it's correctly saved in database.
        long tradeCount = tradeRepository.count();
        TradeDTO t1 = TradeDTO.builder()
                .id("BACKUP_TRADE_06")
                .orderId("EMPTY")
                .type(BID)
                .originalAmount(new BigDecimal("1.100001"))
                .currencyPair(new CurrencyPairDTO(USDT, BTC))
                .price(new BigDecimal("2.200002"))
                .timestamp(createZonedDateTime("01-09-2020"))
                .feeAmount(new BigDecimal("3.300003"))
                .feeCurrency(BTC)
                .create();
        tradeFlux.emitValue(t1);

        // Wait until it is saved & check results.
        await().untilAsserted(() -> assertEquals(tradeCount + 1, tradeRepository.count()));
        Optional<Trade> t1FromDatabase = tradeRepository.findById("BACKUP_TRADE_06");
        assertTrue(t1FromDatabase.isPresent());
        assertEquals("BACKUP_TRADE_06", t1FromDatabase.get().getId());
        assertEquals("EMPTY", t1FromDatabase.get().getOrderId());
        assertEquals("BID", t1FromDatabase.get().getType());
        assertEquals(0, t1FromDatabase.get().getOriginalAmount().compareTo(new BigDecimal("1.100001")));
        assertEquals("USDT/BTC", t1FromDatabase.get().getCurrencyPair());
        assertEquals(0, t1FromDatabase.get().getPrice().compareTo(new BigDecimal("2.200002")));
        assertEquals(createZonedDateTime("01-09-2020"), t1FromDatabase.get().getTimestamp());
        assertEquals(0, t1FromDatabase.get().getFeeAmount().compareTo(new BigDecimal("3.300003")));
        assertEquals("BTC", t1FromDatabase.get().getFeeCurrency());
    }

}
