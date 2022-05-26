---
lang: en-US
title: Import historical data
description: Learn how to use import historical data when your trading bot start and use them in your strategy
---

# Import historical data

## Overview

This feature allows you to import historical data at Cassandre startup, so you could use them to initialize your
strategies.

## Data file & format

At startup, Cassandre will search for files starting with `tickers-to-import` and ending with `csv`.

This is how the files must be formatted:

```
CURRENCY_PAIR,OPEN,LAST,BID,ASK,HIGH,LOW,VWAP,VOLUME,QUOTE_VOLUME,BID_SIZE,ASK_SIZE,TIMESTAMP
"BTC/USDT","0.00000001","0.00000002","0.00000003","0.00000004","0.00000005","0.00000006","0.00000007","0.00000008","0.00000009","0.00000010","0.00000011",1508546000
"BTC/USDT","1.00000001","1.00000002","1.00000003","1.00000004","1.00000005","1.00000006","1.00000007","1.00000008","1.00000009","1.00000010","1.00000011",1508446000
"ETH/USDT","2.00000001","2.00000002","2.00000003","2.00000004","2.00000005","2.00000006","2.00000007","2.00000008","2.00000009","2.00000010","2.00000011",1508346000
```

## When to initialize data?

In you strategy, you can implement
the [initialize()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#initialize())
method. This method is executed by Cassandre before any other data (tickers, orders, trades...) is pushed to the
strategy.

## Access your data in your strategy

In your strategy, you can access the data with two methods:

* [getImportedTickers()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#getImportedTickers())
  .
* [getImportedTickers(CurrencyPairDTO currencyPairDTO)](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#getImportedTickers(tech.cassandre.trading.bot.dto.util.CurrencyPairDTO))
  .