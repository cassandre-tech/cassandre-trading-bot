---
lang: en-US
title: Explore generated code
description: Cassandre tutorial - Explore generated code
---

# Explore generated code

Our archetype has created a strategy in `src/main/java/com/mycompany/bot/SimpleStrategy.java`:

```java
package com.mycompany.bot;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Simple strategy.
 */
@CassandreStrategy(strategyName = "Simple strategy")
public final class SimpleStrategy extends BasicCassandreStrategy {

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        // We only ask for BTC/USDT tickers (Base currency : BTC / Quote currency : USDT).
        return Set.of(new CurrencyPairDTO(BTC, USDT));
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // From all the accounts we have on the exchange, we must return the one we use for trading.
        if (accounts.size() == 1) {
            // If there is only one on the exchange, we choose this one.
            return accounts.stream().findFirst();
        } else {
            // If there are several accounts on the exchange, we choose the one whose name is "trade".
            return accounts.stream()
                    .filter(a -> "trade".equalsIgnoreCase(a.getName()))
                    .findFirst();
        }
    }

    @Override
    public void onAccountsUpdates(final Map<String, AccountDTO> accounts) {
        // Here, we will receive an AccountDTO each time there is a change on your account.
        accounts.values().forEach(account -> System.out.println("Received information about an account: " + account));
    }

    @Override
    public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        // Here we will receive all tickers we required from the exchange.
        tickers.values().forEach(ticker -> System.out.println("Received information about a ticker: " + ticker));
    }

    @Override
    public void onOrdersUpdates(final Map<String, OrderDTO> orders) {
        // Here, we will receive an OrderDTO each time order data has changed on the exchange.
        orders.values().forEach(order -> System.out.println("Received information about an order: " + order));
    }

    @Override
    public void onTradesUpdates(final Map<String, TradeDTO> trades) {
        // Here, we will receive a TradeDTO each time trade data has changed on the exchange.
        trades.values().forEach(trade -> System.out.println("Received information about a trade: " + trade));
    }

    @Override
    public void onPositionsUpdates(final Map<Long, PositionDTO> positions) {
        // Here, we will receive a PositionDTO each time a position has changed.
        positions.values().forEach(position -> System.out.println("Received information about a position: " + position));
    }

    @Override
    public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions) {
        // Here, we will receive a PositionDTO each time a position status has changed.
        positions.values().forEach(position -> System.out.println("Received information about a position status: " + position));
    }

}
```

A Cassandre strategy is a class annotated
with [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html)
and
extending [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html)
.

This is how it works:

* In [getRequestedCurrencyPairs()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29)
, you return the list of currency pairs tickers you want to receive from the exchange.
* On the exchange, you usually have several accounts, and Cassandre needs to know which one of your accounts is the
  trading one. To do so, you have to implement
  the [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29)
  method, which gives you as a parameter the list of accounts you own on the exchange, and from that list, you have to
  return the one you use for trading.
* If there is a change in your account
  data, [onAccountsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onAccountsUpdates(java.util.Map))
  will be called.
* When new tickers are
  available, [onTickersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickersUpdates(java.util.Map))
  will be called.
* If there is a change in your
  orders, [onOrdersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onOrdersUpdates(java.util.Map))
  will be called.
* If there is a change in your
  trades, [onTradesUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTradesUpdates(java.util.Map))
  will be called.
* If there is a change in your
  positions, [onPositionsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsUpdates(java.util.Map))
  will be called.
* If there is a change in your position
  status, [onPositionsStatusUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsStatusUpdates(java.util.Map))
  will be called.