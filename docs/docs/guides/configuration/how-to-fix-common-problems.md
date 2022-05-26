---
lang: en-US
title: Fix common problems
description: How to fix common Cassandre problems
---

# How to fix common Cassandre problems

## Your strategies specify a trading account that doesn't exist

First thing to check: your configuration. If you are connecting to a real exchange (not a sandbox) with your real
credentials, you must have those parameters to `false` in your `application.properties`:

```properties
cassandre.trading.bot.exchange.modes.sandbox=false
cassandre.trading.bot.exchange.modes.dry=false
```

Second step, usually, on the exchange, you have one login/password and several accounts, for example: one for trading,
one for savings... In order to know if you can buy or can sell, Cassandre needs to know which account on the exchange is
the one you are using for trading.

To do so, you have to implement
the [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29)
method in your strategy. This method will be called by Cassandre and gives, as parameter, the list of accounts found on
the exchange.

From that list, you must return one of them.

To help you find the right one, when you start Cassandre, it will display in the logs the accounts found on the
exchange and their balance:

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

## Way too much request weight used; IP banned until

On Binance, you should not ask for data too often, or you will get a `Way too much request weight used` error, use those
parameters in your `application.properties`:

```properties
cassandre.trading.bot.exchange.rates.account=PT30S
cassandre.trading.bot.exchange.rates.ticker=PT30S
cassandre.trading.bot.exchange.rates.trade=PT30S
```

## Requested bean is currently in creation: Is there an unresolvable circular reference?

When you have this error message on startup:

```
̀Unknown configuration error: Error creating bean with name 'tech.cassandre.trading.bot.configuration.ExchangeAutoConfiguration': Requested bean is currently in creation: Is there an unresolvable circular reference?
```

Since Spring boot 2.6.0, circular references are prohibited by default and before Cassandre 5.0.7, we had an error
circular references we did not notice. So, if you are using a spring boot 2.6.0, you have to use a Cassandre release
superior to 5.0.7.

## Kucoin - Your strategies specifies a trading account that doesn't exist

You have assets on your account, and `getTradeAccount(Set<AccountDTO> accounts)` is implemented but when your bot is
starting, you get the following error `Your strategies specifies a trading account that doesn't exist`.

You can try this
solution: [https://github.com/cassandre-tech/cassandre-trading-bot/issues/786#issuecomment-999503117](https://github.com/cassandre-tech/cassandre-trading-bot/issues/786#issuecomment-999503117)
.