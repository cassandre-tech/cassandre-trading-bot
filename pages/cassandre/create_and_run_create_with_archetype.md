---
title: Create a new project with Cassandre archetype
sidebar: cassandre_sidebar
summary: How to create a strategy from scratch thanks to our archetype.
permalink: create_and_run_create_with_archetype.html
---

*[Prerequisite : java jdk 11 & maven must be installed.](how_to_install_development_tools)*

## Create your project.
Just type this command :
```sh
mvn archetype:generate -DarchetypeGroupId=tech.cassandre.trading.bot -DarchetypeArtifactId=cassandre-trading-bot-spring-boot-starter-archetype
```
It will ask for the following parameters : 
  * <code>groupId</code> (example : com.mycompany.app).
  * <code>artifactId</code> (example : my-app).
  * <code>version</code> (example : 1.0-SNAPSHOT).
  * <code>package</code> (example : com.mycompany.app).

## Created project.
The created project is a spring boot project with the following structure : 
```
my-app
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
    │       └── application.properties
    └── test
        └── java
            └── com
                └── mycompany
                    └── app
                        └── SimpleStrategyTest.java
```
The <code>src/main/java/com/mycompany/app/Application.java</code> is a classical Application spring boot starter :
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
The <code>src/main/java/com/mycompany/app/SimpleStrategy.java</code> is the strategy executed by the bot :
```java
package com.mycompany.app;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.Strategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple strategy.
 * Please, create your own Kucoin sandbox account and do not make orders with this account.
 * How to do it : https://trading-bot.cassandre.tech/how_to_create_an_exchange_sandbox_for_kucoin.html
 */
@Strategy(name = "Simple strategy")
public final class SimpleStrategy extends CassandreStrategy {

	/** The accounts owned by the user. */
	private final Map<String, AccountDTO> accounts = new LinkedHashMap<>();

	@Override
	public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
		// We only ask about ETC/BTC (Base currency : ETH / Quote currency : BTC).
		return Set.of(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC));
	}

	@Override
	public void onAccountUpdate(final AccountDTO account) {
		// Here, we will receive an AccountDTO each time there is a move on our account.
		System.out.println("Received information about an account : " + account);
		accounts.put(account.getId(), account);
	}

	@Override
	public void onTickerUpdate(final TickerDTO ticker) {
		// Here we will receive a TickerDTO each time a new one is available.
		System.out.println("Received information about a ticker : " + ticker);
	}

	@Override
	public void onOrderUpdate(final OrderDTO order) {
		// Here, we will receive an OrderDTO each an Order data has changed in the exchange.
		System.out.println("Received information about an order : " + order);
	}

	/**
	 * Getter accounts.
	 *
	 * @return accounts
	 */
	public Map<String, AccountDTO> getAccounts() {
		return accounts;
	}

}
```
As described in the [architecture chapter](our_bot_architecture.html), a cassandre strategy is a class annotated with [@Strategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/Strategy.java) and implementing the [CassandreStrategy](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/strategy/CassandreStrategy.java) interface.

This is how it works : 
  * In the method <code>getRequestedCurrencyPairs()</code>, you have to return the list of currency pairs updates you want to receive.
  * If there is a change on your account data, <code>onAccountUpdate()</code> will be called.
  * When a new ticker is available, <code>onTickerUpdate()</code> will be called.
  * If there is a change on your order data, <code>onOrderUpdate()</code> will be called. 
  * Inside this class, you can make an order by accessing the trade service with a call to <code>getTradeService()</code>.

