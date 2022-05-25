---
lang: fr-FR
title: Un aperçu de Cassandre
description: Aperçu global le framework pour trading bot Cassandre
---

# Un aperçu de Cassandre

Un bot de trading est un programme informatique qui peut passer automatiquement des ordres sur un marché sans
intervention humaine. Il travaille pour vous 24h/24 et 7j/7 sans jamais perdre sa concentration.

Le framework Cassandre trading bot (Disponible sous forme
de [Spring boot starter](https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22))
vous permet de créer et d'exécuter rapidement vos propres stratégies sur différents exchange.

Une fois le Spring Boot Starter ajouté à votre projet, il cherchera votre stratégie qui devra avoir
l'annotation [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html)
et hériter
de [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html)
.

Nous fournissons également un mode [dry et un Spring Boot Starter de test](dry-mode-and-backtesting) pour simuler un échange virtuel afin que vous
puissiez tester vos stratégies sur des données historiques et savoir si elles fonctionnent !