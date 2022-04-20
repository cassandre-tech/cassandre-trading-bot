---
home: true
title: Cassandre
heroImage: assets/images/logo/cassandre-trading-bot-without-text.png

actions:
- text: Commencer le tutorial
  link: /learn/quickstart
  type: primary
- text: Voir le code sur GitHub
  link: https://github.com/cassandre-tech/cassandre-trading-bot
  type: secondary

features:
- title: Commencez le trading avec un minimum de tracas
  details: Disponible en tant que Spring Boot Starter, Cassandre s'occupe de la connexion à l'exchange, des comptes, des ordres, des transactions et des positions afin que vous puissiez vous concentrer sur l'élaboration de votre stratégie.
- title: Créez votre propre stratégie en quelques minutes
  details: En fonction de vos idées, il vous suffit de coder lorsque vous souhaitez créer des positions (courtes ou longues), de définir les règles et nous nous occupons de tout (achat, vente, gestion des règles, ordres, transactions et tickers).
- title: Support de multiples exchanges grâce à XChange
  details: Cassandre utilise la bibliothèque XChange pour se connecter à différents exchanges. Nous testons chaque nouvelle version de Cassandre avec Kucoin, Coinbase et Binance pour nous assurer que cela fonctionne pour vous.
- title: Mode "dry"
  details: Nous disposons d'un mode "dry" qui simule un exchange virtuel répondant aux ordres de votre bot pendant les tests. Ainsi, vous pourrez facilement tester votre stratégie sans risquer vos actifs. 
- title: Backtesting
  details: Nous fournissons un spring boot starter qui permet de tester votre bot sur des données historiques. Grâce à lui, lors des tests, Cassandre importera vos données historiques et les enverra à votre stratégie.
- title: API GraphQL
  details: Nous fournissons un spring boot starter qui permet à votre bot d'exposer ses données grâce une API GraphQL (Stratégies, comptes, positions, ordres, transactions...).

footer: GPL-3.0 License | Copyright © Stéphane Traumat
---

### C'est très simple!

<CodeGroup>
  <CodeGroupItem title="Command line" active>

```bash
# Crée simplement votre projet grâce à notre archetype Maven
mvn archetype:generate \
-DarchetypeGroupId=tech.cassandre.trading.bot \
-DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-basic-archetype \
-DarchetypeVersion=CASSANDRE_LATEST_RELEASE \
-DgroupId=com.example \
-DartifactId=my-bot \
-Dversion=1.0-SNAPSHOT \
-Dpackage=com.example

# Lancez les tests unitaires
mvn -f my-bot/pom.xml test
```

  </CodeGroupItem>
</CodeGroup>