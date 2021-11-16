# Objects

### About objects

[Objects](https://graphql.github.io/graphql-spec/June2018/#sec-Objects) in GraphQL represent the resources you can access. An object can contain a list of fields, which are specifically typed.

### Account

<p>User account.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>accountId</strong> (<a href="scalars.md#string">String!</a>)</td> 
    <td><p>A unique identifier for this account.</p></td>
  </tr>
  <tr>
    <td><strong>balances</strong> (<a href="objects.md#balance">[Balance]</a>)</td> 
    <td><p>Represents the different balances for each currency owned by the account.</p></td>
  </tr>
  <tr>
    <td><strong>name</strong> (<a href="scalars.md#string">String</a>)</td> 
    <td><p>A descriptive name for this account. Defaults has the same value than accountId.</p></td>
  </tr>
</table>

---

### Balance

<p>Balance of a user account.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>available</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Returns the amount of the currency in this balance that is available to trade.</p></td>
  </tr>
  <tr>
    <td><strong>borrowed</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Returns the borrowed amount of the available currency in this balance that must be repaid.</p></td>
  </tr>
  <tr>
    <td><strong>currency</strong> (<a href="objects.md#currency">Currency!</a>)</td> 
    <td><p>Currency.</p></td>
  </tr>
  <tr>
    <td><strong>depositing</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Returns the amount of the currency in this balance that is locked in the deposit.</p></td>
  </tr>
  <tr>
    <td><strong>frozen</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Returns the frozen amount of the currency in this balance that is locked in trading.</p></td>
  </tr>
  <tr>
    <td><strong>loaned</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Returns the loaned amount of the total currency in this balance that will be returned.</p></td>
  </tr>
  <tr>
    <td><strong>total</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Returns the total amount of the currency in this balance.</p></td>
  </tr>
  <tr>
    <td><strong>withdrawing</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Returns the amount of the currency in this balance that is locked in withdrawal.</p></td>
  </tr>
</table>

---

### Configuration

<p>Server configuration.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>apiVersion</strong> (<a href="scalars.md#string">String!</a>)</td> 
    <td><p>API Version.</p></td>
  </tr>
</table>

---

### Currency

<p>Currency.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>code</strong> (<a href="scalars.md#id">ID!</a>)</td> 
    <td><p>Code.</p></td>
  </tr>
</table>

---

### CurrencyAmount

<p>Currency amount (amount value and currency).</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>currency</strong> (<a href="objects.md#currency">Currency</a>)</td> 
    <td><p>Currency.</p></td>
  </tr>
  <tr>
    <td><strong>value</strong> (<a href="scalars.md#bigdecimal">BigDecimal</a>)</td> 
    <td><p>Amount value.</p></td>
  </tr>
</table>

---

### CurrencyPair

<p>Currency pair for trading.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>baseCurrency</strong> (<a href="objects.md#currency">Currency!</a>)</td> 
    <td><p>The base currency is the first currency appearing in a currency pair quotation.</p></td>
  </tr>
  <tr>
    <td><strong>quoteCurrency</strong> (<a href="objects.md#currency">Currency!</a>)</td> 
    <td><p>The quote currency is the second currency appearing in a currency pair quotation.</p></td>
  </tr>
</table>

---

### Gain

<p>Gain.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>amount</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>Gain made (amount).</p></td>
  </tr>
  <tr>
    <td><strong>fees</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>Fees.</p></td>
  </tr>
  <tr>
    <td><strong>percentage</strong> (<a href="scalars.md#float">Float</a>)</td> 
    <td><p>Gain made (percentage).</p></td>
  </tr>
</table>

---

### Order

<p>An order is a request by an investor to buy or sell.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>amount</strong> (<a href="objects.md#currencyamount">CurrencyAmount!</a>)</td> 
    <td><p>Amount to be ordered / amount that was ordered.</p></td>
  </tr>
  <tr>
    <td><strong>averagePrice</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>Weighted Average price of the fills in the order.</p></td>
  </tr>
  <tr>
    <td><strong>cumulativeAmount</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>Amount to be ordered / amount that has been matched against order on the order book/filled.</p></td>
  </tr>
  <tr>
    <td><strong>currencyPair</strong> (<a href="objects.md#currencypair">CurrencyPair!</a>)</td> 
    <td><p>Currency pair.</p></td>
  </tr>
  <tr>
    <td><strong>id</strong> (<a href="scalars.md#int">Int!</a>)</td> 
    <td><p>Technical ID.</p></td>
  </tr>
  <tr>
    <td><strong>leverage</strong> (<a href="scalars.md#string">String</a>)</td> 
    <td><p>The leverage to use for margin related to this order.</p></td>
  </tr>
  <tr>
    <td><strong>limitPrice</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>Limit price.</p></td>
  </tr>
  <tr>
    <td><strong>marketPrice</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>Market price - The price Cassandre had when the order was created.</p></td>
  </tr>
  <tr>
    <td><strong>orderId</strong> (<a href="scalars.md#string">String!</a>)</td> 
    <td><p>An identifier set by the exchange that uniquely identifies the order.</p></td>
  </tr>
  <tr>
    <td><strong>status</strong> (<a href="scalars.md#string">String</a>)</td> 
    <td><p>Order status.</p></td>
  </tr>
  <tr>
    <td><strong>strategy</strong> (<a href="objects.md#strategy">Strategy!</a>)</td> 
    <td><p>The strategy that created the order.</p></td>
  </tr>
  <tr>
    <td><strong>timestamp</strong> (<a href="scalars.md#datetime">DateTime</a>)</td> 
    <td><p>The timestamp of the order.</p></td>
  </tr>
  <tr>
    <td><strong>trades</strong> (<a href="objects.md#trade">[Trade]</a>)</td> 
    <td><p>All trades related to order.</p></td>
  </tr>
  <tr>
    <td><strong>type</strong> (<a href="enums.md#ordertype">OrderType!</a>)</td> 
    <td><p>Order type i.e. bid (buy) or ask (sell).</p></td>
  </tr>
  <tr>
    <td><strong>userReference</strong> (<a href="scalars.md#string">String</a>)</td> 
    <td><p>An identifier provided by the user on placement that uniquely identifies the order.</p></td>
  </tr>
</table>

---

### Position

<p>A position is the amount of a security, commodity or currency which is owned by an individual, dealer, institution, or other fiscal entity.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>amount</strong> (<a href="objects.md#currencyamount">CurrencyAmount!</a>)</td> 
    <td><p>Position amount.</p></td>
  </tr>
  <tr>
    <td><strong>closingOrder</strong> (<a href="objects.md#order">Order</a>)</td> 
    <td><p>The order created to close the position.</p></td>
  </tr>
  <tr>
    <td><strong>currencyPair</strong> (<a href="objects.md#currencypair">CurrencyPair!</a>)</td> 
    <td><p>Currency pair.</p></td>
  </tr>
  <tr>
    <td><strong>forceClosing</strong> (<a href="scalars.md#boolean">Boolean!</a>)</td> 
    <td><p>Indicates that the position must be closed no matter the rules.</p></td>
  </tr>
  <tr>
    <td><strong>gain</strong> (<a href="objects.md#gain">Gain</a>)</td> 
    <td><p>If closed, returns the gain made.</p></td>
  </tr>
  <tr>
    <td><strong>highestCalculatedGain</strong> (<a href="objects.md#gain">Gain</a>)</td> 
    <td><p>Highest calculated gain.</p></td>
  </tr>
  <tr>
    <td><strong>id</strong> (<a href="scalars.md#int">Int!</a>)</td> 
    <td><p>Technical ID.</p></td>
  </tr>
  <tr>
    <td><strong>latestCalculatedGain</strong> (<a href="objects.md#gain">Gain</a>)</td> 
    <td><p>Latest calculated gain.</p></td>
  </tr>
  <tr>
    <td><strong>lowestCalculatedGain</strong> (<a href="objects.md#gain">Gain</a>)</td> 
    <td><p>Lowest calculated gain.</p></td>
  </tr>
  <tr>
    <td><strong>openingOrder</strong> (<a href="objects.md#order">Order!</a>)</td> 
    <td><p>The order created to open the position.</p></td>
  </tr>
  <tr>
    <td><strong>positionId</strong> (<a href="scalars.md#int">Int!</a>)</td> 
    <td><p>An identifier that uniquely identifies the position.</p></td>
  </tr>
  <tr>
    <td><strong>rules</strong> (<a href="objects.md#positionrules">PositionRules!</a>)</td> 
    <td><p>Position rules.</p></td>
  </tr>
  <tr>
    <td><strong>status</strong> (<a href="enums.md#positionstatus">PositionStatus</a>)</td> 
    <td><p>Position status.</p></td>
  </tr>
  <tr>
    <td><strong>strategy</strong> (<a href="objects.md#strategy">Strategy!</a>)</td> 
    <td><p>The strategy that created the position.</p></td>
  </tr>
  <tr>
    <td><strong>type</strong> (<a href="enums.md#positiontype">PositionType!</a>)</td> 
    <td><p>Position type (Long or Short).</p></td>
  </tr>
</table>

---

### PositionRules

<p>Position rules is used to know when cassandre should close a position.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>stopGainPercentage</strong> (<a href="scalars.md#float">Float</a>)</td> 
    <td><p>Stop gain percentage.</p></td>
  </tr>
  <tr>
    <td><strong>stopLossPercentage</strong> (<a href="scalars.md#float">Float</a>)</td> 
    <td><p>Stop loss percentage.</p></td>
  </tr>
</table>

---

### Strategy

<p>Strategies.</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>id</strong> (<a href="scalars.md#int">Int!</a>)</td> 
    <td><p>Technical ID.</p></td>
  </tr>
  <tr>
    <td><strong>name</strong> (<a href="scalars.md#string">String</a>)</td> 
    <td><p>Strategy name - Comes from the Java annotation.</p></td>
  </tr>
  <tr>
    <td><strong>strategyId</strong> (<a href="scalars.md#string">String!</a>)</td> 
    <td><p>An identifier that uniquely identifies the strategy - Comes from the Java annotation.</p></td>
  </tr>
  <tr>
    <td><strong>type</strong> (<a href="enums.md#strategytype">StrategyType!</a>)</td> 
    <td><p>Strategy type - Basic or Ta4j.</p></td>
  </tr>
</table>

---

### Trade

<p>A trade is the action of buying and selling (linked to an order).</p>  

#### Fields

<table>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>amount</strong> (<a href="objects.md#currencyamount">CurrencyAmount!</a>)</td> 
    <td><p>Amount to be ordered / amount that was ordered.</p></td>
  </tr>
  <tr>
    <td><strong>currencyPair</strong> (<a href="objects.md#currencypair">CurrencyPair!</a>)</td> 
    <td><p>Currency pair.</p></td>
  </tr>
  <tr>
    <td><strong>fee</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>The fee that was charged by the exchange for this trade.</p></td>
  </tr>
  <tr>
    <td><strong>id</strong> (<a href="scalars.md#int">Int!</a>)</td> 
    <td><p>Technical ID.</p></td>
  </tr>
  <tr>
    <td><strong>order</strong> (<a href="objects.md#order">Order</a>)</td> 
    <td><p>The order responsible for this trade.</p></td>
  </tr>
  <tr>
    <td><strong>orderId</strong> (<a href="scalars.md#string">String!</a>)</td> 
    <td><p>The order id of the order responsible for this trade.</p></td>
  </tr>
  <tr>
    <td><strong>price</strong> (<a href="objects.md#currencyamount">CurrencyAmount</a>)</td> 
    <td><p>The price.</p></td>
  </tr>
  <tr>
    <td><strong>timestamp</strong> (<a href="scalars.md#datetime">DateTime</a>)</td> 
    <td><p>The timestamp of the trade.</p></td>
  </tr>
  <tr>
    <td><strong>tradeId</strong> (<a href="scalars.md#string">String!</a>)</td> 
    <td><p>An identifier set by the exchange that uniquely identifies the trade.</p></td>
  </tr>
  <tr>
    <td><strong>type</strong> (<a href="enums.md#tradetype">TradeType!</a>)</td> 
    <td><p>Trade type i.e. bid (buy) or ask (sell).</p></td>
  </tr>
  <tr>
    <td><strong>userReference</strong> (<a href="scalars.md#string">String</a>)</td> 
    <td><p>An identifier provided by the user on placement that uniquely identifies the order.</p></td>
  </tr>
</table>

---