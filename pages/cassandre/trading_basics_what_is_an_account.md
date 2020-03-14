---
title: What is an account ?
sidebar: cassandre_sidebar
permalink: trading_basics_what_is_an_account.html
---

Usually, in an exchange, you can have several accounts with different usages (Classical, trading, margin trading, future...) and then, you have a balance for each currency.

A balance is not simply the amount you have, it could be more complicated and have the following values : 

| Field  | Description  |
|-------|---------|
| <code>currency</code>   | Currency  |
| <code>total</code>   | Returns the total amount of the <code>currency</code> in this balance  |
| <code>available</code>   | Returns the amount of the <code>currency</code> in this balance that is available to trade  |
| <code>frozen</code>   | Returns the frozen amount of the <code>currency</code> in this balance that is locked in trading  |
| <code>loaned</code>   | Returns the loaned amount of the total <code>currency</code> in this balance that will be returned  |
| <code>borrowed</code>   | Returns the borrowed amount of the available <code>currency</code> in this balance that must be repaid  |
| <code>withdrawing</code>   | Returns the amount of the <code>currency</code> in this balance that is locked in withdrawal  |
| <code>depositing</code>   | Returns the amount of the <code>currency</code> in this balance that is locked in deposit  |
