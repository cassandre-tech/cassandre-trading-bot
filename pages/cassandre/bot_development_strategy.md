---
title: Strategy
sidebar: cassandre_sidebar
summary: Classes managing the user strategy.
permalink: bot_development_strategy.html
---

To create a strategy, the developer must create a class with the [@Strategy annotation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/Strategy.java) and implements the [CassandreStrategy interface](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java) 

## Strategy annotation.
The [@Strategy annotation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/Strategy.java) allows the developer to mark its class as a Strategy and declares it as a Spring bean.

| Parameter  | Description  |
|-------|---------|
| <code>name</code>   | Name of the strategy (Optional)  | 

## CassandreStrategy : Abstract Cassandre strategy.
The [CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java) class must be implemented by the strategy.

| Method  | Description  |
|-------|---------|
| <code>getRequestedCurrencyPairs()</code>   | Implements this method to tell the bot which currency pairs your strategy will receive via the <code>onTickerUpdate()</code> method  | 
| <code>onAccountUpdate()</code>   | Method triggered at every account update  | 
| <code>onTickerUpdate()</code>   | Method triggered at every ticker update  | 
| <code>onOrderUpdate()</code>   | Method triggered on every order update  | 
| <code>getTradeService()</code>   | Returns the trade service you can use to retrieve and create order  | 


