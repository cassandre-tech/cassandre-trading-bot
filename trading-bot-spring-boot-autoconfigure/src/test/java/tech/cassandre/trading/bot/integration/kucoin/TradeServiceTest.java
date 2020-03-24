package tech.cassandre.trading.bot.integration.kucoin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;

/**
 * Trade service tests.
 */
@SpringBootTest
@ActiveProfiles("schedule-disabled")
@DisplayName("Kucoin - Trade service")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.name=${KUCOIN_NAME}",
        "cassandre.trading.bot.exchange.sandbox=true",
        "cassandre.trading.bot.exchange.username=${KUCOIN_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${KUCOIN_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${KUCOIN_KEY}",
        "cassandre.trading.bot.exchange.secret=${KUCOIN_SECRET}",
        "cassandre.trading.bot.exchange.rates.account=100",
        "cassandre.trading.bot.exchange.rates.ticker=101",
        "cassandre.trading.bot.exchange.rates.order=102",
        "testableStrategy.enabled=true",
        "invalidStrategy.enabled=false"
})
public class TradeServiceTest extends BaseTest {

    /** Trade service. */
    @Autowired
    private TradeService tradeService;

    @BeforeEach
    public void setUp() {
        tradeService.getOpenOrders().forEach(order -> tradeService.cancelOrder(order.getId()));
    }

    @Test
    @DisplayName("Creates a buy / sell market order")
    public void testCreateBuySellMarketOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // =============================================================================================================
        // Making a buy market order with a size below the minimum requirement. Testing error management.
        final OrderCreationResultDTO result1 = tradeService.createBuyMarketOrder(cp, new BigDecimal("0.00000001"));
        assertTrue(result1.getOrderId().isEmpty());
        assertTrue(result1.getErrorMessage().isPresent());
        assertEquals("Error calling createBuyMarketOrder : Order size below the minimum requirement.", result1.getErrorMessage().get());
        assertTrue(result1.getException().isPresent());

        // =============================================================================================================
        // Making a buy market order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result2 = tradeService.createBuyMarketOrder(cp, new BigDecimal("0.0001"));
        assertTrue(result2.getOrderId().isPresent());
        assertTrue(result2.getErrorMessage().isEmpty());
        assertTrue(result2.getException().isEmpty());

        // Testing the order created.
        final String order2Id = result2.getOrderId().get();
        assertNotNull(order2Id);

        // =============================================================================================================
        // Refunding the account.
        final OrderCreationResultDTO result3 = tradeService.createSellMarketOrder(cp, new BigDecimal("0.0001"));
        assertTrue(result3.getOrderId().isPresent());
    }

    @Test
    @DisplayName("Creates a buy limit order")
    public void testCreateBuyLimitOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // =============================================================================================================
        // Making a buy limit order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result1 = tradeService.createBuyLimitOrder(cp, new BigDecimal("0.0001"), new BigDecimal("0.000001"));
        getLogger().info("Error message : " + result1.getErrorMessage());
        assertTrue(result1.getErrorMessage().isEmpty());
        assertTrue(result1.getException().isEmpty());
        assertTrue(result1.getOrderId().isPresent());

        // TODO Find why the new order doesn't appear in the open orders.
        // with().pollInterval(fibonacci(SECONDS)).await()
        //	.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
        //	.untilAsserted(() -> assertEquals(openOrdersCount + 1, tradeService.getOpenOrders().size()));

        // =============================================================================================================
        // Getting a non existing order.
        assertFalse(tradeService.getOpenOrderByOrderId("").isPresent());

        // =============================================================================================================
        // Getting the order and testing the data.
        final Optional<OrderDTO> order1 = tradeService.getOpenOrderByOrderId(result1.getOrderId().get());
        assertTrue(order1.isPresent());
        assertEquals(BID, order1.get().getType());
        assertEquals(0, order1.get().getOriginalAmount().compareTo(new BigDecimal("0.0001")));
        assertEquals(cp, order1.get().getCurrencyPair());
        assertEquals(result1.getOrderId().get(), order1.get().getId());
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
        tradeService.cancelOrder(result1.getOrderId().get());
    }

    @Test
    @DisplayName("Cancel an order")
    public void testCancelOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // Making a buy limit order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result1 = tradeService.createSellLimitOrder(cp, new BigDecimal("0.0001"), new BigDecimal("10000000"));
        assertTrue(result1.getOrderId().isPresent());

        // The order must exist.
        with().pollInterval(fibonacci(SECONDS)).await()
                .atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
                .untilAsserted(() -> assertTrue(tradeService.getOpenOrderByOrderId(result1.getOrderId().get()).isPresent()));

        // Cancel the order.
        assertTrue(tradeService.cancelOrder(result1.getOrderId().get()));

        // The order must have disappeared.
        with().pollInterval(fibonacci(SECONDS)).await()
                .atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
                .untilAsserted(() -> assertFalse(tradeService.getOpenOrderByOrderId(result1.getOrderId().get()).isPresent()));

        // Cancel the order again and check it gives false.
        assertFalse(tradeService.cancelOrder(result1.getOrderId().get()));
    }

}
