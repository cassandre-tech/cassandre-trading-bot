package tech.cassandre.trading.bot.test.service.dry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.mock.service.dry.TradeServiceDryModeTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Service - Dry - Trade service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(TradeServiceDryModeTestMock.class)
public class TradeServiceDryModeTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TickerFlux tickerFlux;

    @Test
    @DisplayName("Check buy and sell order creation")
    public void checkCreateBuyAndSellOrder() throws InterruptedException {
        tickerFlux.update();

        // What we expect.
        final String orderId01 = "DRY_ORDER_000000001";
        final String tradeId01 = "DRY_TRADE_000000001";
        final String orderId02 = "DRY_ORDER_000000002";
        final String tradeId02 = "DRY_TRADE_000000002";

        // Check that everything is empty.
        assertEquals(0, tradeService.getOrders().size());
        assertEquals(0, tradeService.getTrades().size());

        // We create a buy order.
        final OrderCreationResultDTO buyMarketOrder01 = strategy.createBuyMarketOrder(cp1, new BigDecimal("0.001"));
        assertTrue(buyMarketOrder01.isSuccessful());
        assertEquals(orderId01, buyMarketOrder01.getOrderId());

        // Testing the received order.
        with().await().until(() -> strategy.getOrdersUpdateReceived().stream().anyMatch(o -> o.getOrderId().equals(orderId01)));
        final Optional<OrderDTO> order01 = strategy.getOrdersUpdateReceived().stream().filter(o -> o.getOrderId().equals(orderId01)).findFirst();
        assertTrue(order01.isPresent());
        assertEquals(orderId01, order01.get().getOrderId());
        assertEquals(cp1, order01.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.001").compareTo(order01.get().getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), order01.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.2").compareTo(order01.get().getAveragePrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), order01.get().getAveragePrice().getCurrency());
        assertEquals(BID, order01.get().getType());

        // Testing the received trade.
        with().await().until(() -> strategy.getTradesUpdateReceived().stream().anyMatch(o -> o.getTradeId().equals(tradeId01)));
        final Optional<TradeDTO> trade01 = strategy.getTradesUpdateReceived().stream().filter(o -> o.getTradeId().equals(tradeId01)).findFirst();
        assertTrue(trade01.isPresent());
        assertEquals(tradeId01, trade01.get().getTradeId());
        assertEquals(orderId01, trade01.get().getOrderId());
        assertEquals(cp1, trade01.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.001").compareTo(trade01.get().getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), trade01.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.2").compareTo(trade01.get().getPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), trade01.get().getPrice().getCurrency());
        assertEquals(BID, trade01.get().getType());

        // We create a sell order to check order numbers and type.
        final OrderCreationResultDTO buyMarketOrder02 = strategy.createSellMarketOrder(cp1, new BigDecimal("0.002"));
        assertTrue(buyMarketOrder02.isSuccessful());
        assertEquals(orderId02, buyMarketOrder02.getOrderId());

        // Testing the received order.
        with().await().until(() -> strategy.getOrdersUpdateReceived().stream().anyMatch(o -> o.getOrderId().equals(orderId02)));
        final Optional<OrderDTO> order02 = strategy.getOrdersUpdateReceived().stream().filter(o -> o.getOrderId().equals(orderId02)).findFirst();
        assertTrue(order02.isPresent());
        assertEquals(ASK, order02.get().getType());

        // Testing the received trade.
        with().await().until(() -> strategy.getTradesUpdateReceived().stream().anyMatch(o -> o.getTradeId().equals(tradeId02)));
        final Optional<TradeDTO> trade02 = strategy.getTradesUpdateReceived().stream().filter(o -> o.getTradeId().equals(tradeId02)).findFirst();
        assertTrue(trade02.isPresent());
        assertEquals(ASK, trade02.get().getType());

        // Testing retrieve methods.
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(2, tradeService.getOrders().size());
        assertFalse(tradeService.getOrders().stream().anyMatch(o -> o.getOrderId().equals("NON_EXISTING")));
        assertTrue(tradeService.getOrders().stream().anyMatch(o -> o.getOrderId().equals(orderId01)));
        assertTrue(tradeService.getOrders().stream().anyMatch(o -> o.getOrderId().equals(orderId02)));
        assertEquals(2, tradeService.getTrades().size());
    }

}
