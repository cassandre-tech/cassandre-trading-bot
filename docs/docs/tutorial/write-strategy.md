---
lang: en-US
title: Write your strategy
description: Cassandre tutorial - Write your strategy
---

# Write your strategy

## Start with a minimal strategy

We start by editing `my-trading-bot/src/main/java/com/mycompany/bot/SimpleStrategy.java` to add some variables that we
will need later: the currency pair we want to deal with, the amount and rules we will use when creating a new position.

```java
/** Currency pair. */
private static final CurrencyPairDTO POSITION_CURRENCY_PAIR=new CurrencyPairDTO(BTC,USDT);

/** Amount we take on every position - 0.001 BTC = 29,000 USD on 18th May 2022. */
private static final BigDecimal POSITION_AMOUNT=new BigDecimal("0.001");

/** Rules set for every position. */
private static final PositionRulesDTO POSITION_RULES=PositionRulesDTO.builder()
        .stopGainPercentage(4f)
        .stopLossPercentage(8f)
        .build();
```

This is the corresponding code:

```java
package com.mycompany.bot;

import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Simple strategy.
 */
@CassandreStrategy
public final class SimpleStrategy extends BasicCassandreStrategy {

    /** Currency pair. */
    private static final CurrencyPairDTO POSITION_CURRENCY_PAIR = new CurrencyPairDTO(BTC, USDT);

    /** Amount we take on every position - 0.001 BTC = 29,000 USD on 18th May 2022. */
    private static final BigDecimal POSITION_AMOUNT = new BigDecimal("0.001");

    /** Rules set for every position. */
    private static final PositionRulesDTO POSITION_RULES = PositionRulesDTO.builder()
            .stopGainPercentage(4f)
            .stopLossPercentage(8f)
            .build();

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return Set.of(POSITION_CURRENCY_PAIR);
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // We choose the one whose name is "trade".
        return accounts.stream()
                .filter(a -> "trade".equalsIgnoreCase(a.getName()))
                .findFirst();
    }

}
```

## Add business logic

It's now time to write our business logic, we will make something simple:

- We will store in `CircularFifoQueue` the last three tickers we received.
- We add one ticker to `CircularFifoQueue`  every minute.
- If each of the three tickers are lower than the previous one, we create a long position.

Add this variable to our strategy:

```java
/** Tickers list. */
private final CircularFifoQueue<TickerDTO> tickerHistory=new CircularFifoQueue<>(3);
```

On each received ticker
 ([onTickersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickersUpdates(java.util.Map)))
, we will compare the new ticker timestamp with the timestamp of the latest ticker added to `CircularFifoQueue`. If the difference is superior or equals to one minute, we add it to `CircularFifoQueue`.

This is the corresponding code:
```java
    @Override
    public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        final TickerDTO newTicker = tickers.get(POSITION_CURRENCY_PAIR);
        if (newTicker != null) {
            if (tickerHistory.isEmpty() ||
                    newTicker.getTimestamp().isEqual(tickerHistory.get(tickerHistory.size() - 1).getTimestamp().plus(Duration.ofMinutes(1))) ||
                    newTicker.getTimestamp().isAfter(tickerHistory.get(tickerHistory.size() - 1).getTimestamp().plus(Duration.ofMinutes(1)))
            ) {
                // In that case, we had a new ticker to the list.
                tickerHistory.add(newTicker);

                boolean allInferior = true;
                for (int i = 0; i < tickerHistory.size() - 1; i++) {
                    boolean isInferior = tickerHistory.get(i).getLast().compareTo(tickerHistory.get(i + 1).getLast()) > 0;
                    if (!isInferior) {
                        allInferior = false;
                        break;
                    }
                }
                if (allInferior && canBuy(POSITION_CURRENCY_PAIR, POSITION_AMOUNT) && tickerHistory.size() == 3) {
                    final PositionCreationResultDTO positionCreationResultDTO = createLongPosition(POSITION_CURRENCY_PAIR,
                            POSITION_AMOUNT,
                            POSITION_RULES);
                    if (!positionCreationResultDTO.isSuccessful()) {
                        System.err.println("createLongPosition failed " + positionCreationResultDTO.getErrorMessage());
                    }
                }
            }
        }
    }
```

For every new ticker, we also check if it's the moment to create the position (eg: if each of the three tickers stored are lower than the previous one).

## The whole strategy

This is the code of the complete strategy:
```java
package com.mycompany.bot;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Simple strategy.
 */
@CassandreStrategy
public final class SimpleStrategy extends BasicCassandreStrategy {

    /** Currency pair. */
    private static final CurrencyPairDTO POSITION_CURRENCY_PAIR = new CurrencyPairDTO(BTC, USDT);

    /** Amount we take on every position - 0.001 BTC = 29,000 USD on 18th May 2022. */
    private static final BigDecimal POSITION_AMOUNT = new BigDecimal("0.001");

    /** Rules set for every position. */
    private static final PositionRulesDTO POSITION_RULES = PositionRulesDTO.builder()
            .stopGainPercentage(4f)
            .stopLossPercentage(8f)
            .build();

    /** Tickers list. */
    private final CircularFifoQueue<TickerDTO> tickerHistory = new CircularFifoQueue<>(3);

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return Set.of(POSITION_CURRENCY_PAIR);
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // We choose the one whose name is "trade".
        return accounts.stream()
                .filter(a -> "trade".equalsIgnoreCase(a.getName()))
                .findFirst();
    }

    @Override
    public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        final TickerDTO newTicker = tickers.get(POSITION_CURRENCY_PAIR);
        if (newTicker != null) {
            if (tickerHistory.isEmpty() ||
                    newTicker.getTimestamp().isEqual(tickerHistory.get(tickerHistory.size() - 1).getTimestamp().plus(Duration.ofMinutes(1))) ||
                    newTicker.getTimestamp().isAfter(tickerHistory.get(tickerHistory.size() - 1).getTimestamp().plus(Duration.ofMinutes(1)))
            ) {
                // In that case, we had a new ticker to the list.
                tickerHistory.add(newTicker);

                boolean allInferior = true;
                for (int i = 0; i < tickerHistory.size() - 1; i++) {
                    boolean isInferior = tickerHistory.get(i).getLast().compareTo(tickerHistory.get(i + 1).getLast()) > 0;
                    if (!isInferior) {
                        allInferior = false;
                        break;
                    }
                }
                if (allInferior && canBuy(POSITION_CURRENCY_PAIR, POSITION_AMOUNT) && tickerHistory.size() == 3) {
                    final PositionCreationResultDTO positionCreationResultDTO = createLongPosition(POSITION_CURRENCY_PAIR,
                            POSITION_AMOUNT,
                            POSITION_RULES);
                    if (!positionCreationResultDTO.isSuccessful()) {
                        System.err.println("createLongPosition failed " + positionCreationResultDTO.getErrorMessage());
                    }
                }
            }
        }
    }

}
```