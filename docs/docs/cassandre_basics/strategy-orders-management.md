---
lang: en-US
title: Order management
description: Create buying/selling order in Cassandre
---

# Order management

From your strategy, you can create buying/selling order by giving the amount and the currency pair.

## Market orders

You can create market orders with the following methods:

* [createBuyMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29)
* [createSellMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29)

## Limit orders

You can create limit orders with the following methods:

* [createBuyLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29)
* [createSellLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29)