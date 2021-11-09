---
lang: en-US
title: Supported cryptocurrency exchanges
description: List of cryptocurrency exchanges supported by Cassandre thanks to XChange library
---
# Supported cryptocurrency exchanges

## Supported by XChange
Cassandre uses [XChange](https://github.com/knowm/XChange), a Java library providing a streamlined API for interacting with 60+ Bitcoin and Altcoin exchanges providing a consistent interface for trading and accessing market data.

You can find [here](https://github.com/knowm/XChange/wiki/Exchange-Support) a table showing a list of exchanges and how XChange supports them.

## Validated by Cassandre
Cassandre can theoretically support the 60+ cryptocurrency exchanges the way XChange does but we can't test them all. 

For the exchanges providing a sandbox, we were able to create integration tests for : 
 * [Coinbase](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/spring-boot-starter/autoconfigure/src/test/java/tech/cassandre/trading/bot/test/integration/coinbasepro): Working except cancel order.
 * [Gemini](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/spring-boot-starter/autoconfigure/src/test/java/tech/cassandre/trading/bot/test/integration/gemini): Working but market orders are not supported.
 * [Kucoin](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/spring-boot-starter/autoconfigure/src/test/java/tech/cassandre/trading/bot/test/integration/kucoin): Working.

We also run real bots with real assets on [Kucoin](https://www.kucoin.com/ucenter/signup?utm_source=Cassandre), [Coinbase](https://www.coinbase.com/join/straumat) and [Binance](https://accounts.binance.com/en/register?ref=122742137&utm_campaign=web_share_link) to make sure you can use it safely.

## Configuration

### Kucoin
Add this dependency to your  ̀pom.xml`:
```xml
<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-kucoin</artifactId>
    <version>5.0.12</version>
</dependency>
```
and update your `application.properties`:
```properties
cassandre.trading.bot.exchange.driver-class-name=kucoin
```

### Coinbase
Add this dependency to your  ̀pom.xml`:
```xml
<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-coinbasepro</artifactId>
    <version>5.0.12</version>
</dependency>
```
and update your `application.properties`:
```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.coinbasepro.CoinbaseProExchange
```

### Binance
Add this dependency to your  ̀pom.xml`:
```xml
<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-binance</artifactId>
    <version>5.0.12</version>
</dependency>
```
and update your `application.properties`:
```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.binance.BinanceExchange
```