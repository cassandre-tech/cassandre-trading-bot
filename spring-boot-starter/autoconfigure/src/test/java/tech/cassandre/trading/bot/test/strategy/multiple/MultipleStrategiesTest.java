package tech.cassandre.trading.bot.test.strategy.multiple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.util.exception.PositionException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy1.PARAMETER_STRATEGY_1_ENABLED;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy2.PARAMETER_STRATEGY_2_ENABLED;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy3.PARAMETER_STRATEGY_3_ENABLED;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;
import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;

@SpringBootTest
@DisplayName("Strategy - Running multiple strategies")
@Configuration({
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, value = "false"),
        // Using strategies 1, 2 & 3 in dry mode.
        @Property(key = PARAMETER_STRATEGY_1_ENABLED, value = "true"),
        @Property(key = PARAMETER_STRATEGY_2_ENABLED, value = "true"),
        @Property(key = PARAMETER_STRATEGY_3_ENABLED, value = "true"),
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_CLASS)
public class MultipleStrategiesTest extends BaseTest {

    @Autowired
    private AccountFlux accountFlux;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private Strategy1 strategy1;

    @Autowired
    private Strategy2 strategy2;

    @Autowired
    private Strategy3 strategy3;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @DisplayName("Check multiple strategies behavior")
    public void checkMultipleStrategyBehavior() throws InterruptedException {
        //==============================================================================================================
        // Strategies tested.
        // Strategy 1 - Requesting BTC/USDT.
        // Strategy 2 - Requesting BTC/ETH.
        // Strategy 3 - Requesting BTC/USDT & ETH/USDT.

        //==============================================================================================================
        // Checking the MarketService have all the currency pairs.
        final Set<CurrencyPairDTO> availableCurrencyPairs = exchangeService.getAvailableCurrencyPairs();
        assertEquals(3, availableCurrencyPairs.size());
        assertTrue(availableCurrencyPairs.contains(BTC_USDT));
        assertTrue(availableCurrencyPairs.contains(BTC_ETH));
        assertTrue(availableCurrencyPairs.contains(ETH_USDT));

        //==============================================================================================================
        // Checking the three strategies is stored in the database.
        assertEquals(3, strategyRepository.count());
        final Optional<Strategy> s1 = strategyRepository.findByStrategyId("01");
        assertTrue(s1.isPresent());
        assertEquals(1, s1.get().getId());
        assertEquals("01", s1.get().getStrategyId());
        assertEquals("Strategy 1", s1.get().getName());
        final Optional<Strategy> s2 = strategyRepository.findByStrategyId("02");
        assertTrue(s2.isPresent());
        assertEquals(2, s2.get().getId());
        assertEquals("02", s2.get().getStrategyId());
        assertEquals("Strategy 2", s2.get().getName());
        final Optional<Strategy> s3 = strategyRepository.findByStrategyId("03");
        assertTrue(s3.isPresent());
        assertEquals(3, s3.get().getId());
        assertEquals("03", s3.get().getStrategyId());
        assertEquals("Strategy 3", s3.get().getName());

        //==============================================================================================================
        // Check balances on each strategy & onAccountUpdate().
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(3, strategy3.getAccountsUpdatesReceived().size()));

        // Strategy 1 test.
        Map<String, AccountDTO> strategyAccounts = strategy1.getAccounts();
        Optional<AccountDTO> strategyTradeAccount = strategy1.getTradeAccount();
        assertEquals(3, strategyAccounts.size());
        assertTrue(strategyAccounts.containsKey("main"));
        assertTrue(strategyAccounts.containsKey("trade"));
        assertTrue(strategyAccounts.containsKey("savings"));
        assertTrue(strategyTradeAccount.isPresent());
        assertEquals("trade", strategyTradeAccount.get().getName());
        assertEquals(3, strategyTradeAccount.get().getBalances().size());
        assertTrue(strategyTradeAccount.get().getBalance(BTC).isPresent());
        assertEquals(0, new BigDecimal("0.99962937").compareTo(strategyTradeAccount.get().getBalance(BTC).get().getAvailable()));
        assertTrue(strategyTradeAccount.get().getBalance(USDT).isPresent());
        assertEquals(0, new BigDecimal("1000").compareTo(strategyTradeAccount.get().getBalance(USDT).get().getAvailable()));
        assertTrue(strategyTradeAccount.get().getBalance(ETH).isPresent());
        assertEquals(0, new BigDecimal("10").compareTo(strategyTradeAccount.get().getBalance(ETH).get().getAvailable()));

