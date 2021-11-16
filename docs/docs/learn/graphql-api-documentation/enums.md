# Enums

### About enums

[Enums](https://graphql.github.io/graphql-spec/June2018/#sec-Enums) represent possible sets of values for a field.

### OrderType



<table>
  <tr>
    <th>Value</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>ASK</strong></td>
    <td><p>Selling.</p></td>
  </tr>
  <tr>
    <td><strong>BID</strong></td>
    <td><p>Buying.</p></td>
  </tr>
</table>

---

### PositionStatus



<table>
  <tr>
    <th>Value</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>CLOSED</strong></td>
    <td><p>Closed - the sell order has been accepted.</p></td>
  </tr>
  <tr>
    <td><strong>CLOSING</strong></td>
    <td><p>Closing - a sell order has been made but not yet completed.</p></td>
  </tr>
  <tr>
    <td><strong>CLOSING_FAILURE</strong></td>
    <td><p>Closing failure - the sell order did not succeed.</p></td>
  </tr>
  <tr>
    <td><strong>OPENED</strong></td>
    <td><p>Opened - the buy order has been accepted.</p></td>
  </tr>
  <tr>
    <td><strong>OPENING</strong></td>
    <td><p>Opening - a position has been created, a buy order has been made but not yet completed.</p></td>
  </tr>
  <tr>
    <td><strong>OPENING_FAILURE</strong></td>
    <td><p>Opening failure - a position has been created, but the buy order did not succeed.</p></td>
  </tr>
</table>

---

### PositionType



<table>
  <tr>
    <th>Value</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>LONG</strong></td>
    <td><p>Long position is nothing but buying share and selling them later for more.</p></td>
  </tr>
  <tr>
    <td><strong>SHORT</strong></td>
    <td><p>Short position is nothing but selling share and buying back later for less.</p></td>
  </tr>
</table>

---

### StrategyType



<table>
  <tr>
    <th>Value</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>BASIC_STRATEGY</strong></td>
    <td><p>Basic strategy.</p></td>
  </tr>
  <tr>
    <td><strong>BASIC_TA4J_STRATEGY</strong></td>
    <td><p>Basic Ta4j strategy.</p></td>
  </tr>
</table>

---

### TradeType



<table>
  <tr>
    <th>Value</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><strong>ASK</strong></td>
    <td><p>Selling.</p></td>
  </tr>
  <tr>
    <td><strong>BID</strong></td>
    <td><p>Buying.</p></td>
  </tr>
</table>

---