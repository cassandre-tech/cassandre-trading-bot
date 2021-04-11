# Supported cryptocurrency exchanges

## Supported by XChange
Cassandre uses [XChange](https://github.com/knowm/XChange), a Java library providing a streamlined API for interacting with 60+ Bitcoin and Altcoin exchanges providing a consistent interface for trading and accessing market data.

You can find [here](https://github.com/knowm/XChange/wiki/Exchange-Support) a table showing a list of exchanges and where we are in terms of supporting them.

## Validated by Cassandre
So Cassandre could theoretically support the 60+ cryptocurrency exchange the way XChange does. 

Some exchanges provide sandbox, so we were able to build integration tests for : 
 * [Coinbase](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/spring-boot-starter/autoconfigure/src/test/java/tech/cassandre/trading/bot/integration/coinbasepro): Working except cancel order.
 * [Gemini](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/spring-boot-starter/autoconfigure/src/test/java/tech/cassandre/trading/bot/integration/gemini): Working but market orders are not supported.
 * [Kucoin](https://github.com/cassandre-tech/cassandre-trading-bot/tree/development/spring-boot-starter/autoconfigure/src/test/java/tech/cassandre/trading/bot/integration/kucoin): Working.

Some users are also using Binance.
