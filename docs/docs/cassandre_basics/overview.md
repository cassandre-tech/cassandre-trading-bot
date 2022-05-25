---
lang: en-US
title: Cassandre overview
description: Cassandre trading bot framework overview
---

# Cassandre overview

A trading bot is a computer program that can automatically place orders to a market or exchange without the need for
human intervention. They are working for you 24/7 and never lose their focus.

Cassandre trading bot framework (available as
a [Spring boot starter](https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22))
allows you to quickly create and execute your trading strategies on several crypto exchanges.

Once the starter is added to your Spring Boot project, it will search for a class having
the [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html)
annotation and
extending [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html)
.

We also provide a [dry mode and a spring boot starter](dry-mode-and-backtesting.md) to simulate a virtual
exchange, so you can backtest your strategies on historical and/or real-time data.