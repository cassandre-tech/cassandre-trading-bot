package tech.cassandre.trading.bot.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.test.util.BaseTest.BTC_USDT;
import static tech.cassandre.trading.bot.test.util.BaseTest.ETH_USDT;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_ONLY_ORDERS_STRATEGY_ENABLED;

/**
 * Simple orders strategy.
 */
@SuppressWarnings("unused")
@CassandreStrategy
@ConditionalOnProperty(
        value = PARAMETER_ONLY_ORDERS_STRATEGY_ENABLED,
        havingValue = "true")
public final class OnlyOrdersStrategy extends BasicCassandreStrategy {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdateReceived = new LinkedList<>();

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> list = new LinkedHashSet<>();
        list.add(BTC_USDT);
        list.add(ETH_USDT);
        return list;
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream().filter(a -> "trade".equals(a.getAccountId())).findFirst();
    }

    @Override
    public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        // We have 200 000 USDT & 100 Ether.
        tickers.values().forEach(tickerDTO -> logger.info("Ticker received: {} \n", tickerDTO));

        // BTC/USDT.
        final TickerDTO tickerBTCUSDT = tickers.get(BTC_USDT);
        if (tickerBTCUSDT != null) {
            // We buy one bitcoin when it reaches 50 000 USDT.
            if (tickerBTCUSDT.getLast().compareTo(new BigDecimal("50000")) == 0) {
                final OrderCreationResultDTO buyMarketOrder = createBuyMarketOrder(BTC_USDT, new BigDecimal(2));
                logger.info("- BTC/USDT: Buying 2 BTC at 50 000 USDT, order n°{}\n", buyMarketOrder.getOrderId());
            }

            // We sell this bitcoin when it reaches 70 000 USDT
            if (tickerBTCUSDT.getLast().compareTo(new BigDecimal("70000")) == 0) {
                final OrderCreationResultDTO sellMarketOrder = createSellMarketOrder(BTC_USDT, new BigDecimal(1));
                logger.info("- BTC/USDT: Selling 1 BTC at 70 000 USDT, order n°{}\n", sellMarketOrder.getOrderId());
            }
        }

        // ETH/USDT.
        final TickerDTO tickerETHUSDT = tickers.get(ETH_USDT);
        if (tickerETHUSDT != null) {
            // We buy 3 ethers when it reaches 5 000 USDT.
            if (tickerETHUSDT.getLast().compareTo(new BigDecimal("5000")) == 0) {
                final OrderCreationResultDTO buyMarketOrder = createBuyMarketOrder(ETH_USDT, new BigDecimal(3));
                logger.info("- ETH/USDT: Buying 3 ETH at 5 000 USDT, order n°{}\n", buyMarketOrder.getOrderId());
            }

            // We sell 4 ethers when it reaches 10 000 USDT.
            if (tickerETHUSDT.getLast().compareTo(new BigDecimal("10000")) == 0) {
                final OrderCreationResultDTO sellMarketOrder = createSellMarketOrder(ETH_USDT, new BigDecimal(4));
                logger.info("- ETH/USDT: Selling 4 ETH at 10 000 USDT, order n° {}\n", sellMarketOrder.getOrderId());
            }
        }

        tickersUpdateReceived.addAll(tickers.values());
    }

    /**
     * Getter tickersUpdateReceived.
     *
     * @return tickersUpdateReceived
     */
    public List<TickerDTO> getTickersUpdateReceived() {
        return tickersUpdateReceived;
    }

}
