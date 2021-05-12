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

### Run the build
```bash
mvn package
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
