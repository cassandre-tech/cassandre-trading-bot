package tech.cassandre.trading.bot.test.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest(properties = {"POSITIONS_STRATEGY_ENABLED=true", "SIMPLE_ORDER_STRATEGY_ENABLED=false"})
@ComponentScan("tech.cassandre.trading.bot")
@Import(TickerFluxMock.class)
@DisplayName("Positions strategy test")
@DirtiesContext(classMode = BEFORE_CLASS)
public class PositionsStrategyTest extends BaseTest {

    @Autowired
    private PositionsStrategy positionsStrategy;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TickerFluxMock tickerFluxMock;

    @Autowired
    private MarketService marketService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Check strategy behavior")
    public void checkStrategyBehavior() {
        CurrencyPairDTO BTC_USDT = new CurrencyPairDTO(BTC, USDT);

        // We wait for all the tickers to be treated.
        // BTC/USDT => 7.
        with().await().untilAsserted(() -> assertEquals(7, positionsStrategy.getTickersUpdateReceived().size()));
        with().await().untilAsserted(() -> assertEquals(9, orderRepository.count()));
        with().await().untilAsserted(() -> assertEquals(9, tradeRepository.count()));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // =============================================================================================================
        // We check the three positions have really been closed.
        final Optional<PositionDTO> position1 = positionsStrategy.getPositionByPositionId(1L);
        assertTrue(position1.isPresent());
        assertEquals(CLOSED, position1.get().getStatus());
        final Optional<PositionDTO> position2 = positionsStrategy.getPositionByPositionId(2L);
        assertTrue(position2.isPresent());
        assertEquals(CLOSED, position2.get().getStatus());
        final Optional<PositionDTO> position3 = positionsStrategy.getPositionByPositionId(3L);
        assertTrue(position3.isPresent());
        assertEquals(CLOSED, position3.get().getStatus());
        final Optional<PositionDTO> position4 = positionsStrategy.getPositionByPositionId(4L);
        assertTrue(position4.isPresent());
        assertEquals(CLOSED, position4.get().getStatus());

        // =============================================================================================================
        // We check the three positions gains.

        // Long position n°1 (rules : 200.0 % gain).
        //  Opening order: 20 000 USDT.
        //  Closed with trade DRY_TRADE_000000007 : 70 000 USDT.
        //  250 % evolution => ((70000 - 20000) / 20000) * 100 = 250 %
        // We must have 200% gain according to the rule so the closing trade must be at 60 000 USDT.
        GainDTO position1Gain = position1.get().getGain();
        assertEquals(200f, position1Gain.getPercentage());
        assertEquals(0, new BigDecimal("60000").compareTo(position1.get()
                .getClosingOrder()
                .getTrades()
                .iterator().next()
                .getPrice().getValue()));

        // Long position n°2 (rules : 20.0 % loss).
        //  Opening order: 50 000 USDT.
        //  Closed with trade DRY_TRADE_000000004: 30 000 USDT.
        //  -40 % evolution => ((30000 - 50000) / 50000) * 100 = -40 %
        GainDTO position2Gain = position2.get().getGain();
        assertEquals(-20f, position2Gain.getPercentage());
        assertEquals(0, new BigDecimal("40000").compareTo(position2.get()
                .getClosingOrder()
                .getTrades()
                .iterator().next()
                .getPrice().getValue()));

        // Short position n°3 (rules : 10.0 % loss)
        //  Opening order: 40 000 USDT.
        //  Closed with trade DRY_TRADE_000000008: 70 000 USDT.
        //  It's a shot position so:
        //  We sold 1 bitcoin for 40 000 USDT.
        //  When the price reached 70 000 USDT, with the 40 000 USDT, we could buy 0.57 BTC.
        //  We had 1 BTC, we now only have 0.57 BTC
        //  -43 % evolution => ((0.57 - 1) / 1) * 100 = -43 %
        GainDTO position3Gain = position3.get().getGain();
        assertEquals(-10f, position3Gain.getPercentage());
        assertEquals(0, new BigDecimal("44444.44444444").compareTo(position3.get()
                .getClosingOrder()
                .getTrades()
                .iterator().next()
                .getPrice().getValue()));

        // Short position n°4 (rules : 100.0 % gain)
        //  Opening order: 70 000 USDT.
        //  Closed with DRY_TRADE_000000009: 25 000 USDT.
        //  It's a shot position so:
        //  We sold one bitcoin for 70 000 USDT.
        //  When the price reached 25 000 USDT, with the 70 000 USDT, we could buy 2.8 BTC.
        //  180 % evolution => ((2.8 - 1) / 1) * 100 = 180 %
        GainDTO position4Gain = position4.get().getGain();
        assertEquals(100f, position4Gain.getPercentage());
        assertEquals(0, new BigDecimal("35000").compareTo(position4.get()
                .getClosingOrder()
                .getTrades()
                .iterator().next()
                .getPrice().getValue()));

    }

}
