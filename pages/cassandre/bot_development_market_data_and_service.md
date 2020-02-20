---
title: Market data & service
sidebar: cassandre_sidebar
summary: Classes to manage market information (ticker).
permalink: bot_development_market_data_and_service.html
---

## Data.

{% include image.html file="project_development/package_market.png" alt="Market package class diagram" caption="Market package class diagram" %}

### The TickerDTO class.
[TickerDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/dto/market/TickerDTO.java) represents a stock ticker. A stock ticker is a report of the price of certain securities, updated continuously throughout the trading session by the various stock market exchanges. A "tick" is any change in the price of the security, whether that movement is up or down.

| Field  | Description  |
|-------|---------|
| <code>currencyPair</code>  | Currency pair  |
| <code>open</code>  | The opening price is the first trade price that was recorded during the day’s trading  |
| <code>last</code>  | Last trade field is the price at which the last trade was executed  |
| <code>bid</code>  | The bid price shown represents the highest bid price  |
| <code>ask</code>  | The ask price shown represents the lowest bid price  |
| <code>high</code>  | The day’s high price  |
| <code>low</code>  | The day’s low price  |
| <code>vwap</code>  | Volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day)  |
| <code>volume</code>  | Volume is the number of shares or contracts traded  |
| <code>quoteVolume</code>  | Quote volume  |
| <code>bidSize</code>  | The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price  |
| <code>askSize</code>  | The ask size represents the quantity of a security that investors are willing to sell at a specified selling price  |
| <code>timestamp</code>  | The moment at which the account information was retrieved  |

## Service.

### Market service.

[Market service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/MarketService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/MarketServiceXChangeImplementation.java) :

| Method  | Description  |
|-------|---------|
| <code>getTicker()</code>   | Returns a ticker for a currency pair  |

This service uses <code>org.knowm.xchange.service.marketdata.MarketDataService</code>.