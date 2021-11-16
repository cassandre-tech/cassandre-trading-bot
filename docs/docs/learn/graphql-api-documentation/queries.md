# Queries

### About queries



### accountByAccountId

#### Type: [Account](objects.md#account)

Returns the account with the corresponding id. 

#### Arguments

| Name | Description |
|------|-------------|
| accountId ([String](scalars.md#string)) |  |

---

### accounts

#### Type: [[Account]](objects.md#account)

Retours all the accounts. 

---

### configuration

#### Type: [Configuration](objects.md#configuration)

Returns configuration. 

---

### order

#### Type: [Order](objects.md#order)

Returns the order with the corresponding id. 

#### Arguments

| Name | Description |
|------|-------------|
| id ([Int](scalars.md#int)) |  |

---

### orderByOrderId

#### Type: [Order](objects.md#order)

Returns the order with the corresponding orderId. 

#### Arguments

| Name | Description |
|------|-------------|
| orderId ([String](scalars.md#string)) |  |

---

### orders

#### Type: [[Order]](objects.md#order)

Returns all the orders. 

---

### position

#### Type: [Position](objects.md#position)

Returns the position with the corresponding id. 

#### Arguments

| Name | Description |
|------|-------------|
| id ([Int](scalars.md#int)) |  |

---

### positions

#### Type: [[Position]](objects.md#position)

Returns all the positions 

---

### positionsByStrategy

#### Type: [[Position]](objects.md#position)

Returns all the positions of the specified strategy. 

#### Arguments

| Name | Description |
|------|-------------|
| id ([Int](scalars.md#int)) |  |

---

### positionsByStrategyAndStatus

#### Type: [[Position]](objects.md#position)

Returns all the positions of the specified strategy and a certain status. 

#### Arguments

| Name | Description |
|------|-------------|
| id ([Int](scalars.md#int)) |  |
| status ([PositionStatus](enums.md#positionstatus)) |  |

---

### positionsByStrategyId

#### Type: [[Position]](objects.md#position)

Returns all the positions of the specified strategy. 

#### Arguments

| Name | Description |
|------|-------------|
| strategyId ([String](scalars.md#string)) |  |

---

### positionsByStrategyIdAndStatus

#### Type: [[Position]](objects.md#position)

Returns all the positions of the specified strategy and a certain status. 

#### Arguments

| Name | Description |
|------|-------------|
| strategyId ([String](scalars.md#string)) |  |
| status ([PositionStatus](enums.md#positionstatus)) |  |

---

### strategies

#### Type: [[Strategy]](objects.md#strategy)

Returns all the strategies. 

---

### strategy

#### Type: [Strategy](objects.md#strategy)

Returns the strategy with the corresponding id. 

#### Arguments

| Name | Description |
|------|-------------|
| id ([Int](scalars.md#int)) |  |

---

### strategyByStrategyId

#### Type: [Strategy](objects.md#strategy)

Returns the strategy with the corresponding strategyId. 

#### Arguments

| Name | Description |
|------|-------------|
| strategyId ([String](scalars.md#string)) |  |

---

### trade

#### Type: [Trade](objects.md#trade)

Returns the trade with the corresponding id. 

#### Arguments

| Name | Description |
|------|-------------|
| id ([Int](scalars.md#int)) |  |

---

### tradeByTradeId

#### Type: [Trade](objects.md#trade)

Returns the trade with the corresponding tradeId. 

#### Arguments

| Name | Description |
|------|-------------|
| tradeId ([String](scalars.md#string)) |  |

---

### trades

#### Type: [[Trade]](objects.md#trade)

Returns all the trades. 

---