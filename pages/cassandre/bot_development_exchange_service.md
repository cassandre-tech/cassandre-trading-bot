---
title: Exchange service
sidebar: cassandre_sidebar
summary: Service giving information about the exchange. 
permalink: bot_development_exchange_service.html
---

## Exchange service.

[Exchange service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/ExchangeService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/ExchangeServiceXChangeImplementation.java) :

| Method  | Description  |
|-------|---------|
| <code>getAvailableCurrencyPairs()</code>   | Get the list of available currency pairs for trading  |

This service uses <code>org.knowm.xchange.Exchange</code>.