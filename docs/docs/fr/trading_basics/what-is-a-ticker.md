---
lang: fr-FR
title: Qu'est-ce qu'un ticker ?
description: Trading - Qu'est-ce qu'un ticker ?
---

# Qu'est-ce qu'un ticker ?

Un ticker, synonyme de symbole boursier, est la forme abrégée d'un actif. Ses informations sont mises à jour en continu
tout au long d'une séance de négociation.

Par exemple, `ETH-BTC`, si vous avez installé les outils [curl](https://curl.haxx.se/)
et [jq](https://stedolan.github.io/jq/), vous pouvez obtenir un ticker de Kucoin avec cette commande:

```bash
curl -s https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=ETH-BTC | jq .data
```

Vous allez avoir un résultat ressemblant à cela :

```json
{
  "time": 1597187421265,
  "sequence": "1594340550066",
  "price": "0.033131",
  "size": "0.0013217",
  "bestBid": "0.03313",
  "bestBidSize": "2.1812529",
  "bestAsk": "0.033131",
  "bestAskSize": "2.8001025"
}
```

La première devise répertoriée d'une paire de devises est appelée devise de base (ETH dans notre exemple) et la deuxième
devise est appelée devise de cotation (BTC dans notre exemple). Un prix à 0,033131 signifie que 1 Ether peut être acheté
avec 0,033131 Bitcoin.

Voici les champs que vous pouvez trouver sur un ticker :

| Field | Description                                                                                                                                      |
| :--- |:-------------------------------------------------------------------------------------------------------------------------------------------------|
| currencyPair | Currency pair (ETH-BTC for example)                                                                                                              |
| open | The opening price is the first trade price that was recorded during the day’s trading                                                            |
| last | Last trade field is the price at which the last trade was executed                                                                               |
| bid | The bid price shown represents the highest price                                                                                                 |
| ask | The ask price shown represents the lowest price                                                                                                  |
| high | The day’s high price                                                                                                                             |
| low | The day’s low price                                                                                                                              |
| vwap | Volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day) |
| volume | Volume is the number of shares or contracts traded                                                                                               |
| quoteVolume | Quote volume                                                                                                                                     |
| bidSize | The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price                               |
| askSize | The ask size represents the quantity of a security that investors are willing to sell at a specified selling price                               |
| timestamp | The moment at which the account information was retrieved                                                                                        |