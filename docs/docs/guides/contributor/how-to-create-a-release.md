---
lang: en-US
title: Create a release
description: How to create an official Cassandre release
---
# Create a release

## Things to check before release
* Fix Intellij warnings.
* Fix [Codacy](https://app.codacy.com/gh/cassandre-tech/cassandre-trading-bot/issues) warnings.
* Update [security.md](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/SECURITY.md).

## Create the release with Maven
You must be using `ssh` and not `https`. To switch to `ssh`, type : 
```bash
git remote set-url origin git@github.com:cassandre-tech/cassandre-trading-bot.git
```

Check that you are on the `development` branch and that everything is committed:
```bash
git checkout development
git status
```

Start the release with:
```bash
mvn gitflow:release-start
```

After choosing the release number, finish the release, push branches and tags, with this command:
```bash
mvn gitflow:release-finish
```

## Update
* Close the corresponding [milestone in Github](https://github.com/cassandre-tech/cassandre-trading-bot/milestones?direction=asc&sort=due_date&state=open).
* Write and send a [substack post](https://cassandre.substack.com/publish?utm_source=menu).
* Update cassandre release number on production trading bots.

## Releases text
* English: "We've just released Cassandre 5.0.7, a Spring boot starter to create and run your java crypto trading bot in minutes. Details here: URL #trading #tradingbot #crypto #bitcoin #java"
* French: "Sortie de Cassandre 5.0.7, notre Spring Boot Starter qui permet de créer son propre bot de trading pour les cryptos en quelques minutes. Plus de détails ici : URL #trading #tradingbot #crypto #bitcoin #java"

## Tell the world
* Retrieve the news URL from [GitHub](https://github.com/cassandre-tech/cassandre-trading-bot/releases).
* Publish on Cassandre Twitter (should be done by GitHub CI).
* Publish on Cassandre Discord (should be done by GitHub CI).
* Publish on my personal accounts (Facebook, Twitter & LinkedIn).
* Publish on [linuxfr](https://linuxfr.org/).
* Publish on [bitcointalk](https://bitcointalk.org/index.php?board=8.0).
* Publish on [IndieHackers](https://www.indiehackers.com/new-post).
* Publish on Reddit.
  * Programming: [Java](https://www.reddit.com/r/java/), [Kotlin](https://www.reddit.com/r/Kotlin/).
  * Exchanges: [Kucoin](https://www.reddit.com/r/kucoin/), [Coinbase](https://www.reddit.com/r/CoinBase/), [Binance](https://www.reddit.com/r/binance/).
  * Trading: [Cryptotrading](https://www.reddit.com/r/cryptotrading/).
  * Crypto currencies: TODO.