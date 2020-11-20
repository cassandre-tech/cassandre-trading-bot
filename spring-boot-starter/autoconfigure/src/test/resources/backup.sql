-- Sum up.
-- Position 1 (OPENING) - 10 on BTC/USDT - Waiting for trades for order BACKUP_OPEN_ORDER_01.
-- Position 2 (OPENED)  - 20 on BTC/USDT - Opened because trade BACKUP_TRADE_01 (for order BACKUP_OPEN_ORDER_02).
-- Position 3 (CLOSING) - 30 on BTC/USDT - Opening because trade BACKUP_TRADE_02 (for order BACKUP_OPEN_ORDER_03).
-- Position 4 (CLOSED)  - 40 on BTC/USDT - Opened because trade BACKUP_TRADE_03 (BACKUP_OPEN_ORDER_04) & BACKUP_TRADE_04 (BACKUP_OPEN_ORDER_05).
-- Position 5 (CLOSED)  - 50 on ETH/USD  - Closed - Opened with OPEN_ORDER_01 and closed with CLOSE_ORDER_01.
--                      -> Order OPEN_ORDER_01 - TRADE_01 with 10.
--                      -> Order OPEN_ORDER_01 - TRADE_02 with 40.
--                      -> Order CLOSE_ORDER_01 - TRADE_03 with 15.
--                      -> Order CLOSE_ORDER_01 - TRADE_04 with 05.
--                      -> Order CLOSE_ORDER_01 - TRADE_05 with 30.

-- =====================================================================================================================
-- Insert positions.
INSERT INTO MY_STRATEGY_POSITIONS (ID, STATUS, CURRENCY_PAIR, AMOUNT, RULES_STOP_GAIN_PERCENTAGE,
                                   RULES_STOP_LOSS_PERCENTAGE, OPEN_ORDER_ID, CLOSE_ORDER_ID, LOWEST_PRICE,
                                   HIGHEST_PRICE, LATEST_PRICE)
VALUES -- Position 1 : Opening, no rules, waiting for BACKUP_OPEN_ORDER_01 to arrive (but will not arrive).
       (1, 'OPENING', 'BTC/USDT', 10, null, null, 'BACKUP_OPEN_ORDER_01', null, null, null, null),

       -- Position 2 : Opened position, 10% gain rule.
       (2, 'OPENED', 'BTC/USDT', 20, 10, null, 'BACKUP_OPEN_ORDER_02', null, 1, 2, 3),

       -- Position 3 : Closing position, 20% loss rule, waiting for a not coming trade 'NON_EXISTING_TRADE'.
       (3, 'CLOSING', 'BTC/USDT', 30, null, 20, 'BACKUP_OPEN_ORDER_03', 'NON_EXISTING_TRADE', 17, 68, 92),

       -- Position 4 : Closed position, 30% gain & 40 % loss.
       (4, 'CLOSED', 'BTC/USDT', 40, 30, 40, 'BACKUP_OPEN_ORDER_04', 'BACKUP_OPEN_ORDER_05', 17, 68, 93),

       -- Position 5 : closed.
       (5, 'CLOSED', 'ETH/USD', 50, 30, 40, 'OPEN_ORDER_01', 'CLOSE_ORDER_01', 17, 68, 94);

-- =====================================================================================================================
-- Insert trades.
INSERT INTO MY_STRATEGY_TRADES (ID, ORDER_ID, ORDER_TYPE, ORIGINAL_AMOUNT, CURRENCY_PAIR, PRICE, ORDER_TIMESTAMP,
                                FEE_AMOUNT, FEE_CURRENCY, POSITION_ID)
values -- note : No trade for order BACKUP_OPEN_ORDER_01 - This is why position 1 has the opening status.
       -- Order BACKUP_TRADE_01 - Trade from the order buying BACKUP_OPEN_ORDER_02.
       ('BACKUP_TRADE_01', 'BACKUP_OPEN_ORDER_02', 'BID', 20, 'BTC/USDT', 10, DATE '2020-08-01', 1, 'USDT', 2),

       -- Order BACKUP_TRADE_02 - Trade from the order buying BACKUP_OPEN_ORDER_02.
       ('BACKUP_TRADE_02', 'BACKUP_OPEN_ORDER_03', 'BID', 30, 'BTC/USDT', 20, DATE '2020-08-02', 2, 'USDT', 3),

       -- Order BACKUP_TRADE_03 - Trade from the order buying BACKUP_OPEN_ORDER_03.
       ('BACKUP_TRADE_03', 'BACKUP_OPEN_ORDER_04', 'BID', 40, 'BTC/USDT', 30, DATE '2020-08-03', 3, 'USDT', 4),

       -- Order BACKUP_TRADE_04 - Trade from the order selling BACKUP_OPEN_ORDER_04.
       ('BACKUP_TRADE_04', 'BACKUP_OPEN_ORDER_05', 'ASK', 40, 'BTC/USDT', 40, DATE '2020-08-04', 4, 'USDT', 4),

       -- Order BACKUP_TRADE_05 - Trade from the order selling BACKUP_OPEN_ORDER_05.
       ('BACKUP_TRADE_05', 'BACKUP_OPEN_ORDER_06', 'ASK', 50, 'ETH/USD', 50, DATE '2020-08-05', 5, 'USD', null),

       -- For position 5.
       ('TRADE_01', 'OPEN_ORDER_01', 'BID', 10, 'ETH/USD', 11, DATE '2020-08-05', 5, 'USD', 5),
       ('TRADE_02', 'OPEN_ORDER_01', 'BID', 40, 'ETH/USD', 12, DATE '2020-08-06', 5, 'USD', 5),
       ('TRADE_03', 'CLOSE_ORDER_01', 'ASK', 15, 'ETH/USD', 13, DATE '2020-08-07', 5, 'USD', 5),
       ('TRADE_04', 'CLOSE_ORDER_01', 'ASK', 5, 'ETH/USD', 14, DATE '2020-08-08', 5, 'USD', 5),
       ('TRADE_05', 'CLOSE_ORDER_01', 'ASK', 30, 'ETH/USD', 15, DATE '2020-08-09', 5, 'USD', 5);

-- =====================================================================================================================
-- Insert trades.
INSERT INTO MY_STRATEGY_ORDERS (ID, TYPE, ORIGINAL_AMOUNT, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS,
                                CUMULATIVE_AMOUNT, AVERAGE_PRICE, FEE, LEVERAGE, LIMIT_PRICE)
values -- Order BACKUP_ORDER_01.
       ('BACKUP_ORDER_01', 'ASK', 0.000005, 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 0.000003,
        0.000002, 'LEVERAGE_1', 0.000001),

       -- Order BACKUP_ORDER_02.
       ('BACKUP_ORDER_02', 'BID', 0.000015, 'USDT/BTC', 'My reference 2', '2020-11-19', 'PENDING_NEW', 0.000014,
        0.000013, 0.000012, 'LEVERAGE_2', 0.000011);
