---
title: Add indicators
sidebar: cassandre_sidebar
permalink: technical_analysis_add_indicators.html
---

## Adding simple moving average (SMA).

We will add an indicator : simple moving average (SMA). Moving averages are one of the core indicators in technical analysis.  It is simply the average price over the specified period.

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
}
```

SMAs are often used to determine trend direction. If the SMA is moving up, the trend is up. If the SMA is moving down, the trend is down.

{% include note.html content="Ta4j includes more than [130 technical indicators](https://oss.sonatype.org/service/local/repositories/releases/archive/org/ta4j/ta4j-core/0.11/ta4j-core-0.11-javadoc.jar/!/index.html)" %}

