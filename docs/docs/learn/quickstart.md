---
lang: en-US
title: Quickstart your trading bot
description: How to create your own crypto trading bot in few simple steps
---
# Quickstart

::: tip
If you are new to trading, you can read our tutorial "[Trading basics](../ressources/trading-basics.md)".
:::

## Create your project
If you don't have an existing spring boot project, you can use our [maven archetype](https://search.maven.org/search?q=a:cassandre-trading-bot-spring-boot-starter-basic-archetype) to generate one:
```bash
mvn archetype:generate \
-DarchetypeGroupId=tech.cassandre.trading.bot \
-DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-basic-archetype \
-DarchetypeVersion=CASSANDRE_LATEST_RELEASE \
-DgroupId=com.mycompany.app \
-DartifactId=my-app \
-Dversion=1.0-SNAPSHOT \
-Dpackage=com.mycompany.app
```

The created project will have the following structure and files:

```
my-app/
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── mycompany
    │   │           └── app
    │   │               ├── Application.java
    │   │               ├── package-info.java
    │   │               └── SimpleStrategy.java
    │   └── resources
    │       ├── application.properties
    │       ├── user-main.tsv
    │       └── user-trade.tsv
    └── test
        ├── java
        │   └── com
        │       └── mycompany
        │           └── app
        │               └── SimpleStrategyTest.java
        └── resources
            ├── application.properties
            ├── tickers-btc-usdt.tsv
            ├── user-main.tsv
            └── user-trade.tsv
```

## Review configuration
Your bot configuration is located in `src/main/resources/application.properties`: 

```properties
#
# Exchange configuration.
cassandre.trading.bot.exchange.driver-class-name=kucoin
cassandre.trading.bot.exchange.username=kucoin.cassandre.test@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=6054ad25365ac6000689a998
cassandre.trading.bot.exchange.secret=af080d55-afe3-47c9-8ec1-4b479fbcc5e7
#
# Modes.
cassandre.trading.bot.exchange.modes.sandbox=true
cassandre.trading.bot.exchange.modes.dry=false
#
# Exchange API calls rates (In ms or standard ISO 8601 duration like 'PT5S').
cassandre.trading.bot.exchange.rates.account=2000
cassandre.trading.bot.exchange.rates.ticker=2000
cassandre.trading.bot.exchange.rates.trade=2000
#
# Database configuration.
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.url=jdbc:hsqldb:mem:cassandre
spring.datasource.username=sa
spring.datasource.password=
```

::: tip
Please, create and configure your own Kucoin account. You can learn how to do it [here](../ressources/how-tos/how-to-create-a-kucoin-account.md).
:::

## Explore sources
The `src/main/java/com/mycompany/app/Application.java` file is a classical spring boot Application.

```java
package com.mycompany.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application start.
 */
@SpringBootApplication
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```

The `src/main/java/com/mycompany/app/SimpleStrategy.java` is the strategy executed by the bot:

```java
package com.mycompany.app;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple strategy.
 */
@CassandreStrategy(strategyName = "Simple strategy")
public final class SimpleStrategy extends BasicCassandreStrategy {

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        // We only ask BTC/USDT tickers (Base currency : BTC / Quote currency : USDT).
        return Set.of(new CurrencyPairDTO(BTC, USDT));
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // From all the accounts we have on the exchange, we must return the one we use for trading.
        if (accounts.size() == 1) {
            return accounts.stream().findAny();
        } else {
            return accounts.stream()
                    .filter(a -> "trade".equals(a.getName()))
                    .findFirst();
        }
    }

    @Override
    public final void onAccountsUpdates(final Map<String, AccountDTO> accounts) {
        // Here, we will receive an AccountDTO each time there is a change on your account.
        accounts.values().forEach(account -> System.out.println("Received information about an account : " + account));
    }

    @Override
    public final void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        // Here we will receive all tickers we required from the exchange.
        tickers.values().forEach(ticker -> System.out.println("Received information about a ticker : " + ticker));
    }

    @Override
    public final void onOrdersUpdates(final Map<String, OrderDTO> orders) {
        // Here, we will receive an OrderDTO each time order data has changed on the exchange.
        orders.values().forEach(order -> System.out.println("Received information about an order : " + order));
    }

    @Override
    public void onTradesUpdates(final Map<String, TradeDTO> trades) {
        // Here, we will receive a TradeDTO each time trade data has changed on the exchange.
        trades.values().forEach(trade -> System.out.println("Received information about a trade : " + trade));
    }

    @Override
    public void onPositionsUpdates(final Map<Long, PositionDTO> positions) {
        // Here, we will receive a PositionDTO each time a position has changed.
        positions.values().forEach(position -> System.out.println("Received information about a position : " + position));
    }

    @Override
    public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions) {
        // Here, we will receive a PositionDTO each time a position status has changed.
        positions.values().forEach(position -> System.out.println("Received information about a position status : " + position));
    }

}

```