        // Strategy 2 test.
        strategyAccounts = strategy2.getAccounts();
        strategyTradeAccount = strategy2.getTradeAccount();
        assertEquals(3, strategyAccounts.size());
        assertTrue(strategyAccounts.containsKey("main"));
        assertTrue(strategyAccounts.containsKey("trade"));
        assertTrue(strategyAccounts.containsKey("savings"));
        assertTrue(strategyTradeAccount.isPresent());
        assertEquals("trade", strategyTradeAccount.get().getName());
        assertEquals(3, strategyTradeAccount.get().getBalances().size());
        assertTrue(strategyTradeAccount.get().getBalance(BTC).isPresent());
        assertEquals(0, new BigDecimal("0.99962937").compareTo(strategyTradeAccount.get().getBalance(BTC).get().getAvailable()));
        assertTrue(strategyTradeAccount.get().getBalance(USDT).isPresent());
        assertEquals(0, new BigDecimal("1000").compareTo(strategyTradeAccount.get().getBalance(USDT).get().getAvailable()));
        assertTrue(strategyTradeAccount.get().getBalance(ETH).isPresent());
        assertEquals(0, new BigDecimal("10").compareTo(strategyTradeAccount.get().getBalance(ETH).get().getAvailable()));

        // Strategy 3 test.
        strategyAccounts = strategy3.getAccounts();
        strategyTradeAccount = strategy3.getTradeAccount();
        assertEquals(3, strategyAccounts.size());
        assertTrue(strategyAccounts.containsKey("main"));
        assertTrue(strategyAccounts.containsKey("trade"));
        assertTrue(strategyAccounts.containsKey("savings"));
        assertTrue(strategyTradeAccount.isPresent());
        assertEquals("trade", strategyTradeAccount.get().getName());
        assertEquals(3, strategyTradeAccount.get().getBalances().size());
        assertTrue(strategyTradeAccount.get().getBalance(BTC).isPresent());
        assertEquals(0, new BigDecimal("0.99962937").compareTo(strategyTradeAccount.get().getBalance(BTC).get().getAvailable()));
        assertTrue(strategyTradeAccount.get().getBalance(USDT).isPresent());
        assertEquals(0, new BigDecimal("1000").compareTo(strategyTradeAccount.get().getBalance(USDT).get().getAvailable()));
        assertTrue(strategyTradeAccount.get().getBalance(ETH).isPresent());
        assertEquals(0, new BigDecimal("10").compareTo(strategyTradeAccount.get().getBalance(ETH).get().getAvailable()));

