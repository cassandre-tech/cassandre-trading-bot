---
lang: en-US
title: Strategy events
description: Strategy events in Cassandre
---

# Strategy events

By implementing some methods in your strategy, you can receive updates when there is a change on your data on the
exchange (for example: a new order or new assets received), or new events (for example: a new ticker on BTC/USDT).

## Accounts updates

If the amount of your assets on your account
change, [onAccountsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onAccountsUpdates(java.util.Map))
will be called.

```java
@Override
public final void onAccountsUpdates(final Map<String, AccountDTO> accounts){
        // Here, we will receive an AccountDTO each time there is a change on your account.
        accounts.values().forEach(account->System.out.println("Received information about an account: "+account));
}
```

## Tickers updates

If a new ticker is
available, [onTickersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickersUpdates(java.util.Map))
will be called.

```java
@Override
public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers){
        // Here we will receive all tickers we required from the exchange.
        tickers.values().forEach(ticker->System.out.println("Received information about a ticker: "+ticker));
}
```

## Orders updates

If an order is created or updated,
[onOrdersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onOrdersUpdates(java.util.Map))
will be called.

```java
@Override
public void onOrdersUpdates(final Map<String, OrderDTO> orders){
        // Here, we will receive an OrderDTO each time order data has changed on the exchange.
        orders.values().forEach(order->System.out.println("Received information about an order: "+order));
}
```

## Trades updates

If a trade is created or
updated, [onTradesUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTradesUpdates(java.util.Map))
will be called.

```java
@Override
public void onTradesUpdates(final Map<String, TradeDTO> trades){
        // Here, we will receive a TradeDTO each time trade data has changed on the exchange.
        trades.values().forEach(trade->System.out.println("Received information about a trade: "+trade));
}
```

## Positions updates

If a position is created or
updated, [onPositionsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsUpdates(java.util.Map))
will be called.

```java
@Override
public void onPositionsUpdates(final Map<Long, PositionDTO> positions){
        // Here, we will receive a PositionDTO each time a position has changed.
        positions.values().forEach(position->System.out.println("Received information about a position: "+position));
}
```

## Positions status updates

If a position status is
updated, [onPositionsStatusUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsStatusUpdates(java.util.Map))
will be called.

```java
@Override
public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions){
        // Here, we will receive a PositionDTO each time a position status has changed.
        positions.values().forEach(position->System.out.println("Received information about a position status: "+position));
}
```