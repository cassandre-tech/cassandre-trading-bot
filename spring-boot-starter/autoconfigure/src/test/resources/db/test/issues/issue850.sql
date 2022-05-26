--
-- Strategy.
--

INSERT INTO public.strategies   (uid, strategy_id, name, created_on, updated_on)
VALUES                          (1, '001', 'ETH', '2021-10-16 23:47:07.795914', NULL);

-- Orders for position 1.
INSERT INTO public.orders   (uid, order_id, type, fk_strategy_uid, currency_pair, amount_value, amount_currency, average_price_value, average_price_currency, limit_price_value, limit_price_currency, leverage, status, cumulative_amount_value, cumulative_amount_currency, user_reference, timestamp, created_on, updated_on, market_price_value, market_price_currency)
VALUES                      (1, '6285774074', 'BID', 1, 'ETH/USDT', 0.02000000, 'ETH', 3849.41000000, 'USDT', NULL, NULL, NULL, 'NEW', 0.02000000, 'ETH', NULL, '2021-10-17 14:47:11.522484', '2021-10-17 14:47:11.567372', '2021-10-17 14:48:11.333022', 3849.41000000, 'USDT');
INSERT INTO public.orders   (uid, order_id, type, fk_strategy_uid, currency_pair, amount_value, amount_currency, average_price_value, average_price_currency, limit_price_value, limit_price_currency, leverage, status, cumulative_amount_value, cumulative_amount_currency, user_reference, timestamp, created_on, updated_on, market_price_value, market_price_currency)
VALUES                      (2, '6286292971', 'BID', 1, 'ETH/USDT', 0.02000000, 'ETH', 3852.50000000, 'USDT', NULL, NULL, NULL, 'NEW', 0.02000000, 'ETH', NULL, '2021-10-17 15:47:11.797272', '2021-10-17 15:47:11.801485', '2021-10-17 15:48:11.060481', 3852.50000000, 'USDT');

-- Orders for position 2.
INSERT INTO public.orders   (uid, order_id, type, fk_strategy_uid, currency_pair, amount_value, amount_currency, average_price_value, average_price_currency, limit_price_value, limit_price_currency, leverage, status, cumulative_amount_value, cumulative_amount_currency, user_reference, timestamp, created_on, updated_on, market_price_value, market_price_currency)
VALUES                      (11, '6331002853', 'ASK', 1, 'ETH/USDT', 0.02000000, 'ETH', 4081.86000000, 'USDT', NULL, NULL, NULL, 'NEW', 0.02000000, 'ETH', NULL, '2021-10-20 18:00:11.922574', '2021-10-20 18:00:11.924201', '2021-10-20 18:01:11.368912', 4081.86000000, 'USDT');
INSERT INTO public.orders   (uid, order_id, type, fk_strategy_uid, currency_pair, amount_value, amount_currency, average_price_value, average_price_currency, limit_price_value, limit_price_currency, leverage, status, cumulative_amount_value, cumulative_amount_currency, user_reference, timestamp, created_on, updated_on, market_price_value, market_price_currency)
VALUES                      (13, '6348044386', 'ASK', 1, 'ETH/USDT', 0.02000000, 'ETH', 4126.82000000, 'USDT', NULL, NULL, NULL, 'NEW', 0.02000000, 'ETH', NULL, '2021-10-21 16:47:13.407879', '2021-10-21 16:47:13.410553', '2021-10-21 16:48:12.258556', 4126.82000000, 'USDT');

-- Position 1.
INSERT INTO public.positions    (uid, position_id, type, fk_strategy_uid, currency_pair, amount_value, amount_currency, rules_stop_gain_percentage, rules_stop_loss_percentage, status, FK_OPENING_ORDER_UID, FK_CLOSING_ORDER_UID, lowest_gain_price_value, lowest_gain_price_currency, highest_gain_price_value, highest_gain_price_currency, latest_gain_price_value, latest_gain_price_currency, created_on, updated_on, force_closing)
VALUES                          (1, 1, 'LONG', 1, 'ETH/USDT', 0.02000000, 'ETH', 6, 15, 'CLOSED', 1, 2, 3660.11000000, 'USDT', 4079.63000000, 'USDT', 4081.86000000, 'USDT', '2021-10-17 14:47:11.714212', '2021-10-20 18:01:10.401066', false);

