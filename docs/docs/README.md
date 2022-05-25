---
home: true
title: Cassandre - Create your java crypto trading bot in minutes
heroImage: assets/images/logo/cassandre-trading-bot-without-text.png

actions:
- text: Get Started
  link: /cassandre_basics/overview
  type: primary
- text: View on GitHub
  link: https://github.com/cassandre-tech/cassandre-trading-bot
  type: secondary

features:
- title: Get into trading with minimum fuss
  details: Available as a Spring boot starter, Cassandre takes care of exchange connection, accounts, orders, trades, and positions, so you can focus on building your strategy.
- title: Create your strategy in minutes
  details: Just code when you want to create short/long positions, set the rules, and we take care of everything (buying, selling, rules management, orders, trades, and tickers).
- title: Several exchanges supported
  details: Cassandre uses XChange library to  connect to multiple exchanges. We test each Cassandre releases on Kucoin, Coinbase & Binance to be sure it works for you.
- title: Dry mode
  details: We provide a dry mode to simulate a virtual exchange replying to your orders, so you can easily test your strategy. This way, you can simulate your gains/loss over a period of time.
- title: Backtesting
  details: We provide a spring boot starter to backtest your bot on historical data. With this, during tests, Cassandre will import your data and push them to your strategy.
- title: GraphQL API
  details: We provide a spring boot starter, so you can query your trading bot data throw a simple GraphQL API.
footer: GPL-3.0 License | Copyright © Stéphane Traumat
---

### As Easy as 1, 2, 3

<CodeGroup>
  <CodeGroupItem title="Command line" active>

```bash
mvn archetype:generate \
-DarchetypeGroupId=tech.cassandre.trading.bot \
-DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-basic-archetype \
-DarchetypeVersion=CASSANDRE_LATEST_RELEASE \
-DgroupId=com.example \
-DartifactId=my-bot \
-Dversion=1.0-SNAPSHOT \
-Dpackage=com.example

mvn -f my-bot/pom.xml test
```

  </CodeGroupItem>
</CodeGroup>