---
lang: fr-FR
title: Construire Cassandre
description: Comment construire Cassandre (et sa documentation) sur votre ordinateur
---

# Construire Cassandre à partir des sources

## Construire Cassandre

### Récupérez les sources depuis Github

```bash
git clone git@github.com:cassandre-tech/cassandre-trading-bot.git
```

### Allez dans le répertoire Cassandre

```bash
cd cassandre-trading-bot
```

### Construire sans les tests

```bash
mvn install -Dgpg.skip -DskipTests
```

### Construire avec les tests

```bash
mvn install -Dgpg.skip
```

## Construire la documentation

### Lancer la documentation en local

```bash
vuepress dev docs/src
```

Le site web de documentation sera disponible à l'adresse [http://0.0.0.0:8080/](http://0.0.0.0:8080/).

### Construire le site web final

```bash
yarn --cwd docs build
```
