---
title: Strategy
sidebar: cassandre_sidebar
summary: Classes managing the user strategy.
permalink: bot_development_strategy.html
---

## Strategy annotation.
The [@Strategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/strategy/Strategy.java) annotation allows the developer to mark its class as a Strategy.

| Parameter  | Description  |
|-------|---------|
| <code>name</code>   | Name of the strategy (Optional)  | 

## CassandreStrategy : Abstract Cassandre strategy.
The [CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java) class must be implemented by the user to create its own strategy.

| Method  | Description  |
|-------|---------|
| <code>getRequestedCurrencyPairs()</code>   | Implements this method to tell the bot which currency pairs your strategy will receive via the <code>onTickerUpdate()</code> method  | 
| <code>onAccountUpdate()</code>   | Method triggered at every account update  | 
| <code>onTickerUpdate()</code>   | Method triggered at every ticker update  | 
| <code>onOrderUpdate()</code>   | Method triggered on every order update  | 
| <code>getTradeService()</code>   | Returns the trade service you can use to retrieve and create order  | 

## StrategyConfiguration.
The [StrategyConfiguration](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/configuration/StrategyConfiguration.java) search for the user strategy class marked with the [@Strategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/strategy/Strategy.java) annotation and extending [CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java).

This class does the following steps : 
  * Check that there is one and only one User strategy marked with the [@Strategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/strategy/Strategy.java) annotation.
  * Check that this strategy extends [CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java).
  * Connect the AccountFlux to the <code>onAccountUpdate()</code> method of the strategy.
  * Connect the TickerFlux to the <code>onTickerUpdate()</code> method of the strategy (after calling <code>getRequestedCurrencyPairs()</code> on strategy to know which currency pairs to retrieve).
  * Connect the OrderFlux to the <code>onOrderUpdate()</code> method of the strategy.

