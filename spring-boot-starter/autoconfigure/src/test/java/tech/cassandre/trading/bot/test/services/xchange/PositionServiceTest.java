package tech.cassandre.trading.bot.test.services.xchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.services.xchange.mocks.PositionServiceTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.ZERO;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING_FAILURE;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING_FAILURE;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.CANCELED;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.FILLED;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.STOPPED;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Service - XChange - Position service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(PositionServiceTestMock.class)
public class PositionServiceTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private PositionFlux positionFlux;

    @Test
    @DisplayName("Check position creation")
    public void checkCreatePosition() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrder().getOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        assertEquals(LONG, positionService.getPositionById(1).get().getType());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = strategy.createLongPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertTrue(p2.isSuccessful());
        assertEquals(2, p2.getPosition().getId());
        assertEquals("ORDER00020", p2.getPosition().getOpeningOrder().getOrderId());
        assertNull(p2.getErrorMessage());
        assertNull(p2.getException());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        final PositionCreationResultDTO p3 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0003"),
                PositionRulesDTO.builder().stopGainPercentage(30f).stopLossPercentage(30f).build());
        assertFalse(p3.isSuccessful());
        assertNull(p3.getPosition());
        assertTrue(p3.getErrorMessage().contains("TradeService - Error calling createMarketOrder"));
        assertEquals("Error exception", p3.getException().getMessage());
        assertEquals(2, positionService.getPositions().size());
    }

    @Test
    @DisplayName("Check position order update")
    public void checkPositionOrderUpdate() {
        // =============================================================================================================
        // Creates two positions (1 & 2).

        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrder().getOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        long position1Id = p1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));

        // Check order created internally by Cassandre.
        final OrderDTO orderP1 = strategy.getOrdersUpdatesReceived().get(0);
        assertNotNull(orderP1);
        assertEquals("ORDER00010", orderP1.getOrderId());
        assertEquals(BID, orderP1.getType());
        assertNotNull(orderP1.getStrategy());
        assertEquals(1, orderP1.getStrategy().getId());
        assertEquals("01", orderP1.getStrategy().getStrategyId());
        assertEquals(ETH_BTC, orderP1.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(orderP1.getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), orderP1.getAmount().getCurrency());
        assertNull(orderP1.getLimitPrice());
        assertNull(orderP1.getLeverage());
        assertEquals(NEW, orderP1.getStatus());
        assertEquals(0, new BigDecimal("0.0001").compareTo(orderP1.getCumulativeAmount().getValue()));
        assertNull(orderP1.getUserReference());
        assertNotNull(orderP1.getTimestamp());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = strategy.createLongPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertTrue(p2.isSuccessful());
        assertEquals(2, p2.getPosition().getId());
        assertEquals("ORDER00020", p2.getPosition().getOpeningOrder().getOrderId());
        assertNull(p2.getErrorMessage());
        assertNull(p2.getException());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());
        long position2Id = p2.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(2, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdatesReceived().size()));

        // Position 1.
        Optional<PositionDTO> position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());

        // Opening order.
        OrderDTO p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(ETH_BTC, p1OpeningOrder.getCurrencyPair());
        assertEquals(NEW, p1OpeningOrder.getStatus());
        // Closing order.
        OrderDTO p1ClosingOrder = position1.get().getClosingOrder();
        assertNull(p1ClosingOrder);

        // Position 2.
        Optional<PositionDTO> position2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(position2.isPresent());
        // Opening order.
        OrderDTO p2OpeningOrder = position2.get().getOpeningOrder();
        assertNotNull(p2OpeningOrder);
        assertEquals("ORDER00020", p2OpeningOrder.getOrderId());
        assertEquals(ETH_USDT, p2OpeningOrder.getCurrencyPair());
        assertEquals(NEW, p1OpeningOrder.getStatus());
        // Closing order.
        OrderDTO p2ClosingOrder = position2.get().getClosingOrder();
        assertNull(p2ClosingOrder);

        // =============================================================================================================
        // An update for opening order ORDER00020 (position 2) arrives and change status.
        final long positionUpdateCount1 = strategy.getPositionsUpdatesReceived().size();
        OrderDTO order00020 = OrderDTO.builder()
                .orderId("ORDER00020")
                .type(BID)
                .strategy(strategyDTO)
                .currencyPair(ETH_USDT)
                .amount(new CurrencyAmountDTO("1.00001", ETH_USDT.getBaseCurrency()))
                .status(FILLED)
                .timestamp(ZonedDateTime.now())
                .build();
        orderFlux.emitValue(order00020);
        await().untilAsserted(() -> assertEquals(positionUpdateCount1 + 1, strategy.getPositionsUpdatesReceived().size()));

        // Position 1 - No changes.
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(ETH_BTC, p1OpeningOrder.getCurrencyPair());
        assertEquals(NEW, p1OpeningOrder.getStatus());
        // Closing order.
        p1ClosingOrder = position1.get().getClosingOrder();
        assertNull(p1ClosingOrder);

        // Position 2 - Order status changed.
        position2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(position2.isPresent());
        // Opening order.
        p2OpeningOrder = position2.get().getOpeningOrder();
        assertNotNull(p2OpeningOrder);
        assertEquals("ORDER00020", p2OpeningOrder.getOrderId());
        assertEquals(ETH_USDT, p2OpeningOrder.getCurrencyPair());
        assertEquals(FILLED, p2OpeningOrder.getStatus());
        // Closing order.
        p2ClosingOrder = position2.get().getClosingOrder();
        assertNull(p2ClosingOrder);

        // =============================================================================================================
        // We are now closing position 1 with a trade and setCloseOrderId.

        // We move the position 1 to OPENED.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0001", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        // We close position 1 with setClosingOrderId().
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        OrderDTO closingOrder01 = OrderDTO.builder()
                .orderId("CLOSING_ORDER_01")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.00001", ETH_USDT.getBaseCurrency()))
                .status(FILLED)
                .timestamp(ZonedDateTime.now())
                .build();
        position1.get().closePositionWithOrder(closingOrder01);
        positionFlux.emitValue(position1.get());

        // An update arrives and changes the status order of position 1.
        orderFlux.emitValue(closingOrder01);
        await().untilAsserted(() -> assertEquals(4, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(CLOSING, getPositionDTO(position1Id).getStatus()));

        // Position 1 - closing order status should have changed.
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(ETH_BTC, p1OpeningOrder.getCurrencyPair());
        assertEquals(NEW, p1OpeningOrder.getStatus());
        // Closing order.
        p1ClosingOrder = position1.get().getClosingOrder();
        assertNotNull(p1ClosingOrder);
        assertEquals("CLOSING_ORDER_01", p1ClosingOrder.getOrderId());
        assertEquals(ASK, p1ClosingOrder.getType());
        assertEquals(ETH_BTC, p1ClosingOrder.getCurrencyPair());
        assertEquals(FILLED, p1ClosingOrder.getStatus());

        // Position 2 - No change
        position2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(position2.isPresent());
        // Opening order.
        p2OpeningOrder = position2.get().getOpeningOrder();
        assertNotNull(p2OpeningOrder);
        assertEquals("ORDER00020", p2OpeningOrder.getOrderId());
        assertEquals(ETH_USDT, p2OpeningOrder.getCurrencyPair());
        assertEquals(FILLED, p2OpeningOrder.getStatus());
        // Closing order.
        p2ClosingOrder = position2.get().getClosingOrder();
        assertNull(p2ClosingOrder);
    }

    @Test
    @DisplayName("Check opening order failure")
    public void checkOpeningOrderFailure() {
        // =============================================================================================================
        // Creates a position. Then send an order update with an error.
        // The position must end up being in OPENING_FAILURE

        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrder().getOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        long position1Id = p1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));

        // Position 1.
        Optional<PositionDTO> position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        OrderDTO p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(ETH_BTC, p1OpeningOrder.getCurrencyPair());
        assertEquals(NEW, p1OpeningOrder.getStatus());
        // Closing order.
        OrderDTO p1ClosingOrder = position1.get().getClosingOrder();
        assertNull(p1ClosingOrder);

        // =============================================================================================================
        // An update for opening order ORDER00020 (position 2) arrives and change status with an error !
        OrderDTO order00010 = OrderDTO.builder()
                .orderId("ORDER00010")
                .type(BID)
                .strategy(strategyDTO)
                .amount(new CurrencyAmountDTO("0.00012", ETH_BTC.getBaseCurrency()))
                .currencyPair(ETH_BTC)
                .timestamp(ZonedDateTime.now())
                .status(STOPPED)
                .build();
        orderFlux.emitValue(order00010);
        // The position should move to failure.
        await().untilAsserted(() -> assertEquals(OPENING_FAILURE, getPositionDTO(position1Id).getStatus()));
    }

    @Test
    @DisplayName("Check closing order failure")
    public void checkClosingOrderFailure() {
        // =============================================================================================================
        // Creates a position. Then, when closing an order update with an error.
        // The position must end up being in CLOSING_FAILURE

        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals(1, p1.getPosition().getPositionId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrder().getOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        long position1Id = p1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));

        // Position 1.
        Optional<PositionDTO> position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        OrderDTO p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(ETH_BTC, p1OpeningOrder.getCurrencyPair());
        assertEquals(NEW, p1OpeningOrder.getStatus());
        // Closing order.
        OrderDTO p1ClosingOrder = position1.get().getClosingOrder();
        assertNull(p1ClosingOrder);

        // =============================================================================================================
        // We are now closing position 1 with a trade and setCloseOrderId.
        // We move the position 1 to OPENED.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000002")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0001", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        // We close position 1 with setClosingOrderId().
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());

        // We close the position.
        OrderDTO closingOrder01 = OrderDTO.builder()
                .orderId("CLOSING_ORDER_01")
                .type(ASK)
                .strategy(strategyDTO)
                .amount(new CurrencyAmountDTO("1.00001", ETH_BTC.getBaseCurrency()))
                .currencyPair(ETH_BTC)
                .timestamp(ZonedDateTime.now())
                .status(FILLED)
                .cumulativeAmount(new CurrencyAmountDTO("0.0002", ETH_BTC.getBaseCurrency()))
                .build();

        position1.get().closePositionWithOrder(closingOrder01);
        positionFlux.emitValue(position1.get());
        await().untilAsserted(() -> assertEquals(CLOSING, getPositionDTO(position1Id).getStatus()));

        // =============================================================================================================
        // An update arrives to change status order of position 1 in error.
        closingOrder01 = OrderDTO.builder()
                .orderId("CLOSING_ORDER_01")
                .type(ASK)
                .strategy(strategyDTO)
                .amount(new CurrencyAmountDTO("1.00001", ETH_BTC.getBaseCurrency()))
                .currencyPair(ETH_BTC)
                .timestamp(ZonedDateTime.now())
                .status(CANCELED)
                .cumulativeAmount(new CurrencyAmountDTO("0.0002", ETH_BTC.getBaseCurrency()))
                .build();
        orderFlux.emitValue(closingOrder01);
        await().untilAsserted(() -> assertEquals(CLOSING_FAILURE, getPositionDTO(position1Id).getStatus()));

        // We check the type.
        final Optional<PositionDTO> p = positionService.getPositionById(position1Id);
        assertTrue(p.isPresent());
        assertEquals(LONG, p.get().getType());
    }

    @Test
    @DisplayName("Check get positions and get positions by id")
    public void checkGetPosition() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        strategy.createLongPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0003"),
                PositionRulesDTO.builder().stopGainPercentage(30f).stopLossPercentage(30f).build());

        // Tests.
        assertEquals(2, positionService.getPositions().size());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(1, positionService.getPositionById(1).get().getId());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(2, positionService.getPositionById(2).get().getId());
        assertFalse(positionService.getPositionById(3).isPresent());
    }

    @Test
    @DisplayName("Check trade update")
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void checkTradeUpdate() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrder().getOrderId());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = strategy.createLongPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertEquals("ORDER00020", p2.getPosition().getOpeningOrder().getOrderId());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(2, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdatesReceived().size()));

        // Trade 2 - should change status of position 1.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0001", ETH_BTC.getBaseCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(1).get().getStatus()));
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Trade 3 - should change status of position 2.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000003")
                .type(BID)
                .orderId("ORDER00020")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0002", ETH_BTC.getBaseCurrency()))
                .build());
        assertEquals(OPENED, positionService.getPositionById(1).get().getStatus());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(2).get().getStatus()));
    }

    @Test
    @DisplayName("Check close position")
    public void checkClosePosition() throws InterruptedException {
        // =============================================================================================================
        // Creates position 1 (ETH/BTC, 0.0001, 100% stop gain).
        final PositionCreationResultDTO creationResult1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        final long position1Id = creationResult1.getPosition().getId();
        assertEquals("ORDER00010", creationResult1.getPosition().getOpeningOrder().getOrderId());

        // The opening trade arrives, change the status to OPENED and set the price.
        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));

        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0001", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        // =============================================================================================================
        // We send tickers.

        // A first ticker arrives with a gain of 100% but for the wrong CP - so it must still be OPENED.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.5")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        PositionDTO p = getPositionDTO(position1Id);
        assertEquals(OPENED, p.getStatus());
        // We check the last calculated gain - should be none.
        Optional<GainDTO> gain = p.getLatestCalculatedGain();
        assertFalse(gain.isPresent());

        // A second ticker arrives with a gain of 50%.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.3")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        p = getPositionDTO(position1Id);
        // We check the last calculated gain - should be 50%.
        gain = p.getLatestCalculatedGain();

        assertTrue(gain.isPresent());
        assertEquals(50, gain.get().getPercentage());
        assertEquals(0, new BigDecimal("0.00001").compareTo(gain.get().getAmount().getValue()));
        assertEquals(BTC, gain.get().getAmount().getCurrency());
        assertEquals(ZERO, gain.get().getFees().getValue());
        assertEquals(BTC, gain.get().getFees().getCurrency());
    }

    @Test
    @DisplayName("Check lowest, highest and latest gain")
    public void checkLowestHighestAndLatestGain() {
        // A position is created on ETH/BTC.
        // We buy 10 ETH for 100 BTC.
        final PositionCreationResultDTO creationResult1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .build());
        final long position1Id = creationResult1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));

        // Two tickers arrived - min and max gain should not be set.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("100")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.000001")).build());
        await().untilAsserted(() -> assertEquals(2, strategy.getTickersUpdatesReceived().size()));

        // Trade arrives, position is now opened.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000001")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("10", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.03", ETH_BTC.getQuoteCurrency()))
                .build());

        // The two tickers arrived during the OPENING status should not have change highest lowest and latest gain.
        await().untilAsserted(() -> assertEquals(1, strategy.getTradesUpdatesReceived().size()));
        PositionDTO position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isEmpty());
        assertTrue(position1.getHighestCalculatedGain().isEmpty());
        assertTrue(position1.getLatestCalculatedGain().isEmpty());

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        // We had 2 positions updates (Closing then closed).
        // +1 with trade arriving
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.18")).build());
        await().untilAsserted(() -> assertEquals(6, strategy.getPositionsUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(500, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getLatestCalculatedGain().get().getPercentage());

        // Second ticker arrives (100% gain) - min gain should be set to that value.
        // 1 more update because of a new ticker.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.06")).build());
        await().untilAsserted(() -> assertEquals(7, strategy.getPositionsUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(100, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Third ticker arrives (200% gain) - nothing should change.
        // 1 more update because of a new ticker.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.09")).build());
        await().untilAsserted(() -> assertEquals(8, strategy.getPositionsUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(100, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.015")).build());
        await().untilAsserted(() -> assertEquals(9, strategy.getPositionsUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Firth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.21")).build());
        await().untilAsserted(() -> assertEquals(10, strategy.getPositionsUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());

        // Closing the trade - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("100")).build());
        await().untilAsserted(() -> assertEquals(11, strategy.getPositionsUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertEquals(CLOSING, position1.getStatus());

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(2, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(12, strategy.getPositionsUpdatesReceived().size()));

        // The close trade arrives, change the status and set the price.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(ASK)
                .orderId("ORDER00011")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("10", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("20", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position1Id).getStatus()));

        // Trade arrival should create an update
        await().untilAsserted(() -> assertEquals(13, strategy.getPositionsUpdatesReceived().size()));

        // Sixth ticker arrives (800% gain) - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.27")).build());
        await().untilAsserted(() -> assertEquals(9, strategy.getTickersUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());

        // Seventh ticker arrives (90% loss) - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.003")).build());
        await().untilAsserted(() -> assertEquals(10, strategy.getTickersUpdatesReceived().size()));
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());
    }

    @Test
    @DisplayName("Check update rules on position")
    public void checkUpdateRulesOnPosition() throws InterruptedException {
        // =============================================================================================================
        // Creates position 1 (ETH/BTC, 0.0001, 100% stop gain).
        final PositionCreationResultDTO creationResult1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopLossPercentage(90f).stopGainPercentage(100f).build());
        final long position1Id = creationResult1.getPosition().getId();
        assertEquals("ORDER00010", creationResult1.getPosition().getOpeningOrder().getOrderId());

        // The opening trade arrives, change the status to OPENED and set the price.
        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));

        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0001", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        final Optional<PositionDTO> positionBefore = strategy.getPositionByPositionId(creationResult1.getPosition().getPositionId());
        assertTrue(positionBefore.isPresent());

        // We update the rules.
        final PositionRulesDTO newRules = PositionRulesDTO.builder().stopGainPercentage(40f).stopLossPercentage(20f).build();
        strategy.updatePositionRules(position1Id, newRules);
        Optional<PositionDTO> positionAfter = strategy.getPositionByPositionId(creationResult1.getPosition().getPositionId());
        assertTrue(positionAfter.isPresent());
        assertTrue(positionAfter.get().getRules().isStopGainPercentageSet());
        assertEquals(40f, positionAfter.get().getRules().getStopGainPercentage());
        assertTrue(positionAfter.get().getRules().isStopLossPercentageSet());
        assertEquals(20f, positionAfter.get().getRules().getStopLossPercentage());

        // We send again the value of the position before rules are updated.
        positionFlux.emitValue(positionBefore.get());

        // We check that the rules have been updated.
        positionAfter = strategy.getPositionByPositionId(creationResult1.getPosition().getPositionId());
        assertTrue(positionAfter.isPresent());
        assertTrue(positionAfter.get().getRules().isStopGainPercentageSet());
        assertEquals(40f, positionAfter.get().getRules().getStopGainPercentage());
        assertTrue(positionAfter.get().getRules().isStopLossPercentageSet());
        assertEquals(20f, positionAfter.get().getRules().getStopLossPercentage());

        // We set it to null.
        strategy.updatePositionRules(position1Id, PositionRulesDTO.builder().build());
        positionAfter = strategy.getPositionByPositionId(creationResult1.getPosition().getPositionId());
        assertTrue(positionAfter.isPresent());
        assertFalse(positionAfter.get().getRules().isStopGainPercentageSet());
        assertFalse(positionAfter.get().getRules().isStopLossPercentageSet());
    }

    /**
     * Retrieve position from database.
     *
     * @param id position id
     * @return position
     */
    private PositionDTO getPositionDTO(final long id) {
        final Optional<PositionDTO> p = positionService.getPositionById(id);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new NoSuchElementException("Position not found : " + id);
        }
    }

}
