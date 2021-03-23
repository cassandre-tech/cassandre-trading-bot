# Create your strategy

Your strategy is in `src/main/java/com/example/SimpleTa4jStrategy.java`

## Choose the requested currency pair
This is done by implementing [getRequestedCurrencyPair()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#getRequestedCurrencyPair%28%29) this way : 

```java
@Override
public CurrencyPairDTO getRequestedCurrencyPair() {
	return new CurrencyPairDTO(BTC, USDT);
}
```

## Choose your trading account
On the exchange, you usually have several accounts, and Cassandre needs to know which one of your accounts is the trading one. To do so, you have to implement the [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29) method, which gives you as a parameter the list of accounts you own, and from that list, you have to return the one you use for trading.

```java
@Override
public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
    return accounts.stream()
                    .filter(a -> "trade".equals(a.getName()))
                    .findFirst();
}
```

## Choose the number of bars
This is done by implementing [getMaximumBarCount()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#getMaximumBarCount%28%29) this way : 

```java
@Override
public int getMaximumBarCount() {
	return 10;
}
```

## Choose the delay between two bars
This is done by implementing [getDelayBetweenTwoBars()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#getDelayBetweenTwoBars%28%29) this way : 

```java
@Override
public Duration getDelayBetweenTwoBars() {
    return Duration.ofDays(1);
}
```

::: tip
This method allows you, for example, to receive tickers every second but only add one to the bar every day.
:::

## Create your strategy
Now it's time to implement your strategy, and we chose [Simple Moving Average (SMA)](https://www.investopedia.com/terms/s/sma.asp) :

```java
@Override
public Strategy getStrategy() {
    ClosePriceIndicator closePrice = new ClosePriceIndicator(getSeries());
    SMAIndicator sma = new SMAIndicator(closePrice, getMaximumBarCount());
    return new BaseStrategy(new UnderIndicatorRule(sma, closePrice), new OverIndicatorRule(sma, closePrice));
}
```
