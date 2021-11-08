# GraphQL API

## Overview
Cassandre GraphQL API allows you to ONLY query your data (balances, strategies, orders, trades and positions).

## Installation
To deploy the GraphQLAPI, just add this spring boot starter to your pom:
```xml
<dependency>
    <groupId>tech.cassandre.trading.bot</groupId>
    <artifactId>cassandre-trading-bot-spring-boot-starter-api-graphql</artifactId>
    <version>CASSANDRE_LATEST_RELEASE</version>
</dependency>
```

## Access the API with GraphiQL
Start the application and open a browser to [http://localhost:8080/graphiql](http://localhost:8080/graphiql). GraphiQL is a query editor that comes out of the box with the DGS framework.

To start, you can try this query to display all your strategies:
```
query {
    strategies{ strategyId name }
}
```

## Secure your API
To protect the access to your API with a key, add this property: `cassandre.trading.bot.api.graphql.key` in your `applications.properties`.