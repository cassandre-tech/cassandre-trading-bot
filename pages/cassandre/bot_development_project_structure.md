---
title: Project structure
summary: This page describes the project organization.
sidebar: cassandre_sidebar
permalink: bot_development_project_structure.html
---

Cassandre trading bot is provided as a spring boot starter. This is why you can find two projects in the sources : [trading-bot-spring-boot-autoconfigure](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure) & [trading-bot-spring-boot-starter](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-starter). These starters enable developers to avoid complex configuration and quickly jumpstart their development. 

## Batch ([tech.cassandre.trading.bot.batch](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch)).
_Tasks that can run without end user interaction, or can be scheduled. You will find the different flux pushed to the strategy :_
  * [AccountFlux](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch/AccountFlux.java) : calls the exchange to retrieve accounts & balances.
  * [TickerFlux](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch/TickerFlux.java) : calls the exchange to retrieve the tickers requested by the user strategy
  * [OrderFlux](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/batch/OrderFlux.java) : calls the exchange to retrieve orders.

## Configuration ([tech.cassandre.trading.bot.configuration](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/configuration)).
_Auto Configuration Classes that configures our beans :_
  * [ExchangeAutoConfiguration](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/configuration/ExchangeAutoConfiguration.java) : ExchangeConfiguration class configures the exchange connection.
  * [StrategyAutoConfiguration](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/configuration/StrategyAutoConfiguration.java) : StrategyAutoConfiguration class configures the strategy.
  * [ScheduleAutoConfiguration](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/configuration/ScheduleAutoConfiguration.java) : ScheduleAutoConfiguration activates flux scheduler.
 
## Domain objects ([tech.cassandre.trading.bot.domain](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/domain)).
_Objects from the business specific area that represent something we want to store._

## Data transfer objects ([tech.cassandre.trading.bot.dto](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/dto)).
_Objects that carries data between processes :_
  * User DTOs ([tech.cassandre.trading.bot.dto.user](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/dto/user)).
  * Market DTOs ([tech.cassandre.trading.bot.dto.market](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/dto/market)).
  * Trade DTOs ([tech.cassandre.trading.bot.dto.trade](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/dto/trade)).

## Repositories ([tech.cassandre.trading.bot.repository](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/repository)).
_Isolate the application/business layer from the persistence layer._

## Services ([tech.cassandre.trading.bot.service](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service)).
_Offers high level services :_
  * [Exchange service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/ExchangeService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/ExchangeServiceXChangeImplementation.java).
  * [User service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/UserService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/UserServiceXChangeImplementation.java).
  * [Market service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/MarketService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/MarketServiceXChangeImplementation.java).
  * [Trade service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/TradeService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/TradeServiceXChangeImplementation.java).

## Strategy ([tech.cassandre.trading.bot.strategy](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy)).
_Strategy management :_
  * [Strategy annotation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/Strategy.java) : this annotation allows cassandre trading bot to know which class is a strategy.
  * [CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java) : cassandre bot will run the first CassandreStrategy implementation found that also have the @strategy annotation.

## Util ([tech.cassandre.trading.bot.util](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util)).
_Utility classes : _
  * [base](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util/base) : base classes.
  * [dto](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util/dto) : common dto.
  * [exception](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util/exception) : exception management.
  * [mapper](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util/mapper) : mappers.
  * [parameters](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/util/parameters) : application.properties parameters management.
