package tech.cassandre.trading.bot.test.modes.dry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
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
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TRADE_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.EUR;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ORDER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

@SetSystemProperty(key = PARAMETER_NAME, value = PARAMETER_NAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SANDBOX, value = PARAMETER_SANDBOX_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_DRY, value = "true")
@SetSystemProperty(key = PARAMETER_USERNAME, value = PARAMETER_USERNAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_PASSPHRASE, value = PARAMETER_PASSPHRASE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_KEY, value = PARAMETER_KEY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SECRET, value = PARAMETER_SECRET_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_TRADE_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SpringBootTest
@ActiveProfiles("schedule-disabled")
@Import(TradeServiceDryModeTestMock.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("userService (dry mode)")
public class UserServiceDryModeTest extends BaseTest {

    private static final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

    @Autowired
    private UserService userService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private AccountFlux accountFlux;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Import user data")
    public void importUserDataTest() {
        // Retrieve user.
        final Optional<UserDTO> user = userService.getUser();
        assertTrue(user.isPresent());
        assertEquals(2, user.get().getAccounts().size());

        // Main account.
        final AccountDTO mainAccount = user.get().getAccounts().get("main");
        assertEquals("main", mainAccount.getId());
        assertEquals("main", mainAccount.getName());
        assertEquals(1, mainAccount.getBalances().size());
        Optional<BalanceDTO> mainBTC = mainAccount.getBalance(BTC);
        assertTrue(mainBTC.isPresent());
        assertEquals(0, new BigDecimal("99.0001").compareTo(mainBTC.get().getAvailable()));

        // Trade account.
        final AccountDTO tradeAccount = user.get().getAccounts().get("trade");
        assertEquals("trade", tradeAccount.getId());
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
    }

    @Test
    @DisplayName("Account update test")
    public void accountUpdateTest() {
        // We retrieve the account information in the strategy.
        assertTrue(strategy.getAccountsUpdatesReceived().isEmpty());
        accountFlux.update();
        assertEquals(2, strategy.getAccountsUpdatesReceived().size());

        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032661 BTC.
        // last = 0.032661 (Last trade field is the price set during the last trade)
        // TickerDTO{ currencyPair=ETH/BTC, open=null, last=0.032661, bid=0.032466, ask=0.032657, high=0.034441, low=0.032355, vwap=null, volume=33794.9795777, quoteVolume=1146.8453384314658, bidSize=null, askSize=null, timestamp=2020-09-21T14:55:54.047+02:00[Europe/Paris]}
        TickerDTO ticker = TickerDTO.builder()
                .currencyPair(cp)
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .create();
        tickerFlux.emitValue(ticker);

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
        final OrderCreationResultDTO buyMarketOrder = tradeService.createBuyMarketOrder(cp, new BigDecimal("0.02"));
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(3, strategy.getAccountsUpdatesReceived().size()));

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
        with().await().until(() -> tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(buyMarketOrder.getOrderId())));
        final Optional<TradeDTO> buyingTrade = tradeService.getTrades()
                .stream()
                .filter(t -> t.getOrderId().equals(buyMarketOrder.getOrderId())).findFirst();
        assertTrue(buyingTrade.isPresent());
        assertEquals(BID, buyingTrade.get().getType());
        assertEquals(0, new BigDecimal("0.02").compareTo(buyingTrade.get().getOriginalAmount()));
        assertEquals(0, new BigDecimal("0.032666").compareTo(buyingTrade.get().getPrice()));

        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032466 BTC.
        // last = 0.032466 (Last trade field is the price set during the last trade)
        ticker = TickerDTO.builder()
                .currencyPair(cp)
                .last(new BigDecimal("0.032466"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .create();
        tickerFlux.emitValue(ticker);

        // =============================================================================================================
        // Selling 0.02 ETH.
        // Amount * Last price from ticker
        // 0.02 * 0.032466 = 0.00064932 ETH
        // TradeDTO{ id='5f68a2e812e82b0006be5fec', orderId='5f68a2e85c77b40006880392', type=ASK, originalAmount=0.02, currencyPair=ETH/BTC, price=0.032466, timestamp=2020-09-21T14:56:08.403+02:00[Europe/Paris], fee=4.54524E-7 BTC}
        final OrderCreationResultDTO sellMarketOrder = tradeService.createSellMarketOrder(cp, new BigDecimal("0.02"));
        accountFlux.update();
        await().untilAsserted(() -> assertEquals(4, strategy.getAccountsUpdatesReceived().size()));

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
        with().await().until(() -> tradeService.getTrades().stream().anyMatch(t -> t.getOrderId().equals(sellMarketOrder.getOrderId())));
        final Optional<TradeDTO> sellingTrade = tradeService.getTrades()
                .stream()
                .filter(t -> t.getOrderId().equals(sellMarketOrder.getOrderId())).findFirst();
        assertTrue(sellingTrade.isPresent());
        assertEquals(ASK, sellingTrade.get().getType());
        assertEquals(0, new BigDecimal("0.02").compareTo(sellingTrade.get().getOriginalAmount()));
        assertEquals(0, new BigDecimal("0.032466").compareTo(sellingTrade.get().getPrice()));
    }

    @Test
    @DisplayName("Buying error")
    public void buyingError() {
        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032661 BTC.
        // last = 0.032661 (Last trade field is the price set during the last trade)
        // TickerDTO{ currencyPair=ETH/BTC, open=null, last=0.032661, bid=0.032466, ask=0.032657, high=0.034441, low=0.032355, vwap=null, volume=33794.9795777, quoteVolume=1146.8453384314658, bidSize=null, askSize=null, timestamp=2020-09-21T14:55:54.047+02:00[Europe/Paris]}
        TickerDTO ticker = TickerDTO.builder()
                .currencyPair(cp)
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .create();
        tickerFlux.emitValue(ticker);

        // =============================================================================================================
        // Received ticker for ETH/EUR
        ticker = TickerDTO.builder()
                .currencyPair(new CurrencyPairDTO(ETH, EUR))
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .create();
        tickerFlux.emitValue(ticker);

        // =============================================================================================================
        // Buying with a currency we don't have.
        final OrderCreationResultDTO buyMarketOrder1 = tradeService.createBuyMarketOrder(new CurrencyPairDTO(ETH, EUR), new BigDecimal("1000"));
        assertFalse(buyMarketOrder1.isSuccessful());
        assertTrue(buyMarketOrder1.getErrorMessage().contains("No assets for EUR"));

        // =============================================================================================================
        // Buying 1000 ether - should not work.
        final OrderCreationResultDTO buyMarketOrder2 = tradeService.createBuyMarketOrder(cp, new BigDecimal("1000"));
        assertFalse(buyMarketOrder2.isSuccessful());
        assertTrue(buyMarketOrder2.getErrorMessage().contains("Not enough assets"));
    }

    @Test
    @DisplayName("Selling error")
    public void sellingError() {
        // =============================================================================================================
        // Received ticker for ETH/BTC - It means 1 ETH can be bought with 0.032661 BTC.
        // last = 0.032661 (Last trade field is the price set during the last trade)
        // TickerDTO{ currencyPair=ETH/BTC, open=null, last=0.032661, bid=0.032466, ask=0.032657, high=0.034441, low=0.032355, vwap=null, volume=33794.9795777, quoteVolume=1146.8453384314658, bidSize=null, askSize=null, timestamp=2020-09-21T14:55:54.047+02:00[Europe/Paris]}
        TickerDTO ticker = TickerDTO.builder()
                .currencyPair(cp)
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .create();
        tickerFlux.emitValue(ticker);

        // =============================================================================================================
        // Received ticker for ETH/EUR
        ticker = TickerDTO.builder()
                .currencyPair(new CurrencyPairDTO(ETH, EUR))
                .last(new BigDecimal("0.032666"))
                .bid(new BigDecimal("0.032466"))
                .ask(new BigDecimal("0.032657"))
                .high(new BigDecimal("0.034441"))
                .low(new BigDecimal("0.032355"))
                .volume(new BigDecimal("33794.9795777"))
                .quoteVolume(new BigDecimal("1146.8453384314658"))
                .create();
        tickerFlux.emitValue(ticker);

        // =============================================================================================================
        // Buying with a currency we don't have.
        final OrderCreationResultDTO sellMarketOrder1 = tradeService.createSellMarketOrder(new CurrencyPairDTO(ETH, EUR), new BigDecimal("1000"));
        assertFalse(sellMarketOrder1.isSuccessful());
        assertTrue(sellMarketOrder1.getErrorMessage().contains("Not enough assets"));

        // =============================================================================================================
        // Buying 1000 ether - should not work.
        final OrderCreationResultDTO sellMarketOrder2 = tradeService.createSellMarketOrder(cp, new BigDecimal("1000"));
        assertFalse(sellMarketOrder2.isSuccessful());
        assertTrue(sellMarketOrder2.getErrorMessage().contains("Not enough assets"));
    }

}
