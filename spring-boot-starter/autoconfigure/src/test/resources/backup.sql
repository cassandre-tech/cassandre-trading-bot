-- =====================================================================================================================
-- Insert trades.
INSERT INTO MY_STRATEGY_TRADES (ID, ORDER_ID, ORDER_TYPE, ORIGINAL_AMOUNT, CURRENCY_PAIR, PRICE, ORDER_TIMESTAMP,
                                FEE_AMOUNT, FEE_CURRENCY)
values -- note : No trade for order BACKUP_OPEN_ORDER_01 - This is why position 1 is in opening.
       -- Order BACKUP_TRADE_01 - Trade from the order buying BACKUP_OPEN_ORDER_01.
       ('BACKUP_TRADE_01', 'BACKUP_OPEN_ORDER_02', 'BID', 12, 'BTC/USDT', 14, DATE '2020-08-01', 11, 'USDT'),

       -- Order BACKUP_TRADE_02 - Trade from the order buying BACKUP_OPEN_ORDER_02.
       ('BACKUP_TRADE_02', 'BACKUP_OPEN_ORDER_03', 'BID', 22, 'BTC/USDT', 24, DATE '2020-08-02', 21, 'USDT'),

       -- Order BACKUP_TRADE_03 - Trade from the order buying BACKUP_OPEN_ORDER_03.
       ('BACKUP_TRADE_03', 'BACKUP_OPEN_ORDER_04', 'BID', 32, 'BTC/USDT', 34, DATE '2020-08-03', 31, 'USDT'),

       -- Order BACKUP_TRADE_04 - Trade from the order selling BACKUP_OPEN_ORDER_04.
       ('BACKUP_TRADE_04', 'BACKUP_OPEN_ORDER_05', 'ASK', 42, 'BTC/USDT', 44, DATE '2020-08-04', 41, 'USDT'),

       -- Order BACKUP_TRADE_05 - Trade from the order selling BACKUP_OPEN_ORDER_05.
       ('BACKUP_TRADE_05', 'BACKUP_OPEN_ORDER_06', 'ASK', 52, 'ETH/USD', 54, DATE '2020-08-05', 51, 'USD');

-- =====================================================================================================================
-- Insert positions.
INSERT INTO MY_STRATEGY_POSITIONS (ID, STATUS, RULES_STOP_GAIN_PERCENTAGE, RULES_STOP_LOSS_PERCENTAGE, OPEN_ORDER_ID,
                                   CLOSE_ORDER_ID, LOWEST_PRICE, HIGHEST_PRICE)
VALUES -- Position 1 : Opening, no rules, waiting for BACKUP_OPEN_ORDER_01 to arrive (but will not arrive).
       (1, 'OPENING', null, null, 'BACKUP_OPEN_ORDER_01', null, null, null),

       -- Position 2 : Opened position, 10% gain rule.
       (2, 'OPENED', 10, null, 'BACKUP_OPEN_ORDER_02', null, 1, 2),

       -- Position 3 : Closing position, 20% loss rule, waiting for a not coming trade 'NON_EXISTING_TRADE'.
       (3, 'CLOSING', null, 20, 'BACKUP_OPEN_ORDER_03', 'NON_EXISTING_TRADE', 17, 68),

       -- Position 4 : Closed position, 30% gain & 40 % loss.
       (4, 'CLOSED', 30, 40, 'BACKUP_OPEN_ORDER_04', 'BACKUP_OPEN_ORDER_05', 17, 68);