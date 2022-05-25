---
lang: en-US
title: What is a trading strategy?
description: What is a trading strategy?
---

# What is a strategy ?

A strategy is a class you write to describe what your want to do (buy, sell, create position...) depending on the assets
you own, the market data (tickers) or any other data you can or want to grab.

To be managed by Cassandre, your strategy must have
the [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html)
annotation and inherits
from [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html)
.

You have to implement :

* [getRequestedCurrencyPairs()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29)
  to indicate the list of currency pairs tickers you want to receive.
* [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29)
  to indicate which account on the exchange is the one you are using for trading.

You will find below the minimal strategy with:

* [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html)
  annotation.
* Inheritance
  from [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html)
  class.
* [getRequestedCurrencyPairs()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29)
  implementation indicating that we want to receive one currency-pair : `BTC/USDT`.
* [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29)
  Implementation indicating that, from all the accounts we own on the exchange, the one we use for trading is the one
  having the name `trade`.

```java
package com.mycompany.app;

import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.Map;
import java.util.Set;

/**
 * Simple strategy.
 */
@CassandreStrategy
public final class SimpleStrategy extends BasicCassandreStrategy {

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        // We only ask for BTC/USDT tickers (Base currency : BTC / Quote currency : USDT).
        return Set.of(new CurrencyPairDTO(BTC, USDT));
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // From all the accounts we have on the exchange,
        // we must return the one we use for trading.
        return accounts.stream()
                .filter(a -> "trade".equalsIgnoreCase(a.getName()))
                .findFirst();
    }

}
```

::: tip
You can run several strategies in a single bot, but they will all be connected to a single exchange.
:::