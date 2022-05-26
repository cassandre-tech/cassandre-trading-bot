-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (uid, STRATEGY_ID, NAME)
VALUES (1, '01', 'My strategy');

-- =====================================================================================================================
-- Insert orders.
INSERT INTO ORDERS (uid, ORDER_ID, TYPE, AMOUNT_VALUE, AMOUNT_CURRENCY, CURRENCY_PAIR, USER_REFERENCE, TIMESTAMP, STATUS,
                    CUMULATIVE_AMOUNT_VALUE, CUMULATIVE_AMOUNT_CURRENCY, AVERAGE_PRICE_VALUE, AVERAGE_PRICE_CURRENCY,
                    LEVERAGE, LIMIT_PRICE_VALUE, LIMIT_PRICE_CURRENCY, fk_strategy_uid)
VALUES -- Order ORDER_0000001.
       (1, 'ORDER_0000001', 'ASK', 0.000005, 'ETH', 'ETH/BTC', 'My reference 1', '2020-11-18', 'NEW', 0.000004, 'BTC',
        0.000003, 'BTC', 'LEVERAGE_1', 0.000001, 'BTC', 1);