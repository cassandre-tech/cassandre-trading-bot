---
lang: fr-FR
title: Créer une version
description: Comment créer une version officielle de Cassandre
---

# Créer une version

## Choses à faire avant chaque nouvelle version
* Corriger les avertissements d'Intellij.
* Corriger les avertissements de [Codacy](https://app.codacy.com/gh/cassandre-tech/cassandre-trading-bot/issues).
* Mettre à jour le fichier [security.md](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/SECURITY.md).

## Créez une release avec Maven
Vous devez utiliser `ssh` et pas `https`. Pour passer à `ssh`, tapez : 
```bash
git remote set-url origin git@github.com:cassandre-tech/cassandre-trading-bot.git
```

Vérifier que vous êtes bien sur la branche `development` et que tout a bien été commité :
```bash
git checkout development
git status
```

Démarrer la release avec :
```bash
mvn gitflow:release-start
```

Après avoir choisi le numéro de release, finissez le processus avec la commande :
```bash
mvn gitflow:release-finish
```

## À faire après la release
* Fermez la [milestone sur Github](https://github.com/cassandre-tech/cassandre-trading-bot/milestones?direction=asc&sort=due_date&state=open).
* Écrivez et postez un article sur [substack](https://cassandre.substack.com/publish?utm_source=menu).
* Mettez à jour les bots de trading qui servent à la pré production.

## Textes à utiliser pour annoncer la sortie
* English: "We've just released Cassandre 5.0.7, a Spring boot starter to create and run your java crypto trading bot in minutes. Details here: URL #trading #tradingbot #crypto #bitcoin #java"
* French: "Sortie de Cassandre 5.0.7, notre Spring Boot Starter qui permet de créer son propre bot de trading pour les cryptos en quelques minutes. Plus de détails ici : URL #trading #tradingbot #crypto #bitcoin #java"

## Où publier
* Récupérez le lien de la release sur [GitHub](https://github.com/cassandre-tech/cassandre-trading-bot/releases).
* Publication sur le twitter de Cassandre (Devrait être fait par GitHub CI).
* Publication sur le Discord de Cassandre (Devrait être fait par GitHub CI).
* Publication sur mes comptes personnels (Facebook, Twitter & LinkedIn).
* Publication sur [linuxfr](https://linuxfr.org/).
* Publication sur [bitcointalk](https://bitcointalk.org/index.php?board=8.0).
* Publication sur [IndieHackers](https://www.indiehackers.com/new-post).
* Publication sur Reddit.
  * Programming: [Java](https://www.reddit.com/r/java/), [Kotlin](https://www.reddit.com/r/Kotlin/).
  * Exchanges: [Kucoin](https://www.reddit.com/r/kucoin/), [Coinbase](https://www.reddit.com/r/CoinBase/), [Binance](https://www.reddit.com/r/binance/).
  * Trading: [Cryptotrading](https://www.reddit.com/r/cryptotrading/).
  * Crypto currencies: TODO.