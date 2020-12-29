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
       (1, 'ORDER00001', 'ASK', 0.000005, 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 0.000003,
        'LEVERAGE_1', 0.000001, 1),

       -- Order BACKUP_ORDER_02 (useless).
       (2, 'ORDER00002', 'BID', 0.000015, 'USDT/BTC', 'My reference 2', '2020-11-19', 'PENDING_NEW', 0.000014,
        0.000013, 'LEVERAGE_2', 0.000011, 1);