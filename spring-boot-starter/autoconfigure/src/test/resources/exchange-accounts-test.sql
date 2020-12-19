-- =====================================================================================================================
-- Insert exchange accounts.
INSERT INTO EXCHANGE_ACCOUNTS (EXCHANGE_NAME, EXCHANGE_ACCOUNT)
VALUES  ('kucoin', 'fake@gmail.com');

-- =====================================================================================================================
-- Insert strategies.
INSERT INTO STRATEGIES (ID, NAME, EXCHANGE_ACCOUNT_ID)
VALUES  ('001', 'My strategy 1 (fake exchange account', 1),
        ('002', 'My strategy 2 (fake exchange account', 1);