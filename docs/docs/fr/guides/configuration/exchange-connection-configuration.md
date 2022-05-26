---
lang: fr-FR
title: Configurer la connexion à un exchange
description: Comment configurer la connexion de Cassandre à un exchange (kucoin, Binance, Coinbase...)
---

# Configurer la connexion à un exchange

## Configuration examples

### Kucoin

Ajoutez cette dépendance à votre `pom.xml` :

```xml

<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-kucoin</artifactId>
    <version>5.0.12</version>
</dependency>
```

et mettez à jour votre fichier `application.properties`:

```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.kucoin.KucoinExchange
```

### Coinbase

Ajoutez cette dépendance à votre `pom.xml` :

```xml

<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-coinbasepro</artifactId>
    <version>5.0.12</version>
</dependency>
```

et mettez à jour votre fichier `application.properties`:

```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.coinbasepro.CoinbaseProExchange
```

### Binance

Ajoutez cette dépendance à votre `pom.xml` :

```xml

<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-binance</artifactId>
    <version>5.0.12</version>
</dependency>
```

et mettez à jour votre fichier `application.properties`:

```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.binance.BinanceExchange
```