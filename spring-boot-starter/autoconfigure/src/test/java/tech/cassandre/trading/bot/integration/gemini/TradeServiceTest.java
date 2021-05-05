package tech.cassandre.trading.bot.integration.gemini;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.strategy.basic.TestableCassandreStrategy;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.name=${GEMINI_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${GEMINI_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${GEMINI_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${GEMINI_KEY}",
        "cassandre.trading.bot.exchange.secret=${GEMINI_SECRET}",
        "cassandre.trading.bot.exchange.rates.account=100",
        "cassandre.trading.bot.exchange.rates.ticker=101",
        "cassandre.trading.bot.exchange.rates.trade=102",
        "cassandre.trading.bot.database.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver",
        "cassandre.trading.bot.database.datasource.url=jdbc:hsqldb:mem:cassandre-database;shutdown=true",
        "cassandre.trading.bot.database.datasource.username=sa",
        "cassandre.trading.bot.database.datasource.password=",
        "testableStrategy.enabled=true",
        "invalidStrategy.enabled=false"
})
@DisplayName("Gemini - Trade service")
public class TradeServiceTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeService tradeService;

    @BeforeEach
    public void setUp() {
        tradeService.getOrders().forEach(order -> tradeService.cancelOrder(order.getOrderId(), null));
    }

    @Test
    @CaseId(94)
    @Tag("integration")
    @DisplayName("Check creates a buy/sell market order")
    @Disabled("Gemini doesn't support market order")
    public void checkCreateBuySellMarketOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // =============================================================================================================
        // Making a buy market order with a size below the minimum requirement. Testing error management.
        final OrderCreationResultDTO result1 = strategy.createBuyMarketOrder(cp, new BigDecimal("0.00000001"));
        assertFalse(result1.isSuccessful());
        assertNull(result1.getOrder().getOrderId());
        assertEquals("TradeService - Error calling createBuyMarketOrder : Order size below the minimum requirement.", result1.getErrorMessage());
        assertNotNull(result1.getException());

        // =============================================================================================================
        // Making a buy market order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result2 = strategy.createBuyMarketOrder(cp, new BigDecimal("0.0001"));
        assertTrue(result2.isSuccessful());
        assertNotNull(result2.getOrder().getOrderId());
        assertNull(result2.getErrorMessage());
        assertNull(result2.getException());

        // =============================================================================================================
        // Refunding the account.
        final OrderCreationResultDTO result3 = strategy.createSellMarketOrder(cp, new BigDecimal("0.0001"));
        assertTrue(result3.isSuccessful());
    }

    @Test
    @CaseId(95)
    @Tag("integration")
    @DisplayName("Check creates a buy limit order")
    @Disabled("Gemini doesn't support market order")
    public void checkCreateBuyLimitOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // =============================================================================================================
        // Making a buy limit order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result1 = strategy.createBuyLimitOrder(cp, new BigDecimal("0.0001"), new BigDecimal("0.000001"));
        assertTrue(result1.isSuccessful());
        assertNull(result1.getErrorMessage());
        assertNull(result1.getException());
        assertNotNull(result1.getOrder().getOrderId());

        // =============================================================================================================
        // Getting a non-existing order.
        assertFalse(tradeService.getOrders().stream().anyMatch(o -> o.getOrderId().equals("")));

        // =============================================================================================================
        // Getting the order and testing the data.
        final Optional<OrderDTO> order1 = tradeService.getOrders().stream().filter(o -> o.getOrderId().equals(result1.getOrder().getOrderId())).findFirst();
        assertTrue(order1.isPresent());
        assertNotNull(order1.get().getOrderId());
        assertEquals(result1.getOrder().getOrderId(), order1.get().getOrderId());
        assertEquals(BID, order1.get().getType());
        assertEquals(cp, order1.get().getCurrencyPair());
        assertEquals(0, order1.get().getAmount().getValue().compareTo(new BigDecimal("0.0001")));
        assertEquals(cp.getBaseCurrency(), order1.get().getAmount().getCurrency());
        assertTrue(order1.get().getAveragePrice().getValue().compareTo(ZERO) > 0);
        assertEquals(cp.getQuoteCurrency(), order1.get().getAveragePrice().getCurrency());
        assertEquals(0, order1.get().getLimitPrice().getValue().compareTo(new BigDecimal("0.000001")));
        assertEquals(cp.getQuoteCurrency(), order1.get().getLimitPrice().getCurrency());
        assertNull(order1.get().getLeverage());
        assertEquals(NEW, order1.get().getStatus());
        assertNotNull(order1.get().getCumulativeAmount());
        assertNull(order1.get().getUserReference());
        assertNotNull(order1.get().getTimestamp());
        assertTrue(order1.get().getTimestamp().isAfter(ZonedDateTime.now().minusMinutes(1)));
        assertTrue(order1.get().getTimestamp().isBefore(ZonedDateTime.now().plusMinutes(1)));

        // Cancel the order.
        tradeService.cancelOrder(result1.getOrder().getOrderId(), cp);
    }

    @Test
    @CaseId(96)
    @Tag("integration")
    @DisplayName("Check cancel an order")
    @Disabled("Gemini doesn't support market order")
    public void checkCancelOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // Making a buy limit order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result1 = strategy.createSellLimitOrder(cp, new BigDecimal("0.0001"), new BigDecimal("10000000"));
        assertNotNull(result1.getOrder().getOrderId());

        // The order must exist.
        await().untilAsserted(() -> assertTrue(tradeService.getOrders().stream().anyMatch(o -> o.getOrderId().equals(result1.getOrder().getOrderId()))));

        // Cancel the order.
        assertTrue(tradeService.cancelOrder(result1.getOrder().getOrderId(), cp));

        // The order must have disappeared.
        await().untilAsserted(() -> assertFalse(tradeService.getOrders().stream().anyMatch(o -> o.getOrderId().equals(result1.getOrder().getOrderId()))));

        // Cancel the order again and check it gives false.
        assertFalse(tradeService.cancelOrder(result1.getOrder().getOrderId(), cp));
    }

    @Test
    @CaseId(97)
    @Tag("integration")
    @DisplayName("Check get trades")
    @Disabled("Gemini doesn't support market order")
    public void checkGetTrades() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // Creates two orders of the same amount (one buy, one sell).
        final OrderCreationResultDTO result1 = strategy.createBuyMarketOrder(cp, new BigDecimal("0.0001"));
        final OrderCreationResultDTO result2 = strategy.createSellMarketOrder(cp, new BigDecimal("0.0001"));

        // Check that the two orders appears in the trade history.
        assertTrue(result1.isSuccessful());
        await().untilAsserted(() -> assertTrue(tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(result1.getOrder().getOrderId()))));
        assertNotNull(result2.getOrder().getOrderId());
        await().untilAsserted(() -> assertTrue(tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(result2.getOrder().getOrderId()))));

        // Retrieve trade & test values.
        final Optional<TradeDTO> t = tradeService.getTrades()
                .stream()
                .filter(trade -> trade.getOrderId().equals(result1.getOrder().getOrderId()))
                .findFirst();
        assertTrue(t.isPresent());
        assertNull(t.get().getId());
        assertNotNull(t.get().getTradeId());
        assertEquals(BID, t.get().getType());
        assertEquals(result1.getOrderId(), t.get().getOrderId());
        assertEquals(cp, t.get().getCurrencyPair());
        assertNotNull(t.get().getAmount().getValue());
        assertEquals(ETH, t.get().getAmount().getCurrency());
        assertNotNull(t.get().getPrice().getValue());
        assertEquals(BTC, t.get().getAmount().getCurrency());
        assertNotNull(t.get().getFee().getValue());
        assertNotNull(t.get().getFee().getCurrency());
        assertTrue(t.get().getTimestamp().isAfter(ZonedDateTime.now().minusMinutes(1)));
        assertTrue(t.get().getTimestamp().isBefore(ZonedDateTime.now().plusMinutes(1)));
    }

}
