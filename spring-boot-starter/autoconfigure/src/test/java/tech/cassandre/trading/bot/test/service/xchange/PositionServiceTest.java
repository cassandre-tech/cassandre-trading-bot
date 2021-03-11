package tech.cassandre.trading.bot.test.service.xchange;

import io.qase.api.annotation.CaseId;
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
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
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
    private OrderRepository orderRepository;

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
    @CaseId(71)
    @DisplayName("Check position creation")
    public void checkCreatePosition() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        assertEquals(LONG, positionService.getPositionById(1).get().getType());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = strategy.createLongPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertTrue(p2.isSuccessful());
        assertEquals(2, p2.getPosition().getId());
        assertEquals("ORDER00020", p2.getPosition().getOpeningOrderId());
        assertNull(p2.getErrorMessage());
        assertNull(p2.getException());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        final PositionCreationResultDTO p3 = strategy.createLongPosition(cp1,
                new BigDecimal("0.0003"),
                PositionRulesDTO.builder().stopGainPercentage(30f).stopLossPercentage(30f).build());
        assertFalse(p3.isSuccessful());
        assertNull(p3.getPosition());
        assertEquals("TradeService - Error calling createBuyMarketOrder : Error exception", p3.getErrorMessage());
        assertEquals("Error exception", p3.getException().getMessage());
        assertEquals(2, positionService.getPositions().size());
    }

    @Test
    @CaseId(72)
    @DisplayName("Check position order update")
    public void checkPositionOrderUpdate() {
        // =============================================================================================================
        // Creates two positions (1 & 2).

        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        long position1Id = p1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));

        // Check order created internally by Cassandre.
        final OrderDTO orderP1 = strategy.getOrdersUpdateReceived().get(0);
        assertNotNull(orderP1);
        assertEquals("ORDER00010", orderP1.getOrderId());
        assertEquals(BID, orderP1.getType());
        assertNotNull(orderP1.getStrategy());
        assertEquals(1, orderP1.getStrategy().getId());
        assertEquals("01", orderP1.getStrategy().getStrategyId());
        assertEquals(cp1, orderP1.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(orderP1.getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), orderP1.getAmount().getCurrency());
        assertNull(orderP1.getAveragePrice());
        assertNull(orderP1.getLimitPrice());
        assertNull(orderP1.getLeverage());
        assertEquals(PENDING_NEW, orderP1.getStatus());
        assertNull(orderP1.getCumulativeAmount());
        assertNull(orderP1.getUserReference());
        assertNotNull(orderP1.getTimestamp());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = strategy.createLongPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertTrue(p2.isSuccessful());
        assertEquals(2, p2.getPosition().getId());
        assertEquals("ORDER00020", p2.getPosition().getOpeningOrderId());
        assertNull(p2.getErrorMessage());
        assertNull(p2.getException());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());
        long position2Id = p2.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(2, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdateReceived().size()));

        // Position 1.
        Optional<PositionDTO> position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());

        // Opening order.
        OrderDTO p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(cp1, p1OpeningOrder.getCurrencyPair());
        assertEquals(PENDING_NEW, p1OpeningOrder.getStatus());
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
        assertEquals(cp2, p2OpeningOrder.getCurrencyPair());
        assertEquals(PENDING_NEW, p1OpeningOrder.getStatus());
        // Closing order.
        OrderDTO p2ClosingOrder = position2.get().getClosingOrder();
        assertNull(p2ClosingOrder);

        // =============================================================================================================
        // An update for opening order ORDER00020 (position 2) arrives and change status.
        final long positionUpdateCount1 = strategy.getPositionsUpdateReceived().size();
        OrderDTO order00020 = OrderDTO.builder()
                .orderId("ORDER00020")
                .type(BID)
                .currencyPair(cp2)
                .amount(new CurrencyAmountDTO("1.00001", cp2.getBaseCurrency()))
                .status(FILLED)
                .timestamp(ZonedDateTime.now())
                .build();
        orderFlux.emitValue(order00020);
        await().untilAsserted(() -> assertEquals(positionUpdateCount1 + 1, strategy.getPositionsUpdateReceived().size()));

        // Position 1 - No changes.
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(cp1, p1OpeningOrder.getCurrencyPair());
        assertEquals(PENDING_NEW, p1OpeningOrder.getStatus());
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
        assertEquals(cp2, p2OpeningOrder.getCurrencyPair());
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
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("0.0001", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", cp1.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        // We close position 1 with setClosingOrderId().
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        position1.get().closePositionWithOrderId("CLOSING_ORDER_01");
        positionFlux.emitValue(position1.get());
        await().untilAsserted(() -> assertEquals(CLOSING, getPositionDTO(position1Id).getStatus()));

        // An update arrives and changes the status order of position 1.
        OrderDTO closingOrder01 = OrderDTO.builder()
                .orderId("CLOSING_ORDER_01")
                .type(ASK)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1.00001", cp2.getBaseCurrency()))
                .status(FILLED)
                .timestamp(ZonedDateTime.now())
                .build();
        orderFlux.emitValue(closingOrder01);
        await().untilAsserted(() -> assertEquals(4, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(8, strategy.getPositionsUpdateReceived().size()));

        // Position 1 - closing order status should have changed.
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(cp1, p1OpeningOrder.getCurrencyPair());
        assertEquals(PENDING_NEW, p1OpeningOrder.getStatus());
        // Closing order.
        p1ClosingOrder = position1.get().getClosingOrder();
        assertNotNull(p1ClosingOrder);
        assertEquals("CLOSING_ORDER_01", p1ClosingOrder.getOrderId());
        assertEquals(ASK, p1ClosingOrder.getType());
        assertEquals(cp1, p1ClosingOrder.getCurrencyPair());
        assertEquals(FILLED, p1ClosingOrder.getStatus());

        // Position 2 - No change
        position2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(position2.isPresent());
        // Opening order.
        p2OpeningOrder = position2.get().getOpeningOrder();
        assertNotNull(p2OpeningOrder);
        assertEquals("ORDER00020", p2OpeningOrder.getOrderId());
        assertEquals(cp2, p2OpeningOrder.getCurrencyPair());
        assertEquals(FILLED, p2OpeningOrder.getStatus());
        // Closing order.
        p2ClosingOrder = position2.get().getClosingOrder();
        assertNull(p2ClosingOrder);
    }

    @Test
    @CaseId(73)
    @DisplayName("Check opening order failure")
    public void checkOpeningOrderFailure() {
        // =============================================================================================================
        // Creates a position. Then send an order update with an error.
        // The position must end up being in OPENING_FAILURE

        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        long position1Id = p1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));

        // Position 1.
        Optional<PositionDTO> position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        OrderDTO p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(cp1, p1OpeningOrder.getCurrencyPair());
        assertEquals(PENDING_NEW, p1OpeningOrder.getStatus());
        // Closing order.
        OrderDTO p1ClosingOrder = position1.get().getClosingOrder();
        assertNull(p1ClosingOrder);

        // =============================================================================================================
        // An update for opening order ORDER00020 (position 2) arrives and change status with an error !
        OrderDTO order00010 = OrderDTO.builder()
                .orderId("ORDER00010")
                .type(BID)
                .amount(new CurrencyAmountDTO("0.00012", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .timestamp(ZonedDateTime.now())
                .status(STOPPED)
                .build();
        orderFlux.emitValue(order00010);
        // The position should move to failure.
        await().untilAsserted(() -> assertEquals(OPENING_FAILURE, getPositionDTO(position1Id).getStatus()));
    }

    @Test
    @CaseId(74)
    @DisplayName("Check closing order failure")
    public void checkClosingOrderFailure() {
        // =============================================================================================================
        // Creates a position. Then, when closing an order update with an error.
        // The position must end up being in CLOSING_FAILURE

        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPosition().getId());
        assertEquals(1, p1.getPosition().getPositionId());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());
        long position1Id = p1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));

        // Position 1.
        Optional<PositionDTO> position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        // Opening order.
        OrderDTO p1OpeningOrder = position1.get().getOpeningOrder();
        assertNotNull(p1OpeningOrder);
        assertEquals("ORDER00010", p1OpeningOrder.getOrderId());
        assertEquals(BID, p1OpeningOrder.getType());
        assertEquals(cp1, p1OpeningOrder.getCurrencyPair());
        assertEquals(PENDING_NEW, p1OpeningOrder.getStatus());
        // Closing order.
        OrderDTO p1ClosingOrder = position1.get().getClosingOrder();
        assertNull(p1ClosingOrder);

        // =============================================================================================================
        // We are now closing position 1 with a trade and setCloseOrderId.
        // We move the position 1 to OPENED.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000002")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("0.0001", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", cp1.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        // We close position 1 with setClosingOrderId().
        position1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(position1.isPresent());
        position1.get().closePositionWithOrderId("CLOSING_ORDER_01");
        positionFlux.emitValue(position1.get());
        await().untilAsserted(() -> assertEquals(CLOSING, getPositionDTO(position1Id).getStatus()));

        // =============================================================================================================
        // An update arrives to change status order of position 1 in error.
        OrderDTO closingOrder01 = OrderDTO.builder()
                .orderId("CLOSING_ORDER_01")
                .type(ASK)
                .amount(new CurrencyAmountDTO("1.00001", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .timestamp(ZonedDateTime.now())
                .status(CANCELED)
                .cumulativeAmount(new CurrencyAmountDTO("0.0002", cp1.getBaseCurrency()))
                .build();
        orderFlux.emitValue(closingOrder01);
        await().untilAsserted(() -> assertEquals(CLOSING_FAILURE, getPositionDTO(position1Id).getStatus()));

        // We check the type.
        final Optional<PositionDTO> p = positionService.getPositionById(position1Id);
        assertTrue(p.isPresent());
        assertEquals(LONG, p.get().getType());
    }

    @Test
    @CaseId(75)
    @DisplayName("Check get positions and get positions by id")
    public void checkGetPosition() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        strategy.createLongPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        strategy.createLongPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        strategy.createLongPosition(cp1,
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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @CaseId(76)
    @DisplayName("Check trade update")
    public void checkTradeUpdate() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = strategy.createLongPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).build());
        assertEquals("ORDER00010", p1.getPosition().getOpeningOrderId());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = strategy.createLongPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertEquals("ORDER00020", p2.getPosition().getOpeningOrderId());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(2, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdateReceived().size()));

        // Trade 2 - should change status of position 1.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("0.0001", cp1.getBaseCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(1).get().getStatus()));
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Trade 3 - should change status of position 2.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000003")
                .type(BID)
                .orderId("ORDER00020")
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("0.0002", cp1.getBaseCurrency()))
                .build());
        assertEquals(OPENED, positionService.getPositionById(1).get().getStatus());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(2).get().getStatus()));
    }

    @Test
    @CaseId(77)
    @DisplayName("Check close position")
    public void checkClosePosition() throws InterruptedException {
        // =============================================================================================================
        // Creates position 1 (ETH/BTC, 0.0001, 100% stop gain).
        final PositionCreationResultDTO creationResult1 = strategy.createLongPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        final long position1Id = creationResult1.getPosition().getId();
        assertEquals("ORDER00010", creationResult1.getPosition().getOpeningOrderId());

        // The opening trade arrives, change the status to OPENED and set the price.
        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));

        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("0.0001", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", cp1.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        // =============================================================================================================
        // We send tickers.

        // A first ticker arrives with a gain of 100% but for the wrong CP - so it must still be OPENED.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp2).last(new BigDecimal("0.5")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        PositionDTO p = getPositionDTO(position1Id);
        assertEquals(OPENED, p.getStatus());
        // We check the last calculated gain - should be none.
        Optional<GainDTO> gain = p.getLatestCalculatedGain();
        assertFalse(gain.isPresent());

        // A second ticker arrives with a gain of 50%.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.3")).build());
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
    @CaseId(78)
    @DisplayName("Check lowest, highest and latest gain")
    public void checkLowestHighestAndLatestGain() throws InterruptedException {
        // A position is created on ETH/BTC.
        // We buy 10 ETH for 100 BTC.
        final PositionCreationResultDTO creationResult1 = strategy.createLongPosition(cp1,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .build());
        final long position1Id = creationResult1.getPosition().getId();

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));

        // Two tickers arrived - min and max gain should not be set.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("100")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.000001")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);

        // Trade arrives, position is now opened.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000001")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("10", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.03", cp1.getQuoteCurrency()))
                .build());

        // The two tickers arrived during the OPENING status should not have change highest lowest and latest gain.
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        PositionDTO position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isEmpty());
        assertTrue(position1.getHighestCalculatedGain().isEmpty());
        assertTrue(position1.getLatestCalculatedGain().isEmpty());

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.18")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(500, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getLatestCalculatedGain().get().getPercentage());

        // Second ticker arrives (100% gain) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.06")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(100, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Third ticker arrives (200% gain) - nothing should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.09")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(100, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.015")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Firth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.21")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());

        // Closing the trade - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("100")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertEquals(CLOSING, position1.getStatus());

        // We retrieve the order from the service and we wait for the order to update the position.
        orderFlux.update();
        await().untilAsserted(() -> assertEquals(2, strategy.getOrdersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(10, strategy.getPositionsUpdateReceived().size()));

        // The close trade arrives, change the status and set the price.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(ASK)
                .orderId("ORDER00011")
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("10", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("20", cp1.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position1Id).getStatus()));

        // Sixth ticker arrives (800% gain) - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.27")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());

        // Seventh ticker arrives (90% loss) - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.003")).build());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());
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
            throw new RuntimeException("Position not found : " + id);
        }
    }

}
