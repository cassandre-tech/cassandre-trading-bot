package tech.cassandre.trading.bot.test.services.dry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.services.dry.mocks.TradeServiceDryModeTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.LargeTestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.EUR;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;
import static tech.cassandre.trading.bot.test.util.strategies.LargeTestableCassandreStrategy.PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;

@SpringBootTest
@DisplayName("Service - Dry - User service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED, value = "true"),
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(TradeServiceDryModeTestMock.class)
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private AccountFlux accountFlux;

    @Autowired
    private LargeTestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check imported user data")
    public void checkImportUserData() {
        // Retrieve user.
        final Optional<UserDTO> user = userService.getUser();
        assertTrue(user.isPresent());
        assertEquals(3, user.get().getAccounts().size());

        // Main account.
        final AccountDTO mainAccount = user.get().getAccounts().get("main");
        assertEquals("main", mainAccount.getAccountId());
        assertEquals("main", mainAccount.getName());
        assertEquals(1, mainAccount.getBalances().size());
        Optional<BalanceDTO> mainBTC = mainAccount.getBalance(BTC);
        assertTrue(mainBTC.isPresent());
        assertEquals(0, new BigDecimal("99.0001").compareTo(mainBTC.get().getAvailable()));

        // Trade account.
        final AccountDTO tradeAccount = user.get().getAccounts().get("trade");
        assertEquals("trade", tradeAccount.getAccountId());
        assertEquals("trade", tradeAccount.getName());
        assertEquals(3, tradeAccount.getBalances().size());
        Optional<BalanceDTO> tradeBTC = tradeAccount.getBalance(BTC);
        assertTrue(tradeBTC.isPresent());
        assertEquals(0, new BigDecimal("0.99962937").compareTo(tradeBTC.get().getAvailable()));
        Optional<BalanceDTO> tradeUSDT = tradeAccount.getBalance(USDT);
        assertTrue(tradeUSDT.isPresent());
        assertEquals(0, new BigDecimal("1000").compareTo(tradeUSDT.get().getAvailable()));
        Optional<BalanceDTO> tradeETH = tradeAccount.getBalance(ETH);
        assertTrue(tradeETH.isPresent());
        assertEquals(0, new BigDecimal("10").compareTo(tradeETH.get().getAvailable()));

        // Savings account.
        final AccountDTO savingsAccount = user.get().getAccounts().get("savings");
        assertEquals("savings", savingsAccount.getAccountId());
        assertEquals("savings", savingsAccount.getName());
        assertEquals(3, savingsAccount.getBalances().size());
        Optional<BalanceDTO> savingsBTC = savingsAccount.getBalance(BTC);
        assertTrue(savingsBTC.isPresent());
        assertEquals(0, new BigDecimal("1.1").compareTo(savingsBTC.get().getAvailable()));
        Optional<BalanceDTO> savingsUSDT = savingsAccount.getBalance(USDT);
        assertTrue(savingsUSDT.isPresent());
        assertEquals(0, new BigDecimal("2.2").compareTo(savingsUSDT.get().getAvailable()));
        Optional<BalanceDTO> savingsETH = savingsAccount.getBalance(ETH);
        assertTrue(savingsETH.isPresent());
        assertEquals(0, new BigDecimal("3.3").compareTo(savingsETH.get().getAvailable()));
    }

    @Test
    @DisplayName("Check balances updates")
    public void checkBalancesUpdate() {
        // We retrieve the account information in the strategy.
        assertTrue(strategy.getAccountsUpdatesReceived().isEmpty());
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(3, strategy.getAccountsUpdatesReceived().size()));

        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032661 BTC.
        // last = 0.032661 (Last trade field is the price set during the last trade)
        // TickerDTO{ currencyPair=ETH/BTC, open=null, last=0.032661, bid=0.032466, ask=0.032657, high=0.034441, low=0.032355, vwap=null, volume=33794.9795777, quoteVolume=1146.8453384314658, bidSize=null, askSize=null, timestamp=2020-09-21T14:55:54.047+02:00[Europe/Paris]}
        tickerFlux.emitValue(TickerDTO.builder()
                .currencyPair(ETH_BTC)
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .build());
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTickers().size()));

        // =============================================================================================================
        // Account before buying.
        // BTC => 0.99962937
        // ETH => 10
        Optional<UserDTO> user = userService.getUser();
        assertTrue(user.isPresent());
        AccountDTO tradeAccount = user.get().getAccounts().get("trade");
        Optional<BalanceDTO> tradeBTC = tradeAccount.getBalance(BTC);
        assertTrue(tradeBTC.isPresent());
        assertEquals(0, new BigDecimal("0.99962937").compareTo(tradeBTC.get().getAvailable()));
        Optional<BalanceDTO> tradeETH = tradeAccount.getBalance(ETH);
        assertTrue(tradeETH.isPresent());
        assertEquals(0, new BigDecimal("10").compareTo(tradeETH.get().getAvailable()));

        // =============================================================================================================
        // Buying 0.02 ETH for 0.00065332 BTC.
        // Last price from ticker * amount ordered
        // 0.032666 * 0.02 = 0.00065332 BTC
        // TradeDTO{ id='5f68a2dc12e82b0006be5f36', orderId='5f68a2dbc9b81a0007f51274', type=BID, originalAmount=0.02, currencyPair=ETH/BTC, price=0.032666, timestamp=2020-09-21T14:55:56.148+02:00[Europe/Paris], fee=4.57324E-7 BTC}
        final OrderCreationResultDTO buyMarketOrder = strategy.createBuyMarketOrder(ETH_BTC, new BigDecimal("0.02"));
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(4, strategy.getAccountsUpdatesReceived().size()));

        // =============================================================================================================
        // Account values in strategy should be :
        // BTC => 0.99897605 (previous amount - amount bought = 0.99962937 - 0.00065332)
        // ETH => 10.02
        tradeAccount = strategy.getAccounts().get("trade");
        assertNotNull(tradeAccount);
        tradeBTC = tradeAccount.getBalance(BTC);
        assertTrue(tradeBTC.isPresent());
        assertEquals(0, new BigDecimal("0.99897605").compareTo(tradeBTC.get().getAvailable()));
        tradeETH = tradeAccount.getBalance(ETH);
        assertTrue(tradeETH.isPresent());
        assertEquals(0, new BigDecimal("10.02").compareTo(tradeETH.get().getAvailable()));

        // =============================================================================================================
        // Account after buying.
        // BTC => 0.99897605 (previous amount - amount bought = 0.99962937 - 0.00065332)
        // ETH => 10.02
        user = userService.getUser();
        assertTrue(user.isPresent());
        tradeAccount = user.get().getAccounts().get("trade");
        tradeBTC = tradeAccount.getBalance(BTC);
        assertTrue(tradeBTC.isPresent());
        assertEquals(0, new BigDecimal("0.99897605").compareTo(tradeBTC.get().getAvailable()));
        tradeETH = tradeAccount.getBalance(ETH);
        assertTrue(tradeETH.isPresent());
        assertEquals(0, new BigDecimal("10.02").compareTo(tradeETH.get().getAvailable()));

        // =============================================================================================================
        // Testing the trade.
        // Amount => 0.02
        // Price => 0.032666
        with().await().until(() -> tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(buyMarketOrder.getOrder().getOrderId())));
        final Optional<TradeDTO> buyingTrade = tradeService.getTrades()
                .stream()
                .filter(t -> t.getOrderId().equals(buyMarketOrder.getOrder().getOrderId())).findFirst();
        assertTrue(buyingTrade.isPresent());
        assertEquals(BID, buyingTrade.get().getType());
        assertEquals(0, new BigDecimal("0.02").compareTo(buyingTrade.get().getAmount().getValue()));
        assertEquals(ETH, buyingTrade.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.032666").compareTo(buyingTrade.get().getPrice().getValue()));
        assertEquals(BTC, buyingTrade.get().getPrice().getCurrency());

        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032466 BTC.
        // last = 0.032466 (Last trade field is the price set during the last trade)
        tickerFlux.emitValue(TickerDTO.builder()
                .currencyPair(ETH_BTC)
                .last(new BigDecimal("0.032466"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .build());
        await().untilAsserted(() -> assertEquals(2, strategy.getTickersUpdatesReceived().size()));

        // =============================================================================================================
        // Selling 0.02 ETH.
        // Amount * Last price from ticker
        // 0.02 * 0.032466 = 0.00064932 ETH
        // TradeDTO{ id='5f68a2e812e82b0006be5fec', orderId='5f68a2e85c77b40006880392', type=ASK, originalAmount=0.02, currencyPair=ETH/BTC, price=0.032466, timestamp=2020-09-21T14:56:08.403+02:00[Europe/Paris], fee=4.54524E-7 BTC}
        final OrderCreationResultDTO sellMarketOrder = strategy.createSellMarketOrder(ETH_BTC, new BigDecimal("0.02"));
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(5, strategy.getAccountsUpdatesReceived().size()));

        // =============================================================================================================
        // Account values in strategy should be :
        // BTC => 0.99962537 (previous sold + amount sold = 0.99897605 + 0.00064932)
        // ETH => 10
        tradeAccount = strategy.getAccounts().get("trade");
        assertNotNull(tradeAccount);
        tradeBTC = tradeAccount.getBalance(BTC);
        assertTrue(tradeBTC.isPresent());
        assertEquals(0, new BigDecimal("0.99962537").compareTo(tradeBTC.get().getAvailable()));
        tradeETH = tradeAccount.getBalance(ETH);
        assertTrue(tradeETH.isPresent());
        assertEquals(0, new BigDecimal("10").compareTo(tradeETH.get().getAvailable()));

        // =============================================================================================================
        // Testing the trade.
        // Amount => 0.02
        // Price => 0.032466
        with().await().until(() -> tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(sellMarketOrder.getOrder().getOrderId())));
        final Optional<TradeDTO> sellingTrade = tradeService.getTrades()
                .stream()
                .filter(t -> t.getOrderId().equals(sellMarketOrder.getOrder().getOrderId())).findFirst();
        assertTrue(sellingTrade.isPresent());
        assertEquals(ASK, sellingTrade.get().getType());
        assertEquals(0, new BigDecimal("0.02").compareTo(sellingTrade.get().getAmount().getValue()));
        assertEquals(ETH, sellingTrade.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.032466").compareTo(sellingTrade.get().getPrice().getValue()));
    }

    @Test
    @DisplayName("Check buying error")
    public void checkBuyingError() {
        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032661 BTC.
        // last = 0.032661 (Last trade field is the price set during the last trade)
        // TickerDTO{ currencyPair=ETH/BTC, open=null, last=0.032661, bid=0.032466, ask=0.032657, high=0.034441, low=0.032355, vwap=null, volume=33794.9795777, quoteVolume=1146.8453384314658, bidSize=null, askSize=null, timestamp=2020-09-21T14:55:54.047+02:00[Europe/Paris]}
        TickerDTO ticker = TickerDTO.builder()
                .currencyPair(ETH_BTC)
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .build();
        tickerFlux.emitValue(ticker);
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTickers().size()));

        // =============================================================================================================
        // Received ticker for ETH/EUR.
        ticker = TickerDTO.builder()
                .currencyPair(new CurrencyPairDTO(ETH, EUR))
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .build();
        tickerFlux.emitValue(ticker);
        await().untilAsserted(() -> assertEquals(2, strategy.getLastTickers().size()));

        // =============================================================================================================
        // Buying with a currency we don't have.
        final OrderCreationResultDTO buyMarketOrder1 = strategy.createBuyMarketOrder(new CurrencyPairDTO(ETH, EUR), new BigDecimal("1000"));
        assertFalse(buyMarketOrder1.isSuccessful());
        assertTrue(buyMarketOrder1.getErrorMessage().contains("No assets (EUR)"));

        // =============================================================================================================
        // Buying 1000 ether we can’t afford.
        final OrderCreationResultDTO buyMarketOrder2 = strategy.createBuyMarketOrder(ETH_BTC, new BigDecimal("1000"));
        assertFalse(buyMarketOrder2.isSuccessful());
        assertTrue(buyMarketOrder2.getErrorMessage().contains("Not enough assets"));
    }

    @Test
    @DisplayName("Check selling error")
    public void checkSellingError() {
        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032661 BTC.
        // last = 0.032661 (Last trade field is the price set during the last trade)
        // TickerDTO{ currencyPair=ETH/BTC, open=null, last=0.032661, bid=0.032466, ask=0.032657, high=0.034441, low=0.032355, vwap=null, volume=33794.9795777, quoteVolume=1146.8453384314658, bidSize=null, askSize=null, timestamp=2020-09-21T14:55:54.047+02:00[Europe/Paris]}
        TickerDTO ticker = TickerDTO.builder()
                .currencyPair(ETH_BTC)
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .build();
        tickerFlux.emitValue(ticker);

        // =============================================================================================================
        // Received ticker for ETH/EUR.
        ticker = TickerDTO.builder()
                .currencyPair(new CurrencyPairDTO(ETH, EUR))
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .build();
        tickerFlux.emitValue(ticker);
        await().untilAsserted(() -> assertEquals(2, strategy.getLastTickers().size()));

        // =============================================================================================================
        // Selling with a currency we don't have.
        final OrderCreationResultDTO sellMarketOrder1 = strategy.createSellMarketOrder(new CurrencyPairDTO(ETH, EUR), new BigDecimal("1000"));
        assertFalse(sellMarketOrder1.isSuccessful());
        System.out.println("=> " + sellMarketOrder1.getErrorMessage());
        assertTrue(sellMarketOrder1.getErrorMessage().contains("Not enough assets"));

        // =============================================================================================================
        // Selling 1000 ether we don't have.
        final OrderCreationResultDTO sellMarketOrder2 = strategy.createSellMarketOrder(ETH_BTC, new BigDecimal("1000"));
        assertFalse(sellMarketOrder2.isSuccessful());
        assertTrue(sellMarketOrder2.getErrorMessage().contains("Not enough assets"));
    }

}
