-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (ID, STRATEGY_ID, TYPE, NAME)
VALUES (1, '01', 'BASIC_STRATEGY', 'My strategy');

-- =====================================================================================================================
-- Insert orders.
INSERT INTO ORDERS (ID, ORDER_ID, TYPE, AMOUNT_VALUE, AMOUNT_CURRENCY, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS,
                    CUMULATIVE_AMOUNT_VALUE, CUMULATIVE_AMOUNT_CURRENCY, AVERAGE_PRICE_VALUE, AVERAGE_PRICE_CURRENCY,
                    LEVERAGE, LIMIT_PRICE_VALUE, LIMIT_PRICE_CURRENCY, MARKET_PRICE_VALUE, MARKET_PRICE_CURRENCY, FK_STRATEGY_ID)
VALUES -- Order BACKUP_ORDER_01 (useless).
       (1, 'BACKUP_ORDER_01', 'ASK', 0.000005, 'ETH', 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 'ETH',
        0.000003, 'BTC', 'LEVERAGE_1', 0.000001, 'BTC', 0.000033, 'KCS', 1),

       -- Order BACKUP_ORDER_02 (useless).
       (2, 'BACKUP_ORDER_02', 'BID', 0.000015, 'USDT', 'USDT/BTC', 'My reference 2', '2020-11-19', 'PENDING_NEW',
        0.000014, 'USDT', 0.000013, 'BTC', 'LEVERAGE_2', 0.000011, 'BTC', 0.000003, 'BTC', 1),

       -- For position 1 (OPENING).
       (3, 'BACKUP_OPENING_ORDER_01', 'BID', 10, 'BTC', 'BTC/USDT', '', '2020-11-20', 'NEW', 10, 'BTC', 1, 'USDT', '',
        1, 'USDT', 0.000003, 'BTC', 1),

       -- For position 2 (OPENED).
       (4, 'BACKUP_OPENING_ORDER_02', 'BID', 20, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 20, 'BTC', 1, 'USDT',
        '', 1, 'USDT', 0.000003, 'BTC', 1),

       -- For position 3 (CLOSING).
       (5, 'BACKUP_OPENING_ORDER_03', 'BID', 30, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 30, 'BTC', 1, 'USDT',
        '', 1, 'USDT', 0.000003, 'BTC', 1),
       (6, 'BACKUP_CLOSING_ORDER_01', 'ASK', 30, 'BTC', 'BTC/USDT', '', '2020-11-20', 'NEW', 30, 'BTC', 1, 'USDT', 1, 1,
        'USDT', 0.000003, 'BTC', 1),

       -- For position 4 (CLOSED).
       (7, 'BACKUP_OPENING_ORDER_04', 'BID', 40, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 40, 'BTC', 1, 'USDT',
        '', 1, 'USDT', 0.000003, 'BTC', 1),
       (8, 'BACKUP_CLOSING_ORDER_02', 'ASK', 40, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 40, 'BTC', 1, 'USDT',
        '', 1, 'USDT', 0.000003, 'BTC', 1),

       -- For position 4 (CLOSED).
       (9, 'BACKUP_OPENING_ORDER_05', 'BID', 50, 'ETH', 'ETH/USD', '', '2020-11-20', 'FILLED', 50, 'ETH', 1, 'ETH', '',
        1, 'USDT', 0.000003, 'BTC', 1),
       (10, 'BACKUP_CLOSING_ORDER_03', 'ASK', 50, 'ETH', 'ETH/USD', '', '2020-11-20', 'FILLED', 50, 'ETH', 1, 'ETH', '',
        1, 'USDT', 0.000003, 'BTC', 1);

-- =====================================================================================================================
-- Insert positions.
INSERT INTO POSITIONS (ID, POSITION_ID, TYPE, STATUS, CURRENCY_PAIR, AMOUNT_VALUE, AMOUNT_CURRENCY,
                       RULES_STOP_GAIN_PERCENTAGE, RULES_STOP_LOSS_PERCENTAGE, FK_OPENING_ORDER_ID,
                       FK_CLOSING_ORDER_ID, LOWEST_GAIN_PRICE_VALUE, LOWEST_GAIN_PRICE_CURRENCY,
                       HIGHEST_GAIN_PRICE_VALUE, HIGHEST_GAIN_PRICE_CURRENCY, LATEST_GAIN_PRICE_VALUE,
                       LATEST_GAIN_PRICE_CURRENCY, FK_STRATEGY_ID)
