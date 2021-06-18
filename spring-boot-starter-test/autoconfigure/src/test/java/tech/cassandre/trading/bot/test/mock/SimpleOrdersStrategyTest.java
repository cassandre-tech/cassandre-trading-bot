package tech.cassandre.trading.bot.test.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
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
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest(properties = {"SIMPLE_ORDER_STRATEGY_ENABLED=true", "POSITIONS_STRATEGY_ENABLED=false"})
@ComponentScan("tech.cassandre.trading.bot")
@Import(TickerFluxMock.class)
@DisplayName("Simple orders strategy test")
@DirtiesContext(classMode = BEFORE_CLASS)
public class SimpleOrdersStrategyTest extends BaseTest {

    @Autowired
    private SimpleOrdersStrategy simpleOrdersStrategy;

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
    @DisplayName("Check simple orders behavior")
    public void checkSimpleOrdersBehavior() {
        CurrencyPairDTO BTC_USDT = new CurrencyPairDTO(BTC, USDT);
        CurrencyPairDTO ETH_USDT = new CurrencyPairDTO(ETH, USDT);

        // We wait for all the tickers to be treated.
        // BTC/USDT => 7.
        // ETH/USDT => 5
        with().await().untilAsserted(() -> assertEquals(12, simpleOrdersStrategy.getTickersUpdateReceived().size()));
        with().await().untilAsserted(() -> assertEquals(4, orderRepository.count()));
        with().await().untilAsserted(() -> assertEquals(4, tradeRepository.count()));

        // =============================================================================================================
        // We check the buying order of 2 BTC.
        final Optional<OrderDTO> buyingOrderBTCUSDT = simpleOrdersStrategy.getOrders()
                .values()
                .stream()
                .filter(orderDTO -> orderDTO.getType().equals(BID))
                .filter(orderDTO -> orderDTO.getCurrencyPair().equals(BTC_USDT))
                .findFirst();
        assertTrue(buyingOrderBTCUSDT.isPresent());

        // Check the trade price.
        assertEquals(1, buyingOrderBTCUSDT.get().getTrades().size());
        final Optional<TradeDTO> tradeForBuyingOrderBTCUSDT = buyingOrderBTCUSDT.get().getTrades().stream().findFirst();
        assertTrue(tradeForBuyingOrderBTCUSDT.isPresent());
        assertEquals(0, new BigDecimal("2").compareTo(tradeForBuyingOrderBTCUSDT.get().getAmount().getValue()));
        assertEquals(0, new BigDecimal("50000").compareTo(tradeForBuyingOrderBTCUSDT.get().getPrice().getValue()));

        // =============================================================================================================
        // We check the buying order of 1 BTC.
        final Optional<OrderDTO> sellingOrderBTCUSDT = simpleOrdersStrategy.getOrders()
                .values()
                .stream()
                .filter(orderDTO -> orderDTO.getType().equals(ASK))
                .filter(orderDTO -> orderDTO.getCurrencyPair().equals(BTC_USDT))
                .findFirst();
        assertTrue(sellingOrderBTCUSDT.isPresent());

        // Check the trade price.
        assertEquals(1, sellingOrderBTCUSDT.get().getTrades().size());
        final Optional<TradeDTO> tradeForSellingOrderBTCUSDT = sellingOrderBTCUSDT.get().getTrades().stream().findFirst();
        assertTrue(tradeForSellingOrderBTCUSDT.isPresent());
        assertEquals(0, new BigDecimal("1").compareTo(tradeForSellingOrderBTCUSDT.get().getAmount().getValue()));
        assertEquals(0, new BigDecimal("70000").compareTo(tradeForSellingOrderBTCUSDT.get().getPrice().getValue()));

        // =============================================================================================================
        // We check the buying order of 3 ETH.
        final Optional<OrderDTO> buyingOrderETHUSDT = simpleOrdersStrategy.getOrders()
                .values()
                .stream()
                .filter(orderDTO -> orderDTO.getType().equals(BID))
                .filter(orderDTO -> orderDTO.getCurrencyPair().equals(ETH_USDT))
                .findFirst();
        assertTrue(buyingOrderETHUSDT.isPresent());

        // Check the trade price.
        assertEquals(1, buyingOrderETHUSDT.get().getTrades().size());
        final Optional<TradeDTO> tradeForBuyingOrderETHUSDT = buyingOrderETHUSDT.get().getTrades().stream().findFirst();
        assertTrue(tradeForBuyingOrderETHUSDT.isPresent());
        assertEquals(0, new BigDecimal("3").compareTo(tradeForBuyingOrderETHUSDT.get().getAmount().getValue()));
        assertEquals(0, new BigDecimal("5000").compareTo(tradeForBuyingOrderETHUSDT.get().getPrice().getValue()));

        // =============================================================================================================
        // We check the selling order of 4 ETH.
        final Optional<OrderDTO> sellingOrderETHUSDT = simpleOrdersStrategy.getOrders()
                .values()
                .stream()
                .filter(orderDTO -> orderDTO.getType().equals(ASK))
                .filter(orderDTO -> orderDTO.getCurrencyPair().equals(ETH_USDT))
                .findFirst();
        assertTrue(sellingOrderETHUSDT.isPresent());

        // Check the trade price.
        assertEquals(1, sellingOrderETHUSDT.get().getTrades().size());
        final Optional<TradeDTO> tradeForSellingOrderETHUSDT = sellingOrderETHUSDT.get().getTrades().stream().findFirst();
        assertTrue(tradeForSellingOrderETHUSDT.isPresent());
        assertEquals(0, new BigDecimal("4").compareTo(tradeForSellingOrderETHUSDT.get().getAmount().getValue()));
        assertEquals(0, new BigDecimal("10000").compareTo(tradeForSellingOrderETHUSDT.get().getPrice().getValue()));

        // =============================================================================================================
        // Assets I owned before running this strategy: 200 000 USDT & 100 ETH
        // Buying 2 BTC at 50 000 USDT: 100 000 USDT, 100 ETH, 2 BTC.
        // Selling 1 BTC at 70 000 USDT: 170 000 USDT, 100 ETH, 1 BTC.
        // Buying 3 ETH at 5 000 USDT: 155 000 USDT, 103 ETH, 1 BTC.
        // Selling 4 ETH at 10 000 USDT 195 000 USDT, 99 ETH, 1 BTC.
        final Optional<UserDTO> user = userService.getUser();
        assertTrue(user.isPresent());
        final Optional<AccountDTO> tradeAccount = user.get().getAccountById("trade");
        assertTrue(tradeAccount.isPresent());

        // Checking we have the balance.
        assertEquals(3, tradeAccount.get().getBalances().size());
        final Optional<BalanceDTO> usdtBalance = tradeAccount.get().getBalance(USDT);
        assertTrue(usdtBalance.isPresent());
        final Optional<BalanceDTO> ethBalance = tradeAccount.get().getBalance(ETH);
        assertTrue(ethBalance.isPresent());
        final Optional<BalanceDTO> btcBalance = tradeAccount.get().getBalance(BTC);
        assertTrue(btcBalance.isPresent());

        // Checking the balance values.
        assertEquals(0, new BigDecimal("195000").compareTo(usdtBalance.get().getAvailable()));
        assertEquals(0, new BigDecimal("99").compareTo(ethBalance.get().getAvailable()));
        assertEquals(0, new BigDecimal("1").compareTo(btcBalance.get().getAvailable()));
    }

}
