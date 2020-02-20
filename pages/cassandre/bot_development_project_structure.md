---
title: Project structure
summary: This page describes the different packages of cassandre.
sidebar: cassandre_sidebar
permalink: bot_development_project_structure.html
---

## Batch ([tech.cassandre.trading.bot.batch](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/batch)).
_Tasks that can run without end user interaction, or can be scheduled._

## Configuration ([tech.cassandre.trading.bot.configuration](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/configuration)).
_Configuration._

## Domain objects ([tech.cassandre.trading.bot.domain](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/domain)).
_Objects from the business specific area that represent something we want to store.._

## Data transfer objects ([tech.cassandre.trading.bot.dto](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/dto)).
_Objects that carries data between processes._

  * User DTOs ([tech.cassandre.trading.bot.dto.user](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/dto/user)).
  * Market DTOs ([tech.cassandre.trading.bot.dto.market](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/dto/market)).
  * Trade DTOs ([tech.cassandre.trading.bot.dto.trade](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/dto/trade)).

## Repositories ([tech.cassandre.trading.bot.repository](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/repository)).
_Isolate the application/business layer from the persistence layer._

## Services ([tech.cassandre.trading.bot.service](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/service)).
_Offers high level services._

  * [Exchange service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/ExchangeService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/ExchangeServiceXChangeImplementation.java).
  * [User service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/UserService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/UserServiceXChangeImplementation.java).
  * [Market service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/MarketService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/MarketServiceXChangeImplementation.java).
  * [Trade service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/TradeService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/TradeServiceXChangeImplementation.java).

## Strategy ([tech.cassandre.trading.bot.strategy](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/strategy)).
_Strategy management._

## Util ([tech.cassandre.trading.bot.util](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/bot/src/main/java/tech/cassandre/trading/bot/util)).
_Utility classes._
