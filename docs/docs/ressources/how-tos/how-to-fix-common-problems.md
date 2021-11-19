---
lang: en-US
title: Fix common problems
description: How to fix common Cassandre problems
---
# How to fix common Cassandre problems

## "Your strategies specifies a trading account that doesn't exist"
First thing to check: your configuration. If you are connecting to a real exchange (not a sandbox) with your real credentials, you must have those parameters to `false` in your `application.properties`:

```properties
cassandre.trading.bot.exchange.modes.sandbox=false
cassandre.trading.bot.exchange.modes.dry=false
```

Second step, usually, on the exchange, you have one login/password and several accounts, for example: one for trading, one for savings... In order to know if you can buy or can sell, Cassandre needs to know which account on the exchange is the one you are using for trading.

To do so, you have to implement the [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29) method in your strategy. This method will be called by Cassandre and gives, as parameter, the list of accounts found on the exchange.

From that list, you must return one of them.

To help you, when you start Cassandre, it will display ion the logs the accounts found on the exchange:
```
2021-11-18 Available accounts on the exchange:
2021-11-18 - Account id / name: trade / trade.
2021-11-18  - 4 TUSD.
2021-11-18  - 5.00007534 UNI.
2021-11-18  - 11.26224436 USDT.
2021-11-18  - 0.01 BTC.
2021-11-18 - Account id / name: main / main.
2021-11-18  - 1 BTC.
```