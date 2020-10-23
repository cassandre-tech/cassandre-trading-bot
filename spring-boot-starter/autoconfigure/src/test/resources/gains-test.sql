-- =====================================================================================================================
-- Insert positions.
INSERT INTO MY_STRATEGY_POSITIONS (ID, STATUS, CURRENCY_PAIR, AMOUNT, RULES_STOP_GAIN_PERCENTAGE,
                                   RULES_STOP_LOSS_PERCENTAGE, OPEN_ORDER_ID, CLOSE_ORDER_ID, LOWEST_PRICE,
                                   HIGHEST_PRICE)
VALUES  (1, 'CLOSED', 'BTC/USDT', 10, null, null, 'OPEN_ORDER_01', 'CLOSE_ORDER_01', null, null),
        (2, 'CLOSED', 'ETH/BTC', 20, null, null, 'OPEN_ORDER_02', 'CLOSE_ORDER_02', null, null),
        (3, 'CLOSED', 'BTC/USDT', 30, null, null, 'OPEN_ORDER_03', 'CLOSE_ORDER_03', null, null),
        (4, 'OPENING', 'BTC/USDT', 50, null, null, 'OPEN_ORDER_04', 'CLOSE_ORDER_04', null, null),
        (5, 'OPENED', 'BTC/USDT', 50, null, null, 'OPEN_ORDER_05', 'CLOSE_ORDER_05', null, null),
        (6, 'CLOSING', 'BTC/USDT', 50, null, null, 'OPEN_ORDER_06', 'CLOSE_ORDER_06', null, null);

-- =====================================================================================================================
-- Insert trades.
INSERT INTO MY_STRATEGY_TRADES (ID, ORDER_ID, ORDER_TYPE, ORIGINAL_AMOUNT, CURRENCY_PAIR, PRICE, ORDER_TIMESTAMP,
                                FEE_AMOUNT, FEE_CURRENCY)
values  -- For position 1.
        ('TRADE_11', 'OPEN_ORDER_01', 'BID', 7, 'BTC/USDT', 11, DATE '2020-08-05', 1, 'USD'),
        ('TRADE_12', 'OPEN_ORDER_01', 'BID', 3, 'BTC/USDT', 12, DATE '2020-08-06', 2, 'USD'),
        ('TRADE_13', 'CLOSE_ORDER_01', 'ASK', 1, 'BTC/USDT', 13, DATE '2020-08-07', 3, 'USD'),
        ('TRADE_14', 'CLOSE_ORDER_01', 'ASK', 2, 'BTC/USDT', 14, DATE '2020-08-08', 4, 'USD'),
        ('TRADE_15', 'CLOSE_ORDER_01', 'ASK', 8, 'BTC/USDT', 15, DATE '2020-08-09', 5, 'USD'),
        -- For position 2.
        ('TRADE_21', 'OPEN_ORDER_02', 'BID', 20, 'ETH/BTC', 100, DATE '2020-08-05', 5, 'USD'),
        ('TRADE_22', 'CLOSE_ORDER_02', 'ASK', 20, 'ETH/BTC', 50, DATE '2020-08-06', 5, 'USD'),
        -- For position 3.
        ('TRADE_31', 'OPEN_ORDER_03', 'BID', 30, 'BTC/USDT', 20, DATE '2020-08-05', 6, 'USD'),
        ('TRADE_32', 'CLOSE_ORDER_03', 'ASK', 30, 'BTC/USDT', 25, DATE '2020-08-06', 5, 'USD'),
        -- For position 4.
        ('TRADE_41', 'OPEN_ORDER_04', 'BID', 50, 'BTC/USDT', 20, DATE '2020-08-05', 6, 'USD'),
        ('TRADE_42', 'CLOSE_ORDER_04', 'ASK', 50, 'BTC/USDT', 25, DATE '2020-08-06', 5, 'USD'),
        -- For position 5.
        ('TRADE_51', 'OPEN_ORDER_05', 'BID', 50, 'BTC/USDT', 20, DATE '2020-08-05', 6, 'USD'),
        ('TRADE_52', 'CLOSE_ORDER_05', 'ASK', 50, 'BTC/USDT', 25, DATE '2020-08-06', 5, 'USD'),
        -- For position 6.
        ('TRADE_61', 'OPEN_ORDER_06', 'BID', 50, 'BTC/USDT', 20, DATE '2020-08-05', 6, 'USD'),
        ('TRADE_62', 'CLOSE_ORDER_06', 'ASK', 50, 'BTC/USDT', 25, DATE '2020-08-06', 5, 'USD');

-- =====================================================================================================================
-- Insert positions trades.
INSERT INTO MY_STRATEGY_POSITIONS_TRADES (POSITION_ID, TRADE_ID)
VALUES -- Trades for position 1.
       (1, 'TRADE_11'),
       (1, 'TRADE_12'),
       (1, 'TRADE_13'),
       (1, 'TRADE_14'),
       (1, 'TRADE_15'),
       -- Trades for position 2.
       (2, 'TRADE_21'),
       (2, 'TRADE_22'),
       -- Trades for position 3.
       (3, 'TRADE_31'),
       (3, 'TRADE_32'),
       -- Trades for position 4.
       (4, 'TRADE_31'),
       (4, 'TRADE_32'),
       -- Trades for position 5.
       (5, 'TRADE_31'),
       (5, 'TRADE_32'),
       -- Trades for position 6.
       (6, 'TRADE_31'),
       (6, 'TRADE_32');