A Cassandre strategy is a class annotated with [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html) and extending  [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html) or [BasicTa4jCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html).

This is how it works:
* In [getRequestedCurrencyPairs()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29), you return the list of currency pairs tickers you want to receive from the exchange.
* On the exchange, you usually have several accounts, and Cassandre needs to know which one of your accounts is the trading one. To do so, you have to implement the [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29) method, which gives you as a parameter the list of accounts you own on the exchange, and from that list, you have to return the one you use for trading.
* If there is a change in your account data, [onAccountsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onAccountsUpdates(java.util.Map)) will be called.
* When new tickers are available, [onTickersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickersUpdates(java.util.Map)) will be called.
* If there is a change in your orders, [onOrdersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onOrdersUpdates(java.util.Map)) will be called.
* If there is a change in your trades, [onTradesUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTradesUpdates(java.util.Map)) will be called.
* If there is a change in your positions, [onPositionsUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsUpdates(java.util.Map)) will be called.
* If there is a change in your position status, [onPositionsStatusUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionsStatusUpdates(java.util.Map)) will be called.

## Manage orders and positions
You can create an order this way:

```java
@Override
public final void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
     createBuyMarketOrder(new CurrencyPairDTO(BTC, USDT), new BigDecimal("0,001"));
}
```

This is the list of available methods to manage orders:
* [createBuyMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29).
* [createSellMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29).
* [createBuyLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29).
* [createSellLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29).

::: tip
Inside your strategy, you can call [canBuy()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canBuy%28tech.cassandre.trading.bot.dto.user.AccountDTO,tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29) and [canSell()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canSell%28tech.cassandre.trading.bot.dto.util.CurrencyDTO,java.math.BigDecimal%29) methods to see if your account has enough money to buy or sell assets.
:::

Cassandre trading bot also provides positions to manage your trading automatically:

```java
// Create rules.
PositionRulesDTO rules = PositionRulesDTO.builder()
                .stopGainPercentage(10)
                .stopLossPercentage(5)
                .build();
// Create a position.
createLongPosition(new CurrencyPairDTO(BTC, USDT),
                new BigDecimal("0,001"),
                rules);
```

First, we created a [rule](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionRulesDTO.html) saying this position should be closed if the calculated gain is more than 10% or if the loss is more than 5%. 

Then we called the [createLongPosition()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createLongPosition(tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO)) method. This will automatically create a buy order. From now, for every ticker received, Cassandre will check the gain or loss made on this position; if it triggers one of the rules, Cassandre will automatically create a sell order to close the position.

::: tip
You can learn more about positions in the [position chapter](./position-management.md).
:::

## Run the bot and the strategy

In the project folder, run:

```bash
mvn spring-boot:run
```

The logs should display something like this:

```bash
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.0)

19:06:04 - Starting Application using Java 11.0.9.1 on straumat-pc-portable with PID 158157 (/home/straumat/tmp/my-app/target/classes started by straumat in /home/straumat/tmp/my-app)
19:06:04 - No active profile set, falling back to default profiles: default
19:06:04 - Bootstrapping Spring Data JPA repositories in DEFERRED mode.
19:06:04 - Finished Spring Data repository scanning in 4 ms. Found 0 JPA repository interfaces.
19:06:04 - Bootstrapping Spring Data JPA repositories in DEFAULT mode.
19:06:04 - Finished Spring Data repository scanning in 49 ms. Found 5 JPA repository interfaces.
19:06:04 - HikariPool-1 - Starting...
19:06:04 - HikariPool-1 - Driver does not support get/set network timeout for connections. (feature not supported)
19:06:04 - HikariPool-1 - Start completed.
19:06:05 - Successfully acquired change log lock
19:06:05 - Creating database history table with name: PUBLIC.DATABASECHANGELOG
19:06:05 - Reading from PUBLIC.DATABASECHANGELOG
19:06:05 - Table EXCHANGE_ACCOUNTS created
19:06:05 - Auto-increment added to EXCHANGE_ACCOUNTS.ID
19:06:05 - Table STRATEGIES created
19:06:05 - Auto-increment added to STRATEGIES.ID
19:06:05 - Table POSITIONS created
19:06:05 - Auto-increment added to POSITIONS.ID
19:06:05 - Table ORDERS created
19:06:05 - Auto-increment added to ORDERS.ID
19:06:05 - Table TRADES created
19:06:05 - Auto-increment added to TRADES.ID
19:06:05 - Foreign key constraint added to STRATEGIES (FK_EXCHANGE_ACCOUNT_ID)
19:06:05 - Foreign key constraint added to POSITIONS (FK_STRATEGY_ID)
19:06:05 - Foreign key constraint added to POSITIONS (FK_OPENING_ORDER_ID)
19:06:05 - Foreign key constraint added to POSITIONS (FK_CLOSING_ORDER_ID)
19:06:05 - Foreign key constraint added to ORDERS (FK_STRATEGY_ID)
19:06:05 - Foreign key constraint added to TRADES (FK_ORDER_ID)
19:06:05 - Index IDX_STRATEGIES_STRATEGY_ID created
19:06:05 - Index IDX_POSITIONS_POSITION_ID created
19:06:05 - Index IDX_POSITIONS_STATUS created
19:06:05 - Index IDX_POSITIONS_FK_OPENING_ORDER_ID created
19:06:05 - Index IDX_POSITIONS_FK_CLOSING_ORDER_ID created
19:06:05 - Index IDX_ORDERS_ORDER_ID created
19:06:05 - Index IDX_TRADES_TRADE_ID created
19:06:05 - Index IDX_TRADES_ORDER_ID created
19:06:05 - ChangeSet db/changelog/db.changelog-4.0.0.xml::changelog-4.0.0::straumat ran successfully in 34ms
19:06:05 - Successfully released change log lock
19:06:05 - Initializing ExecutorService 'applicationTaskExecutor'
19:06:05 - HHH000204: Processing PersistenceUnitInfo [name: default]
19:06:05 - HHH000412: Hibernate ORM core version 5.4.27.Final
19:06:05 - HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
19:06:05 - HHH000400: Using dialect: org.hibernate.dialect.HSQLDialect
19:06:06 - HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
19:06:06 - Initialized JPA EntityManagerFactory for persistence unit 'default'
19:06:06 - Calling Remote Init...
19:06:07 - ExchangeConfiguration - Dry mode is ON
19:06:07 - Adding account 'main'
19:06:07 - - Adding balance 99.0001 BTC
19:06:07 - Adding account 'trade'
19:06:07 - - Adding balance 1 BTC
19:06:07 - - Adding balance 100000 USDT
19:06:07 - - Adding balance 10 ETH
19:06:08 - ExchangeConfiguration - Connection to kucoin successful
19:06:08 - ExchangeConfiguration - Supported currency pairs : BTC/USDT 
19:06:08 - ExchangeConfiguration - exchange configuration saved in database ExchangeAccount(id=1, exchange=kucoin, account=cassandre.crypto.bot@gmail.com)
19:06:08 - StrategyConfiguration - Running strategy 'Simple strategy'
19:06:08 - StrategyConfiguration - The strategy requires the following currency pair(s) : BTC/USDT
19:06:08 - Triggering deferred initialization of Spring Data repositories…
19:06:08 - Spring Data repositories initialized!
19:06:08 - No TaskScheduler/ScheduledExecutorService bean found for scheduled processing
19:06:08 - Started Application in 4.975 seconds (JVM running for 5.193)
Received information about an account : AccountDTO(accountId=main, name=main, features=[], balances={BTC=BalanceDTO(currency=BTC, total=null, available=99.0001, frozen=null, loaned=null, borrowed=null, withdrawing=null, depositing=null)})
Received information about an account : AccountDTO(accountId=trade, name=trade, features=[], balances={BTC=BalanceDTO(currency=BTC, total=null, available=1, frozen=null, loaned=null, borrowed=null, withdrawing=null, depositing=null), USDT=BalanceDTO(currency=USDT, total=null, available=100000, frozen=null, loaned=null, borrowed=null, withdrawing=null, depositing=null), ETH=BalanceDTO(currency=ETH, total=null, available=10, frozen=null, loaned=null, borrowed=null, withdrawing=null, depositing=null)})
Received information about a ticker : TickerDTO(currencyPair=BTC/USDT, open=null, last=37072.9, bid=37070.3, ask=37072.9, high=37250.3, low=34400, vwap=null, volume=74075.19293933, quoteVolume=2677417785.328956311, bidSize=null, askSize=null, timestamp=2021-02-03T19:06:08.055+01:00[Europe/Paris])
```

::: tip
Kucoin sandbox is having some issues right now. You should create your own exchange account and configure Cassandre accordingly.
:::