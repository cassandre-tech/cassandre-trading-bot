-- =====================================================================================================================
-- Insert exchange accounts.
INSERT INTO EXCHANGE_ACCOUNTS (ID, EXCHANGE_NAME, EXCHANGE_ACCOUNT)
VALUES (1, 'kucoin', 'cassandre.crypto.bot@gmail.com');

-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (ID, STRATEGY_ID, TYPE, NAME, FK_EXCHANGE_ACCOUNT_ID)
VALUES (1, '01', 'BASIC_STRATEGY', 'My strategy', 1);

-- =====================================================================================================================
-- Insert orders.
INSERT INTO ORDERS (ID, ORDER_ID, TYPE, AMOUNT_VALUE, AMOUNT_CURRENCY, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS,
                    CUMULATIVE_AMOUNT_VALUE, CUMULATIVE_AMOUNT_CURRENCY, AVERAGE_PRICE_VALUE, AVERAGE_PRICE_CURRENCY, LEVERAGE,
                    LIMIT_PRICE_VALUE, LIMIT_PRICE_CURRENCY, FK_STRATEGY_ID)
VALUES -- Order BACKUP_ORDER_01 (useless).
       (1, 'ORDER00001', 'ASK', 0.000005, 'ETH', 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 'BTC', 0.000003, 'BTC',
        'LEVERAGE_1', 0.000001, 'BTC', 1),

       -- Order BACKUP_ORDER_02 (useless).
       (2, 'ORDER00002', 'BID', 0.000015, 'USDT', 'USDT/BTC', 'My reference 2', '2020-11-19', 'PENDING_NEW', 0.000014, 'BTC',
        0.000013, 'BTC', 'LEVERAGE_2', 0.000011, 'BTC', 1);