        //==============================================================================================================
        // Checking received tickers by strategies.
        // Sending BTC/USDT - BTC/ETH - ETH/USDT.
        // Strategy 1 - Requesting BTC/USDT should receive one ticker - 1 BTC costs 50 000 USDT.
        // Strategy 2 - Requesting BTC/ETH should receive one ticker - 1 BTC costs 25 ETH.
        // Strategy 3 - Requesting BTC/USDT & ETH/USDT should receive two tickers - 1 ETH costs 2000 USDT.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(BTC_USDT).last(new BigDecimal("50000")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(BTC_ETH).last(new BigDecimal("25")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("2000")).build());
        await().untilAsserted(() -> assertEquals(2, strategy3.getTickersUpdatesReceived().size()));
        // Strategy 1.
        assertEquals(1, strategy1.getTickersUpdatesReceived().size());
        final TickerDTO strategy1Ticker1 = strategy1.getTickersUpdatesReceived().get(0);
        assertNotNull(strategy1Ticker1);
        assertEquals(BTC_USDT, strategy1Ticker1.getCurrencyPair());
        assertEquals(0, new BigDecimal("50000").compareTo(strategy1Ticker1.getLast()));
        // Strategy 2.
        assertEquals(1, strategy2.getTickersUpdatesReceived().size());
        final TickerDTO strategy2Ticker1 = strategy2.getTickersUpdatesReceived().get(0);
        assertNotNull(strategy2Ticker1);
        assertEquals(BTC_ETH, strategy2Ticker1.getCurrencyPair());
        assertEquals(0, new BigDecimal("25").compareTo(strategy2Ticker1.getLast()));
        // Strategy 3.
        assertEquals(2, strategy3.getTickersUpdatesReceived().size());
        final TickerDTO strategy3Ticker1 = strategy3.getTickersUpdatesReceived().get(0);
        assertNotNull(strategy3Ticker1);
        assertEquals(BTC_USDT, strategy3Ticker1.getCurrencyPair());
        assertEquals(0, new BigDecimal("50000").compareTo(strategy3Ticker1.getLast()));
        final TickerDTO strategy3Ticker2 = strategy3.getTickersUpdatesReceived().get(1);
        assertNotNull(strategy3Ticker2);
        assertEquals(ETH_USDT, strategy3Ticker2.getCurrencyPair());
        assertEquals(0, new BigDecimal("2000").compareTo(strategy3Ticker2.getLast()));

        //==============================================================================================================
        // Strategy 1 - Creating 1 position on BTC/USDT (0.001 BTC for 50 USDT).
        // The price of 1 BTC is 50 000 USDT and we buy 0.001 BTC for 50 USDT.
        // We stop at 100% gain.
        final PositionCreationResultDTO position1Result = strategy1.createLongPosition(BTC_USDT,
                new BigDecimal("0.001"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        assertTrue(position1Result.isSuccessful());
        final long position1Id = position1Result.getPosition().getId();
        final long position1PositionId = position1Result.getPosition().getPositionId();
        orderFlux.update();
        tradeFlux.update();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        // Check positionId & positionId.
        assertEquals(1, position1Id);
        assertEquals(1, position1PositionId);

        // Check onPositionUpdate() & onPositionStatusUpdate().*
        // For strategy 1:
        // Positions updates 3 : Position created in OPENING, move to OPENED, Updated order.
        // Positions status updates 2 : OPENING and then OPENED.
        await().untilAsserted(() -> assertEquals(3, strategy1.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy1.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy2.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy2.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getPositionsUpdatesReceived().size()));

        // Check onOrderUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy2.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getOrdersUpdatesReceived().size()));

        // Check onTradeUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy2.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getTradesUpdatesReceived().size()));

        // Check getOrders() & getOrderByOrderId().
        // Only strategy 1 should have received an order.
        assertEquals(1, strategy1.getOrders().size());
        assertTrue(strategy1.getOrderByOrderId("DRY_ORDER_000000001").isPresent());
        assertEquals(0, strategy2.getOrders().size());
        assertTrue(strategy2.getOrderByOrderId("DRY_ORDER_000000001").isEmpty());
        assertEquals(0, strategy3.getOrders().size());
        assertTrue(strategy3.getOrderByOrderId("DRY_ORDER_000000001").isEmpty());

        // Check getTrades() & getTradeByTradeId().
        // Only strategy 1 should have received a trade.
        assertEquals(1, strategy1.getTrades().size());
        assertTrue(strategy1.getTradeByTradeId("DRY_TRADE_000000001").isPresent());
        assertEquals(0, strategy2.getTrades().size());
        assertTrue(strategy2.getTradeByTradeId("DRY_TRADE_000000001").isEmpty());
        assertEquals(0, strategy3.getTrades().size());
        assertTrue(strategy3.getTradeByTradeId("DRY_TRADE_000000001").isEmpty());

        // Check getAmountsLockedByPosition().
        BigDecimal amountLockedForBTC = strategy1.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.001").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy2.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.001").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy3.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.001").compareTo(amountLockedForBTC));

        //==============================================================================================================
        // Strategy 2 - Creating 1 position on BTC/ETH (0.2 BTC for 0.5 ETH).
        // The price of 1 BTC is 25 ETH and we buy 0.02 BTC for 0.5 ETH.
        // We stop at 100% gain.
        final PositionCreationResultDTO position2Result = strategy2.createLongPosition(BTC_ETH,
                new BigDecimal("0.02"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        assertTrue(position2Result.isSuccessful());
        final long position2Id = position2Result.getPosition().getId();
        final long position2PositionId = position2Result.getPosition().getPositionId();
        orderFlux.update();
        tradeFlux.update();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position2Id).getStatus()));

