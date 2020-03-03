---
title: User data & service
sidebar: cassandre_sidebar
summary: Service giving information about user, accounts and balances.
permalink: bot_development_user_data_and_service.html
---

## Data.

{% include image.html file="project_development/package_user.png" alt="User package class diagram" caption="User package class diagram" %}

### The UserDTO class.
[UserDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/dto/user/UserDTO.java) represents user information retrieved from the exchange.

| Field  | Description  |
|-------|---------|
| <code>id</code>  | User ID (usually username)  |
| <code>accounts</code>   | The accounts owned by the user  |
| <code>timestamp</code>   | The moment at which the account information was retrieved  |

### The AccountDTO class.
[AccountDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/dto/user/AccountDTO.java) represents an account owned by a user.

| Field  | Description  |
|-------|---------|
| <code>id</code>   | A unique identifier for this account  |
| <code>name</code>   | A descriptive name for this account  |
| <code>balances</code>   | Represents the different balances for each currency owned by the account  |

### The BalanceDTO class.
[BalanceDTO](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/dto/user/BalanceDTO.java) represents a balance in a currency for an account.

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

## Service.

### User service.

[User service](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/UserService.java) and its [XChange implementation](https://github.com/cassandre-tech/cassandre-trading-bot/blob/development/trading-bot-spring-boot-autoconfigure/src/main/java/tech/cassandre/trading/bot/service/UserServiceXChangeImplementation.java)

| Method  | Description  |
|-------|---------|
| <code>getUser()</code>   | Retrieve user information from exchange (user, accounts and balances)  |

This service uses <code>org.knowm.xchange.service.account.UserService</code>.

{% include note.html content="If you want to manage several exchanges, you will have to run one bot instance by exchange." %}