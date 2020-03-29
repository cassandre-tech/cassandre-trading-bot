---
title: Strategy configuration
sidebar: cassandre_sidebar
summary: Classes managing the user strategy configuration.
permalink: bot_development_strategy_configuration.html
---

## StrategyConfiguration.
The [StrategyAutoConfiguration](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/configuration/StrategyAutoConfiguration.java) search for the user strategy class marked with the [@CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java) annotation and implementing [BasicCassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.java).

This class does the following steps : 
  * Check there is one and only one User strategy marked with the [@CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java) annotation.
  * Check this strategy extends [BasicCassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.java).
  * Connect the AccountFlux to the <code>onAccountUpdate()</code> method of the strategy.
  * Connect the TickerFlux to the <code>onTickerUpdate()</code> method of the strategy (after calling <code>getRequestedCurrencyPairs()</code> on strategy to know which currency pairs to retrieve).
  * Connect the OrderFlux to the <code>onOrderUpdate()</code> method of the strategy.
  * Set the trade service.

