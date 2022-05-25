---
lang: fr-FR
title: Qu'est-ce qu'une stratégie ?
description: Qu'est-ce qu'une stratégie de trading ?
---

# Qu'est-ce qu'une stratégie ?

Une stratégie est une classe que vous allez écrire et qui va décrire ce que vous voulez faire (acheter, vendre, créer
une position) en fonction des actifs dont vous disposez sur votre compte et des données du marché (des tickers qui
arrivent) ou de toute autre information que vous irez chercher.

Afin que votre stratégie soit reconnue et exécutée par Cassandre, elle doit avoir
l'annotation [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html)
et hériter
de [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html)
.

Vous devez ensuite implémenter les méthodes suivantes :

* [getRequestedCurrencyPairs()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29)
  pour indiquer la liste des paires de devises que vous souhaitez que votre stratégie reçoive.
* [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29)
  pour indiquer, parmi la liste des comptes dont vous disposez sur l'exchange, lequel est celui qui sert au trading.

Vous trouverez ci-dessous une stratégie minimale, il s'agit d'une simple classe avec :

* L'Annotation [@CassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategy.html)
.
* L'héritage
  de [BasicCassandreStrategy](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/BasicCassandreStrategy.html)
  .
* Une implémentation
  de [getRequestedCurrencyPairs()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getRequestedCurrencyPairs%28%29)
  qui indique que l'on souhaite que l'on souhaite recevoir les tickers pour la paire de devise `BTC/USDT`.
* Une implémentation
  de [getTradeAccount()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/CassandreStrategyInterface.html#getTradeAccount%28java.util.Set%29)
  qui indique que, parmi tous les que l'on a sur l'exchange, celui que l'on utilise pour le trading est celui dont le
  nom est `trade`.

```java
package com.mycompany.app;

import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.Map;
import java.util.Set;

/**
 * Simple strategy.
 */
@CassandreStrategy
public final class SimpleStrategy extends BasicCassandreStrategy {

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        // We only ask for BTC/USDT tickers (Base currency : BTC / Quote currency : USDT).
        return Set.of(new CurrencyPairDTO(BTC, USDT));
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // From all the accounts we have on the exchange,
        // we must return the one we use for trading.
        return accounts.stream()
                .filter(a -> "trade".equalsIgnoreCase(a.getName()))
                .findFirst();
    }

}
```

::: tip
Dans un même trading bot, il est possible de faire fonctionner plusieurs stratégies ! Par contre, elles seront toutes
connectées au même exchange.
:::