---
lang: en-US
title: Exchange configuration
description: Cassandre tutorial - Exchange configuration
---

# Exchange configuration

For the moment, we only ran tests, so we never had to connect to the exchange we want to use. Now, let's suppose our
strategy is ready to deal with real assets and that we want to
use [Kucoin](https://www.kucoin.com/ucenter/signup?rcode=2HMJtt1).

The first step is to add the right XChange library to your project. It's a bit like adding a JDBC driver. Go
to the [XChange](https://github.com/knowm/XChange) website, find the corresponding directory. In our
case, it's [xchange-kucoin directory](https://github.com/knowm/XChange/tree/develop/xchange-kucoin).

Inside this directory, you have to find the name of the class extending `BaseExchange` and implementing `Exchange`. In
our case,
it's [org.knowm.xchange.kucoin.KucoinExchange](https://github.com/knowm/XChange/blob/develop/xchange-kucoin/src/main/java/org/knowm/xchange/kucoin/KucoinExchange.java)
. This name will be used in our `application.properties` file for the `driver-class-name` property.

Now, we can edit our application configuration located in `src/main/resources/application.properties` to change the
following properties with the parameters given by the exchange:

```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.kucoin.KucoinExchange
cassandre.trading.bot.exchange.username=kucoin.cassandre.test@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=61d0c8a041a5330001d0d59c
cassandre.trading.bot.exchange.secret=79edb229-a9c8-449d-a476-04689eaf376b
```

We also have to add the XChange library to our project `pom.xml` (the artifactId is Github directory name corresponding
to the exchange you chose):

```xml

<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-kucoin</artifactId>
    <version>5.0.13</version>
</dependency>
```

Last thing, as we are running in production, the two modes (dry & sandbox) must be set to false:

```properties
cassandre.trading.bot.exchange.modes.sandbox=false
cassandre.trading.bot.exchange.modes.dry=false
```
