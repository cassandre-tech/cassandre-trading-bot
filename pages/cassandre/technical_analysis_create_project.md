---
title: Create project
sidebar: cassandre_sidebar
permalink: technical_analysis_create_project.html
---

{% include note.html content="[ta4j sample project sources are available here](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/trading-bot-strategies/technical_analysis/ta4j-strategy)." %}

## Introduction.

We are going to use [ta4j](https://ta4j.github.io/ta4j-wiki/), an open source Java library for technical analysis. It provides the basic components for creation, evaluation and execution of trading strategies.

## Create your project.

*[Prerequisite : java jdk 11 & maven must be installed.](how_to_install_development_tools)*

Just type this command :
```sh
mvn -B archetype:generate -DarchetypeGroupId=tech.cassandre.trading.bot -DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-archetype -DarchetypeVersion=1.0.0 -DgroupId=tech.cassandre.trading.strategy -DartifactId=ta4j-strategy -Dversion=1.0-SNAPSHOT -Dpackage=tech.cassandre.trading.strategy
```

The latest release is :

[![Maven Central](https://img.shields.io/maven-central/v/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22).

The created project is a spring boot project with the following structure : 
```
ta4j-strategy/
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── tech
    │   │       └── cassandre
    │   │           └── trading
    │   │               └── strategy
    │   │                   ├── Application.java
    │   │                   ├── package-info.java
    │   │                   └── SimpleStrategy.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── tech
                └── cassandre
                    └── trading
                        └── strategy
                            └── SimpleStrategyTest.java
```

## Add ta4j.

Edit `pom.xml` and add this dependency : 

```xml
<dependency>
    <groupId>org.ta4j</groupId>
    <artifactId>ta4j-core</artifactId>
    <version>0.13</version>
</dependency>
```

