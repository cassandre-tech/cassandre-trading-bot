-- =====================================================================================================================
-- Insert exchange accounts.
INSERT INTO EXCHANGE_ACCOUNTS (ID, EXCHANGE_NAME, EXCHANGE_ACCOUNT)
VALUES (1, 'kucoin', 'cassandre.crypto.bot@gmail.com');

-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (ID, STRATEGY_ID, NAME, FK_EXCHANGE_ACCOUNT_ID)
VALUES (1, '01', 'My strategy', 1);

-- =====================================================================================================================
-- Insert orders.
INSERT INTO ORDERS (ID, ORDER_ID, TYPE, AMOUNT, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS, CUMULATIVE_AMOUNT,
                    AVERAGE_PRICE, LEVERAGE, LIMIT_PRICE, FK_STRATEGY_ID)
VALUES -- Order BACKUP_ORDER_01 (useless).
       (1, 'BACKUP_ORDER_01', 'ASK', 0.000005, 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 0.000003,
        'LEVERAGE_1', 0.000001, 1),

       -- Order BACKUP_ORDER_02 (useless).
       (2, 'BACKUP_ORDER_02', 'BID', 0.000015, 'USDT/BTC', 'My reference 2', '2020-11-19', 'PENDING_NEW', 0.000014,
        0.000013, 'LEVERAGE_2', 0.000011, 1),

       -- For position 1 (OPENING).
       (3, 'BACKUP_OPENING_ORDER_01', 'BID', 10, 'BTC/USDT', '', '2020-11-20', 'NEW', 10, 1, '', 1, 1),

       -- For position 2 (OPENED).
       (4, 'BACKUP_OPENING_ORDER_02', 'BID', 20, 'BTC/USDT', '', '2020-11-20', 'FILLED', 20, 1, '', 1, 1),

       -- For position 3 (CLOSING).
       (5, 'BACKUP_OPENING_ORDER_03', 'BID', 30, 'BTC/USDT', '', '2020-11-20', 'FILLED', 30, 1, '', 1, 1),
       (6, 'BACKUP_CLOSING_ORDER_01', 'ASK', 30, 'BTC/USDT', '', '2020-11-20', 'NEW', 30, 1, 1, 1, 1),

       -- For position 4 (CLOSED).
       (7, 'BACKUP_OPENING_ORDER_04', 'BID', 40, 'BTC/USDT', '', '2020-11-20', 'FILLED', 40, 1, '', 1, 1),
       (8, 'BACKUP_CLOSING_ORDER_02', 'ASK', 40, 'BTC/USDT', '', '2020-11-20', 'FILLED', 40, 1, '', 1, 1),

       -- For position 4 (CLOSED).
       (9, 'BACKUP_OPENING_ORDER_05', 'BID', 50, 'ETH/USD', '', '2020-11-20', 'FILLED', 50, 1, '', 1, 1),
       (10, 'BACKUP_CLOSING_ORDER_03', 'ASK', 50, 'ETH/USD', '', '2020-11-20', 'FILLED', 50, 1, '', 1, 1);

-- =====================================================================================================================
-- Insert positions.
INSERT INTO POSITIONS (ID, STATUS, CURRENCY_PAIR, AMOUNT, RULES_STOP_GAIN_PERCENTAGE, RULES_STOP_LOSS_PERCENTAGE,
                       FK_OPENING_ORDER_ID, FK_CLOSING_ORDER_ID, LOWEST_PRICE, HIGHEST_PRICE, LATEST_PRICE, FK_STRATEGY_ID)
VALUES -- Position 1 : Opening, no rules, waiting for BACKUP_OPENING_ORDER_01 to arrive (but will not arrive).
       (1, 'OPENING', 'BTC/USDT', 10, null, null, 3, null, null, null, null, 1),

       -- Position 2 : Opened position, 10% gain rule.
       (2, 'OPENED', 'BTC/USDT', 20, 10, null, 4, null, 1, 2, 3, 1),

       -- Position 3 : Closing position, 20% loss rule, waiting for a not coming trade 'NON_EXISTING_TRADE'.
       (3, 'CLOSING', 'BTC/USDT', 30, null, 20, 5, 6, 17, 68, 92,
        1),

       -- Position 4 : Closed position, 30% gain & 40 % loss.
       (4, 'CLOSED', 'BTC/USDT', 40, 30, 40, 7, 8, 17, 68, 93, 1),

       -- Position 5 : closed.
       (5, 'CLOSED', 'ETH/USD', 50, 30, 40, 9, 10, 17, 68, 94, 1);

-- =====================================================================================================================
-- Insert trades.
INSERT INTO TRADES (ID, TRADE_ID, ORDER_ID, FK_ORDER_ID, TYPE, AMOUNT, CURRENCY_PAIR, PRICE, TIMESTAMP, FEE_AMOUNT, FEE_CURRENCY)
VALUES -- note : No trade for order BACKUP_OPENING_ORDER_01 - This is why position 1 has the opening status.
       -- Order BACKUP_TRADE_01 - Trade from the order buying BACKUP_OPENING_ORDER_02.
       (1, 'BACKUP_TRADE_01', 'BACKUP_OPENING_ORDER_02', 4, 'BID', 20, 'BTC/USDT', 10, '2020-08-01', 1, 'USDT'),

       -- Order BACKUP_TRADE_02 - Trade from the order buying BACKUP_OPENING_ORDER_03.
       (2, 'BACKUP_TRADE_02', 'BACKUP_OPENING_ORDER_03', 5, 'BID', 30, 'BTC/USDT', 20, '2020-08-02', 2, 'USDT'),

       -- Order BACKUP_TRADE_03 - Trade from the order buying BACKUP_OPENING_ORDER_04.
       (3, 'BACKUP_TRADE_03', 'BACKUP_OPENING_ORDER_04', 7, 'BID', 40, 'BTC/USDT', 30, '2020-08-03', 3, 'USDT'),

       -- Order BACKUP_TRADE_04 - Trade from the order selling BACKUP_OPENING_ORDER_04.
       (4, 'BACKUP_TRADE_04', 'BACKUP_CLOSING_ORDER_01', 6, 'ASK', 40, 'BTC/USDT', 40, '2020-08-04', 4, 'USDT'),

       -- Order BACKUP_TRADE_05 - Trade from the order selling BACKUP_OPENING_ORDER_05.
       (5, 'BACKUP_TRADE_05', 'BACKUP_CLOSING_ORDER_02', 8, 'ASK', 50, 'ETH/USD', 50, '2020-08-05', 5, 'USD'),

       -- For position 5.
       (6, 'BACKUP_TRADE_06', 'BACKUP_OPENING_ORDER_05', 9, 'BID', 10, 'ETH/USD', 11, '2020-08-05', 5, 'USD'),
       (7, 'BACKUP_TRADE_07', 'BACKUP_OPENING_ORDER_05', 9, 'BID', 40, 'ETH/USD', 12, '2020-08-06', 5, 'USD'),
       (8, 'BACKUP_TRADE_08', 'BACKUP_CLOSING_ORDER_03', 10, 'ASK', 15, 'ETH/USD', 13, '2020-08-07', 5, 'USD'),
       (9, 'BACKUP_TRADE_09', 'BACKUP_CLOSING_ORDER_03', 10, 'ASK', 5, 'ETH/USD', 14, '2020-08-08', 5, 'USD'),
       (10, 'BACKUP_TRADE_10', 'BACKUP_CLOSING_ORDER_03', 10, 'ASK', 30, 'ETH/USD', 15, '2020-08-09', 5, 'USD');