        // Check positionId & positionId.
        assertEquals(2, position2Id);
        assertEquals(1, position2PositionId);

        // Check onPositionUpdate() & onPositionStatusUpdate().
        // For strategy 2:
        // Positions updates 3 : Position created in OPENING, move to OPENED, Updated order.
        // Positions status updates 2 : OPENING and then OPENED.
        await().untilAsserted(() -> assertEquals(3, strategy1.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy1.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(3, strategy2.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy2.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getPositionsUpdatesReceived().size()));

        // Check onOrderUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy2.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getOrdersUpdatesReceived().size()));

        // Check onTradeUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy2.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(0, strategy3.getTradesUpdatesReceived().size()));

        // Check getOrders() & getOrderByOrderId().
        assertEquals(1, strategy1.getOrders().size());
        assertTrue(strategy1.getOrderByOrderId("DRY_ORDER_000000001").isPresent());
        assertEquals(1, strategy2.getOrders().size());
        assertTrue(strategy2.getOrderByOrderId("DRY_ORDER_000000002").isPresent());
        assertEquals(0, strategy3.getOrders().size());
        assertTrue(strategy3.getOrderByOrderId("DRY_ORDER_000000001").isEmpty());

        // Check getTrades() & getTradeByTradeId().
        assertEquals(1, strategy1.getTrades().size());
        assertTrue(strategy1.getTradeByTradeId("DRY_TRADE_000000001").isPresent());
        assertEquals(1, strategy2.getTrades().size());
        assertTrue(strategy2.getTradeByTradeId("DRY_TRADE_000000002").isPresent());
        assertEquals(0, strategy3.getTrades().size());
        assertTrue(strategy3.getTradeByTradeId("DRY_TRADE_000000001").isEmpty());

        // Check getAmountsLockedByPosition().
        amountLockedForBTC = strategy1.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.021").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy2.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.021").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy3.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.021").compareTo(amountLockedForBTC));

        // =============================================================================================================
        // Changing the price for BTC/USDT.
        // A bitcoin now costs 10000 USDT and the price of position should have change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(BTC_USDT).last(new BigDecimal("10000")).build());
        await().untilAsserted(() -> assertEquals(3, strategy3.getTickersUpdatesReceived().size()));
        assertEquals(2, strategy1.getTickersUpdatesReceived().size());
        assertEquals(1, strategy2.getTickersUpdatesReceived().size());
        assertEquals(3, strategy3.getTickersUpdatesReceived().size());

