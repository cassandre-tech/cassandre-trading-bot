# Backtest your strategy

In simple words, backtesting a trading strategy is the process of testing a trading strategy on prior periods. Cassandre trading bot allows you to simulate your bots' reaction to historical data during tests. 

The first step is to add [cassandre-trading-bot-spring-boot-starter-test](https://mvnrepository.com/artifact/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter-test) to your project dependency.

Edit your `pom.xml` file and add : 

```xml
	<dependencies>
		...
		<dependency>
			<groupId>tech.cassandre.trading.bot</groupId>
			<artifactId>cassandre-trading-bot-spring-boot-starter-test</artifactId>
			<version>4.1.0</version>
			<scope>test</scope>
		</dependency>
		...
	</dependencies>
```

[![Maven Central](https://img.shields.io/maven-central/v/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22)

The second step is to set the Cassandre parameter`cassandre.trading.bot.exchange.modes.dry` to `true`: this will make Cassandre simulate the exchange (buying/selling orders, trades) and increase/decrease your account.

Now, we need to generate the data you want to use during your JUnit tests. To do so, you can run this on the command line (on Linux) :

```bash
startDate=`date --date="3 months ago" +"%s"`
endDate=`date +"%s"`
curl -s "https://api.kucoin.com/api/v1/market/candles?type=1day&symbol=BTC-USDT&startAt=${startDate}&endAt=${endDate}" \
| jq -r -c ".data[] | @tsv" \
| tac $1 > tickers-btc-usdt.tsv
```

It will create a file named `tickers-btc-usdt.tsv` that contains the historical rate of `btc-usdt` from `startDate` (3 months ago) to `endDate` (now). Of course, you can change dates and currency pair.

Now place this file in the `src/test/resources` folder of our project and add this line to your JUnit test class: 

```java
@Import(TickerFluxMock.class)
```

Now, instead of receiving tickers from the exchange, you will receive tickers imported from the `tsv/csv` files you put in `src/test/resources`.

Your test is in `src/test/java/com/SimpleTa4jStrategyTest.java`.

Now we write the tests : 

```java
@Test
@DisplayName("Check gains")
public void gainTest() {
        System.out.println("Cumulated gains:");
        gains.forEach((currency, gain) -> System.out.println(currency + " : " + gain.getAmount()));

        System.out.println("Position still opened :");
        strategy.getPositions()
                .values()
                .stream()
                .filter(p -> p.getStatus().equals(OPENED))
                .forEach(p -> System.out.println(" - " + p.getDescription()));

        assertTrue(gains.get(strategy.getRequestedCurrencyPair().getQuoteCurrency()).getPercentage() > 0);
}
```

The first thing we do with the `await()` method is to wait until all data from `btc-usdt.csv` are imported. Then, we calculate every closed position's gain, and we check that the profits are superior to zero. 

The last thing we do is display the list of open positions to see if there are things to improve.

