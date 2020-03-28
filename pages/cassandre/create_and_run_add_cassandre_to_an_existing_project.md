---
title: Add Cassandre to an existing project
sidebar: cassandre_sidebar
summary: How to add Cassandre to an existing spring boot project.
permalink: create_and_run_add_cassandre_to_an_existing_project.html
---

*[Prerequisite : java jdk 11 & maven must be installed.](how_to_install_development_tools)*

## Existing project.
To demonstrate how to add Cassandre to an existing Spring project, we will create a project with [Spring boot initializr](https://start.spring.io/).

{% include note.html content="[Click here for a simple pre configured spring boot project](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.2.6.RELEASE&packaging=jar&jvmVersion=1.8&groupId=com.mycompany.app&artifactId=my-app&name=my-app&description=Demo%20project%20for%20Spring%20Boot&packageName=com.mycompany.app.my-app)" %}

## Project structure.
```
my-app
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── mycompany
    │   │           └── app
    │   │               └── myapp
    │   │                   └── MyAppApplication.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── mycompany
                    └── app
                        └── myapp
                            └── MyAppApplicationTests.java

```

## Add cassandre trading bot library.
The first step is to add our spring boot starter to the project. Add the following dependency to <code>pom.xml</code> in the <code>dependencies</code> section : 
```xml
<dependencies>
    ...
	<dependency>
		<groupId>tech.cassandre.trading.bot</groupId>
		<artifactId>cassandre-trading-bot-spring-boot-starter</artifactId>
		<version>0.0.6</version>
	</dependency>
     ...
</dependencies>
```

The latest release is :

[![Maven Central](https://img.shields.io/maven-central/v/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22tech.cassandre.trading.bot%22%20AND%20a:%22cassandre-trading-bot-spring-boot-starter%22).

## Add a strategy.
Create a class named <code>MyStrategy</code> in <code>src/main/java</code> : 
```java
package com.mycompany.app.myapp;

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
 * My strategy.
 */
@CassandreStrategy
public final class MyStrategy extends BasicCassandreStrategy {

	@Override
	public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
		// We only ask about ETC/BTC (Base currency : ETH / Quote currency : BTC).
		return Set.of(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC));
	}

	@Override
	public void onAccountUpdate(final AccountDTO account) {
		// Here, we will receive an AccountDTO each time there is a move on our account.
		System.out.println("Received information about an account : " + account);
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

}
```
This strategy will only display the data received.

## Add configuration
Setup the sandbox account in <code>src/main/resources/application.properties</code>, but you'd better create your own [Kucoin sandbox account](how_to_create_an_exchange_sandbox_for_kucoin.html). 
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
A jar file named <code>my-app-0.0.1-SNAPSHOT.jar</code> will be created in the <code>target</code> directory. You can run it with : 
```sh
java -jar target/my-app-0.0.1-SNAPSHOT.jar
```
