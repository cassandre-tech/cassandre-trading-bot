# Overview

::: tip
If you are new to trading, you can read our tutorial "[Trading basics](../ressources/trading-basics.md)".
:::

## Introduction
A trading bot is a computer program that can automatically place orders to a market or exchange without the need for human intervention. They are working for you 24/7 and never lose their focus.

Cassandre trading bot (available as a [Spring boot starter](https://mvnrepository.com/artifact/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-starter)) allows you to create and execute your trading strategy in seconds on most crypto exchanges. We also provide a [dry mode and a spring boot starter](../learn/dry-mode-and-backtesting.md) to backtest your bot on historical and/or real-time data.

Once the starter is added to your Spring Boot project, it will search for a class having the [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html) annotation and extending [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html) or [BasicTa4jCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html).

## BasicCassandreStrategy
For a [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html), you have to implement : 

* [getRequestedCurrencyPairs()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29) to indicate the list of currency pairs tickers you want to receive.
* [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29) to indicate which account is your trading account.

## BasicTa4jCassandreStrategy
For a [BasicTa4jCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html), you have to implement :

* [getRequestedCurrencyPair()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#getRequestedCurrencyPair%28%29) to indicate the currency pair tickers you want to receive.
* [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#getTradeAccount%28%29) to indicate which account is your trading account.
* [getMaximumBarCount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#getMaximumBarCount%28%29) to indicate how many bars you want to keep.
* [getDelayBetweenTwoBars()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#getDelayBetweenTwoBars%28%29) to indicate the delay between two bars.
* [getStrategy()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#getStrategy%28%29) to implement your own strategy.
* [shouldEnter()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#shouldEnter%28%29) to indicate what you want to do when it's time to buy.
* [shouldExit()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#shouldExit%28%29) to indicate what you want to do when it's time to sell.

## Data updates
To be notified of new data, you can override the following methods : 

* [onAccountUpdate()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onAccountUpdate%28tech.cassandre.trading.bot.dto.user.AccountDTO%29) to receive updates about your account.
* [onTickerUpdate()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickerUpdate%28tech.cassandre.trading.bot.dto.market.TickerDTO%29) to receive new tickers.
* [onOrderUpdate()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onOrderUpdate%28tech.cassandre.trading.bot.dto.trade.OrderDTO%29) to receive updates about your orders.
* [onTradeUpdate()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTradeUpdate%28tech.cassandre.trading.bot.dto.trade.TradeDTO%29) to receive updates about your trades.
* [onPositionUpdate()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionUpdate%28tech.cassandre.trading.bot.dto.position.PositionDTO%29) to receive updates about your positions.
* [onPositionStatusUpdate()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onPositionStatusUpdate%28tech.cassandre.trading.bot.dto.position.PositionDTO%29) to receive updates about position status change.

## Buying/selling
Inside your strategy, you can create market orders with the methods :

* [createBuyMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29)
* [createSellMarketOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellMarketOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29)

And limit orders with :

* [createBuyLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createBuyLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29)
* [createSellLimitOrder()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createSellLimitOrder%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,java.math.BigDecimal%29)

## Positions
You can also create positions with :

* [createLongPosition()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createLongPosition%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO%29)
* [createShortPosition()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createShortPosition(tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO))

On Positions, you can get the:
* The lowest calculated gain with [getLowestPrice()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLowestCalculatedGain())
* The highest calculated gain with [getHighestGainPrice()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getHighestGainPrice())
* The latest calculated gain with [getLatestGainPrice()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLatestGainPrice())

On a closed position, you can get the gain & fees with [getGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain())

::: tip
If you don't know what a position is, [read this position system explanation](../learn/position-management.md).
:::