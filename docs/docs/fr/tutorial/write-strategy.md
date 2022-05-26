---
lang: fr-FR
title: Écrivez votre stratégie
description: Cassandre tutorial - Écrivez votre stratégie
---

# Écrivez votre stratégie

## Commencez par une stratégie minimale

Nous allons modifier le fichier `src/main/java/com/mycompany/bot/SimpleStrategy.java` pour ajouter quelques variables
que nous utiliserons plus tard : la paire de devise que l'on veut trader, le montant des positions que nous
allons créer ainsi que les règles qui seront associées à cette position.

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

Voici le code correspondant:

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

## Ajoutez la logique métier

C'est le moment d'écrire notre algorithme d'investissement. Nous allons faire quelque chose de simple et pas forcément
pertinent :
- Nous allons stocker dans `CircularFifoQueue` les trois derniers tickers que nous avons reçus.
- Nous allons ajouter un ticker dans `CircularFifoQueue` toutes les minutes.
- Si chacun des trois tickers a un prix inférieur au précédent, nous allons créer une position.

Commençons par ajouter notre variable `CircularFifoQueue` :

```java
/** Tickers list. */
private final CircularFifoQueue<TickerDTO> tickerHistory=new CircularFifoQueue<>(3);
```

À chaque ticker reçu
([onTickersUpdates()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#onTickersUpdates(java.util.Map)))
, nous allons comparer le timestamp du nouveau ticker avec celui du dernier ticker ajouté à `CircularFifoQueue`. S'il y
a une différence d'une minute ou plus, on l'ajoute à `CircularFifoQueue`.

Voici le code correspondant :
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

À chaque nouveau ticker, on vérifie aussi si c'est le moment de créer une position !

## La stratégie complète

Voici le code source complet:

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