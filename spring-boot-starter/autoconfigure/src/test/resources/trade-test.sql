-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (ID, NAME)
VALUES  ('001', 'My strategy');

-- =====================================================================================================================
-- Insert orders.
INSERT INTO ORDERS (ID, TYPE, ORIGINAL_AMOUNT, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS, CUMULATIVE_AMOUNT,
                    AVERAGE_PRICE, FEE, LEVERAGE, LIMIT_PRICE, STRATEGY_ID)
VALUES -- Order BACKUP_ORDER_01 (useless).
       ('ORDER00001', 'ASK', 0.000005, 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 0.000003,
        0.000002, 'LEVERAGE_1', 0.000001, '001'),

       -- Order BACKUP_ORDER_02 (useless).
       ('ORDER00002', 'BID', 0.000015, 'USDT/BTC', 'My reference 2', '2020-11-19', 'PENDING_NEW', 0.000014,
        0.000013, 0.000012, 'LEVERAGE_2', 0.000011, '001');