---
title: Add time series
sidebar: cassandre_sidebar
permalink: technical_analysis_add_time_series.html
---

{% include note.html content="[ta4j sample project sources are available here](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-strategies/technical_analysis/ta4j-strategy)." %}

By creating your project with the Cassandre trading bot archetype, you should have a class named `SimpleStrategy` in `src/main/java/tech/cassandre/trading/strategy`.

## Creating a time series.

We will first declare a `BarSeries` in `SimpleStrategy` : 

```java
/** Series. */
private BarSeries series;
```

In `SimpleStrategy` constructor, we create the time series : 
```java
/**
 * Constructor.
 */
public SimpleStrategy() {
    // Define series (we keep 100 bars).
    series = new BaseBarSeriesBuilder().withNumTypeOf(DoubleNum.class).withName("ETH/BTC").build();
    series.setMaximumBarCount(100);
}
```

## Adding data to the time series.

We will take advantage of the method `onTickerUpdate(final TickerDTO ticker)` from Cassandre trading bot. This method is called by the bot every time a new ticker is available.

```java
@Override
public void onTickerUpdate(final TickerDTO ticker) {
    // Here we will receive a TickerDTO each time a new one is available.
    // TODO there is a bug with Kucoin Xchange lib, open is always null.
    // https://github.com/knowm/XChange/pull/2946#issuecomment-605036594
    System.out.println("Adding a bar with : " + ticker);
    series.addBar(ticker.getTimestamp(), 0, ticker.getHigh(), ticker.getLow(), ticker.getLast(), ticker.getVolume());
}
```