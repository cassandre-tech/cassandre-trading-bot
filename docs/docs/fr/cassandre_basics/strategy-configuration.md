---
lang: fr-FR
title: Configuration de Cassandre
description: Configuration de Cassandre
---

# Configuration

Le fichier de configuration de Cassandre se trouve ici : `src/main/resources/application.properties`.

Voici un exemple :

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

## Configuration de l'exchange

La section `Exchange configuration` contient les paramètres de connexion à l'exchange. Le premier, `driver-class-name`,
correspond à la classe [XChange](https://github.com/knowm/XChange) que Cassandre doit utiliser pour se connecter.

Les autres paramètres sont les paramètres de connexion que votre exchange vous fournira. Vous pouvez voir des exemples
de configuration [ici](../guides/configuration/exchange-connection-configuration).

## Modes

Cassandre offre de deux modes :

* Le mode `sandbox`, supporté seulement par certains exchanges, permet d'utiliser un 'faux compte' sur l'exchange et
  donc de travailler de manière simulée. Kucoin & Coinbase supportent cette fonctionnalité.
* Le mode `dry` de Cassandre simule, à l'intérieur de Cassandre, un exchange. Ce mode permet de tester votre stratégie
  en local sur des données historiques et de voir si elle génère des bénéfices ou des pertes. Vous pourrez donc valider
  vos idées avec des tests unitaires avant de vous lancer en production.

## Taux de rafraichissement

Les paramètres `rates` permettent de dire à Cassandre à quelle fréquence il faut récupérer les données depuis
l'exchange.

## Configuration de la base de données.

Ces paramètres indiquent à Cassandre dans quelle base de données Cassandre doit sauvegarder ses informations (Cassandre
se charge de créer la structure de la base au démarrage de votre application).