---
lang: fr-FR
title: Configuration de l'exchange
description: Cassandre tutorial - Configuration de l'exchange
---

# Configuration de la connexion à l'exchange

Pour le moment, nous avons seulement lancé des tests en local sans jamais s'être connecté à un vrai exchange.
Maintenant, supposons que notre stratégie est prête et que nous voulons nous lancer dans le grand bain en utilisant, par
exemple, l'exchange [Kucoin](https://www.kucoin.com/ucenter/signup?rcode=2HMJtt1).

La première étape est d'ajouter la librairie XChange correspondante à l'exchange que vous souhaitez utiliser. C'est un
peu comme ajouter un driver JDBC. Allez sur [XChange](https://github.com/knowm/XChange), trouvez le répertoire
correspondant. Dans notre cas, il s'agit
de [xchange-kucoin directory](https://github.com/knowm/XChange/tree/develop/xchange-kucoin)
.

Dans ce répertoire, nous devons ensuite trouver la classe qui hérite de `BaseExchange` et qui implémente `Exchange`.
Dans notre cas, il s'agit
de [org.knowm.xchange.kucoin.KucoinExchange](https://github.com/knowm/XChange/blob/develop/xchange-kucoin/src/main/java/org/knowm/xchange/kucoin/KucoinExchange.java)
. Le nom de cette classe sera utilisée comme `driver-class-name` dans le fichier `application.properties`.

Nous pouvons donc désormais éditer le fichier de configuration `src/main/resources/application.properties` :

```properties
cassandre.trading.bot.exchange.driver-class-name=org.knowm.xchange.kucoin.KucoinExchange
cassandre.trading.bot.exchange.username=kucoin.cassandre.test@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=61d0c8a041a5330001d0d59c
cassandre.trading.bot.exchange.secret=79edb229-a9c8-449d-a476-04689eaf376b
```

Nous devons aussi ajouter la librairie XChange dans votre fichier `pom.xml` (La valeur `artifactId` est le nom du
dossier Github).

```xml

<dependency>
    <groupId>org.knowm.xchange</groupId>
    <artifactId>xchange-kucoin</artifactId>
    <version>5.0.13</version>
</dependency>
```

Dernier point, comme nous allons lancer le bot en production, les deux modes (dry & sandbox) doivent tous les deux être
à `false`.

```properties
cassandre.trading.bot.exchange.modes.sandbox=false
cassandre.trading.bot.exchange.modes.dry=false
```
