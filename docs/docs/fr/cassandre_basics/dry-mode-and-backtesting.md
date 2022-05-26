---
lang: fr-FR
title: Dry mode & backtesting
description: Comment simuler un exchange virtuel et tester sa stratégie
---

# Dry mode & backtesting

## Dry mode

Cassandre propose un mode dry qui permet de simuler un exchange virtuel et ses réponses. Vous pouvez l'activer en
mettant le paramètre `cassandre.trading.bot.exchange.modes.dry` à `true` dans le
fichier `src/test/resources/application.properties`.

Cassandre va alors émuler un exchange virtuel qui répondra à vos ordres et mettra à jour votre compte virtuel (où se
trouvent vos assets virtuels). De cette façon, vous allez pouvoir tester votre strategy et voir les gains virtuels
réalisés.

La première étape est de configurer vos actifs. Cassandre va charger tous les fichiers commençant par  `user-` et
finissant par `.tsv` ou `.csv` dans `src/test/resources`.

Dans ces fichiers, pour chaque compte, vous allez configurer la balance de chaque crypto monnaie, par exemple, voici le
fichier `user-trade.csv` :

```
BTC,0.99962937
USDT,1000
ETH,10 
```

Lorsque vous démarrerez Cassandre, vous devriez voir :

```
22:53:38 - Adding account 'trade'
22:53:38 - - Adding balance 0.99962937 BTC
22:53:38 - - Adding balance 1000 USDT
22:53:38 - - Adding balance 10 ETH
```

Vous pouvez désormais créer des ordres et des positions, vos actifs sur vos comptes virtuels seront mis à jour.

## Backtesting

Pour faire simple, "backtester" une stratégie est le process qui consiste à tester votre stratégie sur une période de
temps précédente. Cassandre va vous permettre de simuler la réaction de vos stratégies à ces données.

Le premier pas consiste à
ajouter [cassandre-trading-bot-spring-boot-starter-test](https://search.maven.org/search?q=a:cassandre-trading-bot-spring-boot-starter-test)
à votre projet.

Editez votre fichier `pom.xml` et ajoutez cette dépendance :

```xml

<dependencies>
    ...
    <dependency>
        <groupId>tech.cassandre.trading.bot</groupId>
        <artifactId>cassandre-trading-bot-spring-boot-starter-test</artifactId>
        <version>CASSANDRE_LATEST_RELEASE</version>
        <scope>test</scope>
    </dependency>
    ...
</dependencies>
```

Maintenant, nous devons ajouter les données que nous voulons utiliser lors de notre test JUnit. Ceci peut se faire avec
la commande :

```bash
SYMBOL=BTC-USDT
START_DATE=`date --date="3 months ago" +"%s"`
END_DATE=`date +"%s"`
echo '"TIMESTAMP", "OPEN", "CLOSE", "HIGH", "LOW", "VOLUME", "QUOTE_VOLUME", "CURRENCY_PAIR"' > src/test/resources/candles-for-backtesting-btc-usdt.csv
curl -s "https://api.kucoin.com/api/v1/market/candles?type=15min&symbol=${SYMBOL}&startAt=${START_DATE}&endAt=${END_DATE}" \
| jq --arg SYMBOL "$SYMBOL" -r -c '.data[] | . + [$SYMBOL] | @csv' \
| tac $1 >> src/test/resources/candles-for-backtesting-btc-usdt.csv
```

Ceci va créer un fichier nommé `candles-for-backtesting-btc-usdt.csv` avec les données historiques qui sera importé par
votre test si vous avez l'annotation suivante :

```java
@Import(TickerFluxMock.class)
```

Désormais, durant vos tests, au lieu de recevoir des tickers depuis l'exchange, vous receverez des tickers importé
depuis les fichiers csv se trouvant dans `src/test/resources`.
