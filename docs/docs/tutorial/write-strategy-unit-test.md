---
lang: en-US
title: Write your strategy unit tests
description: Cassandre tutorial - Write your strategy unit tests
---

# Write your strategy unit test

## What are we going to do ?

Our goal is to check that our strategy can make gains. We can't predict the future, so we will test it on historical
data with a simulated exchange provided by Cassandre (AKA [dry mode](../cassandre_basics/dry-mode-and-backtesting.md)).

There is three steps:

- Configure how much fake assets your strategy will play with.
- Download and import the historical data Cassandre will send to your strategy.
- Write the unit tests that check the gains we made when all historical tickers will have been treated by your strategy.

## Configure assets

To correctly simulate the behavior your strategy, you need tell Cassandre dry mode how many assets the strategy can use.

This is done by creating csv files starting with `user-` and ending with `csv` in the `src/test/resources` directory.

For example, if you open `src/test/resources/user-trade.csv`, you will find those data:

```
BTC,0.99962937
USDT,1000
ETH,10
```

With this file, when Cassandre will start in dry mode, your strategy will act as if it has 0.99962937 BTC, 1,000 USDT and
10 ETH. Of course, when your strategy will be tested, the assets will be updated at each buy/sell action.

## Download historical data

As I said earlier, we will test your bot behavior on historical data. To do this, we have to put the data we want to use
in a file starting with `candles-for-backtesting` and ending with `.csv` in `src/test/resources/`.

This is an example of the file content:

```
"TIMESTAMP","OPEN","CLOSE","HIGH","LOW","VOLUME","QUOTE_VOLUME","CURRENCY_PAIR"
"1508371200","10000","10000","10000","10000","10000","10000","BTC-USDT"
```

You can create those files with the tools/sources you want but this is how, with Linux & Kucoin, I easily generate those
data like this:

```bash
SYMBOL=BTC-USDT
START_DATE=`date --date="3 months ago" +"%s"`
END_DATE=`date +"%s"`
echo '"TIMESTAMP", "OPEN", "CLOSE", "HIGH", "LOW", "VOLUME", "QUOTE_VOLUME", "CURRENCY_PAIR"' > src/test/resources/candles-for-backtesting-btc-usdt.csv
curl -s "https://api.kucoin.com/api/v1/market/candles?type=15min&symbol=${SYMBOL}&startAt=${START_DATE}&endAt=${END_DATE}" \
| jq --arg SYMBOL "$SYMBOL" -r -c '.data[] | . + [$SYMBOL] | @csv' \
| tac $1 >> src/test/resources/candles-for-backtesting-btc-usdt.csv
```

::: tip
You can add as many backtesting files as you want, Cassandre, will load them all before the tests starts.
:::

## Write your test

The test code is quite easy to understand:

```java
@SpringBootTest
@ActiveProfiles("test")
@Import(TickerFluxMock.class)
@DisplayName("Simple strategy test")
public class ETHStrategyTest {

	@Autowired
	private TickerFluxMock tickerFluxMock;

	/** Dumb strategy. */
	@Autowired
	private ETHStrategy strategy;

	/**
	 * Check data reception.
	 */
	@Test
	@DisplayName("Check data reception")
	public void receivedData() {
		await().forever().until(() -> tickerFluxMock.isFluxDone());

		// =============================================================================================================
		System.out.println("");
		System.out.println("Gains by position");
		strategy.getPositions()
				.values()
				.forEach(positionDTO -> {
					if (positionDTO.getStatus().equals(PositionStatusDTO.CLOSED)) {
						System.out.println("Position " + positionDTO.getPositionId() + " closed with gain: " + positionDTO.getGain());
					} else {
						System.out.println("Position " + positionDTO.getPositionId() + " NOT closed with latest gain: " + positionDTO.getLatestCalculatedGain().get());
					}
				});

		// =============================================================================================================
		System.out.println("");
		System.out.println("Global gains");
		Map<CurrencyDTO, GainDTO> gains = strategy.getGains();
		gains.values().forEach(gainDTO -> System.out.println(gainDTO.getAmount()));
		assertFalse(gains.isEmpty(), "Failure, no gains");
		assertNotNull(gains.get(USDT), "Failure, USDT gains");
		assertTrue(gains.get(USDT).isSuperiorTo(GainDTO.ZERO), "Failure, USDT inferior to zero");
	}

}
```

