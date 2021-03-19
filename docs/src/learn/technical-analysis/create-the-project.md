# Create the project

## Introduction

We are going to use [ta4j](https://ta4j.github.io/ta4j-wiki/), an open-source Java library for technical analysis. It provides the basic components for the creation, evaluation, and execution of trading strategies.

## Create your project
Type this command :

```bash
mvn -B archetype:generate -DarchetypeGroupId=tech.cassandre.trading.bot \
-DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-basic-ta4j-archetype \
-DarchetypeVersion=4.1.0 \
-DgroupId=com.example \
-DartifactId=ta4j-strategy \
-Dversion=1.0-SNAPSHOT \
-Dpackage=com.example
```

[![Maven Central](https://img.shields.io/maven-central/v/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22)

The created project is a spring boot project with the following structure :

```
ta4j-strategy/
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           ├── Application.java
    │   │           ├── package-info.java
    │   │           └── SimpleTa4jStrategy.java
    │   └── resources
    │       ├── application.properties
    │       ├── user-main.tsv
    │       └── user-trade.tsv
    └── test
        ├── java
        │   └── com
        │       └── example
        │           └── SimpleTa4jStrategyTest.java
        └── resources
            ├── application.properties
            ├── tickers-btc-usdt.tsv
            ├── user-main.tsv
            └── user-trade.tsv
```

