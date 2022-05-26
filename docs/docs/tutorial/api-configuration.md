---
lang: en-US
title: API configuration
description: Cassandre tutorial - Our GraphQL API allows you to query your data (balances, strategies, orders, trades and positions)
---

# GraphQL API

## Overview

Cassandre GraphQL API allows you to query your data (balances, strategies, orders, trades and positions).

## Installation

To deploy the GraphQL API on your bot, just add this spring boot starter to your `pom.xml`:

```xml
<dependency>
    <groupId>tech.cassandre.trading.bot</groupId>
    <artifactId>cassandre-trading-bot-spring-boot-starter-api-graphql</artifactId>
    <version>CASSANDRE_LATEST_RELEASE</version>
</dependency>
```

## Access the API with GraphiQL

Start your bot and open a browser to [http://localhost:8080/graphiql](http://localhost:8080/graphiql). GraphiQL is a
query editor that comes out of the box with the [DGS framework](https://netflix.github.io/dgs/) we are using.

For example, you can enter this query to display all your strategies:

```
query {
    strategies{ strategyId name }
}
```

## Secure your API

To secure your API with a key, add this property: `cassandre.trading.bot.api.graphql.key` in
your `applications.properties`.

## API Documentation

You can view the API documentation at [this address](graphql-api-documentation).