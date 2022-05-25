---
lang: fr-FR
title: Gestion des évènements
description: Gestion des évènements dans Cassandre
---

# Gestion des évènements

En implémentant certaines méthodes dans votre stratégie, vous allez pouvoir être prévenu des changements dans les
données (par exemple : une commande qui vient d'être créée) ou de nouveaux évènements (par exemple : un nouveau ticker
sur la paire de devis BTC/USDT).

## Modification de vos comptes

Si le montant de vos actifs sur votre compte change, la
méthode [onAccountsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onAccountsUpdates(java.util.Map))
de votre stratégie sera appelée.

```java
@Override
public final void onAccountsUpdates(final Map<String, AccountDTO> accounts){
        // Here, we will receive an AccountDTO each time there is a change on your account.
        accounts.values().forEach(account->System.out.println("Received information about an account: "+account));
}
```

## Nouveau ticker

Si un nouveau ticker est disponible, la
méthode [onTickersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickersUpdates(java.util.Map))
de votre stratégie sera appelée.

```java
@Override
public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers){
        // Here we will receive all tickers we required from the exchange.
        tickers.values().forEach(ticker->System.out.println("Received information about a ticker: "+ticker));
}
```

## Mise à jour des ordres

Si un ordre est créé ou mise à jour, la
méthode [onOrdersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onOrdersUpdates(java.util.Map))
de votre stratégie sera appelée.

```java
@Override
public void onOrdersUpdates(final Map<String, OrderDTO> orders){
        // Here, we will receive an OrderDTO each time order data has changed on the exchange.
        orders.values().forEach(order->System.out.println("Received information about an order: "+order));
}
```

## Mise à jour des trades

Si un trade est créé ou mise à jour, la
méthode [onTradesUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTradesUpdates(java.util.Map))
de votre stratégie sera appelée.

```java
@Override
public void onTradesUpdates(final Map<String, TradeDTO> trades){
        // Here, we will receive a TradeDTO each time trade data has changed on the exchange.
        trades.values().forEach(trade->System.out.println("Received information about a trade: "+trade));
}
```

## Mise à jour des positions

Si une position est créée ou mise à jour, la
méthode [onPositionsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsUpdates(java.util.Map))
de votre stratégie sera appelée.

```java
@Override
public void onPositionsUpdates(final Map<Long, PositionDTO> positions){
        // Here, we will receive a PositionDTO each time a position has changed.
        positions.values().forEach(position->System.out.println("Received information about a position: "+position));
}
```

## Mise à jour des statuts des positions

Si le statut d'une position est mise à jour, la
méthode [onPositionsStatusUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsStatusUpdates(java.util.Map))
de votre stratégie sera appelée.

```java
@Override
public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions){
        // Here, we will receive a PositionDTO each time a position status has changed.
        positions.values().forEach(position->System.out.println("Received information about a position status: "+position));
}
```