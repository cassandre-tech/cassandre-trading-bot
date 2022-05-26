---
lang: fr-FR
title: Configuration de la base de données
description: Cassandre tutorial - Configuration de la base de données
---

# Configuration de la base de données

Par défaut, Cassandre utilise une base de données hsqldb non persistante. En production, vous devez sauvegarder les
données dans une base au cas où (plantage, redémarrage...).

Par exemple, si vous souhaitez utiliser PostgresSQL, vous devez tout d'abord ajouter le driver JDBC à votre `pom.xml` :

```xml

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.3.3</version>
</dependency>
```

Puis, vous devez changer la configuration de la source de données dans le
fichier `src/main/resources/application.properties`:

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://cassandre-postgresql/cassandre_trading_bot_database
spring.datasource.username=cassandre
spring.datasource.password=XjeyL9876
```

