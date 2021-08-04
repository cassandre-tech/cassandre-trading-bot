# Dry mode & backtesting

## Dry mode
Cassandre provides a dry mode allowing you to simulate exchange interactions. You can enable it by setting the parameter `cassandre.trading.bot.exchange.modes.dry` to `true` in `src/main/resources/application.properties.`

Cassandre will emulate valid exchange replies to your orders and will increase/decrease your virtual account. This way, you can test your strategy, see the gains you will make, and validate you have the results you expect.

The first step is to configure your(s) virtual account(s) balances; in Dry mode, Cassandre will search and import all files starting with `user` and ending with `.tsv` or `.csv` in `src/main/resources` or `src/test/resources`. 

In those files, for each account, you set the balances of each cryptocurrency. For example, this is the content of `user-trade.csv` :

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

Now you can create orders and positions, and this will increase/decrease your virtual account. Of course, Cassandre checks that you have enough assets before accepting your orders.

You can use the dry mode to test your trading bot with real data, but you can also use it for backtesting.

## Backtesting
In simple words, backtesting a strategy is the process of testing a trading strategy on prior time periods. Cassandre trading bot allows you to simulate your bots' reaction to historical data during tests. 

The first step is to add [cassandre-trading-bot-spring-boot-starter-test](https://mvnrepository.com/artifact/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter-test) to your project dependency.

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

[![Maven Central](https://img.shields.io/maven-central/v/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22)

Now, we need to generate the data we want to use during the JUnit tests. We can use the [Kucoin API](https://docs.kucoin.com/#get-klines); to do so, run this on the command line :

```bash
startDate=`date --date="3 months ago" +"%s"`
endDate=`date +"%s"`
curl -s "https://api.kucoin.com/api/v1/market/candles?type=1day&symbol=BTC-USDT&startAt=${startDate}&endAt=${endDate}" \
| jq -r -c ".data[] | @tsv" \
| tac $1 > tickers-btc-usdt.tsv
```

It will create a file named `tickers-btc-usdt.tsv` that contains the historical rate of BTC-USDT from `startDate` (3 months ago) to `endDate` (now). Of course, you can choose your own dates and currency pair.

Place this file in the `src/test/resources` folder of your project and add this line to your JUnit test class: 

```java
@Import(TickerFluxMock.class)
```

Now, during the tests, instead of receiving tickers from the exchange, you will receive tickers imported from the `tsv/csv` files you put in `src/test/resources`.

You can see an example of dry mode and backtesting in the [Technical analysis chapter](./technical-analysis).

