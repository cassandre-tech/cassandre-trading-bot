---
lang: fr-FR
title: Gestion des positions
description: Gestion des positions dans Cassandre
---

# Gestion des positions

Cassandre fourni un moyen de gérer facilement et automatiquement des positions.

## Position longue

Dans votre stratégie, vous pouvez créer une position longue avec la
méthode [createLongPosition()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#createLongPosition%28tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal,tech.cassandre.trading.bot.dto.position.PositionRulesDTO%29)

Cette méthode a trois paramètres :

* La paire de devise (Par exemple : `ETH/USDT`).
* Le montant (par exemple : `0.5`).
* Les règles (par exemple : `100% de gain ou 50% de perte`).

Voici comment créer les règles d'une position avec l'
objet [PositionRulesDTO](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionRulesDTO.html) :

```java
PositionRulesDTO rules=PositionRulesDTO.builder()
        .stopGainPercentage(100)
        .stopLossPercentage(50)
        .build();
```

Vous pouvez ensuite créer la position de cette façon:

```java
createLongPosition(new CurrencyPairDTO(ETH,BTC),
        new BigDecimal("0.5"),
        rules);
```

À ce moment, Cassandre va créer un ordre d'achat de 0,5 ETH qui nous coûtera 750 USDT (1 ETH coûtant 1 500 USDT). Le
statut de la position sera alors
[OPENING](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENING)
, et lorsque tous les trades correspondants à cet ordre seront arrivés, le statut passera
à [OPENED](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#OPENED)
.

::: tip
Si vous souhaitez vérifier que vous disposez de suffisamment de fonds (au moins 750 USDT dans notre cas) avant de créer
la position, vous pouvez utiliser la
méthode [canBuy()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/strategy/GenericCassandreStrategy.html#canBuy%28tech.cassandre.trading.bot.dto.user.AccountDTO,tech.cassandre.trading.bot.dto.util.CurrencyPairDTO,java.math.BigDecimal%29)
.
:::

Désormais, pour chaque ticker reçu, Cassandre calculera le gain de la position. Si ce gain correspond une des règles que
l'on a fixées, elle sera automatiquement clôturée.

Par exemple, si nous recevons un nouveau prix de 3 000 USDT pour 1 ETH, Cassandre calculera que si nous vendons notre
position maintenant (ce que l'on appelle "fermer la position"), nous obtiendrons 1 500 USDT, soit un gain de 100%.
Cassandre va donc créer automatiquement un ordre de vente de nos 0,5 ETH. Le statut de la
position passera
à [CLOSING](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSING)
, et lorsque tous les trades correspondants seront arrivés, le statut passera
à [CLOSED](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionStatusDTO.html#CLOSED)
.

Vous pourrez alors connaître votre gain exact sur cette position en appelant la
méthode [getGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain())
.

## Position courte

Une position courte est l'inverse d'une position longue. Avec une position courte, vous pariez sur le fait que le prix
va baisser.

Disons que vous créez une position courte sur 1 ETH avec ce code :

```java
createShortPosition(new CurrencyPairDTO(ETH,BTC),
        new BigDecimal("1"),
        rules);
```

Cassandre vendra 1 ETH, obtiendra 1 500 USDT et attendra que le prix baisse suffisamment pour acheter 2 ETH avec 1 500
USDT.

## Gains

Sur
l'objet [PositionDTO](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html)
, vous pouvez obtenir :

* Le gain calculé le plus bas
  avec [getLowestCalculatedGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLowestCalculatedGain())
* Le gain calculé le plus élevé
  avec [getHighestCalculatedGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getHighestCalculatedGain())
* Le dernier gain calculé
  avec [getLatestCalculatedGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getLatestCalculatedGain())

Sur une position fermée, vous pouvez obtenir le gain et les frais associés avec la
méthode [getGain()](https://www.javadoc.io/doc/tech.cassandre.trading.bot/cassandre-trading-bot-spring-boot-autoconfigure/latest/tech/cassandre/trading/bot/dto/position/PositionDTO.html#getGain())
.