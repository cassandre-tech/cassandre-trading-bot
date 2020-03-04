---
title: Architecture
sidebar: cassandre_sidebar
summary: Details cassandre trading bot architecture.
permalink: bot_development_architecture.html
---

Cassandre trading bot is provided as a Spring boot starter. Once the starter is added to your Spring boot project, it will search for a class having the <code>@Strategy</code> annotation and implementing <code>CassandreStrategy</code>.

This class will receive new available data : 
  * If there is a change on your account data, <code>onAccountUpdate()</code> will be called.
  * When a new ticker is available, <code>onTickerUpdate()</code> will be called.
  * If there is a change on your order data, <code>onOrderUpdate()</code> will be called. 
  
Inside your strategy, you can make an order by accessing the trade service with a call to <code>getTradeService()</code>.

{% include image.html file="cassandre_architecture.png" alt="Cassandre architecture" caption="Cassandre architecture" %}