VALUES -- Position 1 : Opening, no rules, waiting for BACKUP_OPENING_ORDER_01 to arrive (but will not arrive).
       (1, 1, 'LONG', 'OPENING', 'BTC/USDT', 10, 'BTC', null, null, 3, null, null, null, null, null, null, null, 1),

       -- Position 2 : Opened position, 10% gain rule.
       (2, 2, 'LONG', 'OPENED', 'BTC/USDT', 20, 'BTC', 10, null, 4, null, 1, 'USDT', 2, 'USDT', 3, 'USDT', 1),

       -- Position 3 : Closing position, 20% loss rule, waiting for a not coming trade 'NON_EXISTING_TRADE'.
       (3, 3, 'LONG', 'CLOSING', 'BTC/USDT', 30, 'BTC', null, 20, 5, 6, 17, 'USDT', 68, 'USDT', 92, 'USDT', 1),

       -- Position 4 : Closed position, 30% gain & 40 % loss.
       (4, 4, 'LONG', 'CLOSED', 'BTC/USDT', 40, 'BTC', 30, 40, 7, 8, 17, 'USDT', 68, 'USDT', 93, 'USDT', 1),

       -- Position 5 : closed.
       (5, 5, 'LONG', 'CLOSED', 'ETH/USD', 50, 'ETH', 30, 40, 9, 10, 17, 'USD', 68, 'USD', 94, 'USD', 1);

-- =====================================================================================================================
-- Insert trades.
INSERT INTO TRADES (ID, TRADE_ID, FK_ORDER_ID, TYPE, AMOUNT_VALUE, AMOUNT_CURRENCY, CURRENCY_PAIR,
                    PRICE_VALUE, PRICE_CURRENCY, TIMESTAMP, FEE_VALUE, FEE_CURRENCY, USER_REFERENCE)
VALUES -- note : No trade for order BACKUP_OPENING_ORDER_01 - This is why position 1 has the opening status.
       -- Order BACKUP_TRADE_01 - Trade from the order buying BACKUP_OPENING_ORDER_02.
       (1, 'BACKUP_TRADE_01', 4, 'BID', 20, 'BTC', 'BTC/USDT', 10, 'USDT', '2020-08-01', 1, 'USDT', 'Trade 01'),

       -- Order BACKUP_TRADE_02 - Trade from the order buying BACKUP_OPENING_ORDER_03.
       (2, 'BACKUP_TRADE_02', 5, 'BID', 20, 'BTC', 'BTC/USDT', 20, 'USDT', '2020-08-02', 2, 'USDT', 'Trade 02'),

       -- Order BACKUP_TRADE_03 - Trade from the order buying BACKUP_OPENING_ORDER_04.
       (3, 'BACKUP_TRADE_03', 7, 'BID', 40, 'BTC', 'BTC/USDT', 30, 'USDT', '2020-08-03', 3, 'USDT', 'Trade 03'),

       -- Order BACKUP_TRADE_04 - Trade from the order selling BACKUP_OPENING_ORDER_04.
       (4, 'BACKUP_TRADE_04', 6, 'ASK', 20, 'BTC', 'BTC/USDT', 40, 'USDT', '2020-08-04', 4, 'USDT', 'Trade 04'),

       -- Order BACKUP_TRADE_05 - Trade from the order selling BACKUP_OPENING_ORDER_05.
       (5, 'BACKUP_TRADE_05', 8, 'ASK', 40, 'ETH', 'ETH/USD', 40, 'USD', '2020-08-05', 5, 'USD', 'Trade 05'),

       -- For position 5.
       (6, 'BACKUP_TRADE_06', 9, 'BID', 10, 'ETH', 'ETH/USD', 11, 'USD', '2020-08-05', 5, 'USD', 'Trade 06'),
       (7, 'BACKUP_TRADE_07', 9, 'BID', 40, 'ETH', 'ETH/USD', 12, 'USD', '2020-08-06', 5, 'USD', 'Trade 07'),
       (8, 'BACKUP_TRADE_08', 10, 'ASK', 15, 'ETH', 'ETH/USD', 13, 'USD', '2020-08-07', 5, 'USD', 'Trade 08'),
       (9, 'BACKUP_TRADE_09', 10, 'ASK', 5, 'ETH', 'ETH/USD', 14, 'USD', '2020-08-08', 5, 'USD', 'Trade 09'),
       (10, 'BACKUP_TRADE_10', 10, 'ASK', 30, 'ETH', 'ETH/USD', 15, 'USD', '2020-08-09', 5, 'USD', 'Trade 10');