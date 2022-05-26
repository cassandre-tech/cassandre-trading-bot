---
lang: en-US
title: Configure exchange connection
description: Learn how to configure Cassandre to connect to a specific exchange (kucoin, Binance, Coinbase...)
---

# Configure exchange connection

## How does it work?

There are two steps to configure an exchange connection in Cassandre.

Cassandre uses [XChange](https://github.com/knowm/XChange), a Java library providing a streamlined API for interacting
with 60+ Bitcoin and Altcoin exchanges. The first thing you have to do is to find the XChange library suited for the
Exchange you chose. The list is [here](https://search.maven.org/search?q=org.knowm.xchange).

For example, for a Coinbase connection, you have to add this to your `pom.xml`:

```xml

<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-coinbasepro</artifactId>
    <version>5.0.12</version>
</dependency>
```

The second step is to update those properties in your `application.properties`:

```properties
# Exchange configuration.
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.coinbasepro.CoinbaseProExchange
cassandre.trading.bot.exchange.username=kucoin.cassandre.test@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=6054ad25365ac6000689a998
cassandre.trading.bot.exchange.secret=af080d55-afe3-47c9-8ec1-4b479fbcc5e7
```

For `cassandre.trading.bot.exchange.driver-class-name`, you have to set the main class inside the XChange library.

The other parameters `username`, `passphrase`, `key` and`secret` are authentication parameters given by the Exchange
when you will create your API access in your account.

## Configuration examples

### Kucoin

Add this dependency to your `̀pom.xml`:

```xml

<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-kucoin</artifactId>
    <version>5.0.12</version>
</dependency>
```

and update your `application.properties`:

```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.kucoin.KucoinExchange
```

### Coinbase

Add this dependency to your `pom.xml`:

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

Add this dependency to your `pom.xml`:

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

On Binance, you should not ask for data too often, or you will get a `Way too much request weight used` error, use those
parameters in your `application.properties`:

```properties
cassandre.trading.bot.exchange.rates.account=PT30S
cassandre.trading.bot.exchange.rates.ticker=PT30S
cassandre.trading.bot.exchange.rates.trade=PT30S
```
