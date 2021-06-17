package tech.cassandre.trading.bot.issues.v4_x.v4_0_0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

/**
 * Save local order before saving distant order.
 * Issue : https://github.com/cassandre-tech/cassandre-trading-bot/issues/427
 */
@SpringBootTest
@ActiveProfiles("schedule-disabled")
@DisplayName("Github issue 427")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(Issue427TestMock.class)
public class Issue427Test extends BaseTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderFlux orderFlux;

    @Test
    @DisplayName("Save local order before saving distant order")
    public void checkSaveLocalOrderBeforeRemote() throws InterruptedException {
        // Check that a distant order is not saved before the local order is created.

        // Call getOrders to retrieve the distant order.
        orderFlux.update();

        // We wait a bit, the order should not be here as the local order is not saved.
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(0, orderRepository.count());

        // The local order is saved
        orderFlux.emitValue(OrderDTO.builder()
                .orderId("ORDER_000001")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", ETH_BTC.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", ETH_BTC.getQuoteCurrency()))
                .leverage("leverage2")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", ETH_BTC.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .build());

        // We wait a bit, the local order should be here.
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(1, orderRepository.count());
        Optional<Order> o = orderRepository.findByOrderId("ORDER_000001");
        assertTrue(o.isPresent());
        assertEquals("leverage2", o.get().getLeverage());

        // Call getOrders to retrieve the distant order.
        orderFlux.update();

        // We wait a bit, the order in database should be updated with the distant one.
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(1, orderRepository.count());
        o = orderRepository.findByOrderId("ORDER_000001");
        assertTrue(o.isPresent());
        assertEquals("leverage1", o.get().getLeverage());
    }

}