## Edit the configuration.
By default, we had setup our sandbox account in  <code>src/main/resources/application.properties</code> but you'd better create your own [Kucoin sandbox account](how_to_create_an_exchange_sandbox_for_kucoin.html). 
```properties
#
# Exchange configuration.
cassandre.trading.bot.exchange.name=kucoin
cassandre.trading.bot.exchange.sandbox=true
#
# Exchange credentials.
cassandre.trading.bot.exchange.username=cassandre.crypto.bot@gmail.com
cassandre.trading.bot.exchange.passphrase=cassandre
cassandre.trading.bot.exchange.key=5df8eea30092f40009cb3c6a
cassandre.trading.bot.exchange.secret=5f6e91e0-796b-4947-b75e-eaa5c06b6bed
#
# Exchange API calls rates.
cassandre.trading.bot.exchange.rates.account=1000
cassandre.trading.bot.exchange.rates.ticker=1000
cassandre.trading.bot.exchange.rates.order=1000
```
  
## Run the bot and the strategy.
In the project folder, just type : 
```sh
mvn spring-boot:run
```
The logs should display something like this : 
```sh
 ==========================================================================
 ,-----.                                               ,--.
'  .--./  ,--,--.  ,---.   ,---.   ,--,--. ,--,--,   ,-|  | ,--.--.  ,---.
|  |     ' ,-.  | (  .-'  (  .-'  ' ,-.  | |      \ ' .-. | |  .--' | .-. :
'  '--'\ \ '-'  | .-'  `) .-'  `) \ '-'  | |  ||  | \ `-' | |  |    \   --.
 `-----'  `--`--' `----'  `----'   `--`--' `--''--'  `---'  `--'     `----'
 ==========================================================================
Starting Application on straumat-portable with PID 14900 (/home/straumat/tmp/tmp2/my-app/target/classes started by straumat in /home/straumat/tmp/tmp2/my-app)
No active profile set, falling back to default profiles: default
Calling Remote Init...
ExchangeConfiguration - Connection to kucoin successful
ExchangeConfiguration - Supported currency pairs : EOS/BTC, EOS/USDT, EOS/ETH, LTC/USDT, LTC/ETH, LTC/BTC, KCS/USDT, KCS/ETH, KCS/BTC, ETH/USDT, BTC/USDT, ETH/BTC, XRP/BTC, XRP/USDT, XRP/ETH
StrategyConfiguration - Running strategy 'Simple strategy'
StrategyConfiguration - The strategy requires the following currency pair(s) : ETH/BTC
No TaskScheduler/ScheduledExecutorService bean found for scheduled processing
Started Application in 3.217 seconds (JVM running for 3.447)
Received information about an account : AccountDTO{ id='trade', name='trade', balances={BTC=BalanceDTO{ currency=BTC, total=0.99999534, available=0.99999533, frozen=1E-8, loaned=0, borrowed=0, withdrawing=0, depositing=0}, ETH=BalanceDTO{ currency=ETH, total=10.0002, available=10.0001, frozen=0.0001, loaned=0, borrowed=0, withdrawing=0, depositing=0}}}
Received information about an account : AccountDTO{ id='main', name='main', balances={BTC=BalanceDTO{ currency=BTC, total=0, available=0, frozen=0, loaned=0, borrowed=0, withdrawing=0, depositing=0}, ETH=BalanceDTO{ currency=ETH, total=0, available=0, frozen=0, loaned=0, borrowed=0, withdrawing=0, depositing=0}, KCS=BalanceDTO{ currency=KCS, total=1000, available=1000, frozen=0, loaned=0, borrowed=0, withdrawing=0, depositing=0}}}
Received information about a ticker : TickerDTO{ currencyPair=ETH/BTC, open=null, last=0.025083, bid=0.025041, ask=0.025088, high=0.025645, low=0.025019, vwap=null, volume=26731.9949959, quoteVolume=677.6588868165302, bidSize=null, askSize=null, timestamp=2020-03-11T15:27:22.038+01:00[Europe/Paris]}
```

## Package the bot and the strategy.
In the project folder, just type : 
```sh
mvn package spring-boot:repackage
```
A jar file named <code>my-app-1.0-SNAPSHOT.jar</code> will be created in the <code>target</code> directory. You can run it with : 
```sh
java -jar target/my-app-1.0-SNAPSHOT.jar
```
