---
lang: en-US
title: Dry mode & backtesting
description: Learn how to simulate a virtual exchange and import historical data to test your strategy gains
---

# Dry mode & backtesting

## Dry mode

Cassandre provides a dry mode allowing you to simulate a virtual exchange and its replies. You can enable it by setting
the parameter `cassandre.trading.bot.exchange.modes.dry` to `true` in `src/test/resources/application.properties`.

Cassandre will emulate valid exchange replies to your orders and will increase/decrease your virtual account. This way,
you can test your strategy, sees the gains you will make, and validate you have the results you expect.

The first step is to configure your(s) virtual account(s) balances; in Dry mode, Cassandre will search and import all
files starting with `user-` and ending with `.tsv` or `.csv` in `src/test/resources`.

In those files, for each account, you set the balances of each cryptocurrency. For example, this is the content
of `user-trade.csv` :

```
BTC,0.99962937
USDT,1000
ETH,10 
```

When you start Cassandre, you will see this:

```
22:53:38 - Adding account 'trade'
22:53:38 - - Adding balance 0.99962937 BTC
22:53:38 - - Adding balance 1000 USDT
22:53:38 - - Adding balance 10 ETH
```

Now you can create orders and positions, and this will increase/decrease your virtual account. Of course, Cassandre
checks that you have enough assets before accepting your orders.

## Backtesting

In simple words, backtesting a strategy is the process of testing a trading strategy on prior time periods. Cassandre
trading bot allows you to simulate your bots' reaction to historical data during tests.

The first step is to
add [cassandre-trading-bot-spring-boot-starter-test](https://search.maven.org/search?q=a:cassandre-trading-bot-spring-boot-starter-test)
to your project dependency.

Edit your `pom.xml` file and add this dependency :

```xml

<dependencies>
    ...
    <dependency>
        <groupId>tech.cassandre.trading.bot</groupId>
        <artifactId>cassandre-trading-bot-spring-boot-starter-test</artifactId>
        <version>CASSANDRE_LATEST_RELEASE</version>
        <scope>test</scope>
    </dependency>
    ...
</dependencies>
```

Now, we need to generate the data we want to use during the JUnit tests. We can use
the [Kucoin API](https://docs.kucoin.com/#get-klines); to do so, run this on the command line :

```bash
SYMBOL=BTC-USDT
START_DATE=`date --date="3 months ago" +"%s"`
END_DATE=`date +"%s"`
echo '"TIMESTAMP", "OPEN", "CLOSE", "HIGH", "LOW", "VOLUME", "QUOTE_VOLUME", "CURRENCY_PAIR"' > src/test/resources/candles-for-backtesting-btc-usdt.csv
curl -s "https://api.kucoin.com/api/v1/market/candles?type=15min&symbol=${SYMBOL}&startAt=${START_DATE}&endAt=${END_DATE}" \
| jq --arg SYMBOL "$SYMBOL" -r -c '.data[] | . + [$SYMBOL] | @csv' \
| tac $1 >> src/test/resources/candles-for-backtesting-btc-usdt.csv
```

It will create a file named `candles-for-backtesting-btc-usdt.csv` with your historical data, and this file will imported
if your unit test has this annotation:

```java
@Import(TickerFluxMock.class)
```

Now, during the tests, instead of receiving tickers from the exchange, you will receive tickers imported from
the `tsv/csv` files you put in `src/test/resources`.
