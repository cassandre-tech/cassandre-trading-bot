---
lang: en-US
title: Create your Cassandre project
description: Cassandre tutorial - Create your Cassandre project
---

# Create your Cassandre project
If you don't have an existing spring boot project, you can use our [maven archetype](https://search.maven.org/search?q=a:cassandre-trading-bot-spring-boot-starter-basic-archetype) to generate one:
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
The project will have the following structure and files:
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