-- =====================================================================================================================
-- Insert trades.
INSERT INTO TRADES(ID, CURRENCY_PAIR, FEE_AMOUNT, FEE_CURRENCY, ORDER_ID, ORIGINAL_AMOUNT, PRICE, ORDER_TIMESTAMP, ORDER_TYPE)
VALUES  ('BACKUP_TRADE_00', 'ETH/USD', 51, 'USD', 'TEMP', 52, 53, DATE '2020-08-05', 'ASK'),                    -- Useless.
        ('BACKUP_TRADE_01', 'BTC/USDT', 11, 'USDT', 'BACKUP_OPEN_ORDER_02', 12, 13, DATE '2020-08-01', 'BID'),  -- Set trade 2 to opened.
        ('BACKUP_TRADE_02', 'BTC/USDT', 21, 'USDT', 'BACKUP_OPEN_ORDER_03', 22, 23, DATE '2020-08-02', 'BID'),  -- Set trade 3 to closing.
        ('BACKUP_TRADE_03', 'BTC/USDT', 31, 'USDT', 'BACKUP_OPEN_ORDER_04', 32, 33, DATE '2020-08-03', 'BID'),  -- Set trade 4 to closed.
        ('BACKUP_TRADE_04', 'BTC/USDT', 41, 'USDT', 'BACKUP_OPEN_ORDER_05', 42, 43, DATE '2020-08-04', 'ASK');  -- Set trade 4 to closed.

-- =====================================================================================================================
-- Insert positions.
INSERT INTO POSITIONS(ID, RULES_STOP_GAIN_PERCENTAGE, RULES_STOP_LOSS_PERCENTAGE, OPEN_ORDER_ID, CLOSE_ORDER_ID)
VALUES  (5, 51, 52,  'BACKUP_OPEN_ORDER_51', null),                     -- Useless position.
        (6, 61, 62,  'BACKUP_OPEN_ORDER_61', null),                     -- Useless position.
        (1, null, null, 'BACKUP_OPEN_ORDER_01', null),                  -- Opening position - no rules.
        (2, 10, null, 'BACKUP_OPEN_ORDER_02', null),                    -- Opened position - 10% gain.
        (3, null, 20, 'BACKUP_OPEN_ORDER_03', 'NON_EXISTING_TRADE'),    -- Closing position - 20% loss.
        (4, 30, 40,  'BACKUP_OPEN_ORDER_04', 'BACKUP_OPEN_ORDER_05');   -- Closed position - 30% gain & 40 % loss.