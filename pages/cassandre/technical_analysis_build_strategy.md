---
title: Build a trading strategy
sidebar: cassandre_sidebar
permalink: technical_analysis_build_strategy.html
---

{% include note.html content="[ta4j sample project sources are availables here](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-strategies/technical_analysis/ta4j-strategy)." %}

## Creating a strategy.

The strategy allows you to define the rules your bot should follow to buy or sell asset. Those rules can use the indicators created with ta4j like we did for SMA.

For example, we will create a buy rule saying : "We want to buy if the 5-ticks SMA crosses over 30-ticks SMA" and a selling rule saying : "We want to sell if the 5-ticks SMA crosses under 30-ticks SMA". You can also add more simple rules like : "sell if the price loses more than 3%".

In `SimpleStrategy`, we add a new class variable : 
```java
/** Strategy. */
private Strategy strategy;
```

In `SimpleStrategy` constructor, we will create the two rules and then creates the strategy : 
```java
/**
 * Constructor.
 */
public SimpleStrategy() {
    // Define series (we keep 100 bars).
    series = new BaseBarSeriesBuilder().withNumTypeOf(DoubleNum.class).withName("ETH/BTC").build();
    series.setMaximumBarCount(100);

    // Getting the simple moving average (SMA) of the close price over the last 5 ticks.
    ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    SMAIndicator shortSma = new SMAIndicator(closePrice, 5);
    SMAIndicator longSma = new SMAIndicator(closePrice, 30);

    // Buying rule : We want to buy if the 5-ticks SMA crosses over 30-ticks SMA.
    Rule buyingRule = new CrossedUpIndicatorRule(shortSma, longSma);

    // Selling rule : We want to sell if the 5-ticks SMA crosses under 30-ticks SMA.
    Rule sellingRule = new CrossedDownIndicatorRule(shortSma, longSma);

    // Building the strategy.
    strategy = new BaseStrategy(buyingRule, sellingRule);
}
```

## Using the strategy.

At each new ticker, we will ask the strategy if we should enter or exit. Depending on the result, we use Cassandre trading service to make an order.

```java
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
        System.out.println("==> Strategy exit - order " + sellMarketOrder.getOrderId());
    }
}
```