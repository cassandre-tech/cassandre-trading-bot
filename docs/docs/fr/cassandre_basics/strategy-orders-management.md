---
lang: fr-FR
title: Gestion des ordres d'achats et de ventes
description: Gestion des ordres d'achats et de ventes dans Cassandre
---

# Gestion des ordres

Depuis votre stratégie, vous pouvez passer des ordres d'achats et de ventes en indiquant la paire de devises et le
montant.

## Ordres au cours du marché

Pour passer un order au cours du marché, vous disposez de deux méthodes :

* [createBuyMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29)
* [createSellMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29)

## Ordres à cours limité

Pour passer un order à cours limité, vous disposez de deux méthodes :

* [createBuyLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29)
* [createSellLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29)