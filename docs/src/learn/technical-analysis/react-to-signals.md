# React to signals

Now that your strategy is coded, Cassandre will call your [shouldEnter()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#shouldEnter%28%29) method when it's time to buy and your [shouldExit()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#shouldExit%28%29) method when it's time to sell. You can do things manually by creating orders, but you can also use positions.

For example, you can do something like that on [shouldEnter()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/BasicTa4jCassandreStrategy.html#shouldEnter%28%29) :

```java
if (canBuy(new BigDecimal("0.01"))) {
            // Create rules.
            PositionRulesDTO rules = PositionRulesDTO
                    .builder()
                    .stopGainPercentage(10f)
                    .stopLossPercentage(5f)
                    .build();
            // Create position.
            createLongPosition(
                    new CurrencyPairDTO(BTC, USDT),
                    new BigDecimal("0.01"),
                    rules);
        }
```

Cassandre provides positions to manage your trading automatically. First, we created a rule saying this position should be closed if the gain is more than 10% or if the loss is more than 5%. 

Then we called the [createLongPosition()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createLongPosition%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO%29) method. It will automatically create a buy order. From now, with every ticker received, Cassandre will check the gain or loss made on this position; if it triggers one of the rules, Cassandre will automatically create a sell order to close it.

::: tip
You can learn more about positions in the [Position chapter](../position-management.md).
:::

::: tip
Inside your strategy, you can call [canBuy()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canBuy%28tech.cassandre.trading.bot.dto.user.AccountDTO,tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29) and [canSell()](https://www.javadoc.io/static/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/4.1.0/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canSell%28tech.cassandre.trading.bot.dto.util.CurrencyDTO,java.math.BigDecimal%29) methods to see if your account has enough money to buy or sell assets.
:::

