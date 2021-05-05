package tech.cassandre.trading.bot.integration.binance;

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
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.name=${BINANCE_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${BINANCE_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${BINANCE_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${BINANCE_KEY}",
        "cassandre.trading.bot.exchange.secret=${BINANCE_SECRET}",
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
@DisplayName("Binance - Trade service")
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
    @CaseId(105)
    @Tag("integration")
    @DisplayName("Check cancel an order")
    @Disabled("Seems Coinbase pro doesn't support canceling an order")
    public void checkCancelOrder() {
        final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

        // Making a buy limit order (Buy 0.0001 ETH).
        final OrderCreationResultDTO result1 = strategy.createSellLimitOrder(cp, new BigDecimal("0.01"), new BigDecimal("10000000"));
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
}
