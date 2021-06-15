-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (ID, STRATEGY_ID, TYPE, NAME)
VALUES (1, '01', 'BASIC_STRATEGY', 'My strategy');

-- =====================================================================================================================
-- Insert orders.
INSERT INTO ORDERS (ID, ORDER_ID, TYPE, AMOUNT_VALUE, AMOUNT_CURRENCY, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS,
                    CUMULATIVE_AMOUNT_VALUE, CUMULATIVE_AMOUNT_CURRENCY, AVERAGE_PRICE_VALUE, AVERAGE_PRICE_CURRENCY,
                    LEVERAGE, LIMIT_PRICE_VALUE, LIMIT_PRICE_CURRENCY, FK_STRATEGY_ID)
values -- For position 1.
       (1, 'OPEN_ORDER_01', 'BID', 10, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       (2, 'CLOSE_ORDER_01', 'ASK', 10, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       -- For position 2.
       (3, 'OPEN_ORDER_02', 'BID', 20, 'ETH', 'ETH/BTC', '', '2020-11-20', 'FILLED', 10, 'ETH', 1, 'USDT', '', 1,
        'USDT', 1),
       (4, 'CLOSE_ORDER_02', 'ASK', 20, 'ETH', 'ETH/BTC', '', '2020-11-20', 'FILLED', 10, 'ETH', 1, 'USDT', '', 1,
        'USDT', 1),
       -- For position 3.
       (5, 'OPEN_ORDER_03', 'BID', 30, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       (6, 'CLOSE_ORDER_03', 'ASK', 30, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       -- For position 4.
       (7, 'OPEN_ORDER_04', 'BID', 50, 'BTC', 'BTC/USDT', '', '2020-11-20', 'NEW', 10, 'BTC', 1, 'USDT', '', 1, 'USDT',
        1),
       (8, 'CLOSE_ORDER_04', 'ASK', 50, 'BTC', 'BTC/USDT', '', '2020-11-20', 'NEW', 10, 'BTC', 1, 'USDT', '', 1, 'USDT',
        1),
       -- For position 5.
       (9, 'OPEN_ORDER_05', 'BID', 50, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       (10, 'CLOSE_ORDER_05', 'ASK', 50, 'BTC', 'BTC/USDT', '', '2020-11-20', 'NEW', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       -- For position 6.
       (11, 'OPEN_ORDER_06', 'BID', 50, 'BTC', 'BTC/USDT', '', '2020-11-20', 'FILLED', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       (12, 'CLOSE_ORDER_06', 'ASK', 50, 'BTC', 'BTC/USDT', '', '2020-11-20', 'NEW', 10, 'BTC', 1, 'USDT', '', 1,
        'USDT', 1),
       -- For position 7 (Short position).
       (13, 'OPEN_ORDER_07', 'ASK', 10, 'ETH', 'ETH/USDT', '', '2020-11-20', 'NEW', 10, 'USDT', 1, 'USDT', '', null,
        null, 1),
       (14, 'CLOSE_ORDER_07', 'BID', 5, 'ETH', 'ETH/USDT', '', '2020-11-20', 'NEW', 10, 'USDT', 1, 'USDT', '', null,
        null, 1);

-- =====================================================================================================================
-- Insert positions.
INSERT INTO POSITIONS (ID, POSITION_ID, TYPE, STATUS, CURRENCY_PAIR, AMOUNT_VALUE, AMOUNT_CURRENCY,
                       RULES_STOP_GAIN_PERCENTAGE, RULES_STOP_LOSS_PERCENTAGE, FK_OPENING_ORDER_ID, FK_CLOSING_ORDER_ID,
                       LOWEST_GAIN_PRICE_VALUE, HIGHEST_GAIN_PRICE_VALUE, LATEST_GAIN_PRICE_VALUE,
                       LOWEST_GAIN_PRICE_CURRENCY, HIGHEST_GAIN_PRICE_CURRENCY, LATEST_GAIN_PRICE_CURRENCY, FK_STRATEGY_ID)
VALUES (1, 1, 'LONG', 'CLOSED', 'BTC/USDT', 10, 'BTC', null, null, 1, 2, null, null, null, null, null, null, 1),
       (2, 2, 'LONG', 'CLOSED', 'ETH/BTC', 20, 'ETH', null, null, 3, 4, null, null, null, null, null, null, 1),
       (3, 3, 'LONG', 'CLOSED', 'BTC/USDT', 30, 'BTC', null, null, 5, 6, null, null, null, null, null, null, 1),
       (4, 4, 'LONG', 'OPENING', 'BTC/USDT', 50, 'BTC', null, null, 7, null, null, null, null, null, null, null, 1),
       (5, 5, 'LONG', 'OPENED', 'BTC/USDT', 50, 'BTC', null, null, 9, null, null, null, null, null, null, null, 1),
       (6, 6, 'LONG', 'CLOSING', 'BTC/USDT', 50, 'BTC', null, null, 11, 12, null, null, null, null, null, null, 1),
       (7, 7, 'SHORT', 'CLOSED', 'ETH/USDT', 10, 'ETH', null, null, 13, 14, null, null, null, null, null, null, 1);

-- =====================================================================================================================
-- Insert trades.
INSERT INTO TRADES (ID, TRADE_ID, FK_ORDER_ID, TYPE, AMOUNT_VALUE, AMOUNT_CURRENCY, CURRENCY_PAIR,
                    PRICE_VALUE, PRICE_CURRENCY, TIMESTAMP, FEE_VALUE, FEE_CURRENCY)
values -- For position 1.
       (1, 'TRADE_11', 1, 'BID', 7, 'BTC', 'BTC/USDT', 11, 'USDT', DATE '2020-08-05', 1, 'USDT'),
       (2, 'TRADE_12', 1, 'BID', 3, 'BTC', 'BTC/USDT', 12, 'USDT', DATE '2020-08-06', 2, 'USDT'),
       (3, 'TRADE_13', 2, 'ASK', 1, 'BTC', 'BTC/USDT', 13, 'USDT', DATE '2020-08-07', 3, 'USDT'),
       (4, 'TRADE_14', 2, 'ASK', 1, 'BTC', 'BTC/USDT', 14, 'USDT', DATE '2020-08-08', 4, 'USDT'),
       (5, 'TRADE_15', 2, 'ASK', 8, 'BTC', 'BTC/USDT', 15, 'USDT', DATE '2020-08-09', 5, 'USDT'),
       -- For position 2.
       (6, 'TRADE_21', 3, 'BID', 20, 'ETH', 'ETH/BTC', 100, 'USDT', DATE '2020-08-05', 5, 'BTC'),
       (7, 'TRADE_22', 4, 'ASK', 20, 'ETH', 'ETH/BTC', 50, 'USDT', DATE '2020-08-06', 5, 'BTC'),
       -- For position 3.
       (8, 'TRADE_31', 5, 'BID', 30, 'BTC', 'BTC/USDT', 20, 'USDT', DATE '2020-08-05', 6, 'USDT'),
       (9, 'TRADE_32', 6, 'ASK', 30, 'BTC', 'BTC/USDT', 25, 'USDT', DATE '2020-08-06', 5, 'USDT'),
       -- For position 4.
       (10, 'TRADE_41', 7, 'BID', 50, 'BTC', 'BTC/USDT', 20, 'USDT', DATE '2020-08-05', 6, 'USDT'),
       (11, 'TRADE_42', 8, 'ASK', 50, 'BTC', 'BTC/USDT', 25, 'USDT', DATE '2020-08-06', 5, 'USDT'),
       -- For position 5.
       (12, 'TRADE_51', 9, 'BID', 50, 'BTC', 'BTC/USDT', 20, 'USDT', DATE '2020-08-05', 6, 'USDT'),
       (13, 'TRADE_52', 10, 'ASK', 50, 'BTC', 'BTC/USDT', 25, 'USDT', DATE '2020-08-06', 5, 'USDT'),
       -- For position 6.
       (14, 'TRADE_61', 11, 'BID', 50, 'BTC', 'BTC/USDT', 20, 'USDT', DATE '2020-08-05', 6, 'USDT'),
       (15, 'TRADE_62', 12, 'ASK', 40, 'BTC', 'BTC/USDT', 25, 'USDT', DATE '2020-08-06', 5, 'USDT'),
       -- For position 7.
       (16, 'TRADE_63', 13, 'ASK', 10, 'ETH', 'ETH/USDT', 5, 'USDT', DATE '2020-08-05', 1, 'ETH'),
       (17, 'TRADE_64', 14, 'BID', 5, 'ETH', 'ETH/USDT', 10, 'USDT', DATE '2020-08-06', 3, 'ETH');
