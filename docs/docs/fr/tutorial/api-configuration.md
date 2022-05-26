---
lang: fr-FR
title: Configuration de l'API
description: Cassandre tutorial - Notre API permet de consulter les balances, strategies, ordres, trades et positions de votre bot
---

# API GraphQL

## Aperçu

L'API GraphQL Cassandre vous permet de requêter les données de votre bot (Actifs, stratégies, orders, positions...).

## Installation

Pour déployer l'API GraphQL dans votre bot, il suffit d'ajouter ce spring boot starter dans votre `pom.xml` :

```xml
<dependency>
    <groupId>tech.cassandre.trading.bot</groupId>
    <artifactId>cassandre-trading-bot-spring-boot-starter-api-graphql</artifactId>
    <version>CASSANDRE_LATEST_RELEASE</version>
</dependency>
```

## Accéder à votre API grâce à GraphiQL

Démarrez votre bot et ouvrez votre navigateur à l'adresse [http://localhost:8080/graphiql](http://localhost:8080/graphiql).

GraphiQL est un éditeur de requêtes prêt à l'emploi fourni avec le [framework DGS](https://netflix.github.io/dgs/) que nous utilisons.

Par exemple, vous pouvez lancer cette requête pour récupérer la liste de vos stratégies :

```
query {
    strategies{ strategyId name }
}
```

## Sécurisez votre API

Pour sécuriser votre API avec une clé, ajoutez la propriété `cassandre.trading.bot.api.graphql.key` dans votre
fichier `applications.properties`.

## Documentation de l'API

Vous pouvez consulter la documentation de l'API à cette [adresse](graphql-api-documentation).