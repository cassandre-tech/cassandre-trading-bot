---
lang: en-US
title: Database configuration
description: Cassandre tutorial - Database configuration
---

# Database configuration

By default, Cassandre uses a non persistant hsqldb database. If you want to run your strategies in production, you have
to save your data between two restarts.

For example, if you want to use PostgresSQL, you have to add the JDBC driver to your `pom.xml` this way:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.3.3</version>
</dependency>
```

And then, change the datasource configuration in `src/main/resources/application.properties`:

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://127.0.0.1/cassandre_trading_bot_database
spring.datasource.username=cassandre
spring.datasource.password=XjeyL9876
```

