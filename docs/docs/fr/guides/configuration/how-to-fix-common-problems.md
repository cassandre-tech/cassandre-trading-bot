---
lang: fr-FR
title: Comment résoudre les problèmes les plus courants ?
description: Comment résoudre les problèmes les plus courants
---

# Comment résoudre les problèmes les plus courants ?

## Your strategies specify a trading account that doesn't exist

La première chose à faire est de vérifier votre configuration. Si vous souhaitez vous connecter à un exchange avec vos
identifiants de production, les paramètres de mode doivent être à `false`.

```properties
cassandre.trading.bot.exchange.modes.sandbox=false
cassandre.trading.bot.exchange.modes.dry=false
```

Deuxième étape, habituellement, sur un exchange, vous avez un identifiant/mot de passe et plusieurs "accounts". Par
exemple, un pour le trading, un pour vos économies, un pour le courant... Pour savoir ce qu'il peut vendre ou acheter,
Cassandre doit savoir quel compte vous souhaitez utiliser.

Pour se faire, vous devez implémenter dans votre stratégie la
méthode [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29)
. Cette méthode sera appelée par Cassandre au démarrage et vous passera en paramètre la liste des comptes que vous avez
sur l'exchange.

Pour vous aider à savoir lequel est le bon, lors du démarrage de Cassandre, vous devriez voir s'afficher dans les logs
la liste des comptes découverts et leur solde :

```
2021-11-18 Available accounts on the exchange:
2021-11-18 - Account id / name: trade / trade.
2021-11-18  - 4 TUSD.
2021-11-18  - 5.00007534 UNI.
2021-11-18  - 11.26224436 USDT.
2021-11-18  - 0.01 BTC.
2021-11-18 - Account id / name: main / main.
2021-11-18  - 1 BTC.
```

## Way too much request weight used; IP banned until

Sur Binance, vous devez faire très attention à ne pas demander les données trop souvent où vous aurez
l'erreur `Way too much request weight used`. Nos tests montrent que cela marche bien avec les paramètres suivants :

```properties
cassandre.trading.bot.exchange.rates.account=PT30S
cassandre.trading.bot.exchange.rates.ticker=PT30S
cassandre.trading.bot.exchange.rates.trade=PT30S
```

## Requested bean is currently in creation: Is there an unresolvable circular reference?

Lorsque vous avez cette erreur au démmarrage :

```
̀Unknown configuration error: Error creating bean with name 'tech.cassandre.trading.bot.configuration.ExchangeAutoConfiguration': Requested bean is currently in creation: Is there an unresolvable circular reference?
```

Depuis Spring boot 2.6.0, les références circulaires sont interdites par défaut, nous avons corrigé ce problème avec la
version 5.0.7.

## Kucoin - Your strategies specifies a trading account that doesn't exist

Vous avez des actifs sur votre compte, `getTradeAccount(Set<AccountDTO> accounts)` est correctement implémentée mais
vous obtenez l'erreur `Your strategies specifies a trading account that doesn't exist`.

Vous pouvez essayer cette
solution : [https://github.com/cassandre-tech/cassandre-trading-bot/issues/786#issuecomment-999503117](https://github.com/cassandre-tech/cassandre-trading-bot/issues/786#issuecomment-999503117)
.