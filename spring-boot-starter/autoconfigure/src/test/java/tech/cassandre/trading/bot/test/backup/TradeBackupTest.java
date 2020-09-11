package tech.cassandre.trading.bot.test.backup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TRADE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USD;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ORDER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

@SetSystemProperty(key = PARAMETER_NAME, value = PARAMETER_NAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SANDBOX, value = PARAMETER_SANDBOX_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_DRY, value = "false")
@SetSystemProperty(key = PARAMETER_USERNAME, value = PARAMETER_USERNAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_PASSPHRASE, value = PARAMETER_PASSPHRASE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_KEY, value = PARAMETER_KEY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SECRET, value = PARAMETER_SECRET_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_TRADE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = "spring.datasource.data", value = "classpath:/backup.sql")
@SetSystemProperty(key = "spring.jpa.hibernate.ddl-auto", value = "create")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Backup (trades)")
public class TradeBackupTest extends BaseTest {

    /** Currency pair 1 used for test. */
    public static final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

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
        // Check that trades and restored in strategy, services & flux.
        assertTrue(strategy.getTradeService().getTrades().size() >= 5);
        assertTrue(strategy.getTrades().size() >= 5);
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());

        // Check trade 00.
        TradeDTO t = strategy.getTrades().get("BACKUP_TRADE_00");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_00", t.getId());
        assertEquals(new CurrencyPairDTO(ETH, USD), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("51").compareTo(t.getFee().getValue()));
        assertEquals(USD, t.getFee().getCurrency());
        assertEquals("TEMP", t.getOrderId());
        assertEquals(0, new BigDecimal("52").compareTo(t.getOriginalAmount()));
        assertEquals(0, new BigDecimal("53").compareTo(t.getPrice()));
        assertEquals(getZonedDateTime("05-08-2020"), t.getTimestamp());
        assertEquals(ASK, t.getType());
        // Check trade 01.
        t = strategy.getTrades().get("BACKUP_TRADE_01");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_01", t.getId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("11").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        assertEquals("BACKUP_OPEN_ORDER_02", t.getOrderId());
        assertEquals(0, new BigDecimal("12").compareTo(t.getOriginalAmount()));
        assertEquals(0, new BigDecimal("13").compareTo(t.getPrice()));
        assertEquals(getZonedDateTime("01-08-2020"), t.getTimestamp());
        assertEquals(BID, t.getType());
        // Check trade 02.
        t = strategy.getTrades().get("BACKUP_TRADE_02");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_02", t.getId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("21").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        assertEquals("BACKUP_OPEN_ORDER_03", t.getOrderId());
        assertEquals(0, new BigDecimal("22").compareTo(t.getOriginalAmount()));
        assertEquals(0, new BigDecimal("23").compareTo(t.getPrice()));
        assertEquals(getZonedDateTime("02-08-2020"), t.getTimestamp());
        assertEquals(BID, t.getType());
        // Check trade 03.
        t = strategy.getTrades().get("BACKUP_TRADE_03");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_03", t.getId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("31").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        assertEquals("BACKUP_OPEN_ORDER_04", t.getOrderId());
        assertEquals(0, new BigDecimal("32").compareTo(t.getOriginalAmount()));
        assertEquals(0, new BigDecimal("33").compareTo(t.getPrice()));
        assertEquals(getZonedDateTime("03-08-2020"), t.getTimestamp());
        assertEquals(BID, t.getType());
        // Check trade 04.
        t = strategy.getTrades().get("BACKUP_TRADE_04");
        assertNotNull(t);
        assertEquals("BACKUP_TRADE_04", t.getId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.getCurrencyPair());
        assertEquals(0, new BigDecimal("41").compareTo(t.getFee().getValue()));
        assertEquals(USDT, t.getFee().getCurrency());
        assertEquals("BACKUP_OPEN_ORDER_05", t.getOrderId());
        assertEquals(0, new BigDecimal("42").compareTo(t.getOriginalAmount()));
        assertEquals(0, new BigDecimal("43").compareTo(t.getPrice()));
        assertEquals(getZonedDateTime("04-08-2020"), t.getTimestamp());
        assertEquals(ASK, t.getType());
    }

    @Test
    @DisplayName("Check saved trades")
    public void checkSavedTrades() {
        // =============================================================================================================
        // Add two trades and check that they are saved.
        long tradeCount = tradeRepository.count();
        TradeDTO t1 = TradeDTO.builder()
                .id("BACKUP_TRADE_05")
                .orderId("EMPTY")
                .type(BID)
                .originalAmount(new BigDecimal("1.100001"))
                .currencyPair(new CurrencyPairDTO(USDT, BTC))
                .price(new BigDecimal("2.200002"))
                .timestamp(getZonedDateTime("01-09-2020"))
                .feeAmount(new BigDecimal("3.300003"))
                .feeCurrency(BTC)
                .create();
        tradeFlux.emitValue(t1);

        // Wait until it is saved & check results.
        await().untilAsserted(() -> assertEquals(tradeCount + 1, tradeRepository.count()));
        Optional<Trade> t1FromDatabase = tradeRepository.findById("BACKUP_TRADE_05");
        assertTrue(t1FromDatabase.isPresent());
        assertEquals("BACKUP_TRADE_05", t1FromDatabase.get().getId());
        assertEquals("USDT/BTC", t1FromDatabase.get().getCurrencyPair());
        assertEquals(0, t1FromDatabase.get().getFeeAmount().compareTo(new BigDecimal("3.300003")));
        assertEquals("BTC", t1FromDatabase.get().getFeeCurrency());
        assertEquals("EMPTY", t1FromDatabase.get().getOrderId());
        assertEquals(0, t1FromDatabase.get().getOriginalAmount().compareTo(new BigDecimal("1.100001")));
        assertEquals(0, t1FromDatabase.get().getPrice().compareTo(new BigDecimal("2.200002")));
        assertEquals(getZonedDateTime("01-09-2020"), t1FromDatabase.get().getTimestamp());
        assertEquals("BID", t1FromDatabase.get().getType());
    }

}
