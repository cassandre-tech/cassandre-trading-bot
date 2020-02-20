---
title: Trade data & service
sidebar: cassandre_sidebar
summary: Classes to manage trade information and creates order.
permalink: bot_development_trade_data_and_service.html
---

## Data.

{% include image.html file="project_development/package_trade.png" alt="Trade package class diagram" caption="Trade package class diagram" %}

### OrderCreationResultDTO.
[OrderCreationResultDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/dto/trade/OrderCreationResultDTO.java) is returned after an order is created. It contains either the order id or the error message.

| Field  | Description  |
|-------|---------|
| <code>orderId</code>  | Order ID (filled if order creation is successful)  |
| <code>errorMessage</code>  | Error message (filled if order creation failed)  |
| <code>exception</code>  |  Exception (filled if order creation failed)  |
  
### OrderDTO.
[OrderDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/dto/trade/OrderDTO.java) represents order information from the exchange. A market order is a request by an investor to buy or sell in the current market.

| Field  | Description  |
|-------|---------|
| <code>type</code>  | Order type i.e. bid or ask  |
| <code>originalAmount</code>  | Amount to be ordered / amount that was ordered  |
| <code>currencyPair</code>  | The currency pair  |
| <code>id</code>  | An identifier set by the exchange that uniquely identifies the order  |
| <code>userReference</code>  | An identifier provided by the user on placement that uniquely identifies the order  |
| <code>timestamp</code>  | The timestamp on the order according to the exchange's server, null if not provided  |
| <code>status</code>  | Status of order during it lifecycle  |
| <code>cumulativeAmount</code>  | Amount to be ordered / amount that has been matched against order on the order book/filled  |
| <code>averagePrice</code>  | Weighted Average price of the fills in the order  |
| <code>fee</code>  | The total of the fees incurred for all transactions related to this order  |
| <code>leverage</code>  | The leverage to use for margin related to this order  |
| <code>limitPrice</code>  | The limit price  |

### OrderTypeDTO.
[OrderTypeDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/dto/trade/OrderTypeDTO.java) describes the different types of order.

| Field  | Description  |
|-------|---------|
| <code>BID</code>  | Buying order  |
| <code>ASK</code>  | Selling order  |

### OrderStatusDTO.
[OrderStatusDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/dto/trade/OrderStatusDTO.java) describes the different status of an order.

| Field  | Description  |
|-------|---------|
| <code>PENDING_NEW</code>  | Initial order when instantiated  |
| <code>NEW</code>  | Initial order when placed on the order book at exchange  |
| <code>PARTIALLY_FILLED</code>  | Partially match against opposite order on order book at exchange  |
| <code>FILLED</code>  | Fully match against opposite order on order book at exchange  |
| <code>PENDING_CANCEL</code>  | Waiting to be removed from order book at exchange  |
| <code>PARTIALLY_CANCELED</code>  | Order was partially canceled at exchange  |
| <code>CANCELED</code>  | Removed from order book at exchange  |
| <code>PENDING_REPLACE</code>  | Waiting to be replaced by another order on order book at exchange  |
| <code>REPLACED</code>  | Order has been replace by another order on order book at exchange  |
| <code>STOPPED</code>  | Order has been triggered at stop price  |
| <code>REJECTED</code>  | Order has been rejected by exchange and not place on order book  |
| <code>EXPIRED</code>  | Order has expired it's time to live or trading session and been removed from order book  |
| <code>UNKNOWN</code>  | The exchange returned a state which is not in the exchange's API documentation. The state of the order cannot be confirmed  |

## Service.

### Trade service.

[Trade service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/TradeService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/bot/src/main/java/tech/cassandre/trading/bot/service/TradeServiceXChangeImplementation.java) :

| Method  | Description  |
|-------|---------|
| <code>createBuyMarketOrder()</code>   | Creates a buy market order  |
| <code>createSellMarketOrder()</code>   | Creates a sell market order  |
| <code>createBuyLimitOrder()</code>   | Creates a buy limit order  |
| <code>createSellLimitOrder()</code>   | Creates a sell limit order  |
| <code>getOpenOrderByOrderId()</code>   | Get an open order by its id  |
| <code>getOpenOrders()</code>   | Get open orders  |
| <code>cancelOrder()</code>   | Cancel order  |

This service uses <code>org.knowm.xchange.service.trade.TradeService</code>.