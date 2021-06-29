package tech.cassandre.trading.bot.issues.v4_x.v4_0_0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.FILLED;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

/**
 * Trade before order.
 * Issue : https://github.com/cassandre-tech/cassandre-trading-bot/issues/426
 */
@SpringBootTest
@ActiveProfiles("schedule-disabled")
@DisplayName("Github issue 426")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(Issue426TestMock.class)
public class Issue426Test extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @DisplayName("Errors if trades arrives before order")
    public void checkTradeBeforeOrder() throws InterruptedException {
        // First tickers - cp1 & cp2 (dry mode).
        // ETH, BTC - bid 0.2 / ask 0.2.
        // ETH, USDT - bid 0,3 / ask 0.3.
        tickerFlux.update();
        tickerFlux.update();

        // A first trade arrives for a non yet received order.
        // The first trade is duplicated in the result returned.
        tradeFlux.update();
        tradeFlux.update();

        // We wait a bit. Nothing should be saved.
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(0, orderRepository.count());
        assertEquals(0, tradeRepository.count());
        assertEquals(0, positionRepository.count());

        // Order arrives and is now stored.
        orderFlux.update();
        // Trade are updated and should now be stored and linked to the order.
        tradeFlux.update();

        // We wait a bit, the order and the trade should be here.
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(1, orderRepository.count());
        assertEquals(2, tradeRepository.count());
        assertEquals(0, positionRepository.count());

        // We check if we had the second value of order and trade.
        final Optional<OrderDTO> o = strategy.getOrderByOrderId("ORDER_000001");
        assertTrue(o.isPresent());
        assertEquals(FILLED, o.get().getStatus());
        assertEquals(2, o.get().getTrades().size());
        final Optional<TradeDTO> t1 = strategy.getTradeByTradeId("TRADE_000001");
        assertTrue(t1.isPresent());
        final Optional<TradeDTO> t2 = strategy.getTradeByTradeId("TRADE_000002");
        assertTrue(t2.isPresent());
    }

}
