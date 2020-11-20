package tech.cassandre.trading.bot.tmp.modes.dry;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.tmp.modes.dry.mocks.TradeServiceDryModeTestMock;
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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Dry mode - Trade service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Import(TradeServiceDryModeTestMock.class)
@Disabled
public class TradeServiceDryModeTest extends BaseTest {

    private static final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @Tag("notReviewed")
    @DisplayName("Check buy and sell order creation")
    public void checkCreateBuyAndSellOrder() throws InterruptedException {
        tickerFlux.update();

        // What we expect.
        final String orderId01 = "DRY_ORDER_000000001";
        final String tradeId01 = "DRY_TRADE_000000001";
        final String orderId02 = "DRY_ORDER_000000002";
        final String tradeId02 = "DRY_TRADE_000000002";

        // Check that everything is empty.
        assertEquals(0, tradeService.getOpenOrders().size());
        assertEquals(0, tradeService.getTrades().size());

        // We create a buy order.
        final OrderCreationResultDTO buyMarketOrder01 = tradeService.createBuyMarketOrder(cp1, new BigDecimal("0.001"));
        assertTrue(buyMarketOrder01.isSuccessful());
        assertEquals(orderId01, buyMarketOrder01.getOrderId());

        // Testing the received order.
        with().await().until(() -> strategy.getOrdersUpdateReceived().stream().anyMatch(o -> o.getId().equals(orderId01)));
        final Optional<OrderDTO> order01 = strategy.getOrdersUpdateReceived().stream().filter(o -> o.getId().equals(orderId01)).findFirst();
        assertTrue(order01.isPresent());
        assertEquals(orderId01, order01.get().getId());
        assertEquals(cp1, order01.get().getCurrencyPair());
        assertEquals(new BigDecimal("0.001"), order01.get().getOriginalAmount());
        assertEquals(new BigDecimal("0.2"), order01.get().getAveragePrice());
        assertEquals(BID, order01.get().getType());

        // Testing the received trade.
        with().await().until(() -> strategy.getTradesUpdateReceived().stream().anyMatch(o -> o.getId().equals(tradeId01)));
        final Optional<TradeDTO> trade01 = strategy.getTradesUpdateReceived().stream().filter(o -> o.getId().equals(tradeId01)).findFirst();
        assertTrue(trade01.isPresent());
        assertEquals(tradeId01, trade01.get().getId());
        assertEquals(orderId01, trade01.get().getOrderId());
        assertEquals(cp1, trade01.get().getCurrencyPair());
        assertEquals(new BigDecimal("0.001"), trade01.get().getOriginalAmount());
        assertEquals(new BigDecimal("0.2"), trade01.get().getPrice());
        assertEquals(BID, trade01.get().getType());

        // We create a sell order to check order numbers and type.
        final OrderCreationResultDTO buyMarketOrder02 = tradeService.createSellMarketOrder(cp1, new BigDecimal("0.002"));
        assertTrue(buyMarketOrder02.isSuccessful());
        assertEquals(orderId02, buyMarketOrder02.getOrderId());

        // Testing the received order.
        with().await().until(() -> strategy.getOrdersUpdateReceived().stream().anyMatch(o -> o.getId().equals(orderId02)));
        final Optional<OrderDTO> order02 = strategy.getOrdersUpdateReceived().stream().filter(o -> o.getId().equals(orderId02)).findFirst();
        assertTrue(order02.isPresent());
        assertEquals(ASK, order02.get().getType());

        // Testing the received trade.
        with().await().until(() -> strategy.getTradesUpdateReceived().stream().anyMatch(o -> o.getId().equals(tradeId02)));
        final Optional<TradeDTO> trade02 = strategy.getTradesUpdateReceived().stream().filter(o -> o.getId().equals(tradeId02)).findFirst();
        assertTrue(trade02.isPresent());
        assertEquals(ASK, trade02.get().getType());

        // Testing retrieve methods.
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(2, tradeService.getOpenOrders().size());
        assertFalse(tradeService.getOpenOrderByOrderId("NON_EXISTING").isPresent());
        assertTrue(tradeService.getOpenOrderByOrderId(orderId01).isPresent());
        assertTrue(tradeService.getOpenOrderByOrderId(orderId02).isPresent());
        assertEquals(2, tradeService.getTrades().size());
    }

}
