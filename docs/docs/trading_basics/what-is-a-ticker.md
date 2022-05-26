---
lang: en-US
title: What is a ticker?
description: Trading - What is a ticker?
---

# What is a ticker ?

A ticker, a synonym for a stock symbol, is the short form of full security (asset). A ticker has a stream of quotes
(AKA prices) attached to it, continuously updated throughout a trading session by the various exchanges.

For example, take `ETH-BTC` , if you have [curl](https://curl.haxx.se/) and [jq](https://stedolan.github.io/jq/)
installed, you can get a ticker from Kucoin with this command

```bash
curl -s https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=ETH-BTC \
| jq .data
```

The result will look like this :

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

The first listed currency of a currency pair is called the base currency (ETH in our example), and the second currency
is called the quote currency (BTC in our example). A price at 0.033131 means 1 Ether can be bought with 0.033131
Bitcoin.

These are the fields you can find on a ticker quote:

| Field | Description |
| :--- | :--- |
| currencyPair | Currency pair |
| open | The opening price is the first trade price that was recorded during the day’s trading. |
| last | Last trade field is the price at which the last trade was executed. |
| bid | The bid price shown represents the highest price. |
| ask | The ask price shown represents the lowest price. |
| high | The day’s high price. |
| low | The day’s low price. |
| vwap | Volume-weighted average price \(VWAP\) is the ratio of the value traded to total volume traded over a particular time horizon \(usually one day\). |
| volume | Volume is the number of shares or contracts traded. |
| quoteVolume | Quote volume |
| bidSize | The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price. |
| askSize | The ask size represents the quantity of a security that investors are willing to sell at a specified selling price. |
| timestamp | The moment at which the account information was retrieved. |