        // =============================================================================================================
        // Strategy 3
        // - Creating one position on BTC/USDT (0.01 BTC for 100 USDT).
        final PositionCreationResultDTO position3Result = strategy3.createLongPosition(BTC_USDT,
                new BigDecimal("0.01"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        assertTrue(position3Result.isSuccessful());
        final long position3Id = position3Result.getPosition().getId();
        final long position3PositionId = position3Result.getPosition().getPositionId();
        orderFlux.update();
        tradeFlux.update();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position3Id).getStatus()));

        // - Creating one position on ETH/USDT (0.1 ETH for 200 USDT).
        final PositionCreationResultDTO position4Result = strategy3.createLongPosition(ETH_USDT,
                new BigDecimal("0.1"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        assertTrue(position4Result.isSuccessful());
        final long position4Id = position4Result.getPosition().getId();
        final long position4PositionId = position4Result.getPosition().getPositionId();
        orderFlux.update();
        tradeFlux.update();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position4Id).getStatus()));

        // Check positionId & positionId.
        assertEquals(3, position3Id);
        assertEquals(1, position3PositionId);
        assertEquals(4, position4Id);
        assertEquals(2, position4PositionId);

        // Check onPositionUpdate() & onPositionStatusUpdate().
        await().untilAsserted(() -> assertEquals(4, strategy1.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy1.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(3, strategy2.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy2.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(6, strategy3.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(4, strategy3.getPositionsStatusUpdatesReceived().size()));

        // Check onOrderUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy2.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy3.getOrdersUpdatesReceived().size()));

        // Check onTradeUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy2.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy3.getTradesUpdatesReceived().size()));

        // Check getOrders() & getOrderByOrderId().
        assertEquals(1, strategy1.getOrders().size());
        assertTrue(strategy1.getOrderByOrderId("DRY_ORDER_000000001").isPresent());
        assertEquals(1, strategy2.getOrders().size());
        assertTrue(strategy2.getOrderByOrderId("DRY_ORDER_000000002").isPresent());
        assertEquals(2, strategy3.getOrders().size());
        assertTrue(strategy3.getOrderByOrderId("DRY_ORDER_000000003").isPresent());
        assertTrue(strategy3.getOrderByOrderId("DRY_ORDER_000000004").isPresent());

        // Check getTrades() & getTradeByTradeId().
        assertEquals(1, strategy1.getTrades().size());
        assertTrue(strategy1.getTradeByTradeId("DRY_TRADE_000000001").isPresent());
        assertEquals(1, strategy2.getTrades().size());
        assertTrue(strategy2.getTradeByTradeId("DRY_TRADE_000000002").isPresent());
        assertEquals(2, strategy3.getTrades().size());
        assertTrue(strategy3.getTradeByTradeId("DRY_TRADE_000000003").isPresent());
        assertTrue(strategy3.getTradeByTradeId("DRY_TRADE_000000004").isPresent());

        // Check getAmountsLockedByPosition().
        amountLockedForBTC = strategy1.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.031").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy2.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.031").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy3.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.031").compareTo(amountLockedForBTC));
        BigDecimal amountLockedForETH = strategy1.getAmountsLockedByCurrency(ETH);
        assertEquals(0, new BigDecimal("0.1").compareTo(amountLockedForETH));
        amountLockedForETH = strategy2.getAmountsLockedByCurrency(ETH);
        assertEquals(0, new BigDecimal("0.1").compareTo(amountLockedForETH));
        amountLockedForETH = strategy3.getAmountsLockedByCurrency(ETH);
        assertEquals(0, new BigDecimal("0.1").compareTo(amountLockedForETH));

        //==============================================================================================================
        // Check balances, canBuy() & canSell().
        // At the beginning my balances was:
        // - 0.99962937 BTC.
        // - 1 000 USDT.
        // - 10 ETH.
        // ---
        // Position 1 - Bought 0.001 BTC for 50 USDT - BTC/USDT : 50000.
        // Position 2 - Bought 0.2 BTC for 0.5 ETH - BTC/ETH : 25.
        // Position 3 - Bought 0.01 BTC for 100 USDT - BTC/USDT : 10000
        // Position 4 - Bought 0.1 ETH for 200 USDT - ETH/USDT : 2000.
        // --
        // BTC : 0.99962937 + 0.001 (P1) + 0.02 (P2) + 0.01 (P2) = 1.03062937 BTC
        // USDT : 1000 - 50 (P1) - 100 (P3) - 200 (P4) = 650 USDT
        // ETH : 10 - 0.5 (P2) + 0.1 (P4) = 5.1 ETH
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(4, strategy3.getAccountsUpdatesReceived().size()));
        // Strategy 1.
        assertEquals(0, new BigDecimal("1.03062937").compareTo(strategy1.getTradeAccount().get().getBalance(BTC).get().getAvailable()));
        assertEquals(0, new BigDecimal("650").compareTo(strategy1.getTradeAccount().get().getBalance(USDT).get().getAvailable()));
        assertEquals(0, new BigDecimal("9.6").compareTo(strategy1.getTradeAccount().get().getBalance(ETH).get().getAvailable()));
        // Strategy 2.
        assertEquals(0, new BigDecimal("1.03062937").compareTo(strategy2.getTradeAccount().get().getBalance(BTC).get().getAvailable()));
        assertEquals(0, new BigDecimal("650").compareTo(strategy2.getTradeAccount().get().getBalance(USDT).get().getAvailable()));
        assertEquals(0, new BigDecimal("9.6").compareTo(strategy2.getTradeAccount().get().getBalance(ETH).get().getAvailable()));
        // Strategy 3.
        assertEquals(0, new BigDecimal("1.03062937").compareTo(strategy3.getTradeAccount().get().getBalance(BTC).get().getAvailable()));
        assertEquals(0, new BigDecimal("650").compareTo(strategy3.getTradeAccount().get().getBalance(USDT).get().getAvailable()));
        assertEquals(0, new BigDecimal("9.6").compareTo(strategy3.getTradeAccount().get().getBalance(ETH).get().getAvailable()));

        //==============================================================================================================
        // New ticker on BTC USDT to close a position.
        // Before : BTC/USDT - 10 000
        // New : BTC/USDT - 20 000 => Should trigger closing Position 3 (Position 1 of strategy 3).
        // Position 1 - Bought 0.001 BTC for 50 USDT - BTC/USDT : 50000.
        // Position 2 - Bought 0.2 BTC for 0.5 ETH - BTC/ETH : 25.
        // Position 3 - Bought 0.01 BTC for 100 USDT - BTC/USDT : 20000 => Now close, sold 0.01 BTC for 200 USDT.
        // Position 4 - Bought 0.1 ETH for 200 USDT - ETH/USDT : 2000.
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position3Id).getStatus()));
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(BTC_USDT).last(new BigDecimal("20000")).build());
        await().untilAsserted(() -> assertEquals(CLOSING, getPositionDTO(position3Id).getStatus()));
        orderFlux.update();
        tradeFlux.update();
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position3Id).getStatus()));

        // Check position status.
        assertEquals(OPENED, getPositionDTO(position1Id).getStatus());
        assertEquals(OPENED, getPositionDTO(position2Id).getStatus());
        assertEquals(CLOSED, getPositionDTO(position3Id).getStatus());
        assertEquals(OPENED, getPositionDTO(position4Id).getStatus());

        // Check onPositionUpdate() & onPositionStatusUpdate().
        await().untilAsserted(() -> assertEquals(5, strategy1.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy1.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(3, strategy2.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(2, strategy2.getPositionsStatusUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(9, strategy3.getPositionsUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(6, strategy3.getPositionsStatusUpdatesReceived().size()));

        // Check onOrderUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy2.getOrdersUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(3, strategy3.getOrdersUpdatesReceived().size()));

        // Check onTradeUpdate().
        await().untilAsserted(() -> assertEquals(1, strategy1.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy2.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertEquals(3, strategy3.getTradesUpdatesReceived().size()));

        // Check getOrders() & getOrderByOrderId().
        assertEquals(1, strategy1.getOrders().size());
        assertTrue(strategy1.getOrderByOrderId("DRY_ORDER_000000001").isPresent());
        assertEquals(1, strategy2.getOrders().size());
        assertTrue(strategy2.getOrderByOrderId("DRY_ORDER_000000002").isPresent());
        assertEquals(3, strategy3.getOrders().size());
        assertTrue(strategy3.getOrderByOrderId("DRY_ORDER_000000003").isPresent());
        assertTrue(strategy3.getOrderByOrderId("DRY_ORDER_000000004").isPresent());
        assertTrue(strategy3.getOrderByOrderId("DRY_ORDER_000000005").isPresent());

        // Check getTrades() & getTradeByTradeId().
        assertEquals(1, strategy1.getTrades().size());
        assertTrue(strategy1.getTradeByTradeId("DRY_TRADE_000000001").isPresent());
        assertEquals(1, strategy2.getTrades().size());
        assertTrue(strategy2.getTradeByTradeId("DRY_TRADE_000000002").isPresent());
        assertEquals(3, strategy3.getTrades().size());
        assertTrue(strategy3.getTradeByTradeId("DRY_TRADE_000000003").isPresent());
        assertTrue(strategy3.getTradeByTradeId("DRY_TRADE_000000004").isPresent());
        assertTrue(strategy3.getTradeByTradeId("DRY_TRADE_000000005").isPresent());

        // Check getAmountsLockedByPosition().
        // Position 3 - Bought 0.01 BTC for 100 USDT - BTC/USDT : 20000 => Now close, sold 0.01 BTC for 200 USDT.
        amountLockedForBTC = strategy1.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.021").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy2.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.021").compareTo(amountLockedForBTC));
        amountLockedForBTC = strategy3.getAmountsLockedByCurrency(BTC);
        assertEquals(0, new BigDecimal("0.021").compareTo(amountLockedForBTC));
        amountLockedForETH = strategy1.getAmountsLockedByCurrency(ETH);
        assertEquals(0, new BigDecimal("0.1").compareTo(amountLockedForETH));
        amountLockedForETH = strategy2.getAmountsLockedByCurrency(ETH);
        assertEquals(0, new BigDecimal("0.1").compareTo(amountLockedForETH));
        amountLockedForETH = strategy3.getAmountsLockedByCurrency(ETH);
        assertEquals(0, new BigDecimal("0.1").compareTo(amountLockedForETH));

        //==============================================================================================================
        // Check balances, canBuy() & canSell().
        // At the beginning my balances was:
        // - 0.99962937 BTC.
        // - 1 000 USDT.
        // - 10 ETH.
        // ---
        // Position 1 - Bought 0.001 BTC for 50 USDT - BTC/USDT : 50000.
        // Position 2 - Bought 0.2 BTC for 0.5 ETH - BTC/ETH : 25.
        // Position 3 - Bought 0.01 BTC for 100 USDT - BTC/USDT : 10000
        // Position 4 - Bought 0.1 ETH for 200 USDT - ETH/USDT : 2000.
        // New event.
        // Position 3 - Bought 0.01 BTC for 100 USDT - BTC/USDT : 20000 => Now close, sold 0.01 BTC for 200 USDT.
        // --
        // BTC : 0.99962937 + 0.001 (P1) + 0.02 (P2) + 0.01 (P2) - 0.01 (P3 close) = 1.02062937 BTC
        // USDT : 1000 - 50 (P1) - 100 (P3) - 200 (P4) + 200 (P3) = 850 USDT
        // ETH : 10 - 0.5 (P2) + 0.1 (P4) = 5.1 ETH
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(5, strategy3.getAccountsUpdatesReceived().size()));
        // Strategy 1.
        assertEquals(0, new BigDecimal("1.02062937").compareTo(strategy1.getTradeAccount().get().getBalance(BTC).get().getAvailable()));
        assertEquals(0, new BigDecimal("850").compareTo(strategy1.getTradeAccount().get().getBalance(USDT).get().getAvailable()));
        assertEquals(0, new BigDecimal("9.6").compareTo(strategy1.getTradeAccount().get().getBalance(ETH).get().getAvailable()));
        // Strategy 2.
        assertEquals(0, new BigDecimal("1.02062937").compareTo(strategy2.getTradeAccount().get().getBalance(BTC).get().getAvailable()));
        assertEquals(0, new BigDecimal("850").compareTo(strategy2.getTradeAccount().get().getBalance(USDT).get().getAvailable()));
        assertEquals(0, new BigDecimal("9.6").compareTo(strategy2.getTradeAccount().get().getBalance(ETH).get().getAvailable()));
        // Strategy 3.
        assertEquals(0, new BigDecimal("1.02062937").compareTo(strategy3.getTradeAccount().get().getBalance(BTC).get().getAvailable()));
        assertEquals(0, new BigDecimal("850").compareTo(strategy3.getTradeAccount().get().getBalance(USDT).get().getAvailable()));
        assertEquals(0, new BigDecimal("9.6").compareTo(strategy3.getTradeAccount().get().getBalance(ETH).get().getAvailable()));
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
            throw new PositionException("Position not found : " + id);
        }
    }

}
