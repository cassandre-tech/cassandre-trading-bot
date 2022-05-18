---
title: Cassandre configuration
lang: en-US
toc: false
---

# Configuration

The configuration file can be found in: `src/main/resources/application.properties`.

Here is an example:

```properties
#
# Exchange configuration.
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.kucoin.KucoinExchange
cassandre.trading.bot.exchange.username=kucoin.cassandre.test@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=6054ad25365ac6000689a998
cassandre.trading.bot.exchange.secret=af080d55-afe3-47c9-8ec1-4b479fbcc5e7
#
# Modes.
cassandre.trading.bot.exchange.modes.sandbox=true
cassandre.trading.bot.exchange.modes.dry=false
#
# Exchange API calls rates (In ms or standard ISO 8601 duration like 'PT5S').
cassandre.trading.bot.exchange.rates.account=2000
cassandre.trading.bot.exchange.rates.ticker=2000
cassandre.trading.bot.exchange.rates.trade=2000
#
# Database configuration.
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.url=jdbc:hsqldb:mem:cassandre
spring.datasource.username=sa
spring.datasource.password=
```

## Exchange configuration

TODO ajouter la partie ajouter xchange lib
TODO Ajouter un lien vers la doc des exchange

The `Exchange configuration` part contains the parameters used by Cassandre to connect the exchange you chose. The first
parameter `driver-class-name`, the XChange class Cassandre should use to connect.

The other parameters are the one given by you exchange when you will create an API access there.

## Modes

Cassandre has two modes:

* The `sandbox`, only supported by some exchanges, allows you to use a 'fake account' on the exchange to make your bot
  run with simulated data. Kucoin & Coinbase supports this.
* The `dry` makes Cassandre simulate an exchange. This mode allows you to test your strategy on historical data and see
  how many gains it generates. This mode is usually used with JUnit tests.

## Rates

`rates` parameters tells Cassandre at which interval it should get data from the exchange.

## Database configuration

Those parameters indicate the database parameters to use to connect to the database used by Cassandre to save its
information.