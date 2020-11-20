package tech.cassandre.trading.bot.integration.kucoin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.name=${KUCOIN_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${KUCOIN_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${KUCOIN_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${KUCOIN_KEY}",
        "cassandre.trading.bot.exchange.secret=${KUCOIN_SECRET}",
        "cassandre.trading.bot.exchange.rates.account=100",
        "cassandre.trading.bot.exchange.rates.ticker=101",
        "cassandre.trading.bot.exchange.rates.trade=102",
        "spring.jpa.hibernate.ddl-auto=update",
        "cassandre.trading.bot.database.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver",
        "cassandre.trading.bot.database.datasource.url=jdbc:hsqldb:mem:cassandre-database;shutdown=true",
        "cassandre.trading.bot.database.datasource.username=sa",
        "cassandre.trading.bot.database.datasource.password=",
        "testableStrategy.enabled=true",
        "invalidStrategy.enabled=false"
})
@DisplayName("Kucoin - Trade service")
public class TradeServiceTest extends BaseTest {

    @Autowired
    private TradeService tradeService;

    @BeforeEach
    public void setUp() {
        tradeService.getOpenOrders().forEach(order -> tradeService.cancelOrder(order.getId()));
    }

    @Test
    @Tag("integration")
    @DisplayName("Check creates a buy / sell market order")
    public void checkCreateBuySellMarketOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // =============================================================================================================
        // Making a buy market order with a size below the minimum requirement. Testing error management.
        final OrderCreationResultDTO result1 = tradeService.createBuyMarketOrder(cp, new BigDecimal("0.00000001"));
        assertFalse(result1.isSuccessful());
        assertNull(result1.getOrderId());
        assertEquals("TradeService - Error calling createBuyMarketOrder : Order size below the minimum requirement.", result1.getErrorMessage());
        assertNotNull(result1.getException());

        // =============================================================================================================
        // Making a buy market order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result2 = tradeService.createBuyMarketOrder(cp, new BigDecimal("0.0001"));
        assertTrue(result2.isSuccessful());
        assertNotNull(result2.getOrderId());
        assertNull(result2.getErrorMessage());
        assertNull(result2.getException());

        // =============================================================================================================
        // Refunding the account.
        final OrderCreationResultDTO result3 = tradeService.createSellMarketOrder(cp, new BigDecimal("0.0001"));
        assertTrue(result3.isSuccessful());
    }

    @Test
    @Tag("integration")
    @DisplayName("Check creates a buy limit order")
    public void checkCreateBuyLimitOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // =============================================================================================================
        // Making a buy limit order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result1 = tradeService.createBuyLimitOrder(cp, new BigDecimal("0.0001"), new BigDecimal("0.000001"));
        getLogger().info("Error message : " + result1.getErrorMessage());
        assertTrue(result1.isSuccessful());
        assertNull(result1.getErrorMessage());
        assertNull(result1.getException());
        assertNotNull(result1.getOrderId());

        // =============================================================================================================
        // Getting a non existing order.
        assertFalse(tradeService.getOpenOrderByOrderId("").isPresent());

        // =============================================================================================================
        // Getting the order and testing the data.
        final Optional<OrderDTO> order1 = tradeService.getOpenOrderByOrderId(result1.getOrderId());
        assertTrue(order1.isPresent());
        assertEquals(BID, order1.get().getType());
        assertEquals(0, order1.get().getOriginalAmount().compareTo(new BigDecimal("0.0001")));
        assertEquals(cp, order1.get().getCurrencyPair());
        assertEquals(result1.getOrderId(), order1.get().getId());
        assertNull(order1.get().getUserReference());
        assertNotNull(order1.get().getTimestamp());
        assertTrue(order1.get().getTimestamp().isAfter(ZonedDateTime.now().minusMinutes(1)));
        assertTrue(order1.get().getTimestamp().isBefore(ZonedDateTime.now().plusMinutes(1)));
        assertEquals(OrderStatusDTO.NEW, order1.get().getStatus());
        assertNotNull(order1.get().getCumulativeAmount());
        assertTrue(order1.get().getAveragePrice().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(order1.get().getFee());
        assertEquals(0, order1.get().getLimitPrice().compareTo(new BigDecimal("0.000001")));

        // Cancel the order.
        tradeService.cancelOrder(result1.getOrderId());
    }

    @Test
    @Tag("integration")
    @DisplayName("Check cancel an order")
    public void checkCancelOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // Making a buy limit order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result1 = tradeService.createSellLimitOrder(cp, new BigDecimal("0.0001"), new BigDecimal("10000000"));
        assertNotNull(result1.getOrderId());

        // The order must exist.
        await().untilAsserted(() -> assertTrue(tradeService.getOpenOrderByOrderId(result1.getOrderId()).isPresent()));

        // Cancel the order.
        assertTrue(tradeService.cancelOrder(result1.getOrderId()));

        // The order must have disappeared.
        await().untilAsserted(() -> assertFalse(tradeService.getOpenOrderByOrderId(result1.getOrderId()).isPresent()));

        // Cancel the order again and check it gives false.
        assertFalse(tradeService.cancelOrder(result1.getOrderId()));
    }

    @Test
    @Tag("integration")
    @DisplayName("Check get trades")
    public void checkGetTrades() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // Creates two orders of the same amount (one buy, one sell).
        final OrderCreationResultDTO result1 = tradeService.createBuyMarketOrder(cp, new BigDecimal("0.0001"));
        final OrderCreationResultDTO result2 = tradeService.createSellMarketOrder(cp, new BigDecimal("0.0001"));

        // Check that the two orders appears in the trade history.
        assertTrue(result1.isSuccessful());
        await().untilAsserted(() -> assertTrue(tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(result1.getOrderId()))));
        assertNotNull(result2.getOrderId());
        await().untilAsserted(() -> assertTrue(tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(result2.getOrderId()))));
    }

}
