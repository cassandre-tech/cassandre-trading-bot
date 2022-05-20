---
title: Database configuration
lang: en-US
toc: false
---

# Database configuration

By default, Cassandre uses a non persistant hsqldb, if you want to run your strategies in production, you have to set a correct configuration.

For example, if you want to use PostgreSQL, you first have to add the JDBC driver to your `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.3.3</version>
</dependency>
```

then, change the configuration in `src/main/resources/application.properties`:
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://cassandre-postgresql/cassandre_trading_bot_database
spring.datasource.username=cassandre
spring.datasource.password=XjeyL9876
```

