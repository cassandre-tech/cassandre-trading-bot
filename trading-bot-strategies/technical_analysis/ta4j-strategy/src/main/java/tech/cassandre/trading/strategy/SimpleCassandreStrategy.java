package tech.cassandre.trading.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Simple strategy with ta4j.
 * Please, create your own Kucoin sandbox account and do not make orders with this account.
 * How to do it : https://trading-bot.cassandre.tech/how_to_create_an_exchange_sandbox_for_kucoin.html
 */
@CassandreStrategy(name = "Ta4j strategy")
public final class SimpleCassandreStrategy extends BasicCassandreStrategy {

    /** Currency pair we are trading. */
    private CurrencyPairDTO cp = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);

    /** Series. */
    private BarSeries series;

    /** Strategy. */
    private Strategy strategy;

    /**
     * Constructor.
     */
    public SimpleCassandreStrategy() {
        // Define series (we keep 100 bars).
        series = new BaseBarSeriesBuilder().withNumTypeOf(DoubleNum.class).withName("ETH/BTC").build();
        series.setMaximumBarCount(100);

        // Getting the simple moving average (SMA) of the close price over the last 10 ticks.
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator shortSma = new SMAIndicator(closePrice, 10);
        SMAIndicator longSma = new SMAIndicator(closePrice, 30);

        // Buying rule : We want to buy if the 5-ticks SMA crosses over 30-ticks SMA.
        Rule buyingRule = new CrossedUpIndicatorRule(shortSma, longSma);

        // Selling rule : We want to sell if the 5-ticks SMA crosses under 30-ticks SMA.
        Rule sellingRule = new CrossedDownIndicatorRule(shortSma, longSma);

        // Building the strategy.
        strategy = new BaseStrategy(buyingRule, sellingRule);
    }

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        // We only ask about ETC/BTC (Base currency : ETH / Quote currency : BTC).
        return Set.of(cp);
    }

    @Override
    public void onTickerUpdate(final TickerDTO ticker) {
        // Here we will receive a TickerDTO each time a new one is available.
        // TODO there is a bug with Kucoin Xchange lib, open is always null.
        // https://github.com/knowm/XChange/pull/2946#issuecomment-605036594
        System.out.println("- Adding a bar with : " + ticker);
        series.addBar(ticker.getTimestamp(), 0, ticker.getHigh(), ticker.getLow(), ticker.getLast(), ticker.getVolume());

        // We use the defined strategy to see if we should enter, exit or do nothing.
        int endIndex = series.getEndIndex();
        if (strategy.shouldEnter(endIndex)) {
            // Our strategy should enter
            OrderCreationResultDTO buyMarketOrder = getTradeService().createBuyMarketOrder(cp, new BigDecimal(1));
            System.out.println("=> Strategy enter - order " + buyMarketOrder.getOrderId());
        } else if (strategy.shouldExit(endIndex)) {
            // Our strategy should exit
            OrderCreationResultDTO sellMarketOrder = getTradeService().createSellMarketOrder(cp, new BigDecimal(1));
            System.out.println("=> Strategy exit - order " + sellMarketOrder.getOrderId());
        }
    }

}
