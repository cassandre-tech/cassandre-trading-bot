package tech.cassandre.trading.bot.issues.v4_x.v4_1_1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.util.Optional;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;

@SpringBootTest
@DisplayName("Github issue 509")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/issue509.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue509Test extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @DisplayName("Fix empty openingOrder or closing order")
    public void checkEmptyOrderFix() throws InterruptedException {
        with().await().untilAsserted(() -> assertEquals(0, positionRepository.findByStatus(OPENING).size()));
        with().await().untilAsserted(() -> assertEquals(0, positionRepository.findByStatus(CLOSING).size()));

        // Error occurs on loading position 41 (CLOSING status).
        // INSERT INTO positions (id, position_id, type, fk_strategy_id, currency_pair, amount_value, amount_currency, rules_stop_gain_percentage, rules_stop_loss_percentage, status, fk_opening_order_id, opening_order_id, fk_closing_order_id, closing_order_id, lowest_gain_price_value, lowest_gain_price_currency, highest_gain_price_value, highest_gain_price_currency, latest_gain_price_value, latest_gain_price_currency, created_on, updated_on, force_closing)
        // VALUES (41, 41, 'LONG', 1, 'BTC/USDT', 0.00100000, 'BTC', 4, 15, 'CLOSING', 63, '604e6866217145000674031b', NULL, '605c81b212ec17000648322f', 50892.40000000, 'USDT', 60728.90000000, 'USDT', 50869.80000000, 'USDT', '2021-03-14 19:47:50.781799', '2021-03-25 12:27:30.481169', false);
        // The order can be opened and the closing order is indeed 605c81b212ec17000648322f.
        Optional<PositionDTO> position = strategy.getPositionByPositionId(41);
        assertTrue(position.isPresent());
        assertEquals("605c81b212ec17000648322f", position.get().getClosingOrder().getOrderId());

        // The closing order is this one - ID : 94 / ORDER_ID : 605c81b212ec17000648322f.
        // INSERT INTO orders (id, order_id, type, fk_strategy_id, currency_pair, amount_value, amount_currency, average_price_value, average_price_currency, limit_price_value, limit_price_currency, leverage, status, cumulative_amount_value, cumulative_amount_currency, user_reference, timestamp, created_on, updated_on)
        // VALUES (94, '605c81b212ec17000648322f', 'ASK', 1, 'BTC/USDT', 0.00100000, 'BTC', NULL, NULL, NULL, NULL, NULL, 'PENDING_NEW', NULL, NULL, NULL, '2021-03-25 12:27:30.472164', '2021-03-25 12:27:30.479807', NULL);
        // The order can be loaded.
        final Optional<OrderDTO> order = strategy.getOrderByOrderId("605c81b212ec17000648322f");
        assertTrue(order.isPresent());

        // The trade associated to order 94 is loading : ID : 114 / TRADE_ID : 605c81b22e113d29238803fa.
        // INSERT INTO trades (id, trade_id, type, fk_order_id, order_id, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
        // VALUES (114, '605c81b22e113d29238803fa', 'ASK', 94, '605c81b212ec17000648322f', 'BTC/USDT', 0.00100000, 'BTC', 50855.00000000, 'USDT', 0.05085500, 'USDT', NULL, '2021-03-25 12:27:30', '2021-03-25 12:27:33.774656', NULL);
        // The trade can be loaded.
        final Optional<TradeDTO> trade = strategy.getTradeByTradeId("605c81b22e113d29238803fa");
        assertTrue(trade.isPresent());

        // The problem is that the closing order is empty !
        assertNotNull(position.get().getClosingOrder());
    }

}