-- Position 2.
INSERT INTO public.positions    (uid, position_id, type, fk_strategy_uid, currency_pair, amount_value, amount_currency, rules_stop_gain_percentage, rules_stop_loss_percentage, status, FK_OPENING_ORDER_UID, FK_CLOSING_ORDER_UID, lowest_gain_price_value, lowest_gain_price_currency, highest_gain_price_value, highest_gain_price_currency, latest_gain_price_value, latest_gain_price_currency, created_on, updated_on, force_closing)
VALUES                          (2, 2, 'LONG', 1, 'ETH/USDT', 0.02000000, 'ETH', 6, 15, 'CLOSED', 11, 13, 3660.11000000, 'USDT', 4083.52000000, 'USDT', 4126.82000000, 'USDT', '2021-10-17 15:47:11.809771', '2021-10-21 16:48:11.372994', false);

-- Trades for order 1.
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (1, 'ORDER_01_TRADE_01', 'BID', 1, 'ETH/USDT', 0.01000000, 'ETH', 3849.41000000, 'USDT', 0.00002000, 'ETH', NULL, '2021-10-17 14:47:11.405', '2021-10-17 14:48:10.455776', NULL);
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (2, 'ORDER_01_TRADE_02', 'BID', 1, 'ETH/USDT', 0.00500000, 'ETH', 3849.41000000, 'USDT', 0.00003000, 'ETH', NULL, '2021-10-17 14:47:11.405', '2021-10-17 14:48:10.455776', NULL);
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (3, 'ORDER_01_TRADE_03', 'BID', 1, 'ETH/USDT', 0.00300000, 'ETH', 3849.41000000, 'USDT', 0.00004000, 'BTC', NULL, '2021-10-17 14:47:11.405', '2021-10-17 14:48:10.455776', NULL);
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (4, 'ORDER_01_TRADE_04', 'BID', 1, 'ETH/USDT', 0.00200000, 'ETH', 3849.41000000, 'USDT', 0.00005000, 'KCS', NULL, '2021-10-17 14:47:11.405', '2021-10-17 14:48:10.455776', NULL);

-- Trades for order 2.
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (5, 'ORDER_02_TRADE_01', 'BID', 2, 'ETH/USDT', 0.00300000, 'ETH', 3852.53000000, 'USDT', 1.50000000, 'ETH', NULL, '2021-10-17 15:47:11.679', '2021-10-17 15:48:10.362026', NULL);
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (6, 'ORDER_02_TRADE_02', 'BID', 2, 'ETH/USDT', 0.01600000, 'ETH', 3852.53000000, 'USDT', 0.50002000, 'ETH', NULL, '2021-10-17 15:47:11.679', '2021-10-17 15:48:10.362026', NULL);
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (7, 'ORDER_02_TRADE_03', 'BID', 2, 'ETH/USDT', 0.00100000, 'ETH', 3852.53000000, 'USDT', 1.00002000, 'BTC', NULL, '2021-10-17 15:47:11.679', '2021-10-17 15:48:10.362026', NULL);

-- Trades for order 11.
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (11, '641414330', 'ASK', 11, 'ETH/USDT', 0.02000000, 'ETH', 4081.57000000, 'USDT', 0.08163140, 'USDT', NULL, '2021-10-20 18:00:11.8', '2021-10-20 18:01:10.387332', NULL);
-- Trades for order 13.
INSERT INTO public.trades   (uid, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, timestamp, created_on, updated_on)
VALUES                      (13, '643833815', 'ASK', 13, 'ETH/USDT', 0.02000000, 'ETH', 4125.62000000, 'USDT', 0.08251240, 'USDT', NULL, '2021-10-21 16:47:13.3', '2021-10-21 16:48:10.385551', NULL);