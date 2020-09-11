package tech.cassandre.trading.bot.test.modes.dry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TRADE_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
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
@SetSystemProperty(key = PARAMETER_DRY, value = "true")
@SetSystemProperty(key = PARAMETER_USERNAME, value = PARAMETER_USERNAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_PASSPHRASE, value = PARAMETER_PASSPHRASE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_KEY, value = PARAMETER_KEY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SECRET, value = PARAMETER_SECRET_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_TRADE_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SpringBootTest
@ActiveProfiles("schedule-disabled")
@Import(TradeServiceDryModeTestMock.class)
@DisplayName("tradeService in dry mode")
public class TradeServiceDryModeTest extends BaseTest {

    private static final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Create buy and sell order")
    public void createBuyAndSellOrderTest() throws InterruptedException {
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
        final OrderCreationResultDTO buyMarketOrder01 = tradeService.createBuyMarketOrder(cp, new BigDecimal("0.001"));
        assertTrue(buyMarketOrder01.isSuccessful());
        assertEquals(orderId01, buyMarketOrder01.getOrderId());

        // Testing the received order.
        with().await().until(() -> strategy.getOrdersUpdateReceived().stream().anyMatch(o -> o.getId().equals(orderId01)));
        final Optional<OrderDTO> order01 = strategy.getOrdersUpdateReceived().stream().filter(o -> o.getId().equals(orderId01)).findFirst();
        assertTrue(order01.isPresent());
        assertEquals(orderId01, order01.get().getId());
        assertEquals(cp, order01.get().getCurrencyPair());
        assertEquals(new BigDecimal("0.001"), order01.get().getOriginalAmount());
        assertEquals(new BigDecimal("0.2"), order01.get().getAveragePrice());
        assertEquals(BID, order01.get().getType());

        // Testing the received trade.
        with().await().until(() -> strategy.getTradesUpdateReceived().stream().anyMatch(o -> o.getId().equals(tradeId01)));
        final Optional<TradeDTO> trade01 = strategy.getTradesUpdateReceived().stream().filter(o -> o.getId().equals(tradeId01)).findFirst();
        assertTrue(trade01.isPresent());
        assertEquals(tradeId01, trade01.get().getId());
        assertEquals(orderId01, trade01.get().getOrderId());
        assertEquals(cp, trade01.get().getCurrencyPair());
        assertEquals(new BigDecimal("0.001"), trade01.get().getOriginalAmount());
        assertEquals(new BigDecimal("0.2"), trade01.get().getPrice());
        assertEquals(BID, trade01.get().getType());

        // We create a sell order to check order numbers and type.
        final OrderCreationResultDTO buyMarketOrder02 = tradeService.createSellMarketOrder(cp, new BigDecimal("0.002"));
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
        Thread.sleep(TEN_SECONDS);
        assertEquals(2, tradeService.getOpenOrders().size());
        assertFalse(tradeService.getOpenOrderByOrderId("NON_EXISTING").isPresent());
        assertTrue(tradeService.getOpenOrderByOrderId(orderId01).isPresent());
        assertTrue(tradeService.getOpenOrderByOrderId(orderId02).isPresent());
        assertEquals(2, tradeService.getTrades().size());
    }

}
