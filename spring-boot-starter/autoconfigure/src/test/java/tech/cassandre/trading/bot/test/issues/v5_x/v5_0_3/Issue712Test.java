package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 712")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false"),
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/issues/issue712.yaml")
})
@Import(Issue712TestMock.class)
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue712Test {

    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Okex getOpenOrders() specific implementation")
    public void oKexGetOpenOrdersImplementation() {
        final Set<OrderDTO> orders = tradeService.getOrders();
        assertEquals(1, orders.size());
        final OrderDTO order = orders.iterator().next();
        assertEquals("Updated order !", order.getUserReference());
    }

}
