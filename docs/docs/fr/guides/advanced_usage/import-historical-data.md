---
lang: fr-FR
title: Importer des données historiques
description: Comment importer des données historiques au démarrage de votre bot and comment les utiliser
---

# Importer des données historiques

## Présentation

Cette fonctionnalité vous permet d'importer des données historiques au démarrage de votre bot et de les utiliser pour
initialiser votre stratégie.

## Données & format

Au démarrage, Cassandre va chercher des fichiers commençant par `candles-to-import-` et finissant par `csv`.

Voici comment vos données doivent être formatées :

```
"TIMESTAMP", "OPEN", "CLOSE", "HIGH", "LOW", "VOLUME", "QUOTE_VOLUME", "CURRENCY_PAIR"
"1640044800","46898.1","48891.4","49322","46655.6","7389.39809406","357318509.007992951","BTC-USDT"
```

## Quand pouvez-vous initialiser vos données ?

Dans votre stratégie, vous pouvez implémenter la
méthode [initialize()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#initialize())
. Cette méthode est exécutée par Cassandre avant que n'importe quelle autre donnée ne soit poussée à vos stratégies.

## Récupération des données importées

Dans votre stratégie, vous pouvez récupérer les données importées à l'aide de ses deux méthodes :

* [getImportedTickers()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#getImportedTickers())
  .
* [getImportedTickers(CurrencyPairDTO currencyPairDTO)](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#getImportedTickers(tech.cassandre.trading.bot.dto.util.CurrencyPairDTO))
  .