---
lang: en-US
title: Build Cassandre from sources
description: How to build a Cassandre release (and its documentation) on your computer
---
# Build from sources

## Build Cassandre

### Get the sources from Github
```bash
git clone git@github.com:cassandre-tech/cassandre-trading-bot.git
```

### Move to the source directory
```bash
cd cassandre-trading-bot
```

### Build & install without test
```bash
mvn install -Dgpg.skip -DskipTests
```

### Build & install
```bash
mvn install -Dgpg.skip
```

## Build documentation

### Run local documentation
```bash
vuepress dev docs/src
```
Documentation website will be accessible at [http://0.0.0.0:8080/](http://0.0.0.0:8080/).

### Build static website
```bash
yarn --cwd docs build
```
