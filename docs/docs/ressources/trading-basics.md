---
lang: en-US
title: Trading basics
description: Learn the basics of trading (cryptocurrency, trading, exchange, account, ticker, market order, limit order...)
---
# Trading basics

## What is a cryptocurrency ?
A cryptocurrency is a digital asset designed to work as a medium of exchange that uses strong cryptography to secure financial transactions, control the creation of additional units, and verify assets' transfer.

Cryptocurrencies use decentralized control as opposed to centralized digital currency and central banking systems.

Bitcoin is the first decentralized cryptocurrency. Since the release of bitcoin, over 6,000 other cryptocurrencies have been created.

## What is trading ?
Trading means “exchanging one item for another”. In the financial markets, it’s buying shares, futures, options, swaps, bonds, etc..., or like in our case, an amount of cryptocurrency.

The idea here is to buy cryptocurrencies at a specific price and sell it at a higher price to make profits \(even if you can still profit if the price goes down\).

To make it simple, the value of an asset changes due to supply and demand: if many people want one bitcoin, the price will go up. If a lot of people want to sell one bitcoin, the price will go down.

Nowadays, thanks to exchanges like [Coinbase](https://www.coinbase.com/) or [Kucoin](https://www.kucoin.com/ucenter/signup?utm_source=Cassandre), anyone can start trading using the tools provided by those platforms. They even offer API allowing you to create bots \(with Cassandre, for example\), which automatically do the trading for you based on the rules you choose.

## What is an exchange ?
[Cryptocurrency exchanges](https://coinmarketcap.com/rankings/exchanges/) like [Coinbase](https://www.kucoin.com/ucenter/signup?utm_source=Cassandre) or [Kucoin](https://www.kucoin.com/ucenter/signup?utm_source=Cassandre) are online platforms where you can exchange an asset for another one based on the market value.

To make it simple, after creating an account, the exchange will allow you to buy an amount of a cryptocurrency in exchange for your euros/dollars \(they usually take a fee for that\). Be careful; in reality, you don’t really “own” the cryptocurrency you will buy there: if you don’t own the private key, you have a “promise” that an exchange owes you some assets.

Most exchanges also offer tools for trading cryptocurrency. For example, Coinbase provides [this tool](https://pro.coinbase.com/).

More interesting in our case, most exchanges offer API for trading like the [coinbase API](https://developers.coinbase.com/). Instead of spending hours trading in front of your computer, you can write a program that trades for you. That’s the goal of our bot.

One last point, some exchanges offer a sandbox that allows you to trade on the “simulated platform” with fake assets. For example, [Kucoin](https://sandbox.kucoin.com/).

## What is an account ?
Usually, in an exchange, you can have several accounts with different usages \(Classical, trading, margin trading, future…\), and then you have a balance for each currency.

A balance is not merely the amount you have; it could be more complicated and can have the following values :

| Field | Description |
| :--- | :--- |
| currency | Currency |
| total | Returns the total amount of the currency in this balance |
| available | Returns the amount of the currency in this balance that is available to trade |
| frozen | Returns the frozen amount of the currency in this balance that is locked in a trading |
| loaned | Returns the loaned amount of the total currency in this balance that will be returned |
| borrowed | Returns the borrowed amount of the available currency in this balance that must be repaid |
| withdrawing | Returns the amount of the currency in this balance that is locked in withdrawal |
| depositing | Returns the amount of the currency in this balance that is locked in deposit |

## What is a ticker ?
A ticker, a synonym for a stock symbol, is the short form of full security \(asset\). A ticker has a stream of quotes \(AKA prices\) attached to it, continuously updated throughout a trading session by the various exchanges.

For example, take `ETH-BTC` , if you have [curl](https://curl.haxx.se/) and [jq](https://stedolan.github.io/jq/) installed, you can get a ticker from Kucoin with this command
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

The first listed currency of a currency pair is called the base currency \(ETH\), and the second currency is called the quote currency. A price at 0.033131 means 1 Ether can be bought with 0.033131 Bitcoin.

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

## What is an order ?
An order is an instruction to the exchange to purchase or sell an asset for you.

### Market Orders
A market order is an order to buy or sell a specified quantity of the underlying security immediately.
If you buy an asset, you will pay the price or higher than the ask price in the ticker. If you are going to sell an asset, you will receive an amount at or lower than the bid price in the ticker.

### Limit Orders
A limit order, sometimes referred to as a pending order, allows you to buy and sell assets at a specific price in the future.

This type of order is used to execute a trade if the price reaches the price you decided. The order will not be filled if the price does not reach this level. In effect, a limit order sets the maximum or minimum price you are willing to buy or sell.

For example, you can create an order saying, “I want to buy one bitcoin when it reaches 4,000$”.
