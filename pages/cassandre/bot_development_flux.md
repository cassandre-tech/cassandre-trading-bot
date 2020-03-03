---
title: Flux
sidebar: cassandre_sidebar
summary: Classes retrieving data from the exchange (accounts, tickers, and orders) and pushing them to the strategy.
permalink: bot_development_flux.html
---

## BaseFlux.
All batches are using [Project reactor](https://projectreactor.io) and inherits from the [BaseFlux](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util/base/BaseFlux.java) class. 

The only method to implement for a flux is <code>getNewValues()</code>, it will be called by the bot and the values returned will be send to the user strategy throw the flux. 

## AccountFlux.
[AccountFlux](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch/AccountFlux.java) is calling the exchange to retrieve accounts & balances. Any changes will result in a call to the method <code>onAccountUpdate()</code> of the user strategy.

{% include note.html content="The rate accountFlux will call the exchange is defined by the parameter <code>cassandre.trading.bot.exchange.rates.account</code> from <code>application.properties</code>." %}

## TickerFlux.
[TickerFlux](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch/TickerFlux.java) is calling the exchange to retrieve the tickers requested by the user strategy. New values will result in a call to the method <code>onTickerUpdate()</code> of the user strategy.

{% include note.html content="The rate TickerFlux will call the exchange is defined by the parameter <code>cassandre.trading.bot.exchange.rates.ticker</code> from <code>application.properties</code>." %}

## OrderFlux.
[OrderFlux](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch/OrderFlux.java) is calling the exchange to retrieve orders from the exchange. Updated orders will result in a call to the method <code>onOrderUpdate()</code> of the user strategy.

{% include note.html content="The rate OrderFlux will call the exchange is defined by the parameter <code>cassandre.trading.bot.exchange.rates.order</code> from <code>application.properties</code>." %}
