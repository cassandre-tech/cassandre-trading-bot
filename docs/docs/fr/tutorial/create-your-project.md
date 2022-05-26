---
lang: fr-FR
title: Créez votre project Cassandre
description: Cassandre tutorial - Créez votre project Cassandre
---

# Créez votre project Cassandre
Si vous n'avez pas de projet spring boot existant, vous pouvez utiliser notre [archetype maven](https://search.maven.org/search?q=a:cassandre-trading-bot-spring-boot-starter-basic-archetype) pour en générer un :
```bash
mvn archetype:generate -B \
-DarchetypeGroupId=tech.cassandre.trading.bot \
-DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-basic-archetype \
-DarchetypeVersion=CASSANDRE_LATEST_RELEASE \
-DgroupId=com.mycompany.bot \
-DartifactId=my-trading-bot \
-Dversion=1.0-SNAPSHOT \
-Dpackage=com.mycompany.bot
```

Voici la structure du projet qui va être créé pour vous :
```
my-trading-bot/
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── mycompany
    │   │           └── bot
    │   │               ├── Application.java
    │   │               ├── package-info.java
    │   │               └── SimpleStrategy.java
    │   └── resources
    │       └── application.properties
    └── test
        ├── java
        │   └── com
        │       └── mycompany
        │           └── bot
        │               └── SimpleStrategyTest.java
        └── resources
            ├── application.properties
            ├── candles-for-backtesting-BTC-USDT.csv
            ├── user-main.tsv
            ├── user-savings.csv
            └── user-trade.csv

```