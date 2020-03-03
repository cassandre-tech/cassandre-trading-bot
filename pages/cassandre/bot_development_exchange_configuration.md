---
title: Exchange configuration
sidebar: cassandre_sidebar
summary: Class managing the exchange configuration and connection.
permalink: bot_development_exchange_configuration.html
---

## Exchange auto configuration.
[ExchangeAutoConfiguration](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/configuration/ExchangeAutoConfiguration.java) class configures the exchange connexion with the parameters specified in [application.properties](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/resources/application.properties) :
```properties
# Exchange configuration.
#
cassandre.trading.bot.exchange.name=kucoin
cassandre.trading.bot.exchange.sandbox=true
#
# Exchange credentials.
cassandre.trading.bot.exchange.username=cassandre.crypto.bot@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=5df8eea30092f40009cb3c6a
cassandre.trading.bot.exchange.secret=5f6e91e0-796b-4947-b75e-eaa5c06b6bed
#
# Exchange API calls rates.
cassandre.trading.bot.exchange.rates.account=100
cassandre.trading.bot.exchange.rates.ticker=101
cassandre.trading.bot.exchange.rates.order=102
```

This class does the following steps :
  * Validate parameters values.
  * Connect to the specified exchange.
  * Creates the [services beans](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service).
  * Creates the [flux](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch).
  
Those parameters are read and validated by [ExchangeParameters](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util/parameters/ExchangeParameters.java).