This is what you have to notice:
- It's a classical spring boot test.
- Adding `@Import(TickerFluxMock.class)` makes Cassandre load backtesting data and send them to your strategies.
- We add `private TickerFluxMock tickerFluxMock;` because we need to know when all data will have been read.
- When doing this `await().forever().until(() -> tickerFluxMock.isFluxDone());`, we make the test wait until all backtesting data have been treated by Cassandre and received by your strategies.
- We then display all the gains by positions, and we make the test fail if there is no gain !

## Run test

Run the test with the simple command: `mvn test`.

Here is an example of result:
```sh
Gains by position
Position 1 closed with gain: Gains: -3.170296 USDT (-8.0 %)
Position 2 closed with gain: Gains: -3.170208 USDT (-8.0 %)
Position 3 closed with gain: Gains: -3.168264 USDT (-8.0 %)
Position 4 closed with gain: Gains: -3.165088 USDT (-8.0 %)
Position 5 closed with gain: Gains: -3.164112 USDT (-8.0 %)
Position 6 closed with gain: Gains: -3.162976 USDT (-8.0 %)
Position 7 closed with gain: Gains: -3.15744 USDT (-8.0 %)
Position 8 closed with gain: Gains: -3.1594 USDT (-8.0 %)
Position 9 closed with gain: Gains: -3.15688 USDT (-8.0 %)
Position 10 closed with gain: Gains: -3.150648 USDT (-8.0 %)
Position 11 closed with gain: Gains: -3.13744 USDT (-8.0 %)
Position 12 closed with gain: Gains: -3.11872 USDT (-8.0 %)
...
osition 120 closed with gain: Gains: 1.114808 USDT (4.0 %)
Position 121 closed with gain: Gains: 1.141692 USDT (4.0 %)
Position 122 closed with gain: Gains: 1.133536 USDT (4.0 %)
Position 123 closed with gain: Gains: 1.124956 USDT (4.0 %)
Position 124 closed with gain: Gains: 1.128588 USDT (4.0 %)
Position 125 closed with gain: Gains: 1.17594 USDT (4.0 %)
Position 126 closed with gain: Gains: 1.163032 USDT (4.0 %)
Position 127 closed with gain: Gains: 1.145548 USDT (4.0 %)
Position 128 closed with gain: Gains: 1.143424 USDT (4.0 %)
Position 129 closed with gain: Gains: 1.141428 USDT (4.0 %)
Position 130 closed with gain: Gains: 1.133732 USDT (4.0 %)
Position 131 closed with gain: Gains: 1.14464 USDT (4.0 %)
Position 132 closed with gain: Gains: 1.140584 USDT (4.0 %)
Position 133 closed with gain: Gains: 1.130728 USDT (4.0 %)
Position 134 closed with gain: Gains: 1.156604 USDT (4.0 %)
Position 135 closed with gain: Gains: 1.177412 USDT (4.0 %)
Position 136 closed with gain: Gains: 1.173072 USDT (4.0 %)
Position 137 NOT closed with latest gain: Gains: -0.6901 USDT (-2.2868640422821045 %)
Position 138 NOT closed with latest gain: Gains: -0.956 USDT (-3.1403369903564453 %)
Position 139 NOT closed with latest gain: Gains: -0.8586 USDT (-2.8294429779052734 %)
Position 140 NOT closed with latest gain: Gains: -0.7031 USDT (-2.3289411067962646 %)
Position 141 NOT closed with latest gain: Gains: -1.2125 USDT (-3.9496281147003174 %)
Position 142 NOT closed with latest gain: Gains: -1.1565 USDT (-3.7740960121154785 %)
Position 143 NOT closed with latest gain: Gains: -1.0674 USDT (-3.4934868812561035 %)
Position 144 NOT closed with latest gain: Gains: -0.5626 USDT (-1.8538539409637451 %)
Position 145 NOT closed with latest gain: Gains: -0.5489 USDT (-1.809527039527893 %)
...
Global gains
-191.903428 USDT
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 348.345 s <<< FAILURE! - in com.mycompany.bot.SimpleStrategyTest
[ERROR] receivedData  Time elapsed: 339.607 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Failure, USDT inferior to zero (eg loss!) ==> expected: <true> but was: <false>
	at com.mycompany.bot.SimpleStrategyTest.receivedData(SimpleStrategyTest.java:67)
```

As you can see, this strategy is not a winning one!