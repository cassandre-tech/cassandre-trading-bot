-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (ID, NAME)
VALUES ('001', 'My strategy');

-- =====================================================================================================================
-- Insert orders.
INSERT INTO ORDERS (ID, TYPE, AMOUNT, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS, CUMULATIVE_AMOUNT,
                    AVERAGE_PRICE, LEVERAGE, LIMIT_PRICE, STRATEGY_ID)
VALUES -- Order BACKUP_ORDER_01 (useless).
       ('BACKUP_ORDER_01', 'ASK', 0.000005, 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 0.000003,
        'LEVERAGE_1', 0.000001, '001'),

       -- Order BACKUP_ORDER_02 (useless).
       ('BACKUP_ORDER_02', 'BID', 0.000015, 'USDT/BTC', 'My reference 2', '2020-11-19', 'PENDING_NEW', 0.000014,
        0.000013, 'LEVERAGE_2', 0.000011, '001'),

       -- For position 1 (OPENING).
       ('BACKUP_OPENING_ORDER_01', 'BID', 10, 'BTC/USDT', '', '2020-11-20', 'NEW', 10, 1, '', 1, '001'),

       -- For position 2 (OPENED).
       ('BACKUP_OPENING_ORDER_02', 'BID', 20, 'BTC/USDT', '', '2020-11-20', 'FILLED', 20, 1, '', 1, '001'),

       -- For position 3 (CLOSING).
       ('BACKUP_OPENING_ORDER_03', 'BID', 30, 'BTC/USDT', '', '2020-11-20', 'FILLED', 30, 1, '', 1, '001'),
       ('BACKUP_CLOSING_ORDER_01', 'ASK', 30, 'BTC/USDT', '', '2020-11-20', 'NEW', 30, 1, 1, 1, '001'),

       -- For position 4 (CLOSED).
       ('BACKUP_OPENING_ORDER_04', 'BID', 40, 'BTC/USDT', '', '2020-11-20', 'FILLED', 40, 1, '', 1, '001'),
       ('BACKUP_CLOSING_ORDER_02', 'ASK', 40, 'BTC/USDT', '', '2020-11-20', 'FILLED', 40, 1, '', 1, '001'),

       -- For position 4 (CLOSED).
       ('BACKUP_OPENING_ORDER_05', 'BID', 50, 'ETH/USD', '', '2020-11-20', 'FILLED', 50, 1, '', 1, '001'),
       ('BACKUP_CLOSING_ORDER_03', 'ASK', 50, 'ETH/USD', '', '2020-11-20', 'FILLED', 50, 1, '', 1, '001');

-- =====================================================================================================================
-- Insert positions.
INSERT INTO POSITIONS (ID, STATUS, CURRENCY_PAIR, AMOUNT, RULES_STOP_GAIN_PERCENTAGE, RULES_STOP_LOSS_PERCENTAGE,
                       OPENING_ORDER_ID, CLOSING_ORDER_ID, LOWEST_PRICE, HIGHEST_PRICE, LATEST_PRICE, STRATEGY_ID)
VALUES -- Position 1 : Opening, no rules, waiting for BACKUP_OPENING_ORDER_01 to arrive (but will not arrive).
       (1, 'OPENING', 'BTC/USDT', 10, null, null, 'BACKUP_OPENING_ORDER_01', null, null, null, null, '001'),

       -- Position 2 : Opened position, 10% gain rule.
       (2, 'OPENED', 'BTC/USDT', 20, 10, null, 'BACKUP_OPENING_ORDER_02', null, 1, 2, 3, '001'),

       -- Position 3 : Closing position, 20% loss rule, waiting for a not coming trade 'NON_EXISTING_TRADE'.
       (3, 'CLOSING', 'BTC/USDT', 30, null, 20, 'BACKUP_OPENING_ORDER_03', 'BACKUP_CLOSING_ORDER_01', 17, 68, 92,
        '001'),

       -- Position 4 : Closed position, 30% gain & 40 % loss.
       (4, 'CLOSED', 'BTC/USDT', 40, 30, 40, 'BACKUP_OPENING_ORDER_04', 'BACKUP_CLOSING_ORDER_02', 17, 68, 93, '001'),

       -- Position 5 : closed.
       (5, 'CLOSED', 'ETH/USD', 50, 30, 40, 'BACKUP_OPENING_ORDER_05', 'BACKUP_CLOSING_ORDER_03', 17, 68, 94, '001');

-- =====================================================================================================================
-- Insert trades.
INSERT INTO TRADES (ID, ORDER_ID, TYPE, AMOUNT, CURRENCY_PAIR, PRICE, TIMESTAMP, FEE_AMOUNT, FEE_CURRENCY)
VALUES -- note : No trade for order BACKUP_OPENING_ORDER_01 - This is why position 1 has the opening status.
       -- Order BACKUP_TRADE_01 - Trade from the order buying BACKUP_OPENING_ORDER_02.
       ('BACKUP_TRADE_01', 'BACKUP_OPENING_ORDER_02', 'BID', 20, 'BTC/USDT', 10, '2020-08-01', 1, 'USDT'),

       -- Order BACKUP_TRADE_02 - Trade from the order buying BACKUP_OPENING_ORDER_03.
       ('BACKUP_TRADE_02', 'BACKUP_OPENING_ORDER_03', 'BID', 30, 'BTC/USDT', 20, '2020-08-02', 2, 'USDT'),

       -- Order BACKUP_TRADE_03 - Trade from the order buying BACKUP_OPENING_ORDER_04.
       ('BACKUP_TRADE_03', 'BACKUP_OPENING_ORDER_04', 'BID', 40, 'BTC/USDT', 30, '2020-08-03', 3, 'USDT'),

       -- Order BACKUP_TRADE_04 - Trade from the order selling BACKUP_OPENING_ORDER_04.
       ('BACKUP_TRADE_04', 'BACKUP_CLOSING_ORDER_01', 'ASK', 40, 'BTC/USDT', 40, '2020-08-04', 4, 'USDT'),

       -- Order BACKUP_TRADE_05 - Trade from the order selling BACKUP_OPENING_ORDER_05.
       ('BACKUP_TRADE_05', 'BACKUP_CLOSING_ORDER_02', 'ASK', 50, 'ETH/USD', 50, '2020-08-05', 5, 'USD'),

       -- For position 5.
       ('BACKUP_TRADE_06', 'BACKUP_OPENING_ORDER_05', 'BID', 10, 'ETH/USD', 11, '2020-08-05', 5, 'USD'),
       ('BACKUP_TRADE_07', 'BACKUP_OPENING_ORDER_05', 'BID', 40, 'ETH/USD', 12, '2020-08-06', 5, 'USD'),
       ('BACKUP_TRADE_08', 'BACKUP_CLOSING_ORDER_03', 'ASK', 15, 'ETH/USD', 13, '2020-08-07', 5, 'USD'),
       ('BACKUP_TRADE_09', 'BACKUP_CLOSING_ORDER_03', 'ASK', 5, 'ETH/USD', 14, '2020-08-08', 5, 'USD'),
       ('BACKUP_TRADE_10', 'BACKUP_CLOSING_ORDER_03', 'ASK', 30, 'ETH/USD', 15, '2020-08-09', 5, 'USD');