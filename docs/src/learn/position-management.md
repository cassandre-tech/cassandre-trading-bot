# Position management
Cassandre provides a class to manage your positions automatically.

## Long position
In your strategy, you can create a long position with the [createLongPosition()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createLongPosition%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO%29) method.

It has three parameters : 

* The currency pair, for example, ETH/USDT.
* The amount, for example, 0.5 ETH.
* The rules, for example, 100% stop gain and 50% stop loss.

The first step is to create the rules you want to apply to the position thanks to the [PositionRulesDTO](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionRulesDTO.html) class, for example: 

```java
PositionRulesDTO rules = PositionRulesDTO.builder()
                .stopGainPercentage(100)
                .stopLossPercentage(50)
                .build();
```

Then, you can create the position with that rule: 
```java
createLongPosition(new CurrencyPairDTO(ETH, BTC), new BigDecimal("0.5"), rules);
```

At this moment, Cassandre will create a buy order of 0.5 ETH (At that moment, 1 ETH costs 1500 USDT), and this will cost us 750 USDT. The position status will be [OPENING](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENING), and when all the corresponding trades have arrived, the status will move to [OPENED](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENED).

::: tip
Note: if you want to check if you have enough funds available (at least 750 USDT in our case) before creating the position, you can use the [canBuy()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canBuy%28tech.cassandre.trading.bot.dto.user.AccountDTO,tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29) method.
:::

From now on, for every ticker received, Cassandre will automatically calculate, with the new price, if closing the position at that price would trigger one of our two rules (100% stop gain and 50% stop loss).

For example, if we receive a new price of 3000 USDT for 1 ETH, Cassandre will calculate that if we sell our position right now (meaning "closing the position"), we will get 1 500 USDT, a 100% gain. As our rule is triggered, Cassandre will automatically create a selling order of our 0.5 ETH. The position status will move to [CLOSING](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSING), and when all the corresponding trades have arrived, the status will move to [CLOSED](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSED).

You can then know your exact gain on this position by calling the [getGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain%28%29) method. 

## Short position
A [short position](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createShortPosition(tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO)) works the opposite way. With a short position, you bet that the price will go down.

Let's say you create a short position on 1 ETH with this command:

```java
createShortPosition(new CurrencyPairDTO(ETH, BTC), new BigDecimal("1"), rules);
```

Cassandre will sell 1 ETH and get 1 500 USDT and wait until the price is down enough to buy 2 ETH with that 1 500 USDT. 

::: tip
Note: if you want to check if you have enough funds available (at 1 ETH in our case) before creating the position, you can use the [canSell()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canSell%28tech.cassandre.trading.bot.dto.util.CurrencyDTO,java.math.BigDecimal%29) method.
:::

## Gains
On a position you can get the:
* The lowest calculated gain with [getLowestPrice()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLowestCalculatedGain())
* The highest calculated gain with [getHighestGainPrice()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getHighestGainPrice())
* The latest calculated gain with [getLatestGainPrice](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLatestGainPrice())

Once the position is closed, you can get the gain & fees with [getGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain())