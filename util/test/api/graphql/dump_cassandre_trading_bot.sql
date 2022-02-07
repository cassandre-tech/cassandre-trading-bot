--
-- PostgreSQL database dump
--

-- Dumped from database version 13.4
-- Dumped by pg_dump version 13.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: backtesting_tickers; Type: TABLE; Schema: public; Owner: cassandre_trading_bot
--

CREATE TABLE public.backtesting_tickers (
    test_session_id character varying(255) NOT NULL,
    response_sequence_id bigint NOT NULL,
    currency_pair character varying(255) NOT NULL,
    open numeric(16,8),
    last numeric(16,8),
    bid numeric(16,8),
    ask numeric(16,8),
    high numeric(16,8),
    low numeric(16,8),
    vwap numeric(16,8),
    volume numeric(16,8),
    quote_volume numeric(30,12),
    bid_size numeric(16,8),
    ask_size numeric(16,8),
    "timestamp" timestamp with time zone
);


ALTER TABLE public.backtesting_tickers OWNER TO cassandre_trading_bot;

--
-- Name: COLUMN backtesting_tickers.test_session_id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.test_session_id IS 'Defines the test session id (To allow parallel tests)';


--
-- Name: COLUMN backtesting_tickers.response_sequence_id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.response_sequence_id IS 'Defines to which client request those responses are corresponding to';


--
-- Name: COLUMN backtesting_tickers.currency_pair; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.currency_pair IS 'Defines the currency pair';


--
-- Name: COLUMN backtesting_tickers.open; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.open IS 'The opening price is the first trade price that was recorded during the day’s trading';


--
-- Name: COLUMN backtesting_tickers.last; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.last IS 'Last trade field is the price set during the last trade';


--
-- Name: COLUMN backtesting_tickers.bid; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.bid IS 'The bid price shown represents the highest bid price';


--
-- Name: COLUMN backtesting_tickers.ask; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.ask IS 'The ask price shown represents the lowest bid price';


--
-- Name: COLUMN backtesting_tickers.high; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.high IS 'The day’s high price';


--
-- Name: COLUMN backtesting_tickers.low; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.low IS 'The day’s low price';


--
-- Name: COLUMN backtesting_tickers.vwap; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.vwap IS 'Volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day)';


--
-- Name: COLUMN backtesting_tickers.volume; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.volume IS 'Volume is the number of shares or contracts traded';


--
-- Name: COLUMN backtesting_tickers.quote_volume; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.quote_volume IS 'Quote volume';


--
-- Name: COLUMN backtesting_tickers.bid_size; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.bid_size IS 'The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price';


--
-- Name: COLUMN backtesting_tickers.ask_size; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers.ask_size IS 'The ask size represents the quantity of a security that investors are willing to sell at a specified selling price';


--
-- Name: COLUMN backtesting_tickers."timestamp"; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.backtesting_tickers."timestamp" IS 'The timestamp of the ticker';


--
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: cassandre_trading_bot
--

CREATE TABLE public.databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);


ALTER TABLE public.databasechangelog OWNER TO cassandre_trading_bot;

--
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: cassandre_trading_bot
--

CREATE TABLE public.databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO cassandre_trading_bot;

--
-- Name: orders; Type: TABLE; Schema: public; Owner: cassandre_trading_bot
--

CREATE TABLE public.orders (
    id bigint NOT NULL,
    order_id character varying(255),
    type character varying(255),
    FK_STRATEGY_UID bigint,
    currency_pair character varying(255),
    amount_value numeric(16,8),
    amount_currency character varying(255),
    average_price_value numeric(16,8),
    average_price_currency character varying(255),
    limit_price_value numeric(16,8),
    limit_price_currency character varying(255),
    leverage character varying(255),
    status character varying(255),
    cumulative_amount_value numeric(16,8),
    cumulative_amount_currency character varying(255),
    user_reference character varying(255),
    "timestamp" timestamp with time zone,
    created_on timestamp with time zone,
    updated_on timestamp with time zone,
    market_price_value numeric(16,8),
    market_price_currency character varying(255)
);


ALTER TABLE public.orders OWNER TO cassandre_trading_bot;

--
-- Name: COLUMN orders.id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.id IS 'Technical ID';


--
-- Name: COLUMN orders.order_id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.order_id IS 'An identifier set by the exchange that uniquely identifies the order';


--
-- Name: COLUMN orders.type; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.type IS 'Order type i.e. bid or ask';


--
-- Name: COLUMN orders.FK_STRATEGY_UID; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.FK_STRATEGY_UID IS 'The strategy that created the order';


--
-- Name: COLUMN orders.currency_pair; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.currency_pair IS 'Currency pair';


--
-- Name: COLUMN orders.amount_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.amount_value IS 'Amount that was ordered (value)';


--
-- Name: COLUMN orders.amount_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.amount_currency IS 'Amount that was ordered (currency)';


--
-- Name: COLUMN orders.average_price_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.average_price_value IS 'Weighted Average price of the fills in the order (value)';


--
-- Name: COLUMN orders.average_price_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.average_price_currency IS 'Weighted Average price of the fills in the order (currency)';


--
-- Name: COLUMN orders.limit_price_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.limit_price_value IS 'Limit price (value)';


--
-- Name: COLUMN orders.limit_price_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.limit_price_currency IS 'Limit price (currency)';


--
-- Name: COLUMN orders.leverage; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.leverage IS 'The leverage to use for margin related to this order';


--
-- Name: COLUMN orders.status; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.status IS 'Order status';


--
-- Name: COLUMN orders.cumulative_amount_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.cumulative_amount_value IS 'Amount value to be ordered/amount that has been matched against order on the order book/filled (value)';


--
-- Name: COLUMN orders.cumulative_amount_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.cumulative_amount_currency IS 'Amount currency to be ordered/amount that has been matched against order on the order book/filled (currency)';


--
-- Name: COLUMN orders.user_reference; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.user_reference IS 'An identifier provided by the user on placement that uniquely identifies the order';


--
-- Name: COLUMN orders."timestamp"; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders."timestamp" IS 'The timestamp of the order';


--
-- Name: COLUMN orders.created_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.created_on IS 'Data created on';


--
-- Name: COLUMN orders.updated_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.updated_on IS 'Data updated on';


--
-- Name: COLUMN orders.market_price_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.market_price_value IS 'The price Cassandre had when the order was created (value)';


--
-- Name: COLUMN orders.market_price_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.orders.market_price_currency IS 'The price Cassandre had when the order was created (currency)';


--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: cassandre_trading_bot
--

CREATE SEQUENCE public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_id_seq OWNER TO cassandre_trading_bot;

--
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cassandre_trading_bot
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


--
-- Name: positions; Type: TABLE; Schema: public; Owner: cassandre_trading_bot
--

CREATE TABLE public.positions (
    id bigint NOT NULL,
    position_id bigint,
    type character varying(255),
    FK_STRATEGY_UID bigint,
    currency_pair character varying(255),
    amount_value numeric(16,8),
    amount_currency character varying(255),
    rules_stop_gain_percentage double precision,
    rules_stop_loss_percentage double precision,
    status character varying(255),
    FK_OPENING_ORDER_UID bigint,
    FK_CLOSING_ORDER_UID bigint,
    lowest_gain_price_value numeric(16,8),
    lowest_gain_price_currency character varying(255),
    highest_gain_price_value numeric(16,8),
    highest_gain_price_currency character varying(255),
    latest_gain_price_value numeric(16,8),
    latest_gain_price_currency character varying(255),
    created_on timestamp with time zone,
    updated_on timestamp with time zone,
    force_closing boolean DEFAULT false NOT NULL
);


ALTER TABLE public.positions OWNER TO cassandre_trading_bot;

--
-- Name: COLUMN positions.id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.id IS 'Technical ID';


--
-- Name: COLUMN positions.position_id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.position_id IS 'An identifier that uniquely identifies the position';


--
-- Name: COLUMN positions.type; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.type IS 'Position type';


--
-- Name: COLUMN positions.FK_STRATEGY_UID; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.FK_STRATEGY_UID IS 'The strategy that created the position';


--
-- Name: COLUMN positions.currency_pair; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.currency_pair IS 'Currency pair';


--
-- Name: COLUMN positions.amount_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.amount_value IS 'Amount that was ordered (value)';


--
-- Name: COLUMN positions.amount_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.amount_currency IS 'Amount that was ordered (currency)';


--
-- Name: COLUMN positions.rules_stop_gain_percentage; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.rules_stop_gain_percentage IS 'Stop gain percentage rule';


--
-- Name: COLUMN positions.rules_stop_loss_percentage; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.rules_stop_loss_percentage IS 'Stop loss percentage rule';


--
-- Name: COLUMN positions.status; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.status IS 'Position status';


--
-- Name: COLUMN positions.FK_OPENING_ORDER_UID; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.FK_OPENING_ORDER_UID IS 'The order created to open the position';


--
-- Name: COLUMN positions.FK_CLOSING_ORDER_UID; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.FK_CLOSING_ORDER_UID IS 'The order created to close the position';


--
-- Name: COLUMN positions.lowest_gain_price_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.lowest_gain_price_value IS 'Lowest price reached by tis position (value)';


--
-- Name: COLUMN positions.lowest_gain_price_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.lowest_gain_price_currency IS 'Lowest price reached by tis position (currency)';


--
-- Name: COLUMN positions.highest_gain_price_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.highest_gain_price_value IS 'Highest price reached by tis position (value)';


--
-- Name: COLUMN positions.highest_gain_price_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.highest_gain_price_currency IS 'Highest price reached by tis position (currency)';


--
-- Name: COLUMN positions.latest_gain_price_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.latest_gain_price_value IS 'Latest price for this position (value)';


--
-- Name: COLUMN positions.latest_gain_price_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.latest_gain_price_currency IS 'Latest price for this position (currency)';


--
-- Name: COLUMN positions.created_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.created_on IS 'Data created on';


--
-- Name: COLUMN positions.updated_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.updated_on IS 'Data updated on';


--
-- Name: COLUMN positions.force_closing; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.positions.force_closing IS 'Indicates that the position must be closed no matter the rules';


--
-- Name: positions_id_seq; Type: SEQUENCE; Schema: public; Owner: cassandre_trading_bot
--

CREATE SEQUENCE public.positions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.positions_id_seq OWNER TO cassandre_trading_bot;

--
-- Name: positions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cassandre_trading_bot
--

ALTER SEQUENCE public.positions_id_seq OWNED BY public.positions.id;


--
-- Name: strategies; Type: TABLE; Schema: public; Owner: cassandre_trading_bot
--

CREATE TABLE public.strategies (
    id bigint NOT NULL,
    strategy_id character varying(255),
    type character varying(255),
    name character varying(255),
    created_on timestamp with time zone,
    updated_on timestamp with time zone
);


ALTER TABLE public.strategies OWNER TO cassandre_trading_bot;

--
-- Name: COLUMN strategies.id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.strategies.id IS 'Technical ID';


--
-- Name: COLUMN strategies.strategy_id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.strategies.strategy_id IS 'An identifier that uniquely identifies the strategy';


--
-- Name: COLUMN strategies.type; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.strategies.type IS 'Strategy type';


--
-- Name: COLUMN strategies.name; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.strategies.name IS 'Strategy name';


--
-- Name: COLUMN strategies.created_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.strategies.created_on IS 'Data created on';


--
-- Name: COLUMN strategies.updated_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.strategies.updated_on IS 'Data updated on';


--
-- Name: strategies_id_seq; Type: SEQUENCE; Schema: public; Owner: cassandre_trading_bot
--

CREATE SEQUENCE public.strategies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.strategies_id_seq OWNER TO cassandre_trading_bot;

--
-- Name: strategies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cassandre_trading_bot
--

ALTER SEQUENCE public.strategies_id_seq OWNED BY public.strategies.id;


--
-- Name: trades; Type: TABLE; Schema: public; Owner: cassandre_trading_bot
--

CREATE TABLE public.trades (
    id bigint NOT NULL,
    trade_id character varying(255),
    type character varying(255),
    FK_ORDER_UID bigint,
    currency_pair character varying(255),
    amount_value numeric(16,8),
    amount_currency character varying(255),
    price_value numeric(16,8),
    price_currency character varying(255),
    fee_value numeric(16,8),
    fee_currency character varying(255),
    user_reference character varying(255),
    "timestamp" timestamp with time zone,
    created_on timestamp with time zone,
    updated_on timestamp with time zone
);


ALTER TABLE public.trades OWNER TO cassandre_trading_bot;

--
-- Name: COLUMN trades.id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.id IS 'Technical ID';


--
-- Name: COLUMN trades.trade_id; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.trade_id IS 'An identifier set by the exchange that uniquely identifies the trade';


--
-- Name: COLUMN trades.type; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.type IS 'Order type i.e. bid or ask';


--
-- Name: COLUMN trades.FK_ORDER_UID; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.FK_ORDER_UID IS 'The id of the order responsible for execution of this trade';


--
-- Name: COLUMN trades.currency_pair; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.currency_pair IS 'Currency pair';


--
-- Name: COLUMN trades.amount_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.amount_value IS 'Amount that was ordered (value)';


--
-- Name: COLUMN trades.amount_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.amount_currency IS 'Amount that was ordered (currency)';


--
-- Name: COLUMN trades.price_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.price_value IS 'The price (value)';


--
-- Name: COLUMN trades.price_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.price_currency IS 'The price (currency)';


--
-- Name: COLUMN trades.fee_value; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.fee_value IS 'The fee amount that was charged by the exchange for this trade (value)';


--
-- Name: COLUMN trades.fee_currency; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.fee_currency IS 'The fee currency that was charged by the exchange for this trade (currency)';


--
-- Name: COLUMN trades.user_reference; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.user_reference IS 'An identifier provided by the user on placement that uniquely identifies the order';


--
-- Name: COLUMN trades."timestamp"; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades."timestamp" IS 'The timestamp of the trade';


--
-- Name: COLUMN trades.created_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.created_on IS 'Data created on';


--
-- Name: COLUMN trades.updated_on; Type: COMMENT; Schema: public; Owner: cassandre_trading_bot
--

COMMENT ON COLUMN public.trades.updated_on IS 'Data updated on';


--
-- Name: trades_id_seq; Type: SEQUENCE; Schema: public; Owner: cassandre_trading_bot
--

CREATE SEQUENCE public.trades_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.trades_id_seq OWNER TO cassandre_trading_bot;

--
-- Name: trades_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cassandre_trading_bot
--

ALTER SEQUENCE public.trades_id_seq OWNED BY public.trades.id;


--
-- Name: orders id; Type: DEFAULT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- Name: positions id; Type: DEFAULT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.positions ALTER COLUMN id SET DEFAULT nextval('public.positions_id_seq'::regclass);


--
-- Name: strategies id; Type: DEFAULT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.strategies ALTER COLUMN id SET DEFAULT nextval('public.strategies_id_seq'::regclass);


--
-- Name: trades id; Type: DEFAULT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.trades ALTER COLUMN id SET DEFAULT nextval('public.trades_id_seq'::regclass);


--
-- Data for Name: backtesting_tickers; Type: TABLE DATA; Schema: public; Owner: cassandre_trading_bot
--

COPY public.backtesting_tickers (test_session_id, response_sequence_id, currency_pair, open, last, bid, ask, high, low, vwap, volume, quote_volume, bid_size, ask_size, "timestamp") FROM stdin;
\.


--
-- Data for Name: databasechangelog; Type: TABLE DATA; Schema: public; Owner: cassandre_trading_bot
--

COPY public.databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) FROM stdin;
changelog-4.0.0	straumat	db/changelog/db.changelog-4.0.0.xml	2021-07-01 15:21:47.825766	1	EXECUTED	8:3dbcfdd07b6add40768516356fe2c239	createTable tableName=EXCHANGE_ACCOUNTS; addAutoIncrement columnName=ID, tableName=EXCHANGE_ACCOUNTS; createTable tableName=STRATEGIES; addAutoIncrement columnName=ID, tableName=STRATEGIES; createTable tableName=POSITIONS; addAutoIncrement columnN...		\N	4.3.5	\N	\N	5152905268
changelog-4.1.0	straumat	db/changelog/db.changelog-4.1.0.xml	2021-07-01 15:21:47.947743	2	EXECUTED	8:fb60cf253eb02b966584659b56ed8eb1	renameColumn newColumnName=LOWEST_GAIN_PRICE_VALUE, oldColumnName=LOWEST_PRICE_VALUE, tableName=POSITIONS; renameColumn newColumnName=LOWEST_GAIN_PRICE_CURRENCY, oldColumnName=LOWEST_PRICE_CURRENCY, tableName=POSITIONS; renameColumn newColumnName=...		\N	4.3.5	\N	\N	5152905268
changelog-4.1.1	straumat	db/changelog/db.changelog-4.1.1.xml	2021-07-01 15:21:48.002646	3	EXECUTED	8:17f081b54da49ee063ad0a6dec7b0030	addColumn tableName=POSITIONS		\N	4.3.5	\N	\N	5152905268
changelog-5.0.0	straumat	db/changelog/db.changelog-5.0.0.xml	2021-07-01 15:21:48.161682	4	EXECUTED	8:ff66a1882454b18eb2eaaab8312fcb27	dropForeignKeyConstraint baseTableName=STRATEGIES, constraintName=FK_STRATEGIES_EXCHANGE_ACCOUNT_ID; dropColumn columnName=FK_EXCHANGE_ACCOUNT_ID, tableName=STRATEGIES; dropTable tableName=EXCHANGE_ACCOUNTS; addColumn tableName=ORDERS; addUniqueCo...		\N	4.3.5	\N	\N	5152905268
changelog-5.0.3	straumat	db/changelog/db.changelog-5.0.3.xml	2021-08-27 14:57:49.647382	5	EXECUTED	8:cbd25620b44e062e22373286f9d6cbf7	createTable tableName=BACKTESTING_TICKERS; createIndex indexName=IDX_BACKTESTING_TICKERS_RESPONSE_SEQUENCE_ID, tableName=BACKTESTING_TICKERS		\N	4.3.5	\N	\N	0076268314
\.


--
-- Data for Name: databasechangeloglock; Type: TABLE DATA; Schema: public; Owner: cassandre_trading_bot
--

COPY public.databasechangeloglock (id, locked, lockgranted, lockedby) FROM stdin;
1	f	\N	\N
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: cassandre_trading_bot
--

COPY public.orders (id, order_id, type, FK_STRATEGY_UID, currency_pair, amount_value, amount_currency, average_price_value, average_price_currency, limit_price_value, limit_price_currency, leverage, status, cumulative_amount_value, cumulative_amount_currency, user_reference, "timestamp", created_on, updated_on, market_price_value, market_price_currency) FROM stdin;
1	60ddfbc11f8b45000696de3f	BID	1	BTC/USDT	0.00100000	BTC	33183.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-01 19:30:42.054417+02	2021-07-01 19:30:42.12093+02	2021-07-01 19:30:46.086116+02	33183.50000000	USDT
2	60ddfbc2614b0f00061f94d9	BID	2	UNI/USDT	1.00000000	UNI	17.64750000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-01 19:30:42.644773+02	2021-07-01 19:30:42.64654+02	2021-07-01 19:30:46.096879+02	17.64750000	USDT
3	60de17e2b7939500065290f2	BID	1	BTC/USDT	0.00100000	BTC	33034.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-01 21:30:42.542557+02	2021-07-01 21:30:42.544546+02	2021-07-01 21:30:46.975391+02	33034.00000000	USDT
4	60de17e273ca9e0006a1993f	BID	2	UNI/USDT	1.00000000	UNI	17.67810000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-01 21:30:42.84531+02	2021-07-01 21:30:42.846385+02	2021-07-01 21:30:46.978328+02	17.67810000	USDT
5	60de6c4473ca9e0006bb5137	BID	2	UNI/USDT	1.00000000	UNI	17.82170000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-02 03:30:44.661462+02	2021-07-02 03:30:44.66905+02	2021-07-02 03:30:49.672163+02	17.82170000	USDT
6	60de7a5473ca9e0006ecfa86	BID	1	BTC/USDT	0.00100000	BTC	33322.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 04:30:45.060568+02	2021-07-02 04:30:45.061564+02	2021-07-02 04:30:49.671246+02	33322.10000000	USDT
7	60de7a556514160006cbdb04	BID	2	UNI/USDT	1.00000000	UNI	17.74040000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-02 04:30:45.343272+02	2021-07-02 04:30:45.344686+02	2021-07-02 04:30:49.673883+02	17.74040000	USDT
8	60de8864d0db17000626fe23	BID	1	BTC/USDT	0.00100000	BTC	32854.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 05:30:44.596664+02	2021-07-02 05:30:44.59792+02	2021-07-02 05:30:49.710739+02	32854.00000000	USDT
9	60de88642fd9fd00060f3cda	BID	2	UNI/USDT	1.00000000	UNI	17.37420000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-02 05:30:44.883301+02	2021-07-02 05:30:44.885092+02	2021-07-02 05:30:49.712849+02	17.37420000	USDT
10	60de9674f0851400065acd99	BID	1	BTC/USDT	0.00100000	BTC	33041.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 06:30:44.566504+02	2021-07-02 06:30:44.574863+02	2021-07-02 06:30:50.432671+02	33041.90000000	USDT
11	60de9674651416000633a6d9	BID	2	UNI/USDT	1.00000000	UNI	17.28540000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-02 06:30:44.867906+02	2021-07-02 06:30:44.868814+02	2021-07-02 06:30:50.43484+02	17.28540000	USDT
12	60dea484f085140006884f2c	BID	1	BTC/USDT	0.00100000	BTC	33099.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 07:30:44.283289+02	2021-07-02 07:30:44.283985+02	2021-07-02 07:30:44.432228+02	33099.40000000	USDT
13	60dea4843be0650006d89d47	BID	2	UNI/USDT	1.00000000	UNI	17.26870000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-02 07:30:44.567425+02	2021-07-02 07:30:44.568207+02	2021-07-02 07:30:50.432153+02	17.26870000	USDT
14	60deb295614b0f0006939302	BID	1	BTC/USDT	0.00100000	BTC	32899.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 08:30:45.975021+02	2021-07-02 08:30:45.976542+02	2021-07-02 08:30:50.432072+02	32899.70000000	USDT
15	60deb296d0db170006bcfd22	BID	2	UNI/USDT	1.00000000	UNI	17.10440000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-02 08:30:46.285335+02	2021-07-02 08:30:46.286108+02	2021-07-02 08:30:50.434137+02	17.10440000	USDT
16	60dec0a52fd9fd0006d2009c	BID	2	UNI/USDT	1.00000000	UNI	17.34110000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-02 09:30:45.637309+02	2021-07-02 09:30:45.640924+02	2021-07-02 09:30:47.686455+02	17.34110000	USDT
17	60deceb5d0db1700061d7147	ASK	1	BTC/USDT	0.00100000	BTC	33373.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 10:30:45.87385+02	2021-07-02 10:30:45.874729+02	2021-07-02 10:30:47.686994+02	33373.80000000	USDT
18	60df150af08514000609765f	ASK	1	BTC/USDT	0.00100000	BTC	33289.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 15:30:50.454635+02	2021-07-02 15:30:50.463688+02	2021-07-02 15:30:53.686541+02	33289.50000000	USDT
19	60df231c38ec01000687554e	ASK	1	BTC/USDT	0.00100000	BTC	33595.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-02 16:30:53.091689+02	2021-07-02 16:30:53.099183+02	2021-07-02 16:30:53.686963+02	33595.60000000	USDT
20	60df9a1ab9e8af00064195b8	ASK	2	UNI/USDT	1.00000000	UNI	18.13180000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 00:58:34.599483+02	2021-07-03 00:58:34.606235+02	2021-07-03 00:58:34.877589+02	18.13180000	USDT
21	60e01486b7939500069b2b70	ASK	2	UNI/USDT	1.00000000	UNI	18.31000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 09:40:55.059865+02	2021-07-03 09:40:55.063776+02	2021-07-03 09:40:59.140213+02	18.31000000	USDT
22	60e015afb9e8af00069c3434	ASK	2	UNI/USDT	1.00000000	UNI	18.38160000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 09:45:51.308059+02	2021-07-03 09:45:51.308707+02	2021-07-03 09:45:53.548154+02	18.38160000	USDT
23	60e015cf73ca9e0006f39c49	ASK	2	UNI/USDT	1.00000000	UNI	18.39800000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 09:46:24.02592+02	2021-07-03 09:46:24.026738+02	2021-07-03 09:46:29.548667+02	18.39800000	USDT
24	60e01668b9e8af00069ecb55	ASK	2	UNI/USDT	1.00000000	UNI	18.49680000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 09:48:56.856941+02	2021-07-03 09:48:56.857723+02	2021-07-03 09:48:59.548571+02	18.49680000	USDT
25	60e017f538ec0100064e3296	ASK	1	BTC/USDT	0.00100000	BTC	34593.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-03 09:55:33.826143+02	2021-07-03 09:55:33.826723+02	2021-07-03 09:55:35.549572+02	34593.70000000	USDT
26	60e017f502f19e000639d57c	ASK	1	BTC/USDT	0.00100000	BTC	34593.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-03 09:55:34.114939+02	2021-07-03 09:55:34.11561+02	2021-07-03 09:55:35.553671+02	34593.70000000	USDT
27	60e0180b2fd9fd0006e89fbe	ASK	2	UNI/USDT	1.00000000	UNI	18.73070000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 09:55:55.35487+02	2021-07-03 09:55:55.355864+02	2021-07-03 09:55:59.575737+02	18.73070000	USDT
28	60e01bc802f19e0006494931	ASK	2	UNI/USDT	1.00000000	UNI	18.79200000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 10:11:53.136903+02	2021-07-03 10:11:53.137631+02	2021-07-03 10:11:53.575714+02	18.79200000	USDT
29	60e0270ff0851400062f5733	ASK	2	UNI/USDT	1.00000000	UNI	18.83180000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 10:59:59.28888+02	2021-07-03 10:59:59.289623+02	2021-07-03 10:59:59.575882+02	18.83180000	USDT
30	60e02814b793950006dca81d	ASK	2	UNI/USDT	1.00000000	UNI	18.91730000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-03 11:04:20.53723+02	2021-07-03 11:04:20.537971+02	2021-07-03 11:04:24.488514+02	18.91730000	USDT
31	60e029d502f19e000674056d	ASK	1	BTC/USDT	0.00100000	BTC	34742.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-03 11:11:49.296935+02	2021-07-03 11:11:49.297577+02	2021-07-03 11:11:54.489532+02	34742.90000000	USDT
32	60e029d53be065000688d5e1	ASK	1	BTC/USDT	0.00100000	BTC	34742.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-03 11:11:49.573608+02	2021-07-03 11:11:49.574398+02	2021-07-03 11:11:54.491697+02	34742.90000000	USDT
33	60e0526273ca9e0006a6e291	ASK	1	BTC/USDT	0.00100000	BTC	34757.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-03 14:04:50.79292+02	2021-07-03 14:04:50.796601+02	2021-07-03 14:04:54.783945+02	34757.40000000	USDT
34	60e0a1ae65141600065888cc	ASK	1	BTC/USDT	0.00100000	BTC	34890.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-03 19:43:10.431195+02	2021-07-03 19:43:10.43191+02	2021-07-03 19:43:13.242838+02	34890.10000000	USDT
35	60e0efbe651416000633d4e9	BID	1	BTC/USDT	0.00100000	BTC	34397.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-04 01:16:14.787848+02	2021-07-04 01:16:14.80505+02	2021-07-04 01:16:20.039163+02	34397.30000000	USDT
36	60e0efc1b9e8af0006010841	BID	2	UNI/USDT	1.00000000	UNI	18.87850000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-04 01:16:17.484758+02	2021-07-04 01:16:17.485845+02	2021-07-04 01:16:20.045584+02	18.87850000	USDT
37	60e10bdd02f19e0006ecadc6	BID	1	BTC/USDT	0.00100000	BTC	34409.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-04 03:16:13.727713+02	2021-07-04 03:16:13.743325+02	2021-07-04 03:16:14.695+02	34409.10000000	USDT
38	60e10be23be065000601b3c8	BID	2	UNI/USDT	1.00000000	UNI	19.02020000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-04 03:16:18.223076+02	2021-07-04 03:16:18.223976+02	2021-07-04 03:16:20.687616+02	19.02020000	USDT
39	60e14a5238ec010006a436d5	ASK	1	BTC/USDT	0.00100000	BTC	35023.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-04 07:42:42.256917+02	2021-07-04 07:42:42.262496+02	2021-07-04 07:42:45.966343+02	35023.90000000	USDT
40	60e14da865141600063b4290	ASK	2	UNI/USDT	1.00000000	UNI	20.13260000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-04 07:56:56.301405+02	2021-07-04 07:56:56.303459+02	2021-07-04 07:56:58.105599+02	20.13260000	USDT
41	60e14daed0db1700066a32b9	ASK	2	UNI/USDT	1.00000000	UNI	20.18400000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-04 07:57:02.616074+02	2021-07-04 07:57:02.616801+02	2021-07-04 07:57:04.104903+02	20.18400000	USDT
42	60e24f5b651416000621c8da	BID	1	BTC/USDT	0.00100000	BTC	34980.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-05 02:16:27.838913+02	2021-07-05 02:16:27.844482+02	2021-07-05 02:16:32.923848+02	34980.20000000	USDT
43	60e25d6b614b0f0006505b14	BID	1	BTC/USDT	0.00100000	BTC	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	BTC	\N	2021-07-05 03:16:27.009+02	2021-07-05 03:16:27.149424+02	2021-07-05 03:16:27.184831+02	34905.60000000	USDT
44	60e26b7ad0db170006b8497f	BID	1	BTC/USDT	0.00100000	BTC	34548.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-05 04:16:26.916628+02	2021-07-05 04:16:26.92923+02	2021-07-05 04:16:32.923523+02	34548.70000000	USDT
45	60e26b7bd0db170006b84a45	BID	2	UNI/USDT	1.00000000	UNI	20.45530000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-05 04:16:27.238959+02	2021-07-05 04:16:27.239986+02	2021-07-05 04:16:32.926629+02	20.45530000	USDT
46	60e2798bd0db170006e96c1a	BID	1	BTC/USDT	0.00100000	BTC	34482.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-05 05:16:27.882323+02	2021-07-05 05:16:27.88303+02	2021-07-05 05:16:29.078351+02	34482.40000000	USDT
47	60e2798c73ca9e0006dc3296	BID	2	UNI/USDT	1.00000000	UNI	20.42320000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-05 05:16:28.232172+02	2021-07-05 05:16:28.233026+02	2021-07-05 05:16:29.080984+02	20.42320000	USDT
48	60e2879c3be06500065da55f	BID	1	BTC/USDT	0.00100000	BTC	34171.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-05 06:16:28.891298+02	2021-07-05 06:16:28.891971+02	2021-07-05 06:16:33.320854+02	34171.50000000	USDT
49	60e2879db793950006b82e63	BID	2	UNI/USDT	1.00000000	UNI	20.11550000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-05 06:16:29.170463+02	2021-07-05 06:16:29.171307+02	2021-07-05 06:16:33.323387+02	20.11550000	USDT
50	60e295ad1f8b450006893ab4	BID	1	BTC/USDT	0.00100000	BTC	34180.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-05 07:16:29.883594+02	2021-07-05 07:16:29.886036+02	2021-07-05 07:16:33.613498+02	34180.80000000	USDT
51	60e295ae2fd9fd000622fd19	BID	2	UNI/USDT	1.00000000	UNI	20.17890000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-05 07:16:30.170594+02	2021-07-05 07:16:30.171431+02	2021-07-05 07:16:33.616059+02	20.17890000	USDT
52	60e2a3bd73ca9e0006623e41	BID	1	BTC/USDT	0.00100000	BTC	34307.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-05 08:16:29.628573+02	2021-07-05 08:16:29.629284+02	2021-07-05 08:16:33.613665+02	34307.00000000	USDT
53	60e2a3bd8111820006ec9a3d	BID	2	UNI/USDT	1.00000000	UNI	20.41130000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-05 08:16:29.921993+02	2021-07-05 08:16:29.922833+02	2021-07-05 08:16:33.616114+02	20.41130000	USDT
54	60e3c67373ca9e000628d195	ASK	2	UNI/USDT	1.00000000	UNI	21.28960000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-06 04:56:51.183926+02	2021-07-06 04:56:51.229416+02	2021-07-06 04:56:53.310865+02	21.28960000	USDT
55	60e3cd3373ca9e00063d7b40	ASK	2	UNI/USDT	1.00000000	UNI	21.38480000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-06 05:25:39.912949+02	2021-07-06 05:25:39.915074+02	2021-07-06 05:25:40.507279+02	21.38480000	USDT
56	60e3d530b9e8af000600523c	ASK	2	UNI/USDT	1.00000000	UNI	21.64880000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-06 05:59:44.263648+02	2021-07-06 05:59:44.264437+02	2021-07-06 05:59:47.175652+02	21.64880000	USDT
57	60e3d5311f8b450006aa4488	ASK	2	UNI/USDT	1.00000000	UNI	21.64880000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-06 05:59:45.515773+02	2021-07-06 05:59:45.516747+02	2021-07-06 05:59:47.178109+02	21.64880000	USDT
58	60e3d61f25754300063a0611	ASK	2	UNI/USDT	1.00000000	UNI	21.68080000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-06 06:03:43.842704+02	2021-07-06 06:03:43.843455+02	2021-07-06 06:03:47.442752+02	21.68080000	USDT
59	60e42bdc3be0650006e0d5e1	BID	1	BTC/USDT	0.00100000	BTC	34063.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-06 12:09:32.383388+02	2021-07-06 12:09:32.391544+02	2021-07-06 12:09:32.664031+02	34063.30000000	USDT
60	60e439eb8111820006516339	BID	1	BTC/USDT	0.00100000	BTC	33954.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-06 13:09:31.49177+02	2021-07-06 13:09:31.502497+02	2021-07-06 13:09:32.662551+02	33954.90000000	USDT
61	60e447fc8111820006880e01	BID	1	BTC/USDT	0.00100000	BTC	33955.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-06 14:09:32.878556+02	2021-07-06 14:09:32.879401+02	2021-07-06 14:09:38.662005+02	33955.70000000	USDT
62	60ee58ef2fd9fd00068364fc	BID	1	BTC/USDT	0.00105222	BTC	31924.60000000	USDT	\N	\N	\N	NEW	0.00105222	BTC	\N	2021-07-14 05:24:32.040925+02	2021-07-14 05:24:32.170854+02	2021-07-14 05:24:33.151124+02	31924.60000000	USDT
63	60ee593bd0db170006b1ae05	BID	1	BTC/USDT	0.00105078	BTC	31732.30000000	USDT	\N	\N	\N	NEW	0.00105078	BTC	\N	2021-07-14 05:25:47.357855+02	2021-07-14 05:25:47.361623+02	2021-07-14 05:25:49.213112+02	31732.30000000	USDT
64	60ee596c02f19e0006d71796	BID	1	BTC/USDT	0.00105106	BTC	31680.60000000	USDT	\N	\N	\N	NEW	0.00105106	BTC	\N	2021-07-14 05:26:36.650674+02	2021-07-14 05:26:36.651699+02	2021-07-14 05:26:37.112375+02	31680.60000000	USDT
65	60ee74b0b9e8af00069b01ca	BID	2	UNI/USDT	1.00000000	UNI	17.31420000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-14 07:22:56.209679+02	2021-07-14 07:22:56.225091+02	2021-07-14 07:22:57.119745+02	17.31420000	USDT
66	60eeacfdb9e8af0006435458	ASK	1	BTC/USDT	0.00100000	BTC	32362.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-14 11:23:09.897565+02	2021-07-14 11:23:09.898517+02	2021-07-14 11:23:11.113211+02	32362.00000000	USDT
67	60eebb15b9e8af0006748e86	ASK	1	BTC/USDT	0.00100000	BTC	32418.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-14 12:23:17.913382+02	2021-07-14 12:23:17.914308+02	2021-07-14 12:23:19.112328+02	32418.90000000	USDT
68	60eec9280edc1100063ce8c4	ASK	1	BTC/USDT	0.00100000	BTC	32473.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-14 13:23:20.468525+02	2021-07-14 13:23:20.485234+02	2021-07-14 13:23:21.112988+02	32473.40000000	USDT
69	60ef8ce60edc1100066f18a8	ASK	2	UNI/USDT	1.00000000	UNI	18.34530000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-15 03:18:30.929705+02	2021-07-15 03:18:30.945199+02	2021-07-15 03:18:34.803368+02	18.34530000	USDT
70	60efb88bb064d20006ef9d99	BID	2	UNI/USDT	1.00000000	UNI	17.66360000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-15 06:24:43.29819+02	2021-07-15 06:24:43.299096+02	2021-07-15 06:24:43.535392+02	17.66360000	USDT
71	60f5718c0e9305000628962f	BID	1	BTC/USDT	0.00105162	BTC	30819.50000000	USDT	\N	\N	\N	NEW	0.00105162	BTC	\N	2021-07-19 14:35:24.232399+02	2021-07-19 14:35:24.236369+02	2021-07-19 14:35:24.581464+02	30819.50000000	USDT
72	60f5718cb064d20006c33b7c	BID	1	BTC/USDT	0.00105027	BTC	30819.50000000	USDT	\N	\N	\N	NEW	0.00105027	BTC	\N	2021-07-19 14:35:24.762621+02	2021-07-19 14:35:24.763936+02	2021-07-19 14:35:29.089743+02	30819.50000000	USDT
73	60f5718c1bac9d00061646a5	BID	1	BTC/USDT	0.00105339	BTC	30819.50000000	USDT	\N	\N	\N	NEW	0.00105339	BTC	\N	2021-07-19 14:35:25.056161+02	2021-07-19 14:35:25.058792+02	2021-07-19 14:35:29.099962+02	30819.50000000	USDT
74	60f6389e367fab0006429566	ASK	2	UNI/USDT	1.00000000	UNI	15.00550000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-20 04:44:46.362652+02	2021-07-20 04:44:46.377827+02	2021-07-20 04:44:47.368694+02	15.00550000	USDT
75	60f640aaaf6a4300063c2538	ASK	1	BTC/USDT	0.00100000	BTC	29625.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-20 05:19:06.484431+02	2021-07-20 05:19:06.4856+02	2021-07-20 05:19:07.369591+02	29625.70000000	USDT
76	60f640ab367fab0006643d0d	ASK	1	BTC/USDT	0.00100000	BTC	29625.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-20 05:19:07.739672+02	2021-07-20 05:19:07.741027+02	2021-07-20 05:19:11.369165+02	29625.70000000	USDT
77	60f64497c65d020006a01687	BID	1	BTC/USDT	0.00100000	BTC	29597.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-20 05:35:51.686905+02	2021-07-20 05:35:51.688242+02	2021-07-20 05:35:52.187654+02	29597.80000000	USDT
78	60f644980c86de0006efe097	BID	2	UNI/USDT	1.00000000	UNI	14.73490000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-20 05:35:52.845494+02	2021-07-20 05:35:52.846863+02	2021-07-20 05:35:56.188525+02	14.73490000	USDT
79	60f652b28a20ae0006e906c2	BID	1	BTC/USDT	0.00100000	BTC	29604.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-20 06:36:02.843506+02	2021-07-20 06:36:02.846257+02	2021-07-20 06:36:03.030462+02	29604.00000000	USDT
80	60f652b3773f730006c6440d	BID	2	UNI/USDT	1.00000000	UNI	14.68790000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-20 06:36:03.199388+02	2021-07-20 06:36:03.202034+02	2021-09-03 20:14:24.689725+02	14.68790000	USDT
81	60f69ee2ac5138000631c5c5	ASK	1	BTC/USDT	0.00100000	BTC	29328.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-20 12:01:06.168099+02	2021-07-20 12:01:06.169763+02	2021-07-20 12:01:07.069228+02	29328.70000000	USDT
82	60f6a7640c86de0006215ec5	BID	2	UNI/USDT	1.00000000	UNI	14.54950000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-20 12:37:24.364566+02	2021-07-20 12:37:24.36546+02	2021-07-20 12:37:27.055053+02	14.54950000	USDT
83	60f6d1c0367fab0006221e15	BID	2	UNI/USDT	1.00000000	UNI	14.56580000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-20 15:38:09.008676+02	2021-07-20 15:38:09.010148+02	2021-07-20 15:38:11.071482+02	14.56580000	USDT
84	60f6db28af6a430006190c9b	ASK	1	BTC/USDT	0.00100000	BTC	29298.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-20 16:18:16.408679+02	2021-07-20 16:18:16.409548+02	2021-07-20 16:18:19.882529+02	29298.20000000	USDT
85	60f6dfe9af6a4300062b434f	BID	2	UNI/USDT	1.00000000	UNI	14.61570000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-20 16:38:33.918433+02	2021-07-20 16:38:33.919323+02	2021-07-20 16:38:37.88342+02	14.61570000	USDT
86	60f738ea0e93050006918a78	ASK	1	BTC/USDT	0.00100000	BTC	29856.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-20 22:58:18.232344+02	2021-07-20 22:58:18.352434+02	2021-07-20 22:58:21.363751+02	29856.80000000	USDT
87	60f738eb8a20ae000686f7d2	BID	2	UNI/USDT	1.00000000	UNI	14.73740000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-20 22:58:19.14459+02	2021-07-20 22:58:19.161511+02	2021-07-20 22:58:21.422759+02	14.73740000	USDT
88	60f7632daf6a4300068b0c89	ASK	1	BTC/USDT	0.00100000	BTC	29794.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-21 01:58:37.983962+02	2021-07-21 01:58:38.042152+02	2021-07-21 01:58:43.960332+02	29794.50000000	USDT
89	60f77f7d0c86de00067ac46a	ASK	1	BTC/USDT	0.00100000	BTC	29779.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-21 03:59:25.698486+02	2021-07-21 03:59:25.699801+02	2021-07-21 03:59:27.959921+02	29779.20000000	USDT
90	60f7a7335c1a2c000655d8a9	ASK	2	UNI/USDT	1.00000000	UNI	15.47160000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-21 06:48:52.118712+02	2021-07-21 06:48:52.119893+02	2021-07-21 06:48:52.209657+02	15.47160000	USDT
91	60f7a734c3540d00068cb684	ASK	2	UNI/USDT	1.00000000	UNI	15.47160000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-21 06:48:52.521846+02	2021-07-21 06:48:52.522948+02	2021-07-21 06:48:56.209819+02	15.47160000	USDT
92	60f7a74fac513800061e945a	ASK	2	UNI/USDT	1.00000000	UNI	15.49350000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-21 06:49:19.327481+02	2021-07-21 06:49:19.328374+02	2021-07-21 06:49:21.244825+02	15.49350000	USDT
93	60f7bb78456dfd000635a335	ASK	2	UNI/USDT	1.00000000	UNI	15.64370000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-21 08:15:20.927263+02	2021-07-21 08:15:20.928436+02	2021-07-21 08:15:21.245607+02	15.64370000	USDT
94	60f7bb791bac9d0006e8a580	ASK	2	UNI/USDT	1.00000000	UNI	15.64370000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-21 08:15:21.228026+02	2021-07-21 08:15:21.229516+02	2021-07-21 08:15:21.249907+02	15.64370000	USDT
95	60f7ef27b064d200061b409b	ASK	1	BTC/USDT	0.00100000	BTC	31153.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-21 11:55:51.978606+02	2021-07-21 11:55:51.979788+02	2021-07-21 11:55:53.647259+02	31153.10000000	USDT
96	60f7ef28af6a4300060f36df	ASK	1	BTC/USDT	0.00100000	BTC	31153.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-21 11:55:52.381+02	2021-07-21 11:55:52.381717+02	2021-07-21 11:55:53.652359+02	31153.10000000	USDT
97	60f942c0456dfd0006a6b076	BID	2	UNI/USDT	1.00000000	UNI	16.32980000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-22 12:04:48.246974+02	2021-07-22 12:04:48.259908+02	2021-07-22 12:04:50.387415+02	16.32980000	USDT
98	60f950dc0edc1100061477c7	BID	1	BTC/USDT	0.00100000	BTC	31846.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-22 13:05:00.918203+02	2021-07-22 13:05:00.919414+02	2021-07-22 13:05:02.388105+02	31846.30000000	USDT
99	60f950dd0e930500068ffefc	BID	2	UNI/USDT	1.00000000	UNI	16.37040000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-22 13:05:01.39304+02	2021-07-22 13:05:01.394303+02	2021-07-22 13:05:02.405516+02	16.37040000	USDT
100	60f95f00c65d02000694155e	BID	1	BTC/USDT	0.00100000	BTC	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	BTC	\N	2021-07-22 14:05:20.534+02	2021-07-22 14:05:20.693733+02	2021-07-22 14:05:20.791828+02	31863.30000000	USDT
101	60f95f005c1a2c0006491832	BID	2	UNI/USDT	1.00000000	UNI	16.35540000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-22 14:05:21.163249+02	2021-07-22 14:05:21.168996+02	2021-07-22 14:05:26.404335+02	16.35540000	USDT
102	60f99577367fab000607249c	ASK	2	UNI/USDT	1.00000000	UNI	17.29530000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-22 17:57:43.793348+02	2021-07-22 17:57:43.812903+02	2021-07-22 17:57:44.828439+02	17.29530000	USDT
103	60f99609c65d02000632440d	ASK	2	UNI/USDT	1.00000000	UNI	17.33820000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-22 18:00:09.731431+02	2021-07-22 18:00:09.732312+02	2021-07-22 18:00:12.827744+02	17.33820000	USDT
104	60f99620ac51380006b027c5	ASK	2	UNI/USDT	1.00000000	UNI	17.44500000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-22 18:00:32.698857+02	2021-07-22 18:00:32.699658+02	2021-07-22 18:00:32.826898+02	17.44500000	USDT
105	60fa951cac5138000626f228	BID	1	BTC/USDT	0.00100000	BTC	32326.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-23 12:08:28.630393+02	2021-07-23 12:08:28.634817+02	2021-07-23 12:08:28.940642+02	32326.80000000	USDT
106	60fab146c65d020006eddf82	BID	1	BTC/USDT	0.00100000	BTC	32273.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-23 14:08:38.35439+02	2021-07-23 14:08:38.355324+02	2021-07-23 14:08:40.901654+02	32273.00000000	USDT
107	60fab1468a20ae0006038195	BID	2	UNI/USDT	1.00000000	UNI	17.33590000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-23 14:08:38.773415+02	2021-07-23 14:08:38.776258+02	2021-07-23 14:08:40.910191+02	17.33590000	USDT
108	60fb4e100edc1100061f3dce	ASK	2	UNI/USDT	1.00000000	UNI	18.39360000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-24 01:17:37.116999+02	2021-07-24 01:17:37.128363+02	2021-07-24 01:17:39.170944+02	18.39360000	USDT
109	60fb52378a20ae00069cfc79	ASK	1	BTC/USDT	0.00100000	BTC	33443.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-24 01:35:19.566934+02	2021-07-24 01:35:19.567775+02	2021-07-24 01:35:23.174995+02	33443.10000000	USDT
110	60fb5259456dfd0006e3b152	ASK	1	BTC/USDT	0.00100000	BTC	33474.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-24 01:35:53.691174+02	2021-07-24 01:35:53.69203+02	2021-07-24 01:35:57.172102+02	33474.80000000	USDT
111	60fbb7f1773f7300067c2e97	ASK	1	BTC/USDT	0.00100000	BTC	33889.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-24 08:49:21.636722+02	2021-07-24 08:49:21.647422+02	2021-07-24 08:49:23.172202+02	33889.10000000	USDT
112	60fbf63ac3540d00060ab4c1	ASK	1	BTC/USDT	0.00100000	BTC	33958.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-24 13:15:06.450875+02	2021-07-24 13:15:06.45188+02	2021-07-24 13:15:09.171829+02	33958.30000000	USDT
113	60fc3bbf1bac9d0006d8a0d7	BID	2	UNI/USDT	1.00000000	UNI	18.14530000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-24 18:11:43.507155+02	2021-07-24 18:11:43.51648+02	2021-07-24 18:11:47.172204+02	18.14530000	USDT
114	60fc66010e930500065ea4db	BID	2	UNI/USDT	1.00000000	UNI	18.28000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-24 21:12:01.939585+02	2021-07-24 21:12:01.940406+02	2021-07-24 21:12:03.172107+02	18.28000000	USDT
115	60fc7414af6a430006155c0a	BID	2	UNI/USDT	1.00000000	UNI	18.12020000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-24 22:12:04.926786+02	2021-07-24 22:12:04.928043+02	2021-07-24 22:12:07.171863+02	18.12020000	USDT
116	60fc8225af6a4300063babad	BID	2	UNI/USDT	1.00000000	UNI	18.06470000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-24 23:12:05.467751+02	2021-07-24 23:12:05.46911+02	2021-07-24 23:12:07.171905+02	18.06470000	USDT
117	60fc903d367fab000684377f	BID	2	UNI/USDT	1.00000000	UNI	18.22000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-25 00:12:13.941287+02	2021-07-25 00:12:13.94215+02	2021-07-25 00:12:15.171215+02	18.22000000	USDT
118	60fc9e52c3540d0006bce502	BID	2	UNI/USDT	1.00000000	UNI	18.27000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-25 01:12:18.997016+02	2021-07-25 01:12:18.997889+02	2021-07-25 01:12:19.170646+02	18.27000000	USDT
119	60fcac640edc1100069ba497	BID	2	UNI/USDT	1.00000000	UNI	18.32510000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-25 02:12:20.967429+02	2021-07-25 02:12:20.96985+02	2021-07-25 02:12:23.170322+02	18.32510000	USDT
120	60fcba73456dfd00067d9d0c	BID	2	UNI/USDT	1.00000000	UNI	17.96450000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-25 03:12:20.067435+02	2021-07-25 03:12:20.068333+02	2021-07-25 03:12:23.174916+02	17.96450000	USDT
121	60fdf62c0edc110006d349d2	BID	1	BTC/USDT	0.00084985	BTC	35032.20000000	USDT	\N	\N	\N	NEW	0.00084985	BTC	\N	2021-07-26 01:39:24.755851+02	2021-07-26 01:39:24.767952+02	2021-07-26 01:39:25.200456+02	35032.20000000	USDT
122	60fdf643773f730006208fb7	BID	1	BTC/USDT	0.00084984	BTC	35064.40000000	USDT	\N	\N	\N	NEW	0.00084984	BTC	\N	2021-07-26 01:39:48.090271+02	2021-07-26 01:39:48.090965+02	2021-07-26 01:39:51.200036+02	35064.40000000	USDT
123	60fdf69e456dfd00068bf55b	BID	1	BTC/USDT	0.00084993	BTC	35140.50000000	USDT	\N	\N	\N	NEW	0.00084993	BTC	\N	2021-07-26 01:41:18.522011+02	2021-07-26 01:41:18.522743+02	2021-07-26 01:41:21.200643+02	35140.50000000	USDT
124	60fdfe3ab064d20006042f45	ASK	1	BTC/USDT	0.00100000	BTC	35788.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-26 02:13:47.122268+02	2021-07-26 02:13:47.123018+02	2021-07-26 02:13:47.201103+02	35788.90000000	USDT
125	60fdfe3b456dfd0006a54348	ASK	1	BTC/USDT	0.00100000	BTC	35788.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-26 02:13:47.412081+02	2021-07-26 02:13:47.412904+02	2021-07-26 02:13:53.203747+02	35788.90000000	USDT
126	60fdfe3bac51380006c74538	ASK	1	BTC/USDT	0.00100000	BTC	35788.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-26 02:13:47.719781+02	2021-07-26 02:13:47.72346+02	2021-07-26 02:13:53.217527+02	35788.90000000	USDT
127	60fe05dbc3540d00064ffbf3	ASK	1	BTC/USDT	0.00100000	BTC	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	BTC	\N	2021-07-26 02:46:19.875+02	2021-07-26 02:46:20.054996+02	2021-07-26 02:46:20.179188+02	35871.60000000	USDT
128	60fe06288a20ae00067b208c	ASK	1	BTC/USDT	0.00100000	BTC	35875.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-26 02:47:36.707789+02	2021-07-26 02:47:36.708671+02	2021-07-26 02:47:39.890405+02	35875.50000000	USDT
129	60fe074a1bac9d00067a9487	ASK	1	BTC/USDT	0.00100000	BTC	36089.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-26 02:52:26.91985+02	2021-07-26 02:52:26.920507+02	2021-07-26 02:52:30.181446+02	36089.20000000	USDT
130	60fe077c367fab000642564d	ASK	1	BTC/USDT	0.00100000	BTC	36126.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-26 02:53:16.847914+02	2021-07-26 02:53:16.848661+02	2021-07-26 02:53:20.491868+02	36126.60000000	USDT
131	60fe078fc3540d000656aa89	ASK	1	BTC/USDT	0.00100000	BTC	36135.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-26 02:53:35.802589+02	2021-07-26 02:53:35.803575+02	2021-07-26 02:53:38.490828+02	36135.40000000	USDT
132	60fe088dac51380006ecf0bb	ASK	2	UNI/USDT	1.00000000	UNI	19.06030000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 02:57:49.877063+02	2021-07-26 02:57:49.877801+02	2021-07-26 02:57:52.490466+02	19.06030000	USDT
133	60fe08be5c1a2c0006254b87	ASK	2	UNI/USDT	1.00000000	UNI	19.16590000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 02:58:38.823006+02	2021-07-26 02:58:38.823741+02	2021-07-26 02:58:40.491166+02	19.16590000	USDT
134	60fe0950367fab00064aced1	ASK	2	UNI/USDT	1.00000000	UNI	19.24000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 03:01:04.183289+02	2021-07-26 03:01:04.184197+02	2021-07-26 03:01:04.490799+02	19.24000000	USDT
135	60fe095b456dfd0006cf52e6	ASK	2	UNI/USDT	1.00000000	UNI	19.30400000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 03:01:15.419698+02	2021-07-26 03:01:15.432746+02	2021-07-26 03:01:16.49112+02	19.30400000	USDT
136	60fe096e1bac9d000684db00	ASK	2	UNI/USDT	1.00000000	UNI	19.36580000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 03:01:34.344003+02	2021-07-26 03:01:34.344785+02	2021-07-26 03:01:36.491013+02	19.36580000	USDT
137	60fe096ec3540d00065fb26b	ASK	2	UNI/USDT	1.00000000	UNI	19.36580000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 03:01:34.678641+02	2021-07-26 03:01:34.679423+02	2021-07-26 03:01:36.499798+02	19.36580000	USDT
138	60fe0978c65d020006747c16	ASK	2	UNI/USDT	1.00000000	UNI	19.41390000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 03:01:44.873934+02	2021-07-26 03:01:44.874839+02	2021-07-26 03:01:48.489752+02	19.41390000	USDT
139	60fe097e0e930500069410b3	ASK	2	UNI/USDT	1.00000000	UNI	19.48100000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-26 03:01:50.738005+02	2021-07-26 03:01:50.738749+02	2021-07-26 03:01:52.490848+02	19.48100000	USDT
140	60ff4c26b064d20006911da7	BID	1	BTC/USDT	0.00100000	BTC	37335.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-27 01:58:31.157225+02	2021-07-27 01:58:31.504179+02	2021-07-27 01:58:33.030056+02	37335.40000000	USDT
141	60ff4c28367fab0006ae5b06	BID	2	UNI/USDT	1.00000000	UNI	18.59710000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 01:58:32.603116+02	2021-07-27 01:58:32.605053+02	2021-07-27 01:58:33.111094+02	18.59710000	USDT
142	60ff5a3a5c1a2c0006bf807b	BID	2	UNI/USDT	1.00000000	UNI	18.67090000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 02:58:34.558198+02	2021-07-27 02:58:34.579138+02	2021-07-27 02:58:38.091355+02	18.67090000	USDT
143	60ff6848c65d0200063fb556	BID	1	BTC/USDT	0.00100000	BTC	36890.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-27 03:58:32.764406+02	2021-07-27 03:58:32.765475+02	2021-07-27 03:58:34.21891+02	36890.50000000	USDT
144	60ff684faf6a430006edf68c	BID	2	UNI/USDT	1.00000000	UNI	18.04710000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 03:58:39.663344+02	2021-07-27 03:58:39.664353+02	2021-07-27 03:58:40.081879+02	18.04710000	USDT
145	60ff76580e930500068f6c5c	BID	1	BTC/USDT	0.00100000	BTC	36536.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-27 04:58:32.43244+02	2021-07-27 04:58:32.442717+02	2021-07-27 04:58:36.081415+02	36536.90000000	USDT
146	60ff765eb064d200062b3387	BID	2	UNI/USDT	1.00000000	UNI	17.82760000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 04:58:39.112674+02	2021-07-27 04:58:39.113595+02	2021-07-27 04:58:42.081312+02	17.82760000	USDT
147	60ff84670edc110006413c13	BID	1	BTC/USDT	0.00100000	BTC	36833.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-27 05:58:31.807651+02	2021-07-27 05:58:31.809109+02	2021-07-27 05:58:34.081499+02	36833.90000000	USDT
148	60ff84720c86de0006eb1f7a	BID	2	UNI/USDT	1.00000000	UNI	18.02440000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 05:58:42.171946+02	2021-07-27 05:58:42.173036+02	2021-07-27 05:58:44.221559+02	18.02440000	USDT
149	60fff33346ae4a00068c84b2	ASK	2	UNI/USDT	1.00000000	UNI	18.92220000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 13:51:15.861699+02	2021-07-27 13:51:15.872304+02	2021-07-27 13:51:18.805274+02	18.92220000	USDT
150	60fffef39cd98c000665d861	ASK	1	BTC/USDT	0.00100000	BTC	38362.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-27 14:41:23.209722+02	2021-07-27 14:41:23.210641+02	2021-07-27 14:41:26.805453+02	38362.80000000	USDT
151	6100010a46ae4a0006b2e84a	ASK	2	UNI/USDT	1.00000000	UNI	19.11110000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 14:50:19.040897+02	2021-07-27 14:50:19.041638+02	2021-07-27 14:50:20.806558+02	19.11110000	USDT
152	6100014e9281bc0006ecb8a7	ASK	2	UNI/USDT	1.00000000	UNI	19.12570000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-27 14:51:26.863814+02	2021-07-27 14:51:26.864583+02	2021-07-27 14:51:28.805502+02	19.12570000	USDT
153	61000c1bf2e73f0006ac9823	ASK	1	BTC/USDT	0.00100000	BTC	38688.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-27 15:37:31.855252+02	2021-07-27 15:37:31.856007+02	2021-07-27 15:37:32.805047+02	38688.60000000	USDT
154	61000cc2293b290006f599c4	ASK	1	BTC/USDT	0.00100000	BTC	38753.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-27 15:40:18.346053+02	2021-07-27 15:40:18.346962+02	2021-07-27 15:40:18.80572+02	38753.10000000	USDT
155	61008e51c969c10006e1303a	ASK	1	BTC/USDT	0.00100000	BTC	39191.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-07-28 00:53:06.044894+02	2021-07-28 00:53:06.063196+02	2021-07-28 00:53:07.243532+02	39191.60000000	USDT
156	61033ae3293b2900060efd37	ASK	2	UNI/USDT	1.00000000	UNI	19.68920000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-30 01:33:55.852096+02	2021-07-30 01:33:55.856062+02	2021-07-30 01:33:56.978945+02	19.68920000	USDT
157	61034a5ab4860f0006cc22ae	ASK	2	UNI/USDT	1.00000000	UNI	19.80830000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-07-30 02:39:54.361802+02	2021-07-30 02:39:54.362586+02	2021-07-30 02:39:56.971667+02	19.80830000	USDT
158	6108325bf2e73f00060fab7e	BID	1	BTC/USDT	0.00100000	BTC	39705.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-02 19:58:51.733262+02	2021-08-02 19:58:51.920821+02	2021-08-02 19:58:55.287536+02	39705.70000000	USDT
159	6108325c39232b0006c5db63	BID	2	UNI/USDT	1.00000000	UNI	22.87350000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-02 19:58:52.838148+02	2021-08-02 19:58:52.844803+02	2021-08-02 19:58:55.308543+02	22.87350000	USDT
160	6108406cf2e73f000636140f	BID	1	BTC/USDT	0.00100000	BTC	39654.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-02 20:58:52.59027+02	2021-08-02 20:58:52.59208+02	2021-08-02 20:58:54.17373+02	39654.20000000	USDT
161	61084e7c39232b00061ae0da	BID	1	BTC/USDT	0.00100000	BTC	39156.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-02 21:58:52.639292+02	2021-08-02 21:58:52.640415+02	2021-08-02 21:58:56.174727+02	39156.90000000	USDT
162	61084e7c48b162000654f412	BID	2	UNI/USDT	1.00000000	UNI	22.60000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-02 21:58:52.940708+02	2021-08-02 21:58:52.942129+02	2021-08-02 21:58:56.179033+02	22.60000000	USDT
163	61085c8c46ae4a0006c23c5b	BID	1	BTC/USDT	0.00100000	BTC	38843.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-02 22:58:52.634087+02	2021-08-02 22:58:52.635482+02	2021-08-02 22:58:54.174411+02	38843.50000000	USDT
164	61085c8c48b162000689c7b2	BID	2	UNI/USDT	1.00000000	UNI	22.40330000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-02 22:58:52.95242+02	2021-08-02 22:58:52.95366+02	2021-08-02 22:58:54.178613+02	22.40330000	USDT
165	61086a9c293b290006078bb8	BID	1	BTC/USDT	0.00100000	BTC	39236.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-02 23:58:52.570654+02	2021-08-02 23:58:52.571532+02	2021-08-02 23:58:56.204648+02	39236.50000000	USDT
166	61086a9cf2e73f0006c13a4d	BID	2	UNI/USDT	1.00000000	UNI	22.50150000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-02 23:58:52.858729+02	2021-08-02 23:58:52.86024+02	2021-08-02 23:58:56.208407+02	22.50150000	USDT
167	610878ac9cd98c0006cd8f4a	BID	2	UNI/USDT	1.00000000	UNI	22.53030000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-03 00:58:52.786416+02	2021-08-03 00:58:52.787305+02	2021-08-03 00:58:54.312733+02	22.53030000	USDT
168	610886bc31ff620006f3ebf8	BID	1	BTC/USDT	0.00100000	BTC	39199.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-03 01:58:52.607915+02	2021-08-03 01:58:52.60902+02	2021-08-03 01:58:54.314762+02	39199.80000000	USDT
169	610886bc39232b0006c84d59	BID	2	UNI/USDT	1.00000000	UNI	22.38560000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-03 01:58:52.909984+02	2021-08-03 01:58:52.911208+02	2021-08-03 01:58:54.32942+02	22.38560000	USDT
170	610894cc9281bc0006afd84f	BID	1	BTC/USDT	0.00100000	BTC	39343.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-03 02:58:52.650787+02	2021-08-03 02:58:52.651737+02	2021-08-03 02:58:56.317604+02	39343.90000000	USDT
171	610894cc31ff6200062974c8	BID	2	UNI/USDT	1.00000000	UNI	22.51510000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-03 02:58:52.982707+02	2021-08-03 02:58:52.984094+02	2021-08-03 02:58:56.321771+02	22.51510000	USDT
172	6108a2dcf34b100006fd738e	BID	2	UNI/USDT	1.00000000	UNI	21.84790000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-03 03:58:52.19404+02	2021-08-03 03:58:52.195206+02	2021-08-03 03:58:55.373156+02	21.84790000	USDT
173	6109e634b4860f000682ace6	ASK	1	BTC/USDT	0.00100000	BTC	38480.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-04 02:58:28.803095+02	2021-08-04 02:58:28.824747+02	2021-08-04 02:58:31.715277+02	38480.80000000	USDT
174	6109f444b4860f0006ae7795	ASK	1	BTC/USDT	0.00100000	BTC	38350.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-04 03:58:28.711369+02	2021-08-04 03:58:28.712727+02	2021-08-04 03:58:29.704079+02	38350.20000000	USDT
175	610ac3d94b2a4900069f5b58	ASK	2	UNI/USDT	1.00000000	UNI	23.20500000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-04 18:44:09.628083+02	2021-08-04 18:44:09.674852+02	2021-08-04 18:44:10.979664+02	23.20500000	USDT
176	610b2b55f34b100006210d57	ASK	2	UNI/USDT	1.00000000	UNI	23.76820000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 02:05:41.661688+02	2021-08-05 02:05:41.674625+02	2021-08-05 02:05:42.96468+02	23.76820000	USDT
177	610b2b55c5b09d00060d5835	ASK	2	UNI/USDT	1.00000000	UNI	23.76820000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 02:05:42.023472+02	2021-08-05 02:05:42.024705+02	2021-08-05 02:05:42.977004+02	23.76820000	USDT
178	610b8c0db4860f0006766268	BID	1	BTC/USDT	0.00100000	BTC	39108.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 08:58:21.8426+02	2021-08-05 08:58:21.843572+02	2021-08-05 08:58:22.964898+02	39108.00000000	USDT
179	610ba82d293b2900063f19c9	BID	2	UNI/USDT	1.00000000	UNI	22.86000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 10:58:21.76357+02	2021-08-05 10:58:21.771104+02	2021-08-05 10:58:22.854535+02	22.86000000	USDT
180	610bf1ddc5b09d0006717285	ASK	2	UNI/USDT	1.00000000	UNI	23.88330000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 16:12:45.327145+02	2021-08-05 16:12:45.328724+02	2021-08-05 16:12:46.853846+02	23.88330000	USDT
182	610bf1e9f2e73f000600d2b0	ASK	2	UNI/USDT	1.00000000	UNI	23.93430000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 16:12:57.350255+02	2021-08-05 16:12:57.353411+02	2021-08-05 16:12:58.859834+02	23.93430000	USDT
181	610bf1e7c969c10006a761f8	ASK	2	UNI/USDT	1.00000000	UNI	23.90120000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 16:12:55.330276+02	2021-08-05 16:12:55.331164+02	2021-08-05 16:12:58.855276+02	23.90120000	USDT
183	610bf2774b2a4900062de0e0	ASK	2	UNI/USDT	1.00000000	UNI	23.98840000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 16:15:19.414735+02	2021-08-05 16:15:19.415802+02	2021-08-05 16:15:22.853343+02	23.98840000	USDT
184	610bf915c5b09d000693367a	ASK	2	UNI/USDT	1.00000000	UNI	24.26540000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 16:43:33.264188+02	2021-08-05 16:43:33.26512+02	2021-08-05 16:43:34.85536+02	24.26540000	USDT
185	610bf91546ae4a00064b7445	ASK	2	UNI/USDT	1.00000000	UNI	24.26540000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 16:43:33.578871+02	2021-08-05 16:43:33.579858+02	2021-08-05 16:43:34.859921+02	24.26540000	USDT
186	610bfc8c9281bc00069a5fe1	BID	1	BTC/USDT	0.00100000	BTC	38898.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 16:58:20.507643+02	2021-08-05 16:58:20.508622+02	2021-08-05 16:58:22.851791+02	38898.60000000	USDT
187	610c0a9df2e73f00066b1e8e	BID	1	BTC/USDT	0.00100000	BTC	38830.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 17:58:21.217129+02	2021-08-05 17:58:21.218401+02	2021-08-05 17:58:24.852921+02	38830.90000000	USDT
188	610c2732c969c1000681372f	ASK	1	BTC/USDT	0.00100000	BTC	40735.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 20:00:18.836508+02	2021-08-05 20:00:18.852041+02	2021-08-05 20:00:19.94631+02	40735.00000000	USDT
189	610c2d9f9281bc00065aa2a2	ASK	1	BTC/USDT	0.00100000	BTC	40794.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 20:27:43.994347+02	2021-08-05 20:27:43.995761+02	2021-08-05 20:27:49.942036+02	40794.00000000	USDT
190	610c2e4b48b1620006e7d93e	ASK	1	BTC/USDT	0.00100000	BTC	40859.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 20:30:35.965801+02	2021-08-05 20:30:35.967118+02	2021-08-05 20:30:37.942995+02	40859.40000000	USDT
191	610c34f49cd98c0006ef1590	BID	2	UNI/USDT	1.00000000	UNI	24.91120000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 20:59:00.702314+02	2021-08-05 20:59:00.703423+02	2021-08-05 20:59:01.953278+02	24.91120000	USDT
192	610c37289281bc00067c4b5c	ASK	1	BTC/USDT	0.00100000	BTC	41097.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 21:08:24.781231+02	2021-08-05 21:08:24.782452+02	2021-08-05 21:08:25.940876+02	41097.80000000	USDT
193	610c373a9281bc00067c98b9	ASK	1	BTC/USDT	0.00100000	BTC	41142.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 21:08:42.78867+02	2021-08-05 21:08:42.789704+02	2021-08-05 21:08:43.940605+02	41142.90000000	USDT
194	610c374748b1620006075405	ASK	1	BTC/USDT	0.00100000	BTC	41159.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 21:08:55.992295+02	2021-08-05 21:08:55.993351+02	2021-08-05 21:08:57.942659+02	41159.10000000	USDT
195	610c3788293b2900065beeef	ASK	1	BTC/USDT	0.00100000	BTC	41184.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 21:10:00.777492+02	2021-08-05 21:10:00.778581+02	2021-08-05 21:10:01.945612+02	41184.00000000	USDT
196	610c3824c5b09d0006885be1	ASK	1	BTC/USDT	0.00100000	BTC	41331.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 21:12:36.763167+02	2021-08-05 21:12:36.764357+02	2021-08-05 21:12:39.940533+02	41331.20000000	USDT
197	610c4303b4860f00060e3a93	BID	1	BTC/USDT	0.00100000	BTC	40647.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-05 21:58:59.514787+02	2021-08-05 21:58:59.515941+02	2021-08-05 21:59:01.942579+02	40647.30000000	USDT
198	610c430348b162000630a21a	BID	2	UNI/USDT	1.00000000	UNI	24.94300000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-05 21:58:59.840317+02	2021-08-05 21:58:59.841197+02	2021-08-05 21:59:01.946509+02	24.94300000	USDT
199	610c893348b16200060894a7	BID	1	BTC/USDT	0.00100000	BTC	40008.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 02:58:27.641617+02	2021-08-06 02:58:27.658788+02	2021-08-06 02:58:28.447281+02	40008.10000000	USDT
200	610c893648b162000608a271	BID	2	UNI/USDT	1.00000000	UNI	25.09850000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 02:58:30.708382+02	2021-08-06 02:58:30.710101+02	2021-08-06 02:58:34.4372+02	25.09850000	USDT
201	610ca55239232b00062259f6	BID	1	BTC/USDT	0.00100000	BTC	40391.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 04:58:26.655335+02	2021-08-06 04:58:26.668434+02	2021-08-06 04:58:30.437029+02	40391.90000000	USDT
202	610ca556c969c1000612bdef	BID	2	UNI/USDT	1.00000000	UNI	25.07040000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 04:58:30.701596+02	2021-08-06 04:58:30.702488+02	2021-08-06 04:58:36.435869+02	25.07040000	USDT
203	610cb36331ff62000677597a	BID	1	BTC/USDT	0.00100000	BTC	40218.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 05:58:27.573354+02	2021-08-06 05:58:27.574522+02	2021-08-06 05:58:28.437353+02	40218.10000000	USDT
204	610cb36787af8600065567ae	BID	2	UNI/USDT	1.00000000	UNI	24.84690000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 05:58:31.568879+02	2021-08-06 05:58:31.570005+02	2021-08-06 05:58:34.436605+02	24.84690000	USDT
205	610cc174b4860f00068f6591	BID	1	BTC/USDT	0.00100000	BTC	40122.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 06:58:28.389926+02	2021-08-06 06:58:28.391652+02	2021-08-06 06:58:30.437833+02	40122.60000000	USDT
206	610cc17887af8600067edbbb	BID	2	UNI/USDT	1.00000000	UNI	24.83000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 06:58:32.415806+02	2021-08-06 06:58:32.417029+02	2021-08-06 06:58:36.437361+02	24.83000000	USDT
207	610ccf88c969c100068c0fd1	BID	2	UNI/USDT	1.00000000	UNI	24.95870000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 07:58:32.417576+02	2021-08-06 07:58:32.419132+02	2021-08-06 07:58:32.434748+02	24.95870000	USDT
208	610ceba8293b2900068440ad	BID	2	UNI/USDT	1.00000000	UNI	24.84760000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 09:58:33.038933+02	2021-08-06 09:58:33.0403+02	2021-08-06 09:58:36.438857+02	24.84760000	USDT
209	610d531a31ff6200065b1414	ASK	1	BTC/USDT	0.00100000	BTC	41658.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 17:19:54.489626+02	2021-08-06 17:19:54.502893+02	2021-08-06 17:19:55.364643+02	41658.10000000	USDT
210	610d532b46ae4a0006a1e09d	ASK	1	BTC/USDT	0.00100000	BTC	41677.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 17:20:11.4609+02	2021-08-06 17:20:11.462364+02	2021-08-06 17:20:13.364731+02	41677.00000000	USDT
211	610d5c3c9cd98c00067f6bd8	ASK	1	BTC/USDT	0.00100000	BTC	42064.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 17:58:52.17026+02	2021-08-06 17:58:52.171405+02	2021-08-06 17:58:55.372306+02	42064.30000000	USDT
212	610d5c45293b290006e3e81b	ASK	1	BTC/USDT	0.00100000	BTC	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	BTC	\N	2021-08-06 17:59:01.423+02	2021-08-06 17:59:01.56671+02	2021-08-06 17:59:01.617018+02	42251.90000000	USDT
213	610d5c45f34b1000062190a3	ASK	1	BTC/USDT	0.00100000	BTC	42251.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 17:59:01.893005+02	2021-08-06 17:59:01.894212+02	2021-08-06 17:59:07.364272+02	42251.90000000	USDT
214	610d5cd2c969c10006469f33	ASK	1	BTC/USDT	0.00100000	BTC	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	BTC	\N	2021-08-06 18:01:22.965+02	2021-08-06 18:01:23.099487+02	2021-08-06 18:01:23.153704+02	42428.30000000	USDT
215	610d5d5f9cd98c0006858e86	ASK	1	BTC/USDT	0.00100000	BTC	42838.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-06 18:03:43.901152+02	2021-08-06 18:03:43.902421+02	2021-08-06 18:03:45.888579+02	42838.40000000	USDT
216	610d768d9cd98c0006ed3196	ASK	2	UNI/USDT	1.00000000	UNI	26.34670000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 19:51:09.811201+02	2021-08-06 19:51:09.813264+02	2021-08-06 19:51:11.890639+02	26.34670000	USDT
217	610d768d9cd98c0006ed330d	ASK	2	UNI/USDT	1.00000000	UNI	26.34670000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 19:51:10.107894+02	2021-08-06 19:51:10.109646+02	2021-08-06 19:51:11.893505+02	26.34670000	USDT
218	610d781046ae4a0006391e47	ASK	2	UNI/USDT	1.00000000	UNI	26.37270000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 19:57:36.275856+02	2021-08-06 19:57:36.277212+02	2021-08-06 19:57:39.901143+02	26.37270000	USDT
219	610d7a2cf2e73f0006188927	ASK	2	UNI/USDT	1.00000000	UNI	26.40480000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 20:06:36.308196+02	2021-08-06 20:06:36.31054+02	2021-08-06 20:06:39.887906+02	26.40480000	USDT
220	610d8799f34b100006c86553	ASK	2	UNI/USDT	1.00000000	UNI	26.45660000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 21:03:53.219077+02	2021-08-06 21:03:53.220249+02	2021-08-06 21:03:55.888326+02	26.45660000	USDT
221	610d87b748b16200063754ef	ASK	2	UNI/USDT	1.00000000	UNI	26.48760000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 21:04:23.168702+02	2021-08-06 21:04:23.16983+02	2021-08-06 21:04:23.889324+02	26.48760000	USDT
222	610d8888c5b09d0006b78b8d	ASK	2	UNI/USDT	1.00000000	UNI	26.52280000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 21:07:52.686514+02	2021-08-06 21:07:52.68766+02	2021-08-06 21:07:55.890728+02	26.52280000	USDT
223	610d88b89cd98c000629be7d	ASK	2	UNI/USDT	1.00000000	UNI	26.59740000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-06 21:08:40.585533+02	2021-08-06 21:08:40.586615+02	2021-08-06 21:08:43.889169+02	26.59740000	USDT
224	610f485cb4860f00064b0964	BID	2	UNI/USDT	1.00000000	UNI	27.53080000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 04:58:36.646075+02	2021-08-08 04:58:36.652619+02	2021-08-08 04:58:39.584473+02	27.53080000	USDT
225	610f795539232b0006c7fe21	BID	1	BTC/USDT	0.00084996	BTC	45113.60000000	USDT	\N	\N	\N	NEW	0.00084996	BTC	\N	2021-08-08 08:27:33.879311+02	2021-08-08 08:27:33.884125+02	2021-08-08 08:27:35.585759+02	45113.60000000	USDT
226	610f7ce987af860006e152c7	BID	1	BTC/USDT	0.00084975	BTC	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	BTC	\N	2021-08-08 08:42:49.62+02	2021-08-08 08:42:49.764725+02	2021-08-08 08:42:49.840094+02	45274.60000000	USDT
227	610fb8de46ae4a00060b7578	BID	2	UNI/USDT	1.00000000	UNI	27.82810000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 12:58:38.653129+02	2021-08-08 12:58:38.654327+02	2021-08-08 12:58:39.58848+02	27.82810000	USDT
228	610fc6edf34b10000694f593	BID	2	UNI/USDT	1.00000000	UNI	27.69970000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 13:58:37.828919+02	2021-08-08 13:58:37.831861+02	2021-08-08 13:58:39.587351+02	27.69970000	USDT
229	610fe30e4b2a4900069354b4	BID	2	UNI/USDT	1.00000000	UNI	27.76360000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 15:58:38.289824+02	2021-08-08 15:58:38.291424+02	2021-08-08 15:58:39.583816+02	27.76360000	USDT
230	610ff11f9cd98c000681d5d7	BID	1	BTC/USDT	0.00100000	BTC	44017.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-08 16:58:39.311578+02	2021-08-08 16:58:39.312776+02	2021-08-08 16:58:39.584461+02	44017.50000000	USDT
231	610ff11ff2e73f00069e87a5	BID	2	UNI/USDT	1.00000000	UNI	27.07450000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 16:58:39.603695+02	2021-08-08 16:58:39.60497+02	2021-08-08 16:58:45.588166+02	27.07450000	USDT
232	610fff2ec969c1000679f9d2	BID	1	BTC/USDT	0.00100000	BTC	43951.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-08 17:58:38.601363+02	2021-08-08 17:58:38.602858+02	2021-08-08 17:58:41.588739+02	43951.20000000	USDT
233	610fff2e293b2900061922be	BID	2	UNI/USDT	1.00000000	UNI	26.84780000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 17:58:38.90185+02	2021-08-08 17:58:38.903511+02	2021-08-08 17:58:41.592095+02	26.84780000	USDT
234	61100d3e39232b0006bcc7db	BID	1	BTC/USDT	0.00100000	BTC	43792.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-08 18:58:38.732988+02	2021-08-08 18:58:38.73463+02	2021-08-08 18:58:39.585692+02	43792.90000000	USDT
235	61101b4e9281bc0006a4ff7d	BID	1	BTC/USDT	0.00100000	BTC	43718.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-08 19:58:38.473499+02	2021-08-08 19:58:38.475249+02	2021-08-08 19:58:39.584578+02	43718.30000000	USDT
236	61101b4e46ae4a000665396c	BID	2	UNI/USDT	1.00000000	UNI	26.48360000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 19:58:38.786871+02	2021-08-08 19:58:38.788147+02	2021-08-08 19:58:39.58739+02	26.48360000	USDT
237	6110295e48b16200065d9b13	BID	1	BTC/USDT	0.00100000	BTC	43623.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-08 20:58:38.652541+02	2021-08-08 20:58:38.659895+02	2021-08-08 20:58:39.584832+02	43623.20000000	USDT
238	6110295ec969c100061219c9	BID	2	UNI/USDT	1.00000000	UNI	26.22260000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 20:58:38.989726+02	2021-08-08 20:58:38.991126+02	2021-08-08 20:58:39.588498+02	26.22260000	USDT
239	6110376d4b2a490006c92ede	BID	1	BTC/USDT	0.00100000	BTC	43618.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-08 21:58:37.950327+02	2021-08-08 21:58:37.963212+02	2021-08-08 21:58:39.585972+02	43618.70000000	USDT
240	6110376eb4860f000671095f	BID	2	UNI/USDT	1.00000000	UNI	26.18060000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-08 21:58:38.532783+02	2021-08-08 21:58:38.534504+02	2021-08-08 21:58:39.588954+02	26.18060000	USDT
241	6110fc4ef2e73f0006322a8b	ASK	1	BTC/USDT	0.00100000	BTC	44878.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 11:58:38.802618+02	2021-08-09 11:58:38.80366+02	2021-08-09 11:58:39.584205+02	44878.10000000	USDT
242	611105159cd98c000635fc44	ASK	2	UNI/USDT	1.00000000	UNI	27.77760000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-09 12:36:05.570454+02	2021-08-09 12:36:05.581992+02	2021-08-09 12:36:09.585735+02	27.77760000	USDT
243	61110515b4860f000625195c	ASK	2	UNI/USDT	1.00000000	UNI	27.77760000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-09 12:36:05.883076+02	2021-08-09 12:36:05.884341+02	2021-08-09 12:36:09.588587+02	27.77760000	USDT
244	61110a5f87af8600062a3152	ASK	1	BTC/USDT	0.00100000	BTC	45591.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 12:58:39.249925+02	2021-08-09 12:58:39.251528+02	2021-08-09 12:58:42.337349+02	45591.10000000	USDT
245	61110ad6293b290006b0a0a3	ASK	1	BTC/USDT	0.00100000	BTC	45745.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 13:00:38.660086+02	2021-08-09 13:00:38.661286+02	2021-08-09 13:00:40.3378+02	45745.50000000	USDT
246	61110ad6c969c10006119ddd	ASK	2	UNI/USDT	1.00000000	UNI	28.08640000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-09 13:00:38.974103+02	2021-08-09 13:00:38.975585+02	2021-08-09 13:00:40.341136+02	28.08640000	USDT
247	61110af5293b290006b13876	ASK	1	BTC/USDT	0.00100000	BTC	45851.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 13:01:09.576034+02	2021-08-09 13:01:09.577369+02	2021-08-09 13:01:10.337863+02	45851.00000000	USDT
248	611116fc48b16200068b575d	ASK	1	BTC/USDT	0.00100000	BTC	45920.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 13:52:29.038442+02	2021-08-09 13:52:29.039448+02	2021-08-09 13:52:30.337296+02	45920.20000000	USDT
249	6111174039232b00064ee5d5	ASK	1	BTC/USDT	0.00100000	BTC	45951.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 13:53:36.312166+02	2021-08-09 13:53:36.31321+02	2021-08-09 13:53:36.368512+02	45951.70000000	USDT
250	6111375e9281bc000676a8a3	ASK	1	BTC/USDT	0.00100000	BTC	46154.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 16:10:38.543154+02	2021-08-09 16:10:38.543928+02	2021-08-09 16:10:41.569417+02	46154.40000000	USDT
251	61114edd4b2a49000687e11e	ASK	2	UNI/USDT	1.00000000	UNI	28.46910000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-09 17:50:53.233102+02	2021-08-09 17:50:53.23421+02	2021-08-09 17:50:55.570802+02	28.46910000	USDT
252	6111763987af860006959abe	ASK	1	BTC/USDT	0.00100000	BTC	46175.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-09 20:38:49.601842+02	2021-08-09 20:38:49.604097+02	2021-08-09 20:38:51.567528+02	46175.20000000	USDT
253	6111765339232b0006891bf5	ASK	2	UNI/USDT	1.00000000	UNI	28.67460000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-09 20:39:15.655783+02	2021-08-09 20:39:15.657291+02	2021-08-09 20:39:17.569461+02	28.67460000	USDT
254	6111ff41f2e73f00067a8fb2	ASK	2	UNI/USDT	1.00000000	UNI	29.18590000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-10 06:23:29.186662+02	2021-08-10 06:23:29.188688+02	2021-08-10 06:23:31.242954+02	29.18590000	USDT
255	61120bb248b16200068e1022	ASK	2	UNI/USDT	1.00000000	UNI	29.33620000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-10 07:16:34.250961+02	2021-08-10 07:16:34.252782+02	2021-08-10 07:16:35.241752+02	29.33620000	USDT
256	61120c5c48b1620006900e39	ASK	2	UNI/USDT	1.00000000	UNI	29.47980000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-10 07:19:25.055014+02	2021-08-10 07:19:25.056272+02	2021-08-10 07:19:27.242367+02	29.47980000	USDT
257	61120c91c5b09d0006111bca	ASK	2	UNI/USDT	1.00000000	UNI	29.51150000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-10 07:20:17.678348+02	2021-08-10 07:20:17.679512+02	2021-08-10 07:20:19.242325+02	29.51150000	USDT
258	6112239c87af8600069b6b26	BID	1	BTC/USDT	0.00100000	BTC	45531.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 08:58:36.52567+02	2021-08-10 08:58:36.529404+02	2021-08-10 08:58:39.240487+02	45531.40000000	USDT
259	61125bdd48b16200066e370f	BID	1	BTC/USDT	0.00100000	BTC	45352.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 12:58:38.003911+02	2021-08-10 12:58:38.004841+02	2021-08-10 12:58:39.242085+02	45352.50000000	USDT
260	611269ed48b16200069c6708	BID	1	BTC/USDT	0.00100000	BTC	45346.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 13:58:37.626185+02	2021-08-10 13:58:37.626977+02	2021-08-10 13:58:39.242463+02	45346.50000000	USDT
261	6112860d31ff620006f6e866	BID	1	BTC/USDT	0.00100000	BTC	45599.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 15:58:37.706804+02	2021-08-10 15:58:37.708005+02	2021-08-10 15:58:39.245353+02	45599.10000000	USDT
262	6112941d31ff620006287855	BID	1	BTC/USDT	0.00100000	BTC	45372.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 16:58:37.506633+02	2021-08-10 16:58:37.507646+02	2021-08-10 16:58:41.240891+02	45372.60000000	USDT
263	6112a22df2e73f000676ffb8	BID	1	BTC/USDT	0.00100000	BTC	45098.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 17:58:38.062057+02	2021-08-10 17:58:38.063107+02	2021-08-10 17:58:39.241909+02	45098.70000000	USDT
264	6112a22f293b290006bd8b9c	BID	2	UNI/USDT	1.00000000	UNI	28.76570000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-10 17:58:39.189562+02	2021-08-10 17:58:39.190886+02	2021-08-10 17:58:39.245428+02	28.76570000	USDT
265	6112b040c5b09d0006227463	BID	1	BTC/USDT	0.00100000	BTC	44886.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 18:58:40.436889+02	2021-08-10 18:58:40.437932+02	2021-08-10 18:58:41.244204+02	44886.50000000	USDT
266	6112b040f2e73f0006aecd04	BID	2	UNI/USDT	1.00000000	UNI	28.40910000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-10 18:58:40.776425+02	2021-08-10 18:58:40.777796+02	2021-08-10 18:58:41.248424+02	28.40910000	USDT
267	6112be51f34b10000661b362	BID	1	BTC/USDT	0.00100000	BTC	45070.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 19:58:41.596511+02	2021-08-10 19:58:41.597418+02	2021-08-10 19:58:43.243767+02	45070.00000000	USDT
268	6112be5131ff620006bd7d87	BID	2	UNI/USDT	1.00000000	UNI	28.71220000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-10 19:58:41.92642+02	2021-08-10 19:58:41.927986+02	2021-08-10 19:58:43.247365+02	28.71220000	USDT
269	6112cc61b4860f0006d81863	BID	1	BTC/USDT	0.00100000	BTC	45299.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-10 20:58:41.771438+02	2021-08-10 20:58:41.772823+02	2021-08-10 20:58:43.785416+02	45299.80000000	USDT
270	611304a1c5b09d0006230ace	BID	2	UNI/USDT	1.00000000	UNI	28.88250000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-11 00:58:41.90234+02	2021-08-11 00:58:41.904387+02	2021-08-11 00:58:43.950613+02	28.88250000	USDT
271	611376bf87af860006cd3167	ASK	2	UNI/USDT	1.00000000	UNI	30.10320000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-11 09:05:35.631938+02	2021-08-11 09:05:35.636263+02	2021-08-11 09:05:38.092863+02	30.10320000	USDT
272	61145621c969c100069db18b	BID	2	UNI/USDT	1.00000000	UNI	28.98870000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-12 00:58:41.988168+02	2021-08-12 00:58:41.991605+02	2021-08-12 00:58:42.092872+02	28.98870000	USDT
273	6116d0d2c969c1000698b63c	ASK	1	BTC/USDT	0.00100000	BTC	47303.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 22:06:42.240054+02	2021-08-13 22:06:42.245113+02	2021-08-13 22:06:42.978731+02	47303.90000000	USDT
274	6116d12af34b1000067a7687	ASK	1	BTC/USDT	0.00100000	BTC	47359.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 22:08:10.746286+02	2021-08-13 22:08:10.747553+02	2021-08-13 22:08:12.976069+02	47359.40000000	USDT
275	6116d12a31ff620006d59392	ASK	1	BTC/USDT	0.00100000	BTC	47359.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 22:08:11.064242+02	2021-08-13 22:08:11.065804+02	2021-08-13 22:08:12.979043+02	47359.40000000	USDT
276	6116d1429281bc00065c9858	ASK	1	BTC/USDT	0.00100000	BTC	47555.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 22:08:34.196459+02	2021-08-13 22:08:34.197607+02	2021-08-13 22:08:34.97386+02	47555.20000000	USDT
277	6116d2d34b2a49000623314c	ASK	1	BTC/USDT	0.00100000	BTC	47600.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 22:15:15.228664+02	2021-08-13 22:15:15.230168+02	2021-08-13 22:15:16.974218+02	47600.00000000	USDT
278	6116d311293b2900064246ad	ASK	1	BTC/USDT	0.00100000	BTC	47707.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 22:16:17.230495+02	2021-08-13 22:16:17.232038+02	2021-08-13 22:16:20.975515+02	47707.30000000	USDT
279	6116d311f34b100006847a9a	ASK	1	BTC/USDT	0.00100000	BTC	47707.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 22:16:17.539047+02	2021-08-13 22:16:17.540586+02	2021-08-13 22:16:20.979101+02	47707.30000000	USDT
280	6116e057c5b09d0006a0800d	ASK	1	BTC/USDT	0.00100000	BTC	47807.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-13 23:12:55.561207+02	2021-08-13 23:12:55.563196+02	2021-08-13 23:12:58.975901+02	47807.70000000	USDT
281	6117021931ff62000670900c	ASK	2	UNI/USDT	1.00000000	UNI	30.44350000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-14 01:36:57.285691+02	2021-08-14 01:36:57.286929+02	2021-08-14 01:37:00.976026+02	30.44350000	USDT
282	6117023748b16200067fe8f9	ASK	2	UNI/USDT	1.00000000	UNI	30.50000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-14 01:37:27.237617+02	2021-08-14 01:37:27.238437+02	2021-08-14 01:37:28.974549+02	30.50000000	USDT
283	6117071c9281bc000608a746	ASK	2	UNI/USDT	1.00000000	UNI	30.62170000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-14 01:58:21.035861+02	2021-08-14 01:58:21.037164+02	2021-08-14 01:58:24.974826+02	30.62170000	USDT
284	61178f8387af860006d9242b	ASK	1	BTC/USDT	0.00100000	BTC	47910.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 11:40:19.429727+02	2021-08-14 11:40:19.500058+02	2021-08-14 11:40:21.941887+02	47910.70000000	USDT
285	611793d9b4860f0006fe2779	BID	1	BTC/USDT	0.00100000	BTC	47000.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 11:58:49.406975+02	2021-08-14 11:58:49.407816+02	2021-08-14 11:58:51.943573+02	47000.00000000	USDT
286	6117a1e94b2a4900069467f4	BID	1	BTC/USDT	0.00100000	BTC	46240.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 12:58:49.882045+02	2021-08-14 12:58:49.883168+02	2021-08-14 12:58:53.187593+02	46240.40000000	USDT
287	6117aff9f34b100006260e69	BID	1	BTC/USDT	0.00100000	BTC	46550.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 13:58:49.308202+02	2021-08-14 13:58:49.309082+02	2021-08-14 13:58:51.189038+02	46550.30000000	USDT
288	6117be084b2a490006f17634	BID	1	BTC/USDT	0.00100000	BTC	46286.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 14:58:48.792408+02	2021-08-14 14:58:48.793325+02	2021-08-14 14:58:49.187803+02	46286.70000000	USDT
289	6117cc17c969c100069848cb	BID	1	BTC/USDT	0.00100000	BTC	46500.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 15:58:48.021919+02	2021-08-14 15:58:48.022769+02	2021-08-14 15:58:51.188404+02	46500.90000000	USDT
290	6117da29f34b100006a37f60	BID	1	BTC/USDT	0.00100000	BTC	46871.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 16:58:49.172344+02	2021-08-14 16:58:49.1735+02	2021-08-14 16:58:49.275745+02	46871.20000000	USDT
291	6117e83739232b0006fa5c9b	BID	1	BTC/USDT	0.00100000	BTC	46927.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 17:58:47.535858+02	2021-08-14 17:58:47.53666+02	2021-08-14 17:58:49.275644+02	46927.00000000	USDT
292	6117f646f34b100006fa16c4	BID	1	BTC/USDT	0.00100000	BTC	46840.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 18:58:47.121408+02	2021-08-14 18:58:47.122275+02	2021-08-14 18:58:49.275596+02	46840.00000000	USDT
293	61180456f2e73f00069ba0af	BID	1	BTC/USDT	0.00100000	BTC	46783.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 19:58:46.41316+02	2021-08-14 19:58:46.414168+02	2021-08-14 19:58:52.05478+02	46783.40000000	USDT
294	611812659cd98c0006a3726a	BID	1	BTC/USDT	0.00100000	BTC	46749.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 20:58:45.550985+02	2021-08-14 20:58:45.551794+02	2021-08-14 20:58:46.058904+02	46749.70000000	USDT
295	6118207648b1620006dbf70b	BID	1	BTC/USDT	0.00100000	BTC	47028.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-14 21:58:46.490252+02	2021-08-14 21:58:46.491023+02	2021-08-14 21:58:48.057871+02	47028.90000000	USDT
296	6119f9b487af8600067a8745	ASK	2	UNI/USDT	1.00000000	UNI	30.75510000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-16 07:37:57.151543+02	2021-08-16 07:37:57.158626+02	2021-08-16 07:38:00.775968+02	30.75510000	USDT
297	611a6f15b4860f0006f77fe7	BID	1	BTC/USDT	0.00100000	BTC	46491.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-16 15:58:45.874729+02	2021-08-16 15:58:45.87583+02	2021-08-16 15:58:49.48216+02	46491.60000000	USDT
298	611fc0491289ad000655caa1	ASK	1	BTC/USDT	0.00100000	BTC	48568.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 16:46:33.81021+02	2021-08-20 16:46:33.866668+02	2021-08-20 16:46:40.826816+02	48568.10000000	USDT
299	611fd0fc85ce750006ab530f	ASK	1	BTC/USDT	0.00100000	BTC	48605.60000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 17:57:48.666703+02	2021-08-20 17:57:48.668896+02	2021-08-20 17:57:49.90686+02	48605.60000000	USDT
300	611fd35d568ba00006d02d6c	ASK	1	BTC/USDT	0.00100000	BTC	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	BTC	\N	2021-08-20 18:07:57.519+02	2021-08-20 18:07:57.697354+02	2021-08-20 18:07:57.903748+02	48791.50000000	USDT
301	611fd5381289ad0006a1f67a	ASK	1	BTC/USDT	0.00100000	BTC	48870.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 18:15:52.35889+02	2021-08-20 18:15:52.360305+02	2021-08-20 18:15:58.788491+02	48870.70000000	USDT
302	611fd574066f650006c3f019	ASK	1	BTC/USDT	0.00100000	BTC	48904.80000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 18:16:52.402844+02	2021-08-20 18:16:52.403901+02	2021-08-20 18:16:58.666606+02	48904.80000000	USDT
303	6120257c02c9d90006d5566f	ASK	1	BTC/USDT	0.00100000	BTC	49098.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 23:58:20.707819+02	2021-08-20 23:58:20.719219+02	2021-08-20 23:58:26.658641+02	49098.10000000	USDT
304	6120258ea520f500061624bc	ASK	1	BTC/USDT	0.00100000	BTC	49221.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 23:58:38.826192+02	2021-08-20 23:58:38.827786+02	2021-08-20 23:58:39.96199+02	49221.50000000	USDT
305	6120258e2b17a900064fdb70	ASK	1	BTC/USDT	0.00100000	BTC	49221.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 23:58:39.10747+02	2021-08-20 23:58:39.109703+02	2021-08-20 23:58:39.964722+02	49221.50000000	USDT
306	6120258f70d591000601a7b8	ASK	1	BTC/USDT	0.00100000	BTC	49221.50000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 23:58:39.389447+02	2021-08-20 23:58:39.390814+02	2021-08-20 23:58:39.967215+02	49221.50000000	USDT
307	612025a698335e0006f0a557	ASK	1	BTC/USDT	0.00100000	BTC	49231.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 23:59:03.049735+02	2021-08-20 23:59:03.050875+02	2021-08-20 23:59:08.618779+02	49231.70000000	USDT
308	612025b6a520f5000616e941	ASK	1	BTC/USDT	0.00100000	BTC	49269.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-20 23:59:18.487329+02	2021-08-20 23:59:18.488236+02	2021-08-20 23:59:24.584734+02	49269.70000000	USDT
309	6120dd0a2b17a9000673e5c4	ASK	1	BTC/USDT	0.00100000	BTC	49392.10000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-21 13:01:30.545753+02	2021-08-21 13:01:30.547275+02	2021-08-21 13:01:32.048305+02	49392.10000000	USDT
310	6120ea73568ba000062cf741	BID	1	BTC/USDT	0.00100000	BTC	48548.40000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-21 13:58:43.315739+02	2021-08-21 13:58:43.316915+02	2021-08-21 13:58:48.860771+02	48548.40000000	USDT
311	6120ea752b17a90006ac5cb6	BID	2	UNI/USDT	1.00000000	UNI	28.73360000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-21 13:58:45.967209+02	2021-08-21 13:58:45.968282+02	2021-08-21 13:58:48.864017+02	28.73360000	USDT
312	61214ce470d5910006904579	BID	2	UNI/USDT	1.00000000	UNI	28.87300000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-21 20:58:45.122502+02	2021-08-21 20:58:45.137559+02	2021-08-21 20:58:46.323595+02	28.87300000	USDT
313	61215af685ce7500067398ce	BID	1	BTC/USDT	0.00100000	BTC	48891.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-21 21:58:46.368281+02	2021-08-21 21:58:46.374717+02	2021-08-21 21:58:48.356678+02	48891.90000000	USDT
314	61215af670d5910006bf48ec	BID	2	UNI/USDT	1.00000000	UNI	28.24030000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-21 21:58:46.712109+02	2021-08-21 21:58:46.713318+02	2021-08-21 21:58:48.359723+02	28.24030000	USDT
315	61216907066f650006a90af4	BID	1	BTC/USDT	0.00100000	BTC	48914.90000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-21 22:58:47.602955+02	2021-08-21 22:58:47.604043+02	2021-08-21 22:58:52.394481+02	48914.90000000	USDT
316	612169077c11c700065c6549	BID	2	UNI/USDT	1.00000000	UNI	28.32530000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-21 22:58:47.970731+02	2021-08-21 22:58:47.971681+02	2021-08-21 22:58:52.397594+02	28.32530000	USDT
317	6121771785ce750006c41375	BID	2	UNI/USDT	1.00000000	UNI	28.56770000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-21 23:58:47.965087+02	2021-08-21 23:58:47.966379+02	2021-08-21 23:58:48.370058+02	28.56770000	USDT
318	6121852770d591000635ba29	BID	2	UNI/USDT	1.00000000	UNI	28.57730000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-22 00:58:47.497876+02	2021-08-22 00:58:47.498957+02	2021-08-22 00:58:50.366624+02	28.57730000	USDT
319	61219336e3405e0006e006a9	BID	1	BTC/USDT	0.00100000	BTC	48856.70000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-22 01:58:46.490986+02	2021-08-22 01:58:46.492467+02	2021-08-22 01:58:51.127158+02	48856.70000000	USDT
320	6121933670d59100065e05ce	BID	2	UNI/USDT	1.00000000	UNI	28.25180000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-22 01:58:46.800924+02	2021-08-22 01:58:46.802671+02	2021-08-22 01:58:51.130284+02	28.25180000	USDT
321	6121a146a520f50006a14b57	BID	1	BTC/USDT	0.00100000	BTC	49003.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-22 02:58:46.750015+02	2021-08-22 02:58:46.750904+02	2021-08-22 02:58:48.364432+02	49003.20000000	USDT
322	6121a147e3405e00060eba96	BID	2	UNI/USDT	1.00000000	UNI	28.63550000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-22 02:58:47.848984+02	2021-08-22 02:58:47.850309+02	2021-08-22 02:58:48.367963+02	28.63550000	USDT
323	6121af57efd0820006b02b35	BID	2	UNI/USDT	1.00000000	UNI	28.62540000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-22 03:58:47.384959+02	2021-08-22 03:58:47.386271+02	2021-08-22 03:58:50.496082+02	28.62540000	USDT
324	6121bd66efd0820006d8625e	BID	1	BTC/USDT	0.00100000	BTC	48848.20000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-08-22 04:58:46.360025+02	2021-08-22 04:58:46.362531+02	2021-08-22 04:58:48.49607+02	48848.20000000	USDT
325	6121bd6785ce75000690ea11	BID	2	UNI/USDT	1.00000000	UNI	28.32180000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-08-22 04:58:47.52087+02	2021-08-22 04:58:47.522459+02	2021-08-22 04:58:48.498679+02	28.32180000	USDT
326	612f162b9388e2000655ce0e	ASK	2	UNI/USDT	1.00000000	UNI	29.92510000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 07:56:59.25952+02	2021-09-01 07:56:59.302318+02	2021-09-01 07:57:02.160558+02	29.92510000	USDT
327	612f169eed64aa0007f8c3ae	ASK	2	UNI/USDT	1.00000000	UNI	29.99000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 07:58:54.412636+02	2021-09-01 07:58:54.413821+02	2021-09-01 07:58:56.14785+02	29.99000000	USDT
328	612f16b5fc5712000629ff8d	ASK	2	UNI/USDT	1.00000000	UNI	30.02300000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 07:59:17.272459+02	2021-09-01 07:59:17.273966+02	2021-09-01 07:59:18.175118+02	30.02300000	USDT
329	612f16cc5abcb70006cc6499	ASK	2	UNI/USDT	1.00000000	UNI	30.07070000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 07:59:40.426797+02	2021-09-01 07:59:40.42812+02	2021-09-01 07:59:44.157483+02	30.07070000	USDT
330	612f1a359388e200066345ae	ASK	2	UNI/USDT	1.00000000	UNI	30.36000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 08:14:13.567109+02	2021-09-01 08:14:13.568591+02	2021-09-01 08:14:14.214147+02	30.36000000	USDT
331	612f1a35d895db0006fc143a	ASK	2	UNI/USDT	1.00000000	UNI	30.36000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 08:14:13.856553+02	2021-09-01 08:14:13.857813+02	2021-09-01 08:14:14.218309+02	30.36000000	USDT
332	612f1a361fd4bc0006c9c342	ASK	2	UNI/USDT	1.00000000	UNI	30.36000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 08:14:14.154652+02	2021-09-01 08:14:14.156253+02	2021-09-01 08:14:14.221138+02	30.36000000	USDT
333	612f1a361fd4bc0006c9c647	ASK	2	UNI/USDT	1.00000000	UNI	30.36570000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 08:14:15.109442+02	2021-09-01 08:14:15.110477+02	2021-09-01 08:14:16.160832+02	30.36570000	USDT
334	612f20b1b9fe620006313a1d	ASK	2	UNI/USDT	1.00000000	UNI	30.47130000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 08:41:53.686708+02	2021-09-01 08:41:53.688856+02	2021-09-01 08:41:55.627299+02	30.47130000	USDT
335	612f2155ed64aa00071b33d6	ASK	2	UNI/USDT	1.00000000	UNI	30.63150000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-01 08:44:37.56733+02	2021-09-01 08:44:37.568494+02	2021-09-01 08:44:42.483283+02	30.63150000	USDT
336	6130257c1fd4bc000656a264	BID	2	UNI/USDT	1.00000000	UNI	30.49240000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 03:14:37.11587+02	2021-09-02 03:14:37.129598+02	2021-09-02 03:14:38.161842+02	30.49240000	USDT
337	6130338e1fd4bc0006896b8e	BID	2	UNI/USDT	1.00000000	UNI	30.56000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 04:14:38.21007+02	2021-09-02 04:14:38.218652+02	2021-09-02 04:14:40.137195+02	30.56000000	USDT
338	6130419efb2f720006f16f6c	BID	2	UNI/USDT	1.00000000	UNI	30.43280000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 05:14:38.450928+02	2021-09-02 05:14:38.45172+02	2021-09-02 05:14:38.53446+02	30.43280000	USDT
339	61304fafd895db000611d81a	BID	2	UNI/USDT	1.00000000	UNI	30.29010000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 06:14:39.48461+02	2021-09-02 06:14:39.485726+02	2021-09-02 06:14:40.521358+02	30.29010000	USDT
340	61305dbfb9fe6200065a1a08	BID	2	UNI/USDT	1.00000000	UNI	30.39940000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 07:14:39.638177+02	2021-09-02 07:14:39.639074+02	2021-09-02 07:14:42.521642+02	30.39940000	USDT
341	61306bce5abcb700063f9e03	BID	2	UNI/USDT	1.00000000	UNI	30.32220000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 08:14:38.810075+02	2021-09-02 08:14:38.811669+02	2021-09-02 08:14:40.51257+02	30.32220000	USDT
342	613079df9388e20006fd2a95	BID	2	UNI/USDT	1.00000000	UNI	30.47170000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 09:14:39.822789+02	2021-09-02 09:14:39.823875+02	2021-09-02 09:14:42.582741+02	30.47170000	USDT
343	613087f089e43f0006642ca9	BID	2	UNI/USDT	1.00000000	UNI	30.39780000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 10:14:40.347721+02	2021-09-02 10:14:40.349087+02	2021-09-02 10:14:40.522901+02	30.39780000	USDT
344	613095ff1001d500064b4c21	BID	2	UNI/USDT	1.00000000	UNI	30.42580000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 11:14:39.682014+02	2021-09-02 11:14:39.688052+02	2021-09-02 11:14:42.51261+02	30.42580000	USDT
345	6130a4109388e200068a2c6d	BID	2	UNI/USDT	1.00000000	UNI	30.31840000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-02 12:14:40.671632+02	2021-09-02 12:14:40.673048+02	2021-09-02 12:14:44.526294+02	30.31840000	USDT
346	61323241f95d470006701fec	ASK	1	BTC/USDT	0.00100000	BTC	50998.00000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-09-03 16:33:37.983643+02	2021-09-03 16:33:37.994318+02	2021-09-03 16:33:39.27018+02	50998.00000000	USDT
347	61323be1f95d4700069764b6	BID	2	UNI/USDT	1.00000000	UNI	29.06160000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-03 17:14:41.245146+02	2021-09-03 17:14:41.246982+02	2021-09-03 17:14:43.215568+02	29.06160000	USDT
348	613249f0ed64aa0007d27d3c	BID	2	UNI/USDT	1.00000000	UNI	29.09680000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-03 18:14:40.754945+02	2021-09-03 18:14:40.756024+02	2021-09-03 18:14:42.780494+02	29.09680000	USDT
349	61352e41f95d47000678fab6	ASK	1	BTC/USDT	0.00100000	BTC	51499.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-09-05 22:53:21.796197+02	2021-09-05 22:53:21.825808+02	2021-09-05 22:53:28.385281+02	51499.30000000	USDT
350	61352e42ed64aa000786c212	ASK	1	BTC/USDT	0.00100000	BTC	51499.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-09-05 22:53:22.283782+02	2021-09-05 22:53:22.285669+02	2021-09-05 22:53:28.389155+02	51499.30000000	USDT
351	61352e421fd4bc00064b6870	ASK	1	BTC/USDT	0.00100000	BTC	51499.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-09-05 22:53:22.613738+02	2021-09-05 22:53:22.617576+02	2021-09-05 22:53:28.393978+02	51499.30000000	USDT
352	61352e4247d1a90006d84b3a	ASK	1	BTC/USDT	0.00100000	BTC	51499.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-09-05 22:53:22.969297+02	2021-09-05 22:53:22.970937+02	2021-09-05 22:53:28.409238+02	51499.30000000	USDT
353	61352e44fc57120006b65839	ASK	1	BTC/USDT	0.00100000	BTC	51499.30000000	USDT	\N	\N	\N	NEW	0.00100000	BTC	\N	2021-09-05 22:53:24.177411+02	2021-09-05 22:53:24.179122+02	2021-09-05 22:53:28.41315+02	51499.30000000	USDT
354	6136062cf95d4700063c0d3f	BID	1	BTC/USDT	0.00200000	BTC	51247.50000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-06 14:14:36.932874+02	2021-09-06 14:14:36.936944+02	2021-09-06 14:14:43.509258+02	51247.50000000	USDT
355	6136062d9388e20006a680d1	BID	2	UNI/USDT	1.00000000	UNI	28.87320000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-06 14:14:37.37827+02	2021-09-06 14:14:37.379661+02	2021-09-06 14:14:43.512007+02	28.87320000	USDT
356	6136143c89e43f000615944b	BID	1	BTC/USDT	0.00200000	BTC	51293.30000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-06 15:14:36.702707+02	2021-09-06 15:14:36.703885+02	2021-09-06 15:14:41.992283+02	51293.30000000	USDT
357	6136143c9388e20006dfc184	BID	2	UNI/USDT	1.00000000	UNI	28.94010000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-06 15:14:37.005798+02	2021-09-06 15:14:37.007521+02	2021-09-06 15:14:41.996224+02	28.94010000	USDT
358	6136d7d6fc571200065615b5	BID	1	BTC/USDT	0.00084963	BTC	52834.00000000	USDT	\N	\N	\N	NEW	0.00084963	BTC	\N	2021-09-07 05:09:10.258562+02	2021-09-07 05:09:10.262756+02	2021-09-07 05:09:14.547986+02	52834.00000000	USDT
359	6137773f3cd33d0006ef5619	ASK	2	UNI/USDT	1.00000000	UNI	25.88410000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:19.265352+02	2021-09-07 16:29:19.270172+02	2021-09-07 16:29:20.899475+02	25.88410000	USDT
360	61377740d895db00069360ac	ASK	2	UNI/USDT	1.00000000	UNI	25.88410000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:20.495878+02	2021-09-07 16:29:20.497192+02	2021-09-07 16:29:20.907254+02	25.88410000	USDT
362	6137774189e43f00062fa8b8	ASK	2	UNI/USDT	1.00000000	UNI	25.88410000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:21.278357+02	2021-09-07 16:29:21.279536+02	2021-09-07 16:29:25.326736+02	25.88410000	USDT
361	6137774047d1a90006f096eb	ASK	2	UNI/USDT	1.00000000	UNI	25.88410000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:20.854411+02	2021-09-07 16:29:20.855267+02	2021-09-07 16:29:25.32216+02	25.88410000	USDT
363	61377745f95d4700068f8dc0	ASK	2	UNI/USDT	1.00000000	UNI	25.83200000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:26.107047+02	2021-09-07 16:29:26.108253+02	2021-09-07 16:29:31.450993+02	25.83200000	USDT
364	61377746d895db0006939a99	ASK	2	UNI/USDT	1.00000000	UNI	25.76670000	USDT	0.00000000	USDT	\N	PARTIALLY_FILLED	1.00000000	UNI	\N	2021-09-07 16:29:26.248+02	2021-09-07 16:29:26.44057+02	2021-09-07 16:29:27.749406+02	25.83200000	USDT
365	61377746b9fe620006b2ed5d	ASK	2	UNI/USDT	1.00000000	UNI	25.76266539	USDT	0.00000000	USDT	\N	PARTIALLY_FILLED	1.00000000	UNI	\N	2021-09-07 16:29:26.594+02	2021-09-07 16:29:26.784709+02	2021-09-07 16:29:27.753413+02	25.83200000	USDT
366	61377747dc83570006767cd7	ASK	2	UNI/USDT	1.00000000	UNI	25.76610000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:28.084248+02	2021-09-07 16:29:28.085258+02	2021-09-07 16:29:31.453917+02	25.76610000	USDT
367	61377748ed64aa00079e5e68	ASK	2	UNI/USDT	1.00000000	UNI	25.76610000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:28.453123+02	2021-09-07 16:29:28.454217+02	2021-09-07 16:29:31.456789+02	25.76610000	USDT
368	61377748d895db000693b38a	ASK	2	UNI/USDT	1.00000000	UNI	25.76610000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:29:28.783491+02	2021-09-07 16:29:28.784848+02	2021-09-07 16:29:31.459584+02	25.76610000	USDT
369	61377bd0fb2f720006ba8d8b	ASK	2	UNI/USDT	1.00000000	UNI	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	UNI	\N	2021-09-07 16:48:48.305+02	2021-09-07 16:48:48.496469+02	2021-09-07 16:48:49.528373+02	24.71280000	USDT
370	61377bd089e43f00064e2a3a	ASK	2	UNI/USDT	1.00000000	UNI	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	UNI	\N	2021-09-07 16:48:48.677+02	2021-09-07 16:48:48.862647+02	2021-09-07 16:48:49.531833+02	24.71280000	USDT
371	61377bd65abcb700068e089a	ASK	2	UNI/USDT	1.00000000	UNI	24.58080000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 16:48:54.532222+02	2021-09-07 16:48:54.533446+02	2021-09-07 16:48:57.359508+02	24.58080000	USDT
372	61377bda5abcb700068e3387	ASK	2	UNI/USDT	1.00000000	UNI	0.00000000	USDT	0.00000000	USDT	\N	NEW	0.00000000	UNI	\N	2021-09-07 16:48:58.302+02	2021-09-07 16:48:58.498101+02	2021-09-07 16:48:59.814017+02	24.44300000	USDT
373	613780b3fb2f720006e038b5	BID	1	BTC/USDT	0.00105516	BTC	43194.30000000	USDT	\N	\N	\N	NEW	0.00105516	BTC	\N	2021-09-07 17:09:40.171026+02	2021-09-07 17:09:40.171884+02	2021-09-07 17:09:40.972111+02	43194.30000000	USDT
374	613780b4f95d470006d378e5	ASK	1	BTC/USDT	0.00200000	BTC	43194.30000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-07 17:09:40.480918+02	2021-09-07 17:09:40.481826+02	2021-09-07 17:09:41.015822+02	43194.30000000	USDT
375	613780b49388e200063dc194	ASK	1	BTC/USDT	0.00200000	BTC	43194.30000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-07 17:09:40.940773+02	2021-09-07 17:09:40.941288+02	2021-09-07 17:09:41.019031+02	43194.30000000	USDT
376	613781e2b9fe620006fd710f	BID	1	BTC/USDT	0.00200000	BTC	45475.50000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-07 17:14:44.171362+02	2021-09-07 17:14:44.172485+02	2021-09-07 17:14:55.65132+02	45475.50000000	USDT
377	613781e4dc83570006c102ef	BID	2	UNI/USDT	1.00000000	UNI	21.60730000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 17:14:44.700271+02	2021-09-07 17:14:44.701497+02	2021-09-07 17:14:55.65492+02	21.60730000	USDT
378	613784b7fb2f720006f4a7ed	ASK	2	UNI/USDT	1.00000000	UNI	22.90240000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 17:26:48.07038+02	2021-09-07 17:26:48.071367+02	2021-09-07 17:26:49.71198+02	22.90240000	USDT
379	61378ff01fd4bc0006f738c0	BID	1	BTC/USDT	0.00200000	BTC	47026.00000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-07 18:14:40.974567+02	2021-09-07 18:14:40.975933+02	2021-09-07 18:14:41.798598+02	47026.00000000	USDT
380	61378ff447d1a90006856c90	BID	2	UNI/USDT	1.00000000	UNI	24.17550000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 18:14:44.93982+02	2021-09-07 18:14:44.941375+02	2021-09-07 18:14:45.820482+02	24.17550000	USDT
381	61379e011fd4bc00064448b2	BID	1	BTC/USDT	0.00200000	BTC	47324.20000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-07 19:14:41.924871+02	2021-09-07 19:14:41.926386+02	2021-09-07 19:14:42.534858+02	47324.20000000	USDT
382	61379e03dc835700065807b4	BID	2	UNI/USDT	1.00000000	UNI	24.94060000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 19:14:43.935377+02	2021-09-07 19:14:43.936984+02	2021-09-07 19:14:46.581087+02	24.94060000	USDT
383	6137ac11d895db0006c82a56	BID	1	BTC/USDT	0.00200000	BTC	46535.90000000	USDT	\N	\N	\N	NEW	0.00200000	BTC	\N	2021-09-07 20:14:41.546818+02	2021-09-07 20:14:41.548464+02	2021-09-07 20:14:42.889487+02	46535.90000000	USDT
384	6137ac13fc57120006016172	BID	2	UNI/USDT	1.00000000	UNI	23.70620000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 20:14:43.240381+02	2021-09-07 20:14:43.241727+02	2021-09-07 20:14:47.797127+02	23.70620000	USDT
385	6137ba22ed64aa00072185ad	BID	2	UNI/USDT	1.00000000	UNI	24.21850000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 21:14:43.13335+02	2021-09-07 21:14:43.134922+02	2021-09-07 21:14:45.428337+02	24.21850000	USDT
386	6137c831f95d4700065317e0	BID	2	UNI/USDT	1.00000000	UNI	24.03360000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 22:14:42.143234+02	2021-09-07 22:14:42.145015+02	2021-09-07 22:14:45.095066+02	24.03360000	USDT
387	6137d641f95d470006969f1d	BID	2	UNI/USDT	1.00000000	UNI	23.85000000	USDT	\N	\N	\N	NEW	1.00000000	UNI	\N	2021-09-07 23:14:41.915697+02	2021-09-07 23:14:41.916775+02	2021-09-07 23:14:43.549676+02	23.85000000	USDT
\.


--
-- Data for Name: positions; Type: TABLE DATA; Schema: public; Owner: cassandre_trading_bot
--

COPY public.positions (id, position_id, type, FK_STRATEGY_UID, currency_pair, amount_value, amount_currency, rules_stop_gain_percentage, rules_stop_loss_percentage, status, FK_OPENING_ORDER_UID, FK_CLOSING_ORDER_UID, lowest_gain_price_value, lowest_gain_price_currency, highest_gain_price_value, highest_gain_price_currency, latest_gain_price_value, latest_gain_price_currency, created_on, updated_on, force_closing) FROM stdin;
14	7	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	14	25	32775.10000000	USDT	34497.20000000	USDT	34593.70000000	USDT	2021-07-02 08:30:45.9797+02	2021-07-03 09:55:45.872881+02	f
62	28	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	107	108	16.94920000	USDT	18.37360000	USDT	18.39360000	USDT	2021-07-23 14:08:38.779914+02	2021-07-24 01:17:45.498778+02	f
76	37	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	145	150	36403.00000000	USDT	38326.00000000	USDT	38362.80000000	USDT	2021-07-27 04:58:32.455549+02	2021-07-27 14:41:31.39421+02	f
11	6	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	11	22	16.94420000	USDT	18.31860000	USDT	18.38160000	USDT	2021-07-02 06:30:44.871762+02	2021-07-03 09:46:03.852722+02	f
8	4	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	8	26	32704.00000000	USDT	34497.20000000	USDT	34593.70000000	USDT	2021-07-02 05:30:44.600939+02	2021-07-03 09:55:46.905321+02	f
43	18	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	70	74	15.04450000	USDT	17.93160000	USDT	15.00550000	USDT	2021-07-15 06:24:43.302715+02	2021-07-20 04:44:53.703406+02	f
24	13	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	42	75	29836.70000000	USDT	35100.00000000	USDT	29625.70000000	USDT	2021-07-05 02:16:27.859549+02	2021-07-20 05:19:13.96968+02	f
21	10	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	36	40	18.81070000	USDT	20.03850000	USDT	20.13260000	USDT	2021-07-04 01:16:17.488737+02	2021-07-04 07:57:08.682609+02	f
25	14	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	43	76	29836.70000000	USDT	35100.00000000	USDT	29625.70000000	USDT	2021-07-05 03:16:27.151957+02	2021-07-20 05:19:15.13908+02	f
13	7	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	13	21	16.94420000	USDT	18.30150000	USDT	18.31000000	USDT	2021-07-02 07:30:44.57001+02	2021-07-03 09:41:03.445394+02	f
41	24	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	67	71	33182.80000000	USDT	31062.60000000	USDT	30819.50000000	USDT	2021-07-14 12:23:17.920488+02	2021-07-19 14:35:27.003165+02	f
6	3	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	6	39	32704.00000000	USDT	34968.70000000	USDT	35023.90000000	USDT	2021-07-02 04:30:45.065367+02	2021-07-04 07:42:50.299206+02	f
61	34	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	106	111	32020.30000000	USDT	33887.90000000	USDT	33889.10000000	USDT	2021-07-23 14:08:38.359865+02	2021-07-24 08:49:27.776981+02	f
18	9	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	18	64	35957.40000000	USDT	31732.30000000	USDT	31680.60000000	USDT	2021-07-02 15:30:50.48387+02	2021-07-14 05:26:39.463192+02	f
10	5	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	10	31	32704.00000000	USDT	34683.90000000	USDT	34742.90000000	USDT	2021-07-02 06:30:44.593572+02	2021-07-03 11:11:58.795998+02	f
3	2	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	3	32	32704.00000000	USDT	34683.90000000	USDT	34742.90000000	USDT	2021-07-01 21:30:42.552455+02	2021-07-03 11:11:59.558349+02	f
19	10	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	19	62	35957.70000000	USDT	32100.40000000	USDT	31924.60000000	USDT	2021-07-02 16:30:53.119943+02	2021-07-14 05:24:39.713865+02	f
17	8	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	17	63	35957.70000000	USDT	31802.10000000	USDT	31732.30000000	USDT	2021-07-02 10:30:45.87733+02	2021-07-14 05:25:55.50719+02	f
7	4	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	7	29	16.94420000	USDT	18.82250000	USDT	18.83180000	USDT	2021-07-02 04:30:45.346848+02	2021-07-03 11:00:03.926175+02	f
39	17	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	65	69	16.74170000	USDT	18.31750000	USDT	18.34530000	USDT	2021-07-14 07:22:56.262332+02	2021-07-15 03:18:41.113582+02	f
2	1	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	2	27	16.94420000	USDT	18.71300000	USDT	18.73070000	USDT	2021-07-01 19:30:42.652937+02	2021-07-03 09:56:04.000576+02	f
4	2	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	4	28	16.94420000	USDT	18.78000000	USDT	18.79200000	USDT	2021-07-01 21:30:42.848757+02	2021-07-03 10:12:03.897246+02	f
1	1	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	1	34	32704.00000000	USDT	34791.40000000	USDT	34890.10000000	USDT	2021-07-01 19:30:42.232446+02	2021-07-03 19:43:17.527399+02	f
48	21	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	82	90	14.04540000	USDT	15.43450000	USDT	15.47160000	USDT	2021-07-20 12:37:24.368722+02	2021-07-21 06:48:58.603543+02	f
23	11	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	38	41	18.94400000	USDT	20.13260000	USDT	20.18400000	USDT	2021-07-04 03:16:18.225979+02	2021-07-04 07:57:14.596823+02	f
15	8	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	15	20	16.98560000	USDT	18.12570000	USDT	18.13180000	USDT	2021-07-02 08:30:46.288003+02	2021-07-03 00:58:39.255276+02	f
12	6	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	12	33	32704.00000000	USDT	34743.90000000	USDT	34757.40000000	USDT	2021-07-02 07:30:44.293793+02	2021-07-03 14:04:59.092444+02	f
52	24	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	87	93	14.04540000	USDT	15.60800000	USDT	15.64370000	USDT	2021-07-20 22:58:19.179074+02	2021-07-21 08:15:31.709415+02	f
44	26	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	77	95	29298.20000000	USDT	30949.90000000	USDT	31153.10000000	USDT	2021-07-20 05:35:51.691512+02	2021-07-21 11:55:58.331244+02	f
59	27	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	101	103	16.10030000	USDT	17.29840000	USDT	17.33820000	USDT	2021-07-22 14:05:21.181897+02	2021-07-22 18:00:15.325561+02	f
16	9	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	16	23	17.01180000	USDT	18.38160000	USDT	18.39800000	USDT	2021-07-02 09:30:45.658106+02	2021-07-03 09:46:33.85198+02	f
55	25	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	97	102	16.10030000	USDT	17.28150000	USDT	17.29530000	USDT	2021-07-22 12:04:48.359198+02	2021-07-22 17:57:51.297858+02	f
9	5	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	9	24	16.94420000	USDT	18.42200000	USDT	18.49680000	USDT	2021-07-02 05:30:44.887344+02	2021-07-03 09:49:03.847347+02	f
33	15	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	51	55	19.14080000	USDT	21.37190000	USDT	21.38480000	USDT	2021-07-05 07:16:30.173293+02	2021-07-06 05:25:45.791521+02	f
5	3	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	5	30	16.94420000	USDT	18.90840000	USDT	18.91730000	USDT	2021-07-02 03:30:44.694874+02	2021-07-03 11:04:28.794835+02	f
27	12	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	45	56	19.14080000	USDT	21.62000000	USDT	21.64880000	USDT	2021-07-05 04:16:27.24296+02	2021-07-06 05:59:51.465446+02	f
31	14	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	49	54	19.14080000	USDT	21.27070000	USDT	21.28960000	USDT	2021-07-05 06:16:29.178748+02	2021-07-06 04:57:03.73602+02	f
35	16	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	53	58	19.14080000	USDT	21.65590000	USDT	21.68080000	USDT	2021-07-05 08:16:29.92535+02	2021-07-06 06:03:51.715151+02	f
29	13	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	47	57	19.14080000	USDT	21.62000000	USDT	21.64880000	USDT	2021-07-05 05:16:28.234885+02	2021-07-06 05:59:52.520533+02	f
68	34	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	118	136	17.25540000	USDT	19.32590000	USDT	19.36580000	USDT	2021-07-25 01:12:19.000957+02	2021-07-26 03:01:42.949378+02	f
67	33	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	117	137	17.25540000	USDT	19.32590000	USDT	19.36580000	USDT	2021-07-25 00:12:13.945086+02	2021-07-26 03:01:43.680694+02	f
28	16	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	46	84	29328.70000000	USDT	35100.00000000	USDT	29298.20000000	USDT	2021-07-05 05:16:27.8857+02	2021-07-20 16:18:24.466252+02	f
40	23	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	66	72	33182.80000000	USDT	31062.60000000	USDT	30819.50000000	USDT	2021-07-14 11:23:09.901058+02	2021-07-19 14:35:28.105995+02	f
42	25	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	68	73	33182.80000000	USDT	31062.60000000	USDT	30819.50000000	USDT	2021-07-14 13:23:20.520927+02	2021-07-19 14:35:35.579783+02	f
58	32	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	100	110	31739.90000000	USDT	33449.60000000	USDT	33474.80000000	USDT	2021-07-22 14:05:20.696702+02	2021-07-24 01:36:01.561111+02	f
90	44	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	168	194	37348.50000000	USDT	41142.90000000	USDT	41159.10000000	USDT	2021-08-03 01:58:52.612773+02	2021-08-05 21:09:08.239927+02	f
49	22	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	83	91	14.04540000	USDT	15.43450000	USDT	15.47160000	USDT	2021-07-20 15:38:09.015511+02	2021-07-21 06:49:00.300899+02	f
64	30	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	114	138	17.25540000	USDT	19.36580000	USDT	19.41390000	USDT	2021-07-24 21:12:01.945672+02	2021-07-26 03:01:46.819163+02	f
46	27	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	79	96	29298.20000000	USDT	30949.90000000	USDT	31153.10000000	USDT	2021-07-20 06:36:02.851778+02	2021-07-21 11:55:59.491191+02	f
56	31	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	98	109	31726.40000000	USDT	33433.90000000	USDT	33443.10000000	USDT	2021-07-22 13:05:00.924528+02	2021-07-24 01:35:27.517426+02	f
69	35	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	119	139	17.25540000	USDT	19.41390000	USDT	19.48100000	USDT	2021-07-25 02:12:20.974138+02	2021-07-26 03:01:58.86485+02	f
79	41	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	148	152	17.99570000	USDT	19.11110000	USDT	19.12570000	USDT	2021-07-27 05:58:42.175754+02	2021-07-27 14:51:35.152306+02	f
45	19	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	78	94	14.04540000	USDT	15.60800000	USDT	15.64370000	USDT	2021-07-20 05:35:52.849444+02	2021-07-21 08:15:32.911596+02	f
71	35	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	140	155	36403.00000000	USDT	39172.10000000	USDT	39191.60000000	USDT	2021-07-27 01:58:31.906467+02	2021-07-28 00:53:17.699079+02	f
77	40	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	146	149	17.71200000	USDT	18.91750000	USDT	18.92220000	USDT	2021-07-27 04:58:39.11645+02	2021-07-27 13:51:25.203393+02	f
75	39	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	144	151	17.71200000	USDT	19.10840000	USDT	19.11110000	USDT	2021-07-27 03:58:39.673156+02	2021-07-27 14:50:27.188497+02	f
78	38	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	147	153	36693.20000000	USDT	38673.80000000	USDT	38688.60000000	USDT	2021-07-27 05:58:31.815314+02	2021-07-27 15:37:37.331347+02	f
47	20	LONG	2	UNI/USDT	1.00000000	UNI	6	15	OPENING	80	\N	\N	\N	\N	\N	\N	\N	2021-07-20 06:36:03.205512+02	2021-09-03 20:14:25.001072+02	f
73	38	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	142	157	17.71200000	USDT	19.78750000	USDT	19.80830000	USDT	2021-07-27 02:58:34.666423+02	2021-07-30 02:40:03.321407+02	f
57	26	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	99	104	16.10030000	USDT	17.33820000	USDT	17.44500000	USDT	2021-07-22 13:05:01.409029+02	2021-07-22 18:00:39.273077+02	f
50	23	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	85	92	14.04540000	USDT	15.47160000	USDT	15.49350000	USDT	2021-07-20 16:38:33.922938+02	2021-07-21 06:49:23.730964+02	f
37	21	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	60	125	29298.20000000	USDT	35604.40000000	USDT	35788.90000000	USDT	2021-07-06 13:09:31.519256+02	2021-07-26 02:13:51.860535+02	f
26	15	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	44	81	29369.60000000	USDT	35100.00000000	USDT	29328.70000000	USDT	2021-07-05 04:16:26.947466+02	2021-07-20 12:01:17.669514+02	f
60	33	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	105	112	32020.30000000	USDT	33945.00000000	USDT	33958.30000000	USDT	2021-07-23 12:08:28.727059+02	2021-07-24 13:15:13.547064+02	f
22	12	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	37	131	29298.20000000	USDT	36126.60000000	USDT	36135.40000000	USDT	2021-07-04 03:16:13.767586+02	2021-07-26 02:53:43.158844+02	f
51	28	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	86	123	35108.70000000	USDT	29503.10000000	USDT	35140.50000000	USDT	2021-07-20 22:58:18.540796+02	2021-07-26 01:41:25.677807+02	f
36	20	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	59	124	29298.20000000	USDT	35604.40000000	USDT	35788.90000000	USDT	2021-07-06 12:09:32.413036+02	2021-07-26 02:13:57.847532+02	f
53	29	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	88	122	35032.20000000	USDT	29515.80000000	USDT	35064.40000000	USDT	2021-07-21 01:58:38.103526+02	2021-07-26 01:39:55.67315+02	f
54	30	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	89	121	35003.10000000	USDT	29708.30000000	USDT	35032.20000000	USDT	2021-07-21 03:59:25.703432+02	2021-07-26 01:39:29.542325+02	f
38	22	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	61	126	29298.20000000	USDT	35604.40000000	USDT	35788.90000000	USDT	2021-07-06 14:09:32.884144+02	2021-07-26 02:13:58.952424+02	f
20	11	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	35	130	29298.20000000	USDT	36094.00000000	USDT	36126.60000000	USDT	2021-07-04 01:16:14.871633+02	2021-07-26 02:53:25.122489+02	f
30	17	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	48	127	29298.20000000	USDT	35854.30000000	USDT	35871.60000000	USDT	2021-07-05 06:16:28.905374+02	2021-07-26 02:46:30.598231+02	f
65	31	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	115	134	17.25540000	USDT	19.18290000	USDT	19.24000000	USDT	2021-07-24 22:12:04.930747+02	2021-07-26 03:01:10.80127+02	f
32	18	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	50	128	29298.20000000	USDT	35871.60000000	USDT	35875.50000000	USDT	2021-07-05 07:16:29.898091+02	2021-07-26 02:47:45.422664+02	f
66	32	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	116	133	17.25540000	USDT	19.06030000	USDT	19.16590000	USDT	2021-07-24 23:12:05.472802+02	2021-07-26 02:58:42.852664+02	f
34	19	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	52	129	29298.20000000	USDT	36028.60000000	USDT	36089.20000000	USDT	2021-07-05 08:16:29.633891+02	2021-07-26 02:52:35.600141+02	f
63	29	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	113	135	17.25540000	USDT	19.24000000	USDT	19.30400000	USDT	2021-07-24 18:11:43.562434+02	2021-07-26 03:01:22.840085+02	f
70	36	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	120	132	17.25540000	USDT	19.04710000	USDT	19.06030000	USDT	2021-07-25 03:12:20.071866+02	2021-07-26 02:57:58.813788+02	f
107	54	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	202	222	24.52360000	USDT	26.51000000	USDT	26.52280000	USDT	2021-08-06 04:58:30.705496+02	2021-08-06 21:07:58.208455+02	f
99	49	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	186	190	38580.20000000	USDT	40831.80000000	USDT	40859.40000000	USDT	2021-08-05 16:58:20.5118+02	2021-08-05 20:30:48.239207+02	f
133	66	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	260	277	43796.90000000	USDT	47575.00000000	USDT	47600.00000000	USDT	2021-08-10 13:58:37.629917+02	2021-08-13 22:15:28.065553+02	f
116	61	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	228	255	25.39230000	USDT	29.33100000	USDT	29.33620000	USDT	2021-08-08 13:58:37.837078+02	2021-08-10 07:16:41.514872+02	f
122	58	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	234	249	42813.90000000	USDT	45945.40000000	USDT	45951.70000000	USDT	2021-08-08 18:58:38.739099+02	2021-08-09 13:53:46.643211+02	f
105	53	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	200	223	24.52360000	USDT	26.56780000	USDT	26.59740000	USDT	2021-08-06 02:58:30.714773+02	2021-08-06 21:08:46.236181+02	f
87	43	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	165	195	37348.50000000	USDT	41170.80000000	USDT	41184.00000000	USDT	2021-08-02 23:58:52.574821+02	2021-08-05 21:10:06.238117+02	f
96	47	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	174	225	45092.90000000	USDT	37348.50000000	USDT	45113.60000000	USDT	2021-08-04 03:58:28.716736+02	2021-08-08 08:27:39.862372+02	f
95	46	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	173	226	45234.30000000	USDT	37348.50000000	USDT	45274.60000000	USDT	2021-08-04 02:58:28.858515+02	2021-08-08 08:42:53.844981+02	f
94	49	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	172	175	20.67090000	USDT	23.15230000	USDT	23.20500000	USDT	2021-08-03 03:58:52.198267+02	2021-08-04 18:44:13.388496+02	f
155	83	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	295	309	43969.00000000	USDT	49355.70000000	USDT	49392.10000000	USDT	2021-08-14 21:58:46.49448+02	2021-08-21 13:01:42.088779+02	f
100	50	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	187	188	38722.50000000	USDT	40712.30000000	USDT	40735.00000000	USDT	2021-08-05 17:58:21.224221+02	2021-08-05 20:00:30.357273+02	f
92	45	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	170	196	37348.50000000	USDT	41295.20000000	USDT	41331.20000000	USDT	2021-08-03 02:58:52.655338+02	2021-08-05 21:12:44.238083+02	f
74	36	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	143	154	36403.00000000	USDT	38732.80000000	USDT	38753.10000000	USDT	2021-07-27 03:58:32.768772+02	2021-07-27 15:40:29.302975+02	f
97	48	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	178	192	37348.50000000	USDT	41044.90000000	USDT	41097.80000000	USDT	2021-08-05 08:58:21.847746+02	2021-08-05 21:08:30.250361+02	f
91	47	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	169	176	20.67090000	USDT	23.72030000	USDT	23.76820000	USDT	2021-08-03 01:58:52.913751+02	2021-08-05 02:05:49.300262+02	f
83	41	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	161	193	37348.50000000	USDT	41097.80000000	USDT	41142.90000000	USDT	2021-08-02 21:58:52.644959+02	2021-08-05 21:08:48.264176+02	f
85	42	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	163	189	37348.50000000	USDT	40776.00000000	USDT	40794.00000000	USDT	2021-08-02 22:58:52.640524+02	2021-08-05 20:27:48.23193+02	f
93	48	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	171	181	20.67090000	USDT	23.88330000	USDT	23.90120000	USDT	2021-08-03 02:58:52.987518+02	2021-08-05 16:13:05.142006+02	f
72	37	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	141	156	17.71200000	USDT	19.64950000	USDT	19.68920000	USDT	2021-07-27 01:58:32.621485+02	2021-07-30 01:34:03.354118+02	f
89	46	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	167	182	20.67090000	USDT	23.90120000	USDT	23.93430000	USDT	2021-08-03 00:58:52.790657+02	2021-08-05 16:13:05.886502+02	f
81	42	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	159	184	20.67090000	USDT	24.23060000	USDT	24.26540000	USDT	2021-08-02 19:58:52.873437+02	2021-08-05 16:43:41.162803+02	f
98	50	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	179	185	22.09300000	USDT	24.23060000	USDT	24.26540000	USDT	2021-08-05 10:58:21.79142+02	2021-08-05 16:43:41.92134+02	f
84	43	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	162	183	20.67090000	USDT	23.95000000	USDT	23.98840000	USDT	2021-08-02 21:58:52.949164+02	2021-08-05 16:15:25.175171+02	f
86	44	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	164	177	20.67090000	USDT	23.72030000	USDT	23.76820000	USDT	2021-08-02 22:58:52.956388+02	2021-08-05 02:05:50.151427+02	f
103	52	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	198	220	24.52360000	USDT	26.45370000	USDT	26.45660000	USDT	2021-08-05 21:58:59.843955+02	2021-08-06 21:04:02.297807+02	f
88	45	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	166	180	20.67090000	USDT	23.85730000	USDT	23.88330000	USDT	2021-08-02 23:58:52.862469+02	2021-08-05 16:12:53.142377+02	f
113	58	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	208	218	24.52360000	USDT	26.37190000	USDT	26.37270000	USDT	2021-08-06 09:58:33.043409+02	2021-08-06 19:57:42.236252+02	f
111	56	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	206	216	24.52360000	USDT	26.32970000	USDT	26.34670000	USDT	2021-08-06 06:58:32.419283+02	2021-08-06 19:51:14.187458+02	f
104	52	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	199	211	39902.00000000	USDT	41885.70000000	USDT	42064.30000000	USDT	2021-08-06 02:58:27.688691+02	2021-08-06 17:58:59.859171+02	f
82	40	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	160	209	37348.50000000	USDT	41582.70000000	USDT	41658.10000000	USDT	2021-08-02 20:58:52.598495+02	2021-08-06 17:19:59.703123+02	f
80	39	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	158	210	37348.50000000	USDT	41658.10000000	USDT	41677.00000000	USDT	2021-08-02 19:58:52.163312+02	2021-08-06 17:20:17.710878+02	f
110	55	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	205	212	40122.60000000	USDT	42064.30000000	USDT	42251.90000000	USDT	2021-08-06 06:58:28.396163+02	2021-08-06 17:59:13.14046+02	f
108	54	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	203	213	39902.00000000	USDT	42064.30000000	USDT	42251.90000000	USDT	2021-08-06 05:58:27.57858+02	2021-08-06 17:59:12.494562+02	f
106	53	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	201	214	39902.00000000	USDT	42379.40000000	USDT	42428.30000000	USDT	2021-08-06 04:58:26.734702+02	2021-08-06 18:01:32.280565+02	f
101	51	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	191	219	24.52360000	USDT	26.40000000	USDT	26.40480000	USDT	2021-08-05 20:59:00.70911+02	2021-08-06 20:06:38.18672+02	f
102	51	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	197	215	39902.00000000	USDT	42481.90000000	USDT	42838.40000000	USDT	2021-08-05 21:58:59.519516+02	2021-08-06 18:03:50.258321+02	f
109	55	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	204	217	24.52360000	USDT	26.32970000	USDT	26.34670000	USDT	2021-08-06 05:58:31.574645+02	2021-08-06 19:51:15.013121+02	f
112	57	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	207	221	24.52360000	USDT	26.45660000	USDT	26.48760000	USDT	2021-08-06 07:58:32.422359+02	2021-08-06 21:04:26.192895+02	f
131	64	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	258	280	43796.90000000	USDT	47790.40000000	USDT	47807.70000000	USDT	2021-08-10 08:58:36.5484+02	2021-08-13 23:13:03.279402+02	f
158	73	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	311	334	25.02470000	USDT	30.41980000	USDT	30.47130000	USDT	2021-08-21 13:58:45.97018+02	2021-09-01 08:41:57.566117+02	f
180	90	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	343	363	25.87870000	USDT	31.20950000	USDT	25.83200000	USDT	2021-09-02 10:14:40.352104+02	2021-09-07 16:29:36.036702+02	f
148	76	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	288	299	43969.00000000	USDT	48600.90000000	USDT	48605.60000000	USDT	2021-08-14 14:58:48.798556+02	2021-08-20 17:57:59.917946+02	f
191	94	LONG	1	BTC/USDT	0.00200000	BTC	5	15	OPENED	379	\N	43457.50000000	USDT	47531.60000000	USDT	46404.40000000	USDT	2021-09-07 18:14:40.979411+02	2021-09-14 15:22:51.673643+02	f
121	64	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	233	251	25.39230000	USDT	28.45110000	USDT	28.46910000	USDT	2021-08-08 17:58:38.906303+02	2021-08-09 17:50:57.831501+02	f
176	86	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	339	367	25.83200000	USDT	31.20950000	USDT	25.76610000	USDT	2021-09-02 06:14:39.488574+02	2021-09-07 16:29:39.673384+02	f
198	102	LONG	2	UNI/USDT	1.00000000	UNI	6	15	OPENED	386	\N	21.36880000	USDT	24.96900000	USDT	23.81290000	USDT	2021-09-07 22:14:42.149044+02	2021-09-14 15:22:51.756849+02	f
120	57	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	232	250	42813.90000000	USDT	46137.90000000	USDT	46154.40000000	USDT	2021-08-08 17:58:38.605858+02	2021-08-09 16:10:45.874991+02	f
146	74	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	286	298	43969.00000000	USDT	48505.00000000	USDT	48568.10000000	USDT	2021-08-14 12:58:49.886863+02	2021-08-20 16:46:40.063294+02	f
117	62	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	229	256	25.39230000	USDT	29.46810000	USDT	29.47980000	USDT	2021-08-08 15:58:38.294883+02	2021-08-10 07:19:29.502577+02	f
129	62	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	241	358	52793.20000000	USDT	43796.90000000	USDT	52834.00000000	USDT	2021-08-09 11:58:38.810547+02	2021-09-07 05:09:18.529842+02	f
125	60	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	237	245	42813.90000000	USDT	45700.70000000	USDT	45745.50000000	USDT	2021-08-08 20:58:38.663816+02	2021-08-09 13:00:44.824789+02	f
161	75	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	314	327	25.02470000	USDT	29.97480000	USDT	29.99000000	USDT	2021-08-21 21:58:46.715446+02	2021-09-01 07:59:02.224627+02	f
149	77	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	289	301	43969.00000000	USDT	48823.70000000	USDT	48870.70000000	USDT	2021-08-14 15:58:48.027255+02	2021-08-20 18:15:57.969553+02	f
182	92	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	345	366	25.83200000	USDT	31.20950000	USDT	25.76610000	USDT	2021-09-02 12:14:40.676524+02	2021-09-07 16:29:38.443114+02	f
174	84	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	337	359	26.03590000	USDT	31.20950000	USDT	25.88410000	USDT	2021-09-02 04:14:38.241883+02	2021-09-07 16:29:23.191145+02	f
137	68	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	264	282	26.86920000	USDT	30.47340000	USDT	30.50000000	USDT	2021-08-10 17:58:39.199676+02	2021-08-14 01:37:35.263419+02	f
184	94	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	348	369	24.83560000	USDT	30.29960000	USDT	24.71280000	USDT	2021-09-03 18:14:40.758987+02	2021-09-07 16:48:56.00451+02	f
151	79	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	291	308	43969.00000000	USDT	49231.70000000	USDT	49269.70000000	USDT	2021-08-14 17:58:47.542569+02	2021-08-20 23:59:23.943156+02	f
141	70	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	268	281	26.86920000	USDT	30.43800000	USDT	30.44350000	USDT	2021-08-10 19:58:41.931484+02	2021-08-14 01:37:03.230501+02	f
134	67	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	261	284	43796.90000000	USDT	47851.50000000	USDT	47910.70000000	USDT	2021-08-10 15:58:37.711226+02	2021-08-14 11:40:26.320768+02	f
178	88	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	341	368	25.83200000	USDT	31.20950000	USDT	25.76610000	USDT	2021-09-02 08:14:38.819491+02	2021-09-07 16:29:39.024163+02	f
157	85	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	310	346	46351.80000000	USDT	50959.00000000	USDT	50998.00000000	USDT	2021-08-21 13:58:43.319547+02	2021-09-03 16:33:43.263017+02	f
167	79	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	320	326	25.02470000	USDT	29.92000000	USDT	29.92510000	USDT	2021-08-22 01:58:46.804927+02	2021-09-01 07:57:08.358353+02	f
164	77	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	317	331	25.02470000	USDT	30.26000000	USDT	30.36000000	USDT	2021-08-21 23:58:47.969492+02	2021-09-01 08:14:22.200982+02	f
144	72	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	272	296	26.86920000	USDT	30.73540000	USDT	30.75510000	USDT	2021-08-12 00:58:42.004843+02	2021-08-16 07:38:03.069313+02	f
127	61	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	239	247	42813.90000000	USDT	45794.10000000	USDT	45851.00000000	USDT	2021-08-08 21:58:38.006213+02	2021-08-09 13:01:20.781566+02	f
170	81	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	323	330	25.02470000	USDT	30.26000000	USDT	30.36000000	USDT	2021-08-22 03:58:47.389142+02	2021-09-01 08:14:23.053287+02	f
140	71	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	267	274	43796.90000000	USDT	47333.40000000	USDT	47359.40000000	USDT	2021-08-10 19:58:41.600707+02	2021-08-13 22:08:17.242197+02	f
136	69	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	263	275	43796.90000000	USDT	47333.40000000	USDT	47359.40000000	USDT	2021-08-10 17:58:38.067004+02	2021-08-13 22:08:18.015983+02	f
186	95	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	355	372	24.58080000	USDT	29.96690000	USDT	24.44300000	USDT	2021-09-06 14:14:37.384268+02	2021-09-07 16:49:06.061023+02	f
153	81	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	293	304	43969.00000000	USDT	49098.10000000	USDT	49221.50000000	USDT	2021-08-14 19:58:46.416582+02	2021-08-20 23:58:43.928192+02	f
130	63	SHORT	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	244	373	52891.40000000	USDT	43648.00000000	USDT	43194.30000000	USDT	2021-08-09 12:58:39.254897+02	2021-09-07 17:09:44.891574+02	f
185	91	LONG	1	BTC/USDT	0.00200000	BTC	5	15	CLOSED	354	374	43648.00000000	USDT	52891.40000000	USDT	43194.30000000	USDT	2021-09-06 14:14:36.964764+02	2021-09-07 17:09:51.475837+02	f
166	88	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	319	349	46351.80000000	USDT	51011.40000000	USDT	51499.30000000	USDT	2021-08-22 01:58:46.495783+02	2021-09-05 22:53:26.206932+02	f
190	97	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	377	378	21.37500000	USDT	22.87060000	USDT	22.90240000	USDT	2021-09-07 17:14:44.704027+02	2021-09-07 17:27:27.709673+02	f
160	86	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	313	350	46351.80000000	USDT	51011.40000000	USDT	51499.30000000	USDT	2021-08-21 21:58:46.384297+02	2021-09-05 22:53:26.96524+02	f
193	95	LONG	1	BTC/USDT	0.00200000	BTC	5	15	OPENED	381	\N	43457.50000000	USDT	47524.30000000	USDT	46404.40000000	USDT	2021-09-07 19:14:41.930467+02	2021-09-14 15:22:51.739783+02	f
159	74	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	312	335	25.02470000	USDT	30.62220000	USDT	30.63150000	USDT	2021-08-21 20:58:45.192956+02	2021-09-01 08:44:41.568853+02	f
172	82	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	325	329	25.02470000	USDT	30.06040000	USDT	30.07070000	USDT	2021-08-22 04:58:47.528142+02	2021-09-01 07:59:46.176732+02	f
138	70	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	265	273	43796.90000000	USDT	47039.80000000	USDT	47303.90000000	USDT	2021-08-10 18:58:40.441536+02	2021-08-13 22:06:48.08932+02	f
194	99	LONG	2	UNI/USDT	1.00000000	UNI	6	15	OPENED	382	\N	21.36880000	USDT	25.28750000	USDT	23.81290000	USDT	2021-09-07 19:14:43.941849+02	2021-09-14 15:22:51.760454+02	f
183	93	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	347	370	24.83560000	USDT	30.29960000	USDT	24.71280000	USDT	2021-09-03 17:14:41.251941+02	2021-09-07 16:48:56.756755+02	f
115	60	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	227	257	25.39230000	USDT	29.50000000	USDT	29.51150000	USDT	2021-08-08 12:58:38.658811+02	2021-08-10 07:20:25.500173+02	f
154	82	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	294	303	43969.00000000	USDT	49058.70000000	USDT	49098.10000000	USDT	2021-08-14 20:58:45.555261+02	2021-08-20 23:58:25.954431+02	f
143	71	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	270	283	26.86920000	USDT	30.60240000	USDT	30.62170000	USDT	2021-08-11 00:58:41.906994+02	2021-08-14 01:58:27.292801+02	f
128	67	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	240	242	25.39230000	USDT	27.74240000	USDT	27.77760000	USDT	2021-08-08 21:58:38.537149+02	2021-08-09 12:36:08.088702+02	f
126	66	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	238	243	25.39230000	USDT	27.74240000	USDT	27.77760000	USDT	2021-08-08 20:58:38.994083+02	2021-08-09 12:36:08.769042+02	f
118	56	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	230	252	42813.90000000	USDT	46168.10000000	USDT	46175.20000000	USDT	2021-08-08 16:58:39.315953+02	2021-08-09 20:38:55.929306+02	f
124	65	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	236	246	25.39230000	USDT	28.07710000	USDT	28.08640000	USDT	2021-08-08 19:58:38.790708+02	2021-08-09 13:00:45.465501+02	f
123	59	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	235	248	42813.90000000	USDT	45892.20000000	USDT	45920.20000000	USDT	2021-08-08 19:58:38.478612+02	2021-08-09 13:52:34.660338+02	f
181	91	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	344	360	26.03590000	USDT	31.20950000	USDT	25.88410000	USDT	2021-09-02 11:14:39.710484+02	2021-09-07 16:29:24.088378+02	f
177	87	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	340	364	25.87870000	USDT	31.20950000	USDT	25.83200000	USDT	2021-09-02 07:14:39.642048+02	2021-09-07 16:29:36.938647+02	f
173	83	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	336	361	26.03590000	USDT	31.20950000	USDT	25.88410000	USDT	2021-09-02 03:14:37.161356+02	2021-09-07 16:29:29.948616+02	f
114	59	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	224	254	25.39230000	USDT	29.14660000	USDT	29.18590000	USDT	2021-08-08 04:58:36.684408+02	2021-08-10 06:23:37.508119+02	f
179	89	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	342	362	26.03590000	USDT	31.20950000	USDT	25.88410000	USDT	2021-09-02 09:14:39.826469+02	2021-09-07 16:29:30.62795+02	f
142	72	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	269	276	43796.90000000	USDT	47359.40000000	USDT	47555.20000000	USDT	2021-08-10 20:58:41.77547+02	2021-08-13 22:08:39.240162+02	f
132	65	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	259	278	43796.90000000	USDT	47623.60000000	USDT	47707.30000000	USDT	2021-08-10 12:58:38.009028+02	2021-08-13 22:16:25.428991+02	f
135	68	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	262	279	43796.90000000	USDT	47623.60000000	USDT	47707.30000000	USDT	2021-08-10 16:58:37.511109+02	2021-08-13 22:16:26.188522+02	f
188	96	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	357	371	24.64280000	USDT	29.96690000	USDT	24.58080000	USDT	2021-09-06 15:14:37.01078+02	2021-09-07 16:49:02.069567+02	f
175	85	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	338	365	25.87870000	USDT	31.20950000	USDT	25.83200000	USDT	2021-09-02 05:14:38.455194+02	2021-09-07 16:29:37.806827+02	f
187	92	LONG	1	BTC/USDT	0.00200000	BTC	5	15	CLOSED	356	375	43648.00000000	USDT	52891.40000000	USDT	43194.30000000	USDT	2021-09-06 15:14:36.708234+02	2021-09-07 17:09:50.863341+02	f
119	63	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	231	253	25.39230000	USDT	28.65500000	USDT	28.67460000	USDT	2021-08-08 16:58:39.607729+02	2021-08-09 20:39:23.836491+02	f
147	75	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	287	302	43969.00000000	USDT	48870.70000000	USDT	48904.80000000	USDT	2021-08-14 13:58:49.3144+02	2021-08-20 18:16:58.045143+02	f
139	69	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	266	271	28.30900000	USDT	30.06050000	USDT	30.10320000	USDT	2021-08-10 18:58:40.781357+02	2021-08-11 09:05:44.391584+02	f
156	84	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	297	300	43969.00000000	USDT	48765.10000000	USDT	48791.50000000	USDT	2021-08-16 15:58:45.878873+02	2021-08-20 18:08:07.931859+02	f
145	73	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	285	306	43969.00000000	USDT	49098.10000000	USDT	49221.50000000	USDT	2021-08-14 11:58:49.410282+02	2021-08-20 23:58:49.966557+02	f
152	80	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	292	305	43969.00000000	USDT	49098.10000000	USDT	49221.50000000	USDT	2021-08-14 18:58:47.124953+02	2021-08-20 23:58:50.683327+02	f
163	76	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	316	328	25.02470000	USDT	30.00000000	USDT	30.02300000	USDT	2021-08-21 22:58:47.974462+02	2021-09-01 07:59:24.164348+02	f
150	78	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	290	307	43969.00000000	USDT	49221.50000000	USDT	49231.70000000	USDT	2021-08-14 16:58:49.17703+02	2021-08-20 23:59:07.954909+02	f
165	78	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	318	332	25.02470000	USDT	30.26000000	USDT	30.36000000	USDT	2021-08-22 00:58:47.502237+02	2021-09-01 08:14:23.787694+02	f
169	80	LONG	2	UNI/USDT	1.00000000	UNI	6	15	CLOSED	322	333	25.02470000	USDT	30.36000000	USDT	30.36570000	USDT	2021-08-22 02:58:47.85302+02	2021-09-01 08:14:24.507887+02	f
168	89	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	321	351	46351.80000000	USDT	51011.40000000	USDT	51499.30000000	USDT	2021-08-22 02:58:46.761634+02	2021-09-05 22:53:27.743569+02	f
162	87	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	315	352	46351.80000000	USDT	51011.40000000	USDT	51499.30000000	USDT	2021-08-21 22:58:47.607121+02	2021-09-05 22:53:31.167566+02	f
171	90	LONG	1	BTC/USDT	0.00100000	BTC	5	15	CLOSED	324	353	46351.80000000	USDT	51011.40000000	USDT	51499.30000000	USDT	2021-08-22 04:58:46.366082+02	2021-09-05 22:53:31.898283+02	f
189	93	LONG	1	BTC/USDT	0.00200000	BTC	5	15	OPENED	376	\N	43457.50000000	USDT	47531.60000000	USDT	46404.40000000	USDT	2021-09-07 17:14:44.174581+02	2021-09-14 15:22:51.743332+02	f
195	96	LONG	1	BTC/USDT	0.00200000	BTC	5	15	OPENED	383	\N	43457.50000000	USDT	47353.60000000	USDT	46404.40000000	USDT	2021-09-07 20:14:41.552279+02	2021-09-14 15:22:51.746709+02	f
192	98	LONG	2	UNI/USDT	1.00000000	UNI	6	15	OPENED	380	\N	21.36880000	USDT	25.28750000	USDT	23.81290000	USDT	2021-09-07 18:14:44.944076+02	2021-09-14 15:22:51.769428+02	f
197	101	LONG	2	UNI/USDT	1.00000000	UNI	6	15	OPENED	385	\N	21.36880000	USDT	24.96900000	USDT	23.81290000	USDT	2021-09-07 21:14:43.139979+02	2021-09-14 15:22:51.798107+02	f
196	100	LONG	2	UNI/USDT	1.00000000	UNI	6	15	OPENED	384	\N	21.36880000	USDT	24.96900000	USDT	23.81290000	USDT	2021-09-07 20:14:43.244522+02	2021-09-14 15:22:51.802021+02	f
199	103	LONG	2	UNI/USDT	1.00000000	UNI	6	15	OPENED	387	\N	21.36880000	USDT	24.96900000	USDT	23.81290000	USDT	2021-09-07 23:14:41.920099+02	2021-09-14 15:22:51.806098+02	f
\.


--
-- Data for Name: strategies; Type: TABLE DATA; Schema: public; Owner: cassandre_trading_bot
--

COPY public.strategies (id, strategy_id, type, name, created_on, updated_on) FROM stdin;
1	001	BASIC_TA4J_STRATEGY	Bitcoin	2021-07-01 17:22:13.521677+02	\N
2	002	BASIC_TA4J_STRATEGY	Uniswap	2021-07-01 17:22:14.097543+02	\N
\.


--
-- Data for Name: trades; Type: TABLE DATA; Schema: public; Owner: cassandre_trading_bot
--

COPY public.trades (id, trade_id, type, FK_ORDER_UID, currency_pair, amount_value, amount_currency, price_value, price_currency, fee_value, fee_currency, user_reference, "timestamp", created_on, updated_on) FROM stdin;
1	60ddfbc12e113d29238c57ea	BID	1	BTC/USDT	0.00050000	BTC	33181.80000000	USDT	0.01659090	USDT	\N	2021-07-01 19:30:42+02	2021-07-01 19:30:50.384136+02	\N
2	60ddfbc12e113d29238c57e9	BID	1	BTC/USDT	0.00050000	BTC	33181.80000000	USDT	0.01659090	USDT	\N	2021-07-01 19:30:42+02	2021-07-01 19:30:50.406256+02	\N
3	60ddfbc21eefb0218434f5d9	BID	2	UNI/USDT	1.00000000	UNI	17.66530000	USDT	0.01766530	USDT	\N	2021-07-01 19:30:42+02	2021-07-01 19:30:50.414531+02	\N
4	60de17e22e113d2923980ea0	BID	3	BTC/USDT	0.00100000	BTC	33034.20000000	USDT	0.03303420	USDT	\N	2021-07-01 21:30:42+02	2021-07-01 21:30:51.223979+02	\N
5	60de17e21eefb0218436f095	BID	4	UNI/USDT	1.00000000	UNI	17.72360000	USDT	0.01772360	USDT	\N	2021-07-01 21:30:42+02	2021-07-01 21:30:51.227194+02	\N
6	60de6c441eefb021843bddd2	BID	5	UNI/USDT	1.00000000	UNI	17.84390000	USDT	0.01784390	USDT	\N	2021-07-02 03:30:44+02	2021-07-02 03:30:53.932969+02	\N
7	60de7a542e113d2923c1e2ce	BID	6	BTC/USDT	0.00100000	BTC	33334.10000000	USDT	0.03333410	USDT	\N	2021-07-02 04:30:45+02	2021-07-02 04:30:53.932308+02	\N
33	60e0180b1eefb021845b83c5	ASK	27	UNI/USDT	1.00000000	UNI	18.71220000	USDT	0.01871220	USDT	\N	2021-07-03 09:55:55+02	2021-07-03 09:56:03.962245+02	\N
28	60e015cf1eefb021845b4b49	ASK	23	UNI/USDT	1.00000000	UNI	18.39990000	USDT	0.01839990	USDT	\N	2021-07-03 09:46:23+02	2021-07-03 09:46:33.811361+02	\N
20	60dec0a51eefb0218442c9dc	BID	16	UNI/USDT	1.00000000	UNI	17.35210000	USDT	0.01735210	USDT	\N	2021-07-02 09:30:45+02	2021-07-02 09:30:58.054347+02	\N
24	60df231c2e113d2923052d18	ASK	19	BTC/USDT	0.00100000	BTC	33591.90000000	USDT	0.03359190	USDT	\N	2021-07-02 16:30:53+02	2021-07-02 16:31:03.979428+02	\N
18	60deb2961eefb0218441bded	BID	15	UNI/USDT	0.85450000	UNI	17.10470000	USDT	0.01461597	USDT	\N	2021-07-02 08:30:46+02	2021-07-02 08:30:54.710701+02	2021-07-03 08:30:45.408412+02
25	60df9a1a1eefb021845365a3	ASK	20	UNI/USDT	1.00000000	UNI	18.11370000	USDT	0.01811370	USDT	\N	2021-07-03 00:58:34+02	2021-07-03 00:58:39.162706+02	\N
11	60de88642e113d2923c7a04f	BID	8	BTC/USDT	0.00100000	BTC	32865.80000000	USDT	0.03286580	USDT	\N	2021-07-02 05:30:44+02	2021-07-02 05:30:54.036107+02	\N
19	60deb2961eefb0218441bdec	BID	15	UNI/USDT	0.14550000	UNI	17.10440000	USDT	0.00248869	USDT	\N	2021-07-02 08:30:46+02	2021-07-02 08:30:54.713934+02	2021-07-03 08:30:45.410993+02
41	60e029d52e113d292358ec9b	ASK	32	BTC/USDT	0.00099000	BTC	34722.00000000	USDT	0.03437478	USDT	\N	2021-07-03 11:11:49+02	2021-07-03 11:11:58.775375+02	\N
34	60e01bc91eefb021845bcf79	ASK	28	UNI/USDT	1.00000000	UNI	18.78040000	USDT	0.01878040	USDT	\N	2021-07-03 10:11:53+02	2021-07-03 10:12:03.863406+02	\N
12	60de88641eefb021843e232e	BID	9	UNI/USDT	1.00000000	UNI	17.39250000	USDT	0.01739250	USDT	\N	2021-07-02 05:30:44+02	2021-07-02 05:30:54.050375+02	\N
42	60e052622e113d2923658d11	ASK	33	BTC/USDT	0.00100000	BTC	34788.90000000	USDT	0.03478890	USDT	\N	2021-07-03 14:04:50+02	2021-07-03 14:04:59.058169+02	\N
13	60de96741eefb021843f4ef3	BID	11	UNI/USDT	1.00000000	UNI	17.30260000	USDT	0.01730260	USDT	\N	2021-07-02 06:30:44+02	2021-07-02 06:30:48.780241+02	\N
14	60de96742e113d2923cd5f5e	BID	10	BTC/USDT	0.00100000	BTC	33044.30000000	USDT	0.03304430	USDT	\N	2021-07-02 06:30:44+02	2021-07-02 06:30:54.705611+02	\N
43	60e0a1ae2e113d29237c566e	ASK	34	BTC/USDT	0.00100000	BTC	34841.40000000	USDT	0.03484140	USDT	\N	2021-07-03 19:43:10+02	2021-07-03 19:43:17.513639+02	\N
44	60e0efbe2e113d292395ff65	BID	35	BTC/USDT	0.00100000	BTC	34397.10000000	USDT	0.03439710	USDT	\N	2021-07-04 01:16:14+02	2021-07-04 01:16:24.394806+02	\N
45	60e0efc11eefb0218469a46f	BID	36	UNI/USDT	1.00000000	UNI	18.90670000	USDT	0.01890670	USDT	\N	2021-07-04 01:16:17+02	2021-07-04 01:16:24.402547+02	\N
17	60deb2952e113d2923d82423	BID	14	BTC/USDT	0.00100000	BTC	32892.40000000	USDT	0.03289240	USDT	\N	2021-07-02 08:30:45+02	2021-07-02 08:30:54.698831+02	\N
49	60e14a522e113d2923b11b20	ASK	39	BTC/USDT	0.00050000	BTC	35005.30000000	USDT	0.01750265	USDT	\N	2021-07-04 07:42:42+02	2021-07-04 07:42:50.262276+02	\N
21	60deceb52e113d2923e41ef4	ASK	17	BTC/USDT	0.00094362	BTC	33343.90000000	USDT	0.03146397	USDT	\N	2021-07-02 10:30:45+02	2021-07-02 10:30:57.963255+02	2021-07-03 10:30:45.83733+02
22	60deceb52e113d2923e41ef3	ASK	17	BTC/USDT	0.00005638	BTC	33343.90000000	USDT	0.00187993	USDT	\N	2021-07-02 10:30:45+02	2021-07-02 10:30:57.967203+02	2021-07-03 10:30:45.840358+02
23	60df150a2e113d2923005f7a	ASK	18	BTC/USDT	0.00100000	BTC	33298.30000000	USDT	0.03329830	USDT	\N	2021-07-02 15:30:50+02	2021-07-02 15:31:04.068721+02	\N
27	60e015af1eefb021845b486a	ASK	22	UNI/USDT	1.00000000	UNI	18.36940000	USDT	0.01836940	USDT	\N	2021-07-03 09:45:51+02	2021-07-03 09:46:03.804611+02	\N
30	60e017f52e113d292351b436	ASK	25	BTC/USDT	0.00050000	BTC	34571.70000000	USDT	0.01728585	USDT	\N	2021-07-03 09:55:33+02	2021-07-03 09:55:45.823055+02	\N
15	60dea4841eefb02184407b13	BID	13	UNI/USDT	1.00000000	UNI	17.26860000	USDT	0.01726860	USDT	\N	2021-07-02 07:30:44+02	2021-07-02 07:30:54.744086+02	\N
16	60dea4842e113d2923d2396c	BID	12	BTC/USDT	0.00100000	BTC	33090.80000000	USDT	0.03309080	USDT	\N	2021-07-02 07:30:44+02	2021-07-02 07:31:00.703686+02	\N
31	60e017f52e113d292351b435	ASK	25	BTC/USDT	0.00050000	BTC	34571.80000000	USDT	0.01728590	USDT	\N	2021-07-03 09:55:33+02	2021-07-03 09:55:45.828456+02	\N
32	60e017f62e113d292351b479	ASK	26	BTC/USDT	0.00100000	BTC	34564.40000000	USDT	0.03456440	USDT	\N	2021-07-03 09:55:34+02	2021-07-03 09:55:45.830901+02	\N
29	60e016681eefb021845b5e23	ASK	24	UNI/USDT	1.00000000	UNI	18.54840000	USDT	0.01854840	USDT	\N	2021-07-03 09:48:56+02	2021-07-03 09:49:03.808651+02	\N
26	60e014861eefb021845b2f3b	ASK	21	UNI/USDT	1.00000000	UNI	18.29150000	USDT	0.01829150	USDT	\N	2021-07-03 09:40:55+02	2021-07-03 09:41:03.400266+02	\N
8	60de7a551eefb021843cf625	BID	7	UNI/USDT	0.49970000	UNI	17.76080000	USDT	0.00887507	USDT	\N	2021-07-02 04:30:45+02	2021-07-02 04:30:53.935166+02	2021-07-03 04:30:45.152805+02
9	60de7a551eefb021843cf624	BID	7	UNI/USDT	0.48730000	UNI	17.76050000	USDT	0.00865469	USDT	\N	2021-07-02 04:30:45+02	2021-07-02 04:30:53.937564+02	2021-07-03 04:30:45.155374+02
10	60de7a551eefb021843cf623	BID	7	UNI/USDT	0.01300000	UNI	17.76010000	USDT	0.00023088	USDT	\N	2021-07-02 04:30:45+02	2021-07-02 04:30:53.939664+02	2021-07-03 04:30:45.15786+02
35	60e0270f1eefb021845c941d	ASK	29	UNI/USDT	1.00000000	UNI	18.82170000	USDT	0.01882170	USDT	\N	2021-07-03 10:59:59+02	2021-07-03 11:00:03.898471+02	\N
36	60e028141eefb021845ca753	ASK	30	UNI/USDT	1.00000000	UNI	18.88980000	USDT	0.01888980	USDT	\N	2021-07-03 11:04:20+02	2021-07-03 11:04:28.77114+02	\N
50	60e14da81eefb02184702d3a	ASK	40	UNI/USDT	1.00000000	UNI	20.18140000	USDT	0.02018140	USDT	\N	2021-07-04 07:56:56+02	2021-07-04 07:57:08.658915+02	\N
47	60e10be21eefb021846bdee5	BID	38	UNI/USDT	1.00000000	UNI	19.02070000	USDT	0.01902070	USDT	\N	2021-07-04 03:16:18+02	2021-07-04 03:16:24.968917+02	\N
39	60e029d52e113d292358ec58	ASK	31	BTC/USDT	0.00050000	BTC	34727.30000000	USDT	0.01736365	USDT	\N	2021-07-03 11:11:49+02	2021-07-03 11:11:58.769462+02	\N
46	60e10bdd2e113d29239f8300	BID	37	BTC/USDT	0.00100000	BTC	34408.70000000	USDT	0.03440870	USDT	\N	2021-07-04 03:16:13+02	2021-07-04 03:16:24.96412+02	\N
37	60e029d52e113d292358ec5a	ASK	31	BTC/USDT	0.00043141	BTC	34727.30000000	USDT	0.01498170	USDT	\N	2021-07-03 11:11:49+02	2021-07-03 11:11:58.762814+02	2021-07-04 11:11:51.030797+02
40	60e029d52e113d292358ec9c	ASK	32	BTC/USDT	0.00001000	BTC	34720.60000000	USDT	0.00034721	USDT	\N	2021-07-03 11:11:49+02	2021-07-03 11:11:58.772611+02	2021-07-04 11:11:51.035648+02
48	60e14a522e113d2923b11b21	ASK	39	BTC/USDT	0.00050000	BTC	35005.30000000	USDT	0.01750265	USDT	\N	2021-07-04 07:42:42+02	2021-07-04 07:42:50.254728+02	\N
51	60e14dae1eefb02184702ecf	ASK	41	UNI/USDT	1.00000000	UNI	20.26610000	USDT	0.02026610	USDT	\N	2021-07-04 07:57:02+02	2021-07-04 07:57:14.582183+02	\N
52	60e24f5b2e113d292306b9f1	BID	42	BTC/USDT	0.00100000	BTC	34987.80000000	USDT	0.03498780	USDT	\N	2021-07-05 02:16:27+02	2021-07-05 02:16:37.185252+02	\N
38	60e029d52e113d292358ec59	ASK	31	BTC/USDT	0.00006859	BTC	34727.30000000	USDT	0.00238195	USDT	\N	2021-07-03 11:11:49+02	2021-07-03 11:11:58.766716+02	2021-07-04 11:11:51.032781+02
53	60e25d6b2e113d29230c3baa	BID	43	BTC/USDT	0.00100000	BTC	34903.40000000	USDT	0.03490340	USDT	\N	2021-07-05 03:16:27+02	2021-07-05 03:16:37.168974+02	\N
54	60e26b7a2e113d2923122ac2	BID	44	BTC/USDT	0.00100000	BTC	34547.20000000	USDT	0.03454720	USDT	\N	2021-07-05 04:16:26+02	2021-07-05 04:16:37.168887+02	\N
55	60e26b7b1eefb021848388ed	BID	45	UNI/USDT	1.00000000	UNI	20.41940000	USDT	0.02041940	USDT	\N	2021-07-05 04:16:27+02	2021-07-05 04:16:37.199518+02	\N
56	60e2798b2e113d292317a82a	BID	46	BTC/USDT	0.00050000	BTC	34482.60000000	USDT	0.01724130	USDT	\N	2021-07-05 05:16:27+02	2021-07-05 05:16:39.33748+02	\N
57	60e2798b2e113d292317a829	BID	46	BTC/USDT	0.00050000	BTC	34482.50000000	USDT	0.01724125	USDT	\N	2021-07-05 05:16:27+02	2021-07-05 05:16:39.34268+02	\N
58	60e2798c1eefb02184849096	BID	47	UNI/USDT	1.00000000	UNI	20.41540000	USDT	0.02041540	USDT	\N	2021-07-05 05:16:28+02	2021-07-05 05:16:39.345697+02	\N
59	60e2879c2e113d29231ca7fb	BID	48	BTC/USDT	0.00100000	BTC	34160.70000000	USDT	0.03416070	USDT	\N	2021-07-05 06:16:28+02	2021-07-05 06:16:37.578906+02	\N
60	60e2879d1eefb021848594ba	BID	49	UNI/USDT	1.00000000	UNI	20.08410000	USDT	0.02008410	USDT	\N	2021-07-05 06:16:29+02	2021-07-05 06:16:37.583921+02	\N
61	60e295ad2e113d29232107ab	BID	50	BTC/USDT	0.00050000	BTC	34164.60000000	USDT	0.01708230	USDT	\N	2021-07-05 07:16:29+02	2021-07-05 07:16:37.871489+02	\N
62	60e295ae1eefb0218486a4d1	BID	51	UNI/USDT	1.00000000	UNI	20.16950000	USDT	0.02016950	USDT	\N	2021-07-05 07:16:30+02	2021-07-05 07:16:37.902185+02	\N
63	60e295ad2e113d29232107aa	BID	50	BTC/USDT	0.00050000	BTC	34164.60000000	USDT	0.01708230	USDT	\N	2021-07-05 07:16:29+02	2021-07-05 07:16:43.880921+02	\N
64	60e2a3bd2e113d2923258130	BID	52	BTC/USDT	0.00100000	BTC	34314.30000000	USDT	0.03431430	USDT	\N	2021-07-05 08:16:29+02	2021-07-05 08:16:37.915071+02	\N
65	60e2a3bd1eefb0218487afcb	BID	53	UNI/USDT	1.00000000	UNI	20.43220000	USDT	0.02043220	USDT	\N	2021-07-05 08:16:29+02	2021-07-05 08:16:37.919857+02	\N
66	60e3c6731eefb021849d354c	ASK	54	UNI/USDT	1.00000000	UNI	21.27520000	USDT	0.02127520	USDT	\N	2021-07-06 04:56:51+02	2021-07-06 04:57:03.647628+02	\N
67	60e3cd331eefb021849dc481	ASK	55	UNI/USDT	1.00000000	UNI	21.34590000	USDT	0.02134590	USDT	\N	2021-07-06 05:25:39+02	2021-07-06 05:25:45.758026+02	\N
68	60e3d5301eefb021849e6d6d	ASK	56	UNI/USDT	1.00000000	UNI	21.65060000	USDT	0.02165060	USDT	\N	2021-07-06 05:59:44+02	2021-07-06 05:59:51.438427+02	\N
69	60e3d5311eefb021849e6db4	ASK	57	UNI/USDT	1.00000000	UNI	21.63210000	USDT	0.02163210	USDT	\N	2021-07-06 05:59:45+02	2021-07-06 05:59:51.442935+02	\N
70	60e3d61f1eefb021849e8be7	ASK	58	UNI/USDT	1.00000000	UNI	21.66150000	USDT	0.02166150	USDT	\N	2021-07-06 06:03:43+02	2021-07-06 06:03:51.698657+02	\N
71	60e42bdc2e113d2923acf80b	BID	59	BTC/USDT	0.00100000	BTC	34060.80000000	USDT	0.03406080	USDT	\N	2021-07-06 12:09:32+02	2021-07-06 12:09:42.920696+02	\N
72	60e439eb2e113d2923b3657b	BID	60	BTC/USDT	0.00050000	BTC	33947.80000000	USDT	0.01697390	USDT	\N	2021-07-06 13:09:31+02	2021-07-06 13:09:42.918364+02	\N
73	60e439eb2e113d2923b3657a	BID	60	BTC/USDT	0.00050000	BTC	33947.80000000	USDT	0.01697390	USDT	\N	2021-07-06 13:09:31+02	2021-07-06 13:09:42.948732+02	\N
86	60efb88b1eefb0218475477a	BID	70	UNI/USDT	1.00000000	UNI	17.66300000	USDT	0.01766300	USDT	\N	2021-07-15 06:24:43+02	2021-07-15 06:24:49.801289+02	\N
88	60f5718c2e113d2923911ad4	BID	71	BTC/USDT	0.00100000	BTC	30813.30000000	USDT	0.03081330	USDT	\N	2021-07-19 14:35:24+02	2021-07-19 14:35:26.878273+02	\N
87	60f5718c2e113d2923911ad5	BID	71	BTC/USDT	0.00005162	BTC	30815.60000000	USDT	0.00159070	USDT	\N	2021-07-19 14:35:24+02	2021-07-19 14:35:26.829656+02	2021-07-20 12:01:17.417181+02
102	60f6d1c01eefb02184e61bb6	BID	83	UNI/USDT	1.00000000	UNI	14.58360000	USDT	0.01458360	USDT	\N	2021-07-20 15:38:08+02	2021-07-20 15:38:18.354213+02	\N
104	60f6dfe91eefb02184e737e1	BID	85	UNI/USDT	1.00000000	UNI	14.60890000	USDT	0.01460890	USDT	\N	2021-07-20 16:38:33+02	2021-07-20 16:38:40.210227+02	\N
82	60eeacfd2e113d29232e6f35	ASK	66	BTC/USDT	0.00100000	BTC	32369.10000000	USDT	0.03236910	USDT	\N	2021-07-14 11:23:09+02	2021-07-14 11:23:21.404215+02	\N
91	60f6389e1eefb02184dcb02a	ASK	74	UNI/USDT	1.00000000	UNI	15.02170000	USDT	0.01502170	USDT	\N	2021-07-20 04:44:46+02	2021-07-20 04:44:53.614983+02	\N
89	60f5718c2e113d2923911c0f	BID	72	BTC/USDT	0.00105027	BTC	30784.40000000	USDT	0.03233193	USDT	\N	2021-07-19 14:35:24+02	2021-07-19 14:35:26.884185+02	2021-07-20 12:01:17.426812+02
98	60f652b22e113d2923eab4b1	BID	79	BTC/USDT	0.00050000	BTC	29602.70000000	USDT	0.01480135	USDT	\N	2021-07-20 06:36:02+02	2021-07-20 06:36:17.385624+02	\N
74	60e447fc2e113d2923b995f9	BID	61	BTC/USDT	0.00051903	BTC	34010.00000000	USDT	0.01765221	USDT	\N	2021-07-06 14:09:32+02	2021-07-06 14:09:42.986305+02	2021-07-07 14:09:29.967502+02
75	60e447fc2e113d2923b995f8	BID	61	BTC/USDT	0.00029087	BTC	34010.00000000	USDT	0.00989249	USDT	\N	2021-07-06 14:09:32+02	2021-07-06 14:09:42.991889+02	2021-07-07 14:09:29.969816+02
76	60e447fc2e113d2923b995f7	BID	61	BTC/USDT	0.00005734	BTC	34010.00000000	USDT	0.00195013	USDT	\N	2021-07-06 14:09:32+02	2021-07-06 14:09:42.995657+02	2021-07-07 14:09:29.972931+02
77	60e447fc2e113d2923b995f6	BID	61	BTC/USDT	0.00013276	BTC	34010.00000000	USDT	0.00451517	USDT	\N	2021-07-06 14:09:32+02	2021-07-06 14:09:42.998324+02	2021-07-07 14:09:29.975887+02
94	60f644972e113d2923e4678c	BID	77	BTC/USDT	0.00100000	BTC	29627.40000000	USDT	0.02962740	USDT	\N	2021-07-20 05:35:51+02	2021-07-20 05:36:00.543635+02	\N
96	60f652b22e113d2923eab4b0	BID	79	BTC/USDT	0.00050000	BTC	29602.70000000	USDT	0.01480135	USDT	\N	2021-07-20 06:36:02+02	2021-07-20 06:36:11.410724+02	\N
83	60eebb152e113d29233433b8	ASK	67	BTC/USDT	0.00100000	BTC	32410.70000000	USDT	0.03241070	USDT	\N	2021-07-14 12:23:17+02	2021-07-14 12:23:25.404849+02	\N
78	60ee58ef2e113d29230d296a	BID	62	BTC/USDT	0.00105222	BTC	31884.50000000	USDT	0.03354951	USDT	\N	2021-07-14 05:24:32+02	2021-07-14 05:24:39.493254+02	2021-07-14 13:23:23.390228+02
79	60ee593b2e113d29230d90b9	BID	63	BTC/USDT	0.00105078	BTC	31744.00000000	USDT	0.03335596	USDT	\N	2021-07-14 05:25:47+02	2021-07-14 05:25:55.368048+02	2021-07-14 13:23:23.45908+02
80	60ee596c2e113d29230dce77	BID	64	BTC/USDT	0.00105106	BTC	31756.00000000	USDT	0.03337746	USDT	\N	2021-07-14 05:26:36+02	2021-07-14 05:26:39.379969+02	2021-07-14 13:23:23.464482+02
90	60f5718c2e113d2923911c44	BID	73	BTC/USDT	0.00105339	BTC	30783.10000000	USDT	0.03242661	USDT	\N	2021-07-19 14:35:25+02	2021-07-19 14:35:35.392635+02	2021-07-20 12:01:17.434327+02
92	60f640aa2e113d2923e19d48	ASK	75	BTC/USDT	0.00100000	BTC	29729.70000000	USDT	0.02972970	USDT	\N	2021-07-20 05:19:06+02	2021-07-20 05:19:13.732207+02	\N
81	60ee74b01eefb021845d2821	BID	65	UNI/USDT	1.00000000	UNI	17.29860000	USDT	0.01729860	USDT	\N	2021-07-14 07:22:56+02	2021-07-14 07:23:07.358568+02	\N
84	60eec9282e113d29233a0a83	ASK	68	BTC/USDT	0.00100000	BTC	32465.00000000	USDT	0.03246500	USDT	\N	2021-07-14 13:23:20+02	2021-07-14 13:23:23.467409+02	\N
85	60ef8ce61eefb0218471eda0	ASK	69	UNI/USDT	1.00000000	UNI	18.33360000	USDT	0.01833360	USDT	\N	2021-07-15 03:18:30+02	2021-07-15 03:18:41.054787+02	\N
93	60f640ab2e113d2923e19f1d	ASK	76	BTC/USDT	0.00100000	BTC	29700.90000000	USDT	0.02970090	USDT	\N	2021-07-20 05:19:07+02	2021-07-20 05:19:13.745962+02	\N
95	60f644981eefb02184dd7ded	BID	78	UNI/USDT	1.00000000	UNI	14.75810000	USDT	0.01475810	USDT	\N	2021-07-20 05:35:52+02	2021-07-20 05:36:00.558554+02	\N
100	60f6a7641eefb02184e3704d	BID	82	UNI/USDT	0.76590000	UNI	14.57780000	USDT	0.01116514	USDT	\N	2021-07-20 12:37:24+02	2021-07-20 12:37:33.32562+02	2021-07-21 12:37:22.974654+02
99	60f69ee22e113d292306c5f1	ASK	81	BTC/USDT	0.00100000	BTC	29291.60000000	USDT	0.02929160	USDT	\N	2021-07-20 12:01:06+02	2021-07-20 12:01:17.441594+02	\N
105	60f738eb1eefb02184ec34aa	BID	87	UNI/USDT	1.00000000	UNI	14.74280000	USDT	0.01474280	USDT	\N	2021-07-20 22:58:19+02	2021-07-20 22:58:26.591708+02	\N
103	60f6db282e113d29231f4a40	ASK	84	BTC/USDT	0.00100000	BTC	29357.50000000	USDT	0.02935750	USDT	\N	2021-07-20 16:18:16+02	2021-07-20 16:18:24.23059+02	\N
97	60f652b31eefb02184de56b1	BID	80	UNI/USDT	0.07387996	UNI	14.67740000	USDT	0.00108437	USDT	\N	2021-07-20 06:36:03+02	2021-07-20 06:36:11.426816+02	2021-07-21 06:36:02.554687+02
106	60f738ea2e113d2923404b0c	ASK	86	BTC/USDT	0.00100000	BTC	29867.10000000	USDT	0.02986710	USDT	\N	2021-07-20 22:58:18+02	2021-07-20 22:58:26.638852+02	\N
133	60fbf63a2e113d29230a8c40	ASK	112	BTC/USDT	0.00100000	BTC	33928.40000000	USDT	0.03392840	USDT	\N	2021-07-24 13:15:06+02	2021-07-24 13:15:13.475024+02	\N
134	60fc3bbf1eefb02184376ce8	BID	113	UNI/USDT	1.00000000	UNI	18.17340000	USDT	0.01817340	USDT	\N	2021-07-24 18:11:43+02	2021-07-24 18:11:53.439068+02	\N
125	60f996201eefb02184104e31	ASK	104	UNI/USDT	1.00000000	UNI	17.39020000	USDT	0.01739020	USDT	\N	2021-07-22 18:00:32+02	2021-07-22 18:00:39.134569+02	\N
101	60f6a7641eefb02184e3704c	BID	82	UNI/USDT	0.23410000	UNI	14.56690000	USDT	0.00341011	USDT	\N	2021-07-20 12:37:24+02	2021-07-20 12:37:33.341904+02	2021-07-21 12:37:22.987012+02
117	60f942c01eefb021840b199a	BID	97	UNI/USDT	1.00000000	UNI	16.31240000	USDT	0.01631240	USDT	\N	2021-07-22 12:04:48+02	2021-07-22 12:04:56.693641+02	\N
118	60f950dc2e113d292316bfbe	BID	98	BTC/USDT	0.00100000	BTC	31846.30000000	USDT	0.03184630	USDT	\N	2021-07-22 13:05:00+02	2021-07-22 13:05:06.699596+02	\N
112	60f7bb781eefb02184f3fba2	ASK	93	UNI/USDT	1.00000000	UNI	15.63580000	USDT	0.01563580	USDT	\N	2021-07-21 08:15:20+02	2021-07-21 08:15:31.535362+02	\N
113	60f7bb791eefb02184f3fbaa	ASK	94	UNI/USDT	1.00000000	UNI	15.63110000	USDT	0.01563110	USDT	\N	2021-07-21 08:15:21+02	2021-07-21 08:15:31.551899+02	\N
119	60f950dd1eefb021840bf5a2	BID	99	UNI/USDT	1.00000000	UNI	16.37040000	USDT	0.01637040	USDT	\N	2021-07-22 13:05:01+02	2021-07-22 13:05:10.697404+02	\N
120	60f95f001eefb021840cc697	BID	101	UNI/USDT	1.00000000	UNI	16.34570000	USDT	0.01634570	USDT	\N	2021-07-22 14:05:20+02	2021-07-22 14:05:30.763358+02	\N
121	60f95f002e113d29231ba93f	BID	100	BTC/USDT	0.00100000	BTC	31863.40000000	USDT	0.03186340	USDT	\N	2021-07-22 14:05:20+02	2021-07-22 14:05:30.781142+02	\N
135	60fc66011eefb0218439e167	BID	114	UNI/USDT	1.00000000	UNI	18.30000000	USDT	0.01830000	USDT	\N	2021-07-24 21:12:01+02	2021-07-24 21:12:13.430533+02	\N
136	60fc74141eefb021843aacd0	BID	115	UNI/USDT	1.00000000	UNI	18.11160000	USDT	0.01811160	USDT	\N	2021-07-24 22:12:04+02	2021-07-24 22:12:13.458828+02	\N
126	60fa951c2e113d292388d358	BID	105	BTC/USDT	0.00100000	BTC	32332.40000000	USDT	0.03233240	USDT	\N	2021-07-23 12:08:28+02	2021-07-23 12:08:39.344283+02	\N
154	60fe078f2e113d2923c6dd56	ASK	131	BTC/USDT	0.00100000	BTC	36143.80000000	USDT	0.03614380	USDT	\N	2021-07-26 02:53:35+02	2021-07-26 02:53:42.913602+02	\N
138	60fc82251eefb021843b8581	BID	116	UNI/USDT	0.00690000	UNI	18.07730000	USDT	0.00012473	USDT	\N	2021-07-24 23:12:05+02	2021-07-24 23:12:17.431339+02	2021-07-25 23:12:03.524827+02
137	60fc82251eefb021843b8580	BID	116	UNI/USDT	0.99310000	UNI	18.07180000	USDT	0.01794710	USDT	\N	2021-07-24 23:12:05+02	2021-07-24 23:12:13.470783+02	2021-07-25 23:12:03.538093+02
107	60f7632d2e113d29234e6be4	ASK	88	BTC/USDT	0.00100000	BTC	29799.20000000	USDT	0.02979920	USDT	\N	2021-07-21 01:58:37+02	2021-07-21 01:58:42.35012+02	\N
109	60f7a7331eefb02184f2c881	ASK	90	UNI/USDT	1.00000000	UNI	15.47390000	USDT	0.01547390	USDT	\N	2021-07-21 06:48:52+02	2021-07-21 06:48:58.501095+02	\N
110	60f7a7341eefb02184f2c88c	ASK	91	UNI/USDT	1.00000000	UNI	15.47460000	USDT	0.01547460	USDT	\N	2021-07-21 06:48:52+02	2021-07-21 06:48:58.51579+02	\N
140	60fc9e521eefb021843d210d	BID	118	UNI/USDT	1.00000000	UNI	18.24580000	USDT	0.01824580	USDT	\N	2021-07-25 01:12:18+02	2021-07-25 01:12:25.479621+02	\N
146	60fdfe3b2e113d2923c24aff	ASK	125	BTC/USDT	0.00100000	BTC	35695.80000000	USDT	0.03569580	USDT	\N	2021-07-26 02:13:47+02	2021-07-26 02:13:51.54726+02	\N
142	60fcba731eefb021843f0267	BID	120	UNI/USDT	1.00000000	UNI	17.97540000	USDT	0.01797540	USDT	\N	2021-07-25 03:12:20+02	2021-07-25 03:12:29.485297+02	\N
143	60fdf62c2e113d2923bde81b	BID	121	BTC/USDT	0.00084985	BTC	35052.00000000	USDT	0.02978894	USDT	\N	2021-07-26 01:39:24+02	2021-07-26 01:39:29.455986+02	2021-07-26 02:53:42.853901+02
150	60fe05db2e113d2923c5eb8e	ASK	127	BTC/USDT	0.00100000	BTC	35930.80000000	USDT	0.03593080	USDT	\N	2021-07-26 02:46:20+02	2021-07-26 02:46:30.26867+02	\N
111	60f7a74f1eefb02184f2cb47	ASK	92	UNI/USDT	1.00000000	UNI	15.49010000	USDT	0.01549010	USDT	\N	2021-07-21 06:49:19+02	2021-07-21 06:49:23.547274+02	\N
124	60f996091eefb02184104ad7	ASK	103	UNI/USDT	1.00000000	UNI	17.39610000	USDT	0.01739610	USDT	\N	2021-07-22 18:00:09+02	2021-07-22 18:00:15.113596+02	\N
127	60fab1461eefb02184205163	BID	107	UNI/USDT	1.00000000	UNI	17.35220000	USDT	0.01735220	USDT	\N	2021-07-23 14:08:38+02	2021-07-23 14:08:45.206397+02	\N
128	60fab1462e113d292392bafb	BID	106	BTC/USDT	0.00100000	BTC	32275.00000000	USDT	0.03227500	USDT	\N	2021-07-23 14:08:38+02	2021-07-23 14:08:45.216594+02	\N
108	60f77f7d2e113d292358c793	ASK	89	BTC/USDT	0.00100000	BTC	29772.40000000	USDT	0.02977240	USDT	\N	2021-07-21 03:59:25+02	2021-07-21 03:59:33.348816+02	\N
139	60fc903d1eefb021843c2980	BID	117	UNI/USDT	1.00000000	UNI	18.23300000	USDT	0.01823300	USDT	\N	2021-07-25 00:12:13+02	2021-07-25 00:12:21.46511+02	\N
114	60f7ef272e113d292383ba26	ASK	95	BTC/USDT	0.00100000	BTC	31200.00000000	USDT	0.03120000	USDT	\N	2021-07-21 11:55:51+02	2021-07-21 11:55:58.091501+02	\N
115	60f7ef282e113d292383bb28	ASK	96	BTC/USDT	0.00050000	BTC	31211.60000000	USDT	0.01560580	USDT	\N	2021-07-21 11:55:52+02	2021-07-21 11:55:58.106941+02	\N
116	60f7ef282e113d292383bb27	ASK	96	BTC/USDT	0.00050000	BTC	31211.70000000	USDT	0.01560585	USDT	\N	2021-07-21 11:55:52+02	2021-07-21 11:55:58.122159+02	\N
141	60fcac641eefb021843dff81	BID	119	UNI/USDT	1.00000000	UNI	18.32980000	USDT	0.01832980	USDT	\N	2021-07-25 02:12:20+02	2021-07-25 02:12:29.454465+02	\N
122	60f995771eefb02184103c26	ASK	102	UNI/USDT	0.92690000	UNI	17.22500000	USDT	0.01596585	USDT	\N	2021-07-22 17:57:43+02	2021-07-22 17:57:51.145454+02	2021-07-23 17:57:42.007822+02
123	60f995771eefb02184103c25	ASK	102	UNI/USDT	0.07310000	UNI	17.22980000	USDT	0.00125950	USDT	\N	2021-07-22 17:57:43+02	2021-07-22 17:57:51.221495+02	2021-07-23 17:57:42.018328+02
129	60fb4e111eefb0218429cc2b	ASK	108	UNI/USDT	1.00000000	UNI	18.41370000	USDT	0.01841370	USDT	\N	2021-07-24 01:17:37+02	2021-07-24 01:17:45.443373+02	\N
151	60fe06282e113d2923c6117e	ASK	128	BTC/USDT	0.00100000	BTC	35875.00000000	USDT	0.03587500	USDT	\N	2021-07-26 02:47:36+02	2021-07-26 02:47:45.187647+02	\N
130	60fb52372e113d2923d01905	ASK	109	BTC/USDT	0.00100000	BTC	33437.90000000	USDT	0.03343790	USDT	\N	2021-07-24 01:35:19+02	2021-07-24 01:35:27.432447+02	\N
153	60fe077c2e113d2923c6d15a	ASK	130	BTC/USDT	0.00100000	BTC	36134.30000000	USDT	0.03613430	USDT	\N	2021-07-26 02:53:16+02	2021-07-26 02:53:24.889008+02	\N
144	60fdf6432e113d2923bdf7d5	BID	122	BTC/USDT	0.00084984	BTC	35066.10000000	USDT	0.02980057	USDT	\N	2021-07-26 01:39:48+02	2021-07-26 01:39:55.542471+02	2021-07-26 02:53:42.868246+02
131	60fb52592e113d2923d02eb7	ASK	110	BTC/USDT	0.00100000	BTC	33469.90000000	USDT	0.03346990	USDT	\N	2021-07-24 01:35:53+02	2021-07-24 01:36:01.459733+02	\N
132	60fbb7f12e113d2923f5903e	ASK	111	BTC/USDT	0.00100000	BTC	33880.00000000	USDT	0.03388000	USDT	\N	2021-07-24 08:49:21+02	2021-07-24 08:49:27.480911+02	\N
149	60fdfe3b2e113d2923c24b2c	ASK	126	BTC/USDT	0.00100000	BTC	35696.30000000	USDT	0.03569630	USDT	\N	2021-07-26 02:13:47+02	2021-07-26 02:13:57.570061+02	\N
145	60fdf69e2e113d2923be2e1f	BID	123	BTC/USDT	0.00084993	BTC	35130.00000000	USDT	0.02985804	USDT	\N	2021-07-26 01:41:18+02	2021-07-26 01:41:25.499393+02	2021-07-26 02:53:42.881243+02
148	60fdfe3b2e113d2923c24aa8	ASK	124	BTC/USDT	0.00014329	BTC	35697.90000000	USDT	0.00511515	USDT	\N	2021-07-26 02:13:47+02	2021-07-26 02:13:57.556098+02	2021-07-27 01:58:44.42405+02
152	60fe074a2e113d2923c6a8d1	ASK	129	BTC/USDT	0.00100000	BTC	36084.40000000	USDT	0.03608440	USDT	\N	2021-07-26 02:52:26+02	2021-07-26 02:52:35.333606+02	\N
155	60fe088d1eefb02184512075	ASK	132	UNI/USDT	1.00000000	UNI	19.03450000	USDT	0.01903450	USDT	\N	2021-07-26 02:57:49+02	2021-07-26 02:57:58.744053+02	\N
156	60fe08be1eefb02184512556	ASK	133	UNI/USDT	1.00000000	UNI	19.15500000	USDT	0.01915500	USDT	\N	2021-07-26 02:58:38+02	2021-07-26 02:58:42.758619+02	\N
157	60fe09501eefb02184512dba	ASK	134	UNI/USDT	1.00000000	UNI	19.29420000	USDT	0.01929420	USDT	\N	2021-07-26 03:01:05+02	2021-07-26 03:01:10.751792+02	\N
158	60fe095b1eefb02184512e99	ASK	135	UNI/USDT	1.00000000	UNI	19.36380000	USDT	0.01936380	USDT	\N	2021-07-26 03:01:15+02	2021-07-26 03:01:22.773887+02	\N
159	60fe096e1eefb02184513079	ASK	136	UNI/USDT	1.00000000	UNI	19.42230000	USDT	0.01942230	USDT	\N	2021-07-26 03:01:34+02	2021-07-26 03:01:42.863164+02	\N
160	60fe096e1eefb02184513085	ASK	137	UNI/USDT	1.00000000	UNI	19.41540000	USDT	0.01941540	USDT	\N	2021-07-26 03:01:34+02	2021-07-26 03:01:42.874744+02	\N
161	60fe09781eefb0218451321e	ASK	138	UNI/USDT	1.00000000	UNI	19.48100000	USDT	0.01948100	USDT	\N	2021-07-26 03:01:44+02	2021-07-26 03:01:46.784415+02	\N
162	60fe097e1eefb021845132c7	ASK	139	UNI/USDT	1.00000000	UNI	19.48960000	USDT	0.01948960	USDT	\N	2021-07-26 03:01:51+02	2021-07-26 03:01:58.830142+02	\N
163	60ff4c281eefb02184690a34	BID	141	UNI/USDT	1.00000000	UNI	18.57240000	USDT	0.01857240	USDT	\N	2021-07-27 01:58:32+02	2021-07-27 01:58:37.657158+02	\N
147	60fdfe3b2e113d2923c24aa9	ASK	124	BTC/USDT	0.00085671	BTC	35697.00000000	USDT	0.03058198	USDT	\N	2021-07-26 02:13:47+02	2021-07-26 02:13:57.543695+02	2021-07-27 01:58:44.33325+02
164	60ff4c262e113d29236ca5ee	BID	140	BTC/USDT	0.00100000	BTC	37320.90000000	USDT	0.03732090	USDT	\N	2021-07-27 01:58:30+02	2021-07-27 01:58:44.488256+02	\N
165	60ff5a3a1eefb021846a3f70	BID	142	UNI/USDT	1.00000000	UNI	18.68400000	USDT	0.01868400	USDT	\N	2021-07-27 02:58:34+02	2021-07-27 02:58:44.385634+02	\N
166	60ff68482e113d292378c26c	BID	143	BTC/USDT	0.00050000	BTC	36892.90000000	USDT	0.01844645	USDT	\N	2021-07-27 03:58:32+02	2021-07-27 03:58:38.350417+02	\N
167	60ff68482e113d292378c26b	BID	143	BTC/USDT	0.00050000	BTC	36892.90000000	USDT	0.01844645	USDT	\N	2021-07-27 03:58:32+02	2021-07-27 03:58:38.368372+02	\N
168	60ff684f1eefb021846b718f	BID	144	UNI/USDT	1.00000000	UNI	18.02810000	USDT	0.01802810	USDT	\N	2021-07-27 03:58:39+02	2021-07-27 03:58:46.34485+02	\N
170	60ff76582e113d29237e2853	BID	145	BTC/USDT	0.00050000	BTC	36523.00000000	USDT	0.01826150	USDT	\N	2021-07-27 04:58:32+02	2021-07-27 04:58:40.383268+02	\N
172	60ff765f1eefb021846cab39	BID	146	UNI/USDT	1.00000000	UNI	17.84990000	USDT	0.01784990	USDT	\N	2021-07-27 04:58:39+02	2021-07-27 04:58:44.352323+02	\N
171	60ff76582e113d29237e2852	BID	145	BTC/USDT	0.00002723	BTC	36522.20000000	USDT	0.00099450	USDT	\N	2021-07-27 04:58:32+02	2021-07-27 04:58:40.395418+02	2021-07-28 00:53:17.573079+02
173	60ff84672e113d292383445f	BID	147	BTC/USDT	0.00028103	BTC	36837.50000000	USDT	0.01035244	USDT	\N	2021-07-27 05:58:31+02	2021-07-27 05:58:38.417557+02	2021-07-28 00:53:17.577853+02
175	60ff84672e113d292383445d	BID	147	BTC/USDT	0.00050000	BTC	36837.50000000	USDT	0.01841875	USDT	\N	2021-07-27 05:58:31+02	2021-07-27 05:58:38.43215+02	\N
176	60ff84721eefb021846dc4a4	BID	148	UNI/USDT	1.00000000	UNI	18.03320000	USDT	0.01803320	USDT	\N	2021-07-27 05:58:42+02	2021-07-27 05:58:46.472153+02	\N
177	60fff3331eefb021847169cf	ASK	149	UNI/USDT	1.00000000	UNI	18.92340000	USDT	0.01892340	USDT	\N	2021-07-27 13:51:15+02	2021-07-27 13:51:25.077377+02	\N
174	60ff84672e113d292383445e	BID	147	BTC/USDT	0.00021897	BTC	36837.50000000	USDT	0.00806631	USDT	\N	2021-07-27 05:58:31+02	2021-07-27 05:58:38.425793+02	2021-07-28 00:53:17.593712+02
184	61008e512e113d2923da1953	ASK	155	BTC/USDT	0.00100000	BTC	39178.00000000	USDT	0.03917800	USDT	\N	2021-07-28 00:53:06+02	2021-07-28 00:53:17.602081+02	\N
185	61033ae31eefb02184a33538	ASK	156	UNI/USDT	1.00000000	UNI	19.70780000	USDT	0.01970780	USDT	\N	2021-07-30 01:33:55+02	2021-07-30 01:34:03.236108+02	\N
186	61034a5a1eefb02184a43734	ASK	157	UNI/USDT	1.00000000	UNI	19.79120000	USDT	0.01979120	USDT	\N	2021-07-30 02:39:54+02	2021-07-30 02:40:03.242317+02	\N
178	60fffef32e113d29239ab3fb	ASK	150	BTC/USDT	0.00100000	BTC	38353.40000000	USDT	0.03835340	USDT	\N	2021-07-27 14:41:23+02	2021-07-27 14:41:31.287193+02	\N
179	6100010a1eefb02184724c31	ASK	151	UNI/USDT	1.00000000	UNI	19.07760000	USDT	0.01907760	USDT	\N	2021-07-27 14:50:18+02	2021-07-27 14:50:27.100335+02	\N
180	6100014e1eefb02184725134	ASK	152	UNI/USDT	1.00000000	UNI	19.12910000	USDT	0.01912910	USDT	\N	2021-07-27 14:51:26+02	2021-07-27 14:51:35.092141+02	\N
187	6108325c1eefb02184f4be0f	BID	159	UNI/USDT	1.00000000	UNI	22.87360000	USDT	0.02287360	USDT	\N	2021-08-02 19:58:52+02	2021-08-02 19:58:59.694332+02	\N
188	6108325b2e113d292315d32e	BID	158	BTC/USDT	0.00050000	BTC	39679.30000000	USDT	0.01983965	USDT	\N	2021-08-02 19:58:51+02	2021-08-02 19:58:59.746459+02	\N
189	6108325b2e113d292315d32d	BID	158	BTC/USDT	0.00050000	BTC	39679.30000000	USDT	0.01983965	USDT	\N	2021-08-02 19:58:51+02	2021-08-02 19:58:59.755199+02	\N
190	6108406c2e113d29231b3424	BID	160	BTC/USDT	0.00050000	BTC	39654.50000000	USDT	0.01982725	USDT	\N	2021-08-02 20:58:52+02	2021-08-02 20:58:58.434846+02	\N
181	61000c1b2e113d2923a006af	ASK	153	BTC/USDT	0.00050000	BTC	38664.50000000	USDT	0.01933225	USDT	\N	2021-07-27 15:37:31+02	2021-07-27 15:37:37.234774+02	\N
182	61000c1b2e113d2923a006ae	ASK	153	BTC/USDT	0.00050000	BTC	38664.80000000	USDT	0.01933240	USDT	\N	2021-07-27 15:37:31+02	2021-07-27 15:37:37.244298+02	\N
191	6108406c2e113d29231b3423	BID	160	BTC/USDT	0.00050000	BTC	39654.50000000	USDT	0.01982725	USDT	\N	2021-08-02 20:58:52+02	2021-08-02 20:58:58.440071+02	\N
192	61084e7c1eefb02184f67dc7	BID	162	UNI/USDT	1.00000000	UNI	22.59820000	USDT	0.02259820	USDT	\N	2021-08-02 21:58:52+02	2021-08-02 21:59:00.451425+02	\N
193	61084e7c2e113d2923219a40	BID	161	BTC/USDT	0.00100000	BTC	39147.20000000	USDT	0.03914720	USDT	\N	2021-08-02 21:58:52+02	2021-08-02 21:59:00.457247+02	\N
194	61085c8c1eefb02184f79187	BID	164	UNI/USDT	1.00000000	UNI	22.40460000	USDT	0.02240460	USDT	\N	2021-08-02 22:58:52+02	2021-08-02 22:58:58.428469+02	\N
195	61085c8c2e113d29232a07a7	BID	163	BTC/USDT	0.00100000	BTC	38847.40000000	USDT	0.03884740	USDT	\N	2021-08-02 22:58:52+02	2021-08-02 22:58:58.432307+02	\N
196	61086a9c1eefb02184f844d9	BID	166	UNI/USDT	1.00000000	UNI	22.52630000	USDT	0.02252630	USDT	\N	2021-08-02 23:58:52+02	2021-08-02 23:59:00.468262+02	\N
197	61086a9c2e113d29232fff66	BID	165	BTC/USDT	0.00100000	BTC	39217.50000000	USDT	0.03921750	USDT	\N	2021-08-02 23:58:52+02	2021-08-02 23:59:00.472524+02	\N
198	610878ac1eefb02184f9109b	BID	167	UNI/USDT	1.00000000	UNI	22.55090000	USDT	0.02255090	USDT	\N	2021-08-03 00:58:52+02	2021-08-03 00:59:00.56755+02	\N
183	61000cc22e113d2923a05e1d	ASK	154	BTC/USDT	0.00100000	BTC	38726.00000000	USDT	0.03872600	USDT	\N	2021-07-27 15:40:18+02	2021-07-27 15:40:29.200029+02	\N
201	610886bc2e113d29233ade62	BID	168	BTC/USDT	0.00050003	BTC	39185.00000000	USDT	0.01959368	USDT	\N	2021-08-03 01:58:52+02	2021-08-03 01:58:58.587334+02	2021-08-03 02:59:00.603988+02
203	610894cc2e113d2923436b7b	BID	170	BTC/USDT	0.00100000	BTC	39341.60000000	USDT	0.03934160	USDT	\N	2021-08-03 02:58:52+02	2021-08-03 02:59:00.607652+02	\N
204	6108a2dc1eefb02184fc06d6	BID	172	UNI/USDT	1.00000000	UNI	21.85120000	USDT	0.02185120	USDT	\N	2021-08-03 03:58:52+02	2021-08-03 03:58:57.750876+02	\N
205	6109e6342e113d2923e9dff4	ASK	173	BTC/USDT	0.00100000	BTC	38472.30000000	USDT	0.03847230	USDT	\N	2021-08-04 02:58:28+02	2021-08-04 02:58:35.99314+02	\N
169	60ff76582e113d29237e2854	BID	145	BTC/USDT	0.00047277	BTC	36523.10000000	USDT	0.01726703	USDT	\N	2021-07-27 04:58:32+02	2021-07-27 04:58:40.367079+02	2021-07-28 00:53:17.56703+02
199	610886bc1eefb02184f9d4e5	BID	169	UNI/USDT	1.00000000	UNI	22.40500000	USDT	0.02240500	USDT	\N	2021-08-03 01:58:52+02	2021-08-03 01:58:58.579978+02	\N
202	610894cc1eefb02184fae728	BID	171	UNI/USDT	1.00000000	UNI	22.53760000	USDT	0.02253760	USDT	\N	2021-08-03 02:58:52+02	2021-08-03 02:59:00.589337+02	\N
200	610886bc2e113d29233ade63	BID	168	BTC/USDT	0.00049997	BTC	39185.10000000	USDT	0.01959137	USDT	\N	2021-08-03 01:58:52+02	2021-08-03 01:58:58.584061+02	2021-08-03 02:59:00.597439+02
206	6109f4442e113d2923efdbbc	ASK	174	BTC/USDT	0.00100000	BTC	38345.00000000	USDT	0.03834500	USDT	\N	2021-08-04 03:58:28+02	2021-08-04 03:58:34.027622+02	\N
207	610ac3d91eefb02184257b57	ASK	175	UNI/USDT	1.00000000	UNI	23.23180000	USDT	0.02323180	USDT	\N	2021-08-04 18:44:09+02	2021-08-04 18:44:13.264014+02	\N
208	610b2b551eefb021842cd2c6	ASK	176	UNI/USDT	1.00000000	UNI	23.76000000	USDT	0.02376000	USDT	\N	2021-08-05 02:05:41+02	2021-08-05 02:05:49.228157+02	\N
209	610b2b551eefb021842cd2cd	ASK	177	UNI/USDT	1.00000000	UNI	23.76000000	USDT	0.02376000	USDT	\N	2021-08-05 02:05:42+02	2021-08-05 02:05:49.260064+02	\N
210	610b8c0d2e113d2923ae35df	BID	178	BTC/USDT	0.00100000	BTC	39123.60000000	USDT	0.03912360	USDT	\N	2021-08-05 08:58:21+02	2021-08-05 08:58:27.214325+02	\N
211	610ba82d1eefb021843596e9	BID	179	UNI/USDT	1.00000000	UNI	22.86000000	USDT	0.02286000	USDT	\N	2021-08-05 10:58:21+02	2021-08-05 10:58:29.113068+02	\N
212	610bf1dd1eefb021843cb5ac	ASK	180	UNI/USDT	1.00000000	UNI	23.85600000	USDT	0.02385600	USDT	\N	2021-08-05 16:12:45+02	2021-08-05 16:12:53.122575+02	\N
213	610bf1e71eefb021843cb764	ASK	181	UNI/USDT	1.00000000	UNI	23.91520000	USDT	0.02391520	USDT	\N	2021-08-05 16:12:55+02	2021-08-05 16:13:05.108657+02	\N
214	610bf1e91eefb021843cb7bf	ASK	182	UNI/USDT	1.00000000	UNI	23.91510000	USDT	0.02391510	USDT	\N	2021-08-05 16:12:57+02	2021-08-05 16:13:05.113227+02	\N
215	610bf2771eefb021843ccedf	ASK	183	UNI/USDT	1.00000000	UNI	23.98260000	USDT	0.02398260	USDT	\N	2021-08-05 16:15:19+02	2021-08-05 16:15:25.136779+02	\N
216	610bf9151eefb021843da473	ASK	184	UNI/USDT	1.00000000	UNI	24.23180000	USDT	0.02423180	USDT	\N	2021-08-05 16:43:33+02	2021-08-05 16:43:41.129272+02	\N
217	610bf9151eefb021843da47d	ASK	185	UNI/USDT	1.00000000	UNI	24.23430000	USDT	0.02423430	USDT	\N	2021-08-05 16:43:33+02	2021-08-05 16:43:41.132987+02	\N
218	610bfc8c2e113d2923f49fd0	BID	186	BTC/USDT	0.00100000	BTC	38892.70000000	USDT	0.03889270	USDT	\N	2021-08-05 16:58:20+02	2021-08-05 16:58:27.118622+02	\N
219	610c0a9d2e113d2923ff7ad1	BID	187	BTC/USDT	0.00100000	BTC	38789.10000000	USDT	0.03878910	USDT	\N	2021-08-05 17:58:21+02	2021-08-05 17:58:29.115782+02	\N
220	610c27322e113d292316456d	ASK	188	BTC/USDT	0.00100000	BTC	40708.00000000	USDT	0.04070800	USDT	\N	2021-08-05 20:00:18+02	2021-08-05 20:00:30.281746+02	\N
221	610c2d9f2e113d29231aa6d7	ASK	189	BTC/USDT	0.00100000	BTC	40793.90000000	USDT	0.04079390	USDT	\N	2021-08-05 20:27:43+02	2021-08-05 20:27:48.205844+02	\N
222	610c2e4b2e113d29231b4706	ASK	190	BTC/USDT	0.00100000	BTC	40849.30000000	USDT	0.04084930	USDT	\N	2021-08-05 20:30:35+02	2021-08-05 20:30:48.223279+02	\N
223	610c34f41eefb02184431b7d	BID	191	UNI/USDT	1.00000000	UNI	24.90580000	USDT	0.02490580	USDT	\N	2021-08-05 20:59:00+02	2021-08-05 20:59:08.205441+02	\N
224	610c37282e113d292320fc5e	ASK	192	BTC/USDT	0.00050000	BTC	41120.50000000	USDT	0.02056025	USDT	\N	2021-08-05 21:08:24+02	2021-08-05 21:08:30.23473+02	\N
225	610c37282e113d292320fc5d	ASK	192	BTC/USDT	0.00050000	BTC	41120.50000000	USDT	0.02056025	USDT	\N	2021-08-05 21:08:24+02	2021-08-05 21:08:30.23815+02	\N
226	610c373a2e113d292321119e	ASK	193	BTC/USDT	0.00100000	BTC	41142.50000000	USDT	0.04114250	USDT	\N	2021-08-05 21:08:42+02	2021-08-05 21:08:48.251031+02	\N
227	610c37472e113d2923211f5f	ASK	194	BTC/USDT	0.00100000	BTC	41137.10000000	USDT	0.04113710	USDT	\N	2021-08-05 21:08:56+02	2021-08-05 21:09:08.227739+02	\N
228	610c37882e113d2923215c46	ASK	195	BTC/USDT	0.00100000	BTC	41177.30000000	USDT	0.04117730	USDT	\N	2021-08-05 21:10:00+02	2021-08-05 21:10:06.227093+02	\N
229	610c38242e113d292321f955	ASK	196	BTC/USDT	0.00100000	BTC	41310.50000000	USDT	0.04131050	USDT	\N	2021-08-05 21:12:36+02	2021-08-05 21:12:44.227559+02	\N
230	610c43031eefb02184443103	BID	198	UNI/USDT	1.00000000	UNI	24.95800000	USDT	0.02495800	USDT	\N	2021-08-05 21:58:59+02	2021-08-05 21:59:06.287016+02	\N
231	610c43032e113d2923292825	BID	197	BTC/USDT	0.00100000	BTC	40661.70000000	USDT	0.04066170	USDT	\N	2021-08-05 21:58:59+02	2021-08-05 21:59:06.297657+02	\N
232	610c89332e113d29234e78a3	BID	199	BTC/USDT	0.00100000	BTC	39988.10000000	USDT	0.03998810	USDT	\N	2021-08-06 02:58:27+02	2021-08-06 02:58:32.797078+02	\N
233	610c89361eefb021844975fc	BID	200	UNI/USDT	1.00000000	UNI	25.06430000	USDT	0.02506430	USDT	\N	2021-08-06 02:58:30+02	2021-08-06 02:58:40.709562+02	\N
234	610ca5522e113d29235cdaa3	BID	201	BTC/USDT	0.00100000	BTC	40366.80000000	USDT	0.04036680	USDT	\N	2021-08-06 04:58:26+02	2021-08-06 04:58:34.750647+02	\N
235	610ca5561eefb021844bae28	BID	202	UNI/USDT	1.00000000	UNI	25.02110000	USDT	0.02502110	USDT	\N	2021-08-06 04:58:30+02	2021-08-06 04:58:38.700076+02	\N
236	610cb3632e113d292363263d	BID	203	BTC/USDT	0.00100000	BTC	40217.50000000	USDT	0.04021750	USDT	\N	2021-08-06 05:58:27+02	2021-08-06 05:58:32.741445+02	\N
237	610cb3671eefb021844caad8	BID	204	UNI/USDT	1.00000000	UNI	24.85370000	USDT	0.02485370	USDT	\N	2021-08-06 05:58:31+02	2021-08-06 05:58:40.711093+02	\N
238	610cc1742e113d292369ce96	BID	205	BTC/USDT	0.00100000	BTC	40115.80000000	USDT	0.04011580	USDT	\N	2021-08-06 06:58:28+02	2021-08-06 06:58:35.499408+02	\N
244	610d532b2e113d2923adb3ca	ASK	210	BTC/USDT	0.00100000	BTC	41718.60000000	USDT	0.04171860	USDT	\N	2021-08-06 17:20:11+02	2021-08-06 17:20:17.693819+02	\N
256	610d87b71eefb021845d4990	ASK	221	UNI/USDT	1.00000000	UNI	26.42110000	USDT	0.02642110	USDT	\N	2021-08-06 21:04:23+02	2021-08-06 21:04:26.169838+02	\N
242	610ceba81eefb0218450d89a	BID	208	UNI/USDT	1.00000000	UNI	24.87930000	USDT	0.02487930	USDT	\N	2021-08-06 09:58:32+02	2021-08-06 09:58:43.679133+02	\N
249	610d5cd22e113d2923b4e38b	ASK	214	BTC/USDT	0.00100000	BTC	42386.40000000	USDT	0.04238640	USDT	\N	2021-08-06 18:01:23+02	2021-08-06 18:01:32.25978+02	\N
251	610d768d1eefb021845be6ef	ASK	216	UNI/USDT	1.00000000	UNI	26.33080000	USDT	0.02633080	USDT	\N	2021-08-06 19:51:09+02	2021-08-06 19:51:14.169255+02	\N
252	610d768e1eefb021845be6f9	ASK	217	UNI/USDT	1.00000000	UNI	26.33080000	USDT	0.02633080	USDT	\N	2021-08-06 19:51:10+02	2021-08-06 19:51:14.174093+02	\N
253	610d78101eefb021845c0fd4	ASK	218	UNI/USDT	1.00000000	UNI	26.35260000	USDT	0.02635260	USDT	\N	2021-08-06 19:57:36+02	2021-08-06 19:57:42.213331+02	\N
245	610d5c3c2e113d2923b40d32	ASK	211	BTC/USDT	0.00100000	BTC	42192.00000000	USDT	0.04219200	USDT	\N	2021-08-06 17:58:52+02	2021-08-06 17:58:59.83246+02	\N
257	610d88881eefb021845d5b30	ASK	222	UNI/USDT	1.00000000	UNI	26.51700000	USDT	0.02651700	USDT	\N	2021-08-06 21:07:52+02	2021-08-06 21:07:58.187451+02	\N
254	610d7a2c1eefb021845c4529	ASK	219	UNI/USDT	1.00000000	UNI	26.39940000	USDT	0.02639940	USDT	\N	2021-08-06 20:06:36+02	2021-08-06 20:06:38.164889+02	\N
243	610d531a2e113d2923ad9950	ASK	209	BTC/USDT	0.00100000	BTC	41534.70000000	USDT	0.04153470	USDT	\N	2021-08-06 17:19:54+02	2021-08-06 17:19:59.684356+02	\N
246	610d5c452e113d2923b42212	ASK	212	BTC/USDT	0.00050000	BTC	42323.00000000	USDT	0.02116150	USDT	\N	2021-08-06 17:59:01+02	2021-08-06 17:59:05.703845+02	\N
255	610d87991eefb021845d46a4	ASK	220	UNI/USDT	1.00000000	UNI	26.44620000	USDT	0.02644620	USDT	\N	2021-08-06 21:03:53+02	2021-08-06 21:04:02.245999+02	\N
259	610f485c1eefb0218483db8c	BID	224	UNI/USDT	1.00000000	UNI	27.53160000	USDT	0.02753160	USDT	\N	2021-08-08 04:58:36+02	2021-08-08 04:58:45.838558+02	\N
247	610d5c452e113d2923b4227d	ASK	213	BTC/USDT	0.00100000	BTC	42314.00000000	USDT	0.04231400	USDT	\N	2021-08-06 17:59:01+02	2021-08-06 17:59:12.478112+02	\N
241	610ccf881eefb021844eb374	BID	207	UNI/USDT	1.00000000	UNI	24.96890000	USDT	0.02496890	USDT	\N	2021-08-06 07:58:32+02	2021-08-06 07:58:38.715431+02	\N
248	610d5c452e113d2923b42213	ASK	212	BTC/USDT	0.00050000	BTC	42319.30000000	USDT	0.02115965	USDT	\N	2021-08-06 17:59:01+02	2021-08-06 17:59:12.482235+02	\N
261	610f7ce92e113d2923e2d434	BID	226	BTC/USDT	0.00034975	BTC	45258.30000000	USDT	0.01582909	USDT	\N	2021-08-08 08:42:49+02	2021-08-08 08:42:53.829458+02	2021-08-08 21:58:49.915232+02
250	610d5d5f2e113d2923b5ae2a	ASK	215	BTC/USDT	0.00100000	BTC	42774.30000000	USDT	0.04277430	USDT	\N	2021-08-06 18:03:43+02	2021-08-06 18:03:50.246664+02	\N
239	610cc1781eefb021844db73f	BID	206	UNI/USDT	0.58090000	UNI	24.85450000	USDT	0.01443798	USDT	\N	2021-08-06 06:58:32+02	2021-08-06 06:58:38.697067+02	2021-08-07 06:58:30.191268+02
258	610d88b81eefb021845d604e	ASK	223	UNI/USDT	1.00000000	UNI	26.56850000	USDT	0.02656850	USDT	\N	2021-08-06 21:08:40+02	2021-08-06 21:08:46.218837+02	\N
240	610cc1781eefb021844db73e	BID	206	UNI/USDT	0.41910000	UNI	24.85440000	USDT	0.01041648	USDT	\N	2021-08-06 06:58:32+02	2021-08-06 06:58:42.712535+02	2021-08-07 06:58:30.197637+02
262	610f7ce92e113d2923e2d433	BID	226	BTC/USDT	0.00050000	BTC	45258.30000000	USDT	0.02262915	USDT	\N	2021-08-08 08:42:49+02	2021-08-08 08:42:53.83374+02	\N
260	610f79552e113d2923e035f1	BID	225	BTC/USDT	0.00084996	BTC	45133.80000000	USDT	0.03836192	USDT	\N	2021-08-08 08:27:33+02	2021-08-08 08:27:39.831024+02	2021-08-08 21:58:49.911711+02
264	610fb8de1eefb021848cbe7b	BID	227	UNI/USDT	0.06550000	UNI	27.83970000	USDT	0.00182350	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.871075+02	2021-08-09 12:58:36.618327+02
266	610fb8de1eefb021848cbe79	BID	227	UNI/USDT	0.03190000	UNI	27.83870000	USDT	0.00088805	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.877645+02	2021-08-09 12:58:36.627485+02
268	610fb8de1eefb021848cbe77	BID	227	UNI/USDT	0.54800000	UNI	27.83780000	USDT	0.01525511	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.884109+02	2021-08-09 12:58:36.638158+02
278	61100d3e2e113d292330cd21	BID	234	BTC/USDT	0.00050000	BTC	43761.20000000	USDT	0.02188060	USDT	\N	2021-08-08 18:58:38+02	2021-08-08 18:58:49.937617+02	\N
279	61100d3e2e113d292330cd20	BID	234	BTC/USDT	0.00050000	BTC	43761.20000000	USDT	0.02188060	USDT	\N	2021-08-08 18:58:38+02	2021-08-08 18:58:49.941617+02	\N
269	610fb8de1eefb021848cbe76	BID	227	UNI/USDT	0.02310000	UNI	27.83570000	USDT	0.00064300	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.887527+02	2021-08-09 12:58:36.644973+02
270	610fb8de1eefb021848cbe75	BID	227	UNI/USDT	0.05440000	UNI	27.83460000	USDT	0.00151420	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.89126+02	2021-08-09 12:58:36.64962+02
291	61110a5f2e113d2923b169c6	ASK	244	BTC/USDT	0.00050000	BTC	45577.00000000	USDT	0.02278850	USDT	\N	2021-08-09 12:58:39+02	2021-08-09 12:58:46.666341+02	\N
292	61110a5f2e113d2923b169c5	ASK	244	BTC/USDT	0.00050000	BTC	45577.00000000	USDT	0.02278850	USDT	\N	2021-08-09 12:58:39+02	2021-08-09 12:58:46.670929+02	\N
293	61110ad61eefb02184aa5ff5	ASK	246	UNI/USDT	1.00000000	UNI	28.08480000	USDT	0.02808480	USDT	\N	2021-08-09 13:00:38+02	2021-08-09 13:00:44.801688+02	\N
294	61110ad62e113d2923b1cd23	ASK	245	BTC/USDT	0.00050000	BTC	45767.80000000	USDT	0.02288390	USDT	\N	2021-08-09 13:00:38+02	2021-08-09 13:00:44.805407+02	\N
295	61110ad62e113d2923b1cd22	ASK	245	BTC/USDT	0.00050000	BTC	45767.80000000	USDT	0.02288390	USDT	\N	2021-08-09 13:00:38+02	2021-08-09 13:00:44.808736+02	\N
282	6110295e1eefb02184962642	BID	238	UNI/USDT	1.00000000	UNI	26.19620000	USDT	0.02619620	USDT	\N	2021-08-08 20:58:38+02	2021-08-08 20:58:43.939953+02	\N
283	6110295e2e113d29233f3922	BID	237	BTC/USDT	0.00050000	BTC	43540.60000000	USDT	0.02177030	USDT	\N	2021-08-08 20:58:38+02	2021-08-08 20:58:43.953363+02	\N
273	610ff11f1eefb02184913e5a	BID	231	UNI/USDT	1.00000000	UNI	27.03730000	USDT	0.02703730	USDT	\N	2021-08-08 16:58:39+02	2021-08-08 16:58:43.894131+02	\N
272	610fe30e1eefb021848ffb98	BID	229	UNI/USDT	1.00000000	UNI	27.81070000	USDT	0.02781070	USDT	\N	2021-08-08 15:58:38+02	2021-08-08 15:58:45.909191+02	\N
274	610ff11f2e113d2923201187	BID	230	BTC/USDT	0.00100000	BTC	43973.40000000	USDT	0.04397340	USDT	\N	2021-08-08 16:58:39+02	2021-08-08 16:58:43.906496+02	\N
285	6110376e1eefb02184976233	BID	240	UNI/USDT	1.00000000	UNI	26.17660000	USDT	0.02617660	USDT	\N	2021-08-08 21:58:38+02	2021-08-08 21:58:49.901634+02	\N
286	6110376d2e113d292347ec44	BID	239	BTC/USDT	0.00100000	BTC	43618.60000000	USDT	0.04361860	USDT	\N	2021-08-08 21:58:37+02	2021-08-08 21:58:49.918014+02	\N
296	61110af52e113d2923b1efec	ASK	247	BTC/USDT	0.00100000	BTC	45854.20000000	USDT	0.04585420	USDT	\N	2021-08-09 13:01:09+02	2021-08-09 13:01:20.748518+02	\N
287	6110fc4e2e113d2923a7540e	ASK	241	BTC/USDT	0.00050000	BTC	44889.80000000	USDT	0.02244490	USDT	\N	2021-08-09 11:58:38+02	2021-08-09 11:58:43.910315+02	\N
288	6110fc4e2e113d2923a7540d	ASK	241	BTC/USDT	0.00050000	BTC	44889.80000000	USDT	0.02244490	USDT	\N	2021-08-09 11:58:38+02	2021-08-09 11:58:43.914758+02	\N
297	611116fc2e113d2923b9a24a	ASK	248	BTC/USDT	0.00100000	BTC	45911.10000000	USDT	0.04591110	USDT	\N	2021-08-09 13:52:28+02	2021-08-09 13:52:34.635868+02	\N
284	6110295e2e113d29233f3923	BID	237	BTC/USDT	0.00050000	BTC	43540.60000000	USDT	0.02177030	USDT	\N	2021-08-08 20:58:38+02	2021-08-08 20:58:49.918056+02	\N
298	611117402e113d2923b9db38	ASK	249	BTC/USDT	0.00100000	BTC	45944.60000000	USDT	0.04594460	USDT	\N	2021-08-09 13:53:36+02	2021-08-09 13:53:46.621437+02	\N
299	6111375e2e113d2923cc6a80	ASK	250	BTC/USDT	0.00100000	BTC	46148.00000000	USDT	0.04614800	USDT	\N	2021-08-09 16:10:38+02	2021-08-09 16:10:45.859444+02	\N
280	61101b4e1eefb0218494fd93	BID	236	UNI/USDT	1.00000000	UNI	26.49140000	USDT	0.02649140	USDT	\N	2021-08-08 19:58:38+02	2021-08-08 19:58:49.882798+02	\N
275	610fff2e2e113d2923285ff5	BID	232	BTC/USDT	0.00050000	BTC	43945.50000000	USDT	0.02197275	USDT	\N	2021-08-08 17:58:38+02	2021-08-08 17:58:45.92054+02	\N
276	610fff2e2e113d2923285ff4	BID	232	BTC/USDT	0.00050000	BTC	43945.50000000	USDT	0.02197275	USDT	\N	2021-08-08 17:58:38+02	2021-08-08 17:58:45.925752+02	\N
281	61101b4e2e113d292338aeb5	BID	235	BTC/USDT	0.00100000	BTC	43720.60000000	USDT	0.04372060	USDT	\N	2021-08-08 19:58:38+02	2021-08-08 19:58:49.892987+02	\N
300	61114edd1eefb02184b08b9b	ASK	251	UNI/USDT	1.00000000	UNI	28.44440000	USDT	0.02844440	USDT	\N	2021-08-09 17:50:53+02	2021-08-09 17:50:57.821051+02	\N
301	611176392e113d2923ecd338	ASK	252	BTC/USDT	0.00100000	BTC	46175.20000000	USDT	0.04617520	USDT	\N	2021-08-09 20:38:49+02	2021-08-09 20:38:55.917673+02	\N
302	611176531eefb02184b31d81	ASK	253	UNI/USDT	1.00000000	UNI	28.67010000	USDT	0.02867010	USDT	\N	2021-08-09 20:39:15+02	2021-08-09 20:39:23.825056+02	\N
303	6111ff411eefb02184bc61bf	ASK	254	UNI/USDT	1.00000000	UNI	29.18820000	USDT	0.02918820	USDT	\N	2021-08-10 06:23:29+02	2021-08-10 06:23:37.499733+02	\N
304	61120bb21eefb02184bd51b2	ASK	255	UNI/USDT	1.00000000	UNI	29.35040000	USDT	0.02935040	USDT	\N	2021-08-10 07:16:34+02	2021-08-10 07:16:41.506386+02	\N
277	610fff2e1eefb02184927b0d	BID	233	UNI/USDT	1.00000000	UNI	26.84340000	USDT	0.02684340	USDT	\N	2021-08-08 17:58:38+02	2021-08-08 17:58:49.883389+02	\N
289	611105151eefb02184a9dac3	ASK	242	UNI/USDT	1.00000000	UNI	27.74970000	USDT	0.02774970	USDT	\N	2021-08-09 12:36:05+02	2021-08-09 12:36:07.898696+02	\N
290	611105151eefb02184a9dad0	ASK	243	UNI/USDT	1.00000000	UNI	27.75150000	USDT	0.02775150	USDT	\N	2021-08-09 12:36:05+02	2021-08-09 12:36:07.903532+02	\N
305	61120c5c1eefb02184bd623d	ASK	256	UNI/USDT	1.00000000	UNI	29.47220000	USDT	0.02947220	USDT	\N	2021-08-10 07:19:25+02	2021-08-10 07:19:29.491543+02	\N
306	61120c911eefb02184bd672b	ASK	257	UNI/USDT	1.00000000	UNI	29.49720000	USDT	0.02949720	USDT	\N	2021-08-10 07:20:17+02	2021-08-10 07:20:25.491281+02	\N
307	6112239c2e113d2923480bd3	BID	258	BTC/USDT	0.00050000	BTC	45526.30000000	USDT	0.02276315	USDT	\N	2021-08-10 08:58:36+02	2021-08-10 08:58:43.549662+02	\N
308	6112239c2e113d2923480bd4	BID	258	BTC/USDT	0.00050000	BTC	45526.30000000	USDT	0.02276315	USDT	\N	2021-08-10 08:58:36+02	2021-08-10 08:58:49.539299+02	\N
271	610fc6ed1eefb021848dcc50	BID	228	UNI/USDT	1.00000000	UNI	27.67540000	USDT	0.02767540	USDT	\N	2021-08-08 13:58:37+02	2021-08-08 13:58:41.875568+02	\N
317	6112b0402e113d292398b1d5	BID	265	BTC/USDT	0.00100000	BTC	44849.50000000	USDT	0.04484950	USDT	\N	2021-08-10 18:58:40+02	2021-08-10 18:58:45.536946+02	\N
313	6112941d2e113d292385d6ea	BID	262	BTC/USDT	0.00100000	BTC	45381.70000000	USDT	0.04538170	USDT	\N	2021-08-10 16:58:37+02	2021-08-10 16:58:45.538647+02	\N
263	610fb8de1eefb021848cbe7c	BID	227	UNI/USDT	0.23270000	UNI	27.84000000	USDT	0.00647837	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.867281+02	2021-08-09 12:58:36.614342+02
265	610fb8de1eefb021848cbe7a	BID	227	UNI/USDT	0.01410000	UNI	27.83870000	USDT	0.00039253	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.874319+02	2021-08-09 12:58:36.623514+02
267	610fb8de1eefb021848cbe78	BID	227	UNI/USDT	0.03030000	UNI	27.83810000	USDT	0.00084349	USDT	\N	2021-08-08 12:58:38+02	2021-08-08 12:58:45.880797+02	2021-08-09 12:58:36.6337+02
311	611269ed2e113d29236bb164	BID	260	BTC/USDT	0.00100000	BTC	45331.70000000	USDT	0.04533170	USDT	\N	2021-08-10 13:58:37+02	2021-08-10 13:58:49.509296+02	\N
312	6112860d2e113d29237e12a5	BID	261	BTC/USDT	0.00100000	BTC	45596.70000000	USDT	0.04559670	USDT	\N	2021-08-10 15:58:37+02	2021-08-10 15:58:43.521097+02	\N
314	6112a22d2e113d29238e8b79	BID	263	BTC/USDT	0.00100000	BTC	45095.70000000	USDT	0.04509570	USDT	\N	2021-08-10 17:58:38+02	2021-08-10 17:58:44.300352+02	\N
315	6112a22f1eefb02184c96461	BID	264	UNI/USDT	1.00000000	UNI	28.77180000	USDT	0.02877180	USDT	\N	2021-08-10 17:58:39+02	2021-08-10 17:58:47.496173+02	\N
316	6112b0401eefb02184caaff1	BID	266	UNI/USDT	1.00000000	UNI	28.36750000	USDT	0.02836750	USDT	\N	2021-08-10 18:58:40+02	2021-08-10 18:58:45.522402+02	\N
318	6112be511eefb02184cbc3f2	BID	268	UNI/USDT	0.47480000	UNI	28.71780000	USDT	0.01363521	USDT	\N	2021-08-10 19:58:41+02	2021-08-10 19:58:53.512631+02	2021-08-11 19:58:40.363667+02
310	61125bdd2e113d2923640a7c	BID	259	BTC/USDT	0.00078078	BTC	45358.20000000	USDT	0.03541478	USDT	\N	2021-08-10 12:58:37+02	2021-08-10 12:58:49.587102+02	2021-08-10 20:58:54.083137+02
321	6112be512e113d29239fb610	BID	267	BTC/USDT	0.00100000	BTC	45080.00000000	USDT	0.04508000	USDT	\N	2021-08-10 19:58:41+02	2021-08-10 19:58:53.53223+02	\N
347	6117e8372e113d292341cba2	BID	291	BTC/USDT	0.00050000	BTC	46909.70000000	USDT	0.02345485	USDT	\N	2021-08-14 17:58:47+02	2021-08-14 17:58:53.57584+02	\N
355	611a6f152e113d29237d846f	BID	297	BTC/USDT	0.00100000	BTC	46467.40000000	USDT	0.04646740	USDT	\N	2021-08-16 15:58:45+02	2021-08-16 15:58:53.735303+02	\N
356	611fc0492e113d2923731fc8	ASK	298	BTC/USDT	0.00100000	BTC	48550.00000000	USDT	0.04855000	USDT	\N	2021-08-20 16:46:33+02	2021-08-20 16:46:39.97604+02	\N
319	6112be511eefb02184cbc3f1	BID	268	UNI/USDT	0.39880000	UNI	28.71680000	USDT	0.01145226	USDT	\N	2021-08-10 19:58:41+02	2021-08-10 19:58:53.516971+02	2021-08-11 19:58:40.367577+02
320	6112be511eefb02184cbc3f0	BID	268	UNI/USDT	0.12640000	UNI	28.71230000	USDT	0.00362923	USDT	\N	2021-08-10 19:58:41+02	2021-08-10 19:58:53.520773+02	2021-08-11 19:58:40.37036+02
325	611456211eefb02184ea24b9	BID	272	UNI/USDT	1.00000000	UNI	29.00060000	USDT	0.02900060	USDT	\N	2021-08-12 00:58:41+02	2021-08-12 00:58:44.341659+02	\N
326	6116d0d22e113d2923bae5f9	ASK	273	BTC/USDT	0.00100000	BTC	47296.70000000	USDT	0.04729670	USDT	\N	2021-08-13 22:06:42+02	2021-08-13 22:06:48.06351+02	\N
327	6116d12a2e113d2923bb71f8	ASK	274	BTC/USDT	0.00100000	BTC	47371.00000000	USDT	0.04737100	USDT	\N	2021-08-13 22:08:10+02	2021-08-13 22:08:17.222352+02	\N
328	6116d12a2e113d2923bb7257	ASK	275	BTC/USDT	0.00100000	BTC	47371.00000000	USDT	0.04737100	USDT	\N	2021-08-13 22:08:11+02	2021-08-13 22:08:17.227038+02	\N
324	611376bf1eefb02184d95bb1	ASK	271	UNI/USDT	1.00000000	UNI	30.02680000	USDT	0.03002680	USDT	\N	2021-08-11 09:05:35+02	2021-08-11 09:05:44.351765+02	\N
357	611fd0fc2e113d29237d44d6	ASK	299	BTC/USDT	0.00100000	BTC	48605.60000000	USDT	0.04860560	USDT	\N	2021-08-20 17:57:48+02	2021-08-20 17:57:59.902825+02	\N
344	6117be082e113d29232cb735	BID	288	BTC/USDT	0.00100000	BTC	46289.20000000	USDT	0.04628920	USDT	\N	2021-08-14 14:58:48+02	2021-08-14 14:58:53.496883+02	\N
331	6116d2d32e113d2923bd7212	ASK	277	BTC/USDT	0.00050000	BTC	47544.90000000	USDT	0.02377245	USDT	\N	2021-08-13 22:15:15+02	2021-08-13 22:15:21.244545+02	\N
358	611fd35d2e113d29237f1a86	ASK	300	BTC/USDT	0.00050000	BTC	48818.00000000	USDT	0.02440900	USDT	\N	2021-08-20 18:07:58+02	2021-08-20 18:08:07.918991+02	\N
348	6117e8372e113d292341cba3	BID	291	BTC/USDT	0.00050000	BTC	46909.70000000	USDT	0.02345485	USDT	\N	2021-08-14 17:58:47+02	2021-08-14 17:58:59.596947+02	\N
332	6116d2d32e113d2923bd7211	ASK	277	BTC/USDT	0.00050000	BTC	47544.90000000	USDT	0.02377245	USDT	\N	2021-08-13 22:15:15+02	2021-08-13 22:15:28.03369+02	\N
351	611804562e113d29234ef800	BID	293	BTC/USDT	0.00100000	BTC	46788.10000000	USDT	0.04678810	USDT	\N	2021-08-14 19:58:46+02	2021-08-14 19:58:56.390062+02	\N
359	611fd35d2e113d29237f1a85	ASK	300	BTC/USDT	0.00050000	BTC	48831.40000000	USDT	0.02441570	USDT	\N	2021-08-20 18:07:58+02	2021-08-20 18:08:07.921958+02	\N
333	6116d3112e113d2923bdb091	ASK	278	BTC/USDT	0.00100000	BTC	47745.90000000	USDT	0.04774590	USDT	\N	2021-08-13 22:16:17+02	2021-08-13 22:16:25.398228+02	\N
334	6116d3112e113d2923bdb0c8	ASK	279	BTC/USDT	0.00100000	BTC	47745.90000000	USDT	0.04774590	USDT	\N	2021-08-13 22:16:17+02	2021-08-13 22:16:25.401082+02	\N
345	6117cc172e113d2923336ea6	BID	289	BTC/USDT	0.00100000	BTC	46501.00000000	USDT	0.04650100	USDT	\N	2021-08-14 15:58:47+02	2021-08-14 15:58:55.470055+02	\N
360	611fd5382e113d2923803d43	ASK	301	BTC/USDT	0.00050000	BTC	48865.70000000	USDT	0.02443285	USDT	\N	2021-08-20 18:15:52+02	2021-08-20 18:15:57.953315+02	\N
335	6116e0572e113d2923c688bd	ASK	280	BTC/USDT	0.00100000	BTC	47804.60000000	USDT	0.04780460	USDT	\N	2021-08-13 23:12:55+02	2021-08-13 23:13:03.252961+02	\N
336	611702191eefb021841d957a	ASK	281	UNI/USDT	1.00000000	UNI	30.43020000	USDT	0.03043020	USDT	\N	2021-08-14 01:36:57+02	2021-08-14 01:37:03.212297+02	\N
337	611702371eefb021841d9810	ASK	282	UNI/USDT	1.00000000	UNI	30.47460000	USDT	0.03047460	USDT	\N	2021-08-14 01:37:27+02	2021-08-14 01:37:35.216987+02	\N
338	6117071c1eefb021841dffdc	ASK	283	UNI/USDT	1.00000000	UNI	30.56570000	USDT	0.03056570	USDT	\N	2021-08-14 01:58:20+02	2021-08-14 01:58:27.277347+02	\N
323	611304a11eefb02184d0c211	BID	270	UNI/USDT	1.00000000	UNI	28.88140000	USDT	0.02888140	USDT	\N	2021-08-11 00:58:41+02	2021-08-11 00:58:50.207234+02	\N
361	611fd5382e113d2923803d42	ASK	301	BTC/USDT	0.00050000	BTC	48865.70000000	USDT	0.02443285	USDT	\N	2021-08-20 18:15:52+02	2021-08-20 18:15:57.957841+02	\N
349	6117f6472e113d29234875bb	BID	292	BTC/USDT	0.00050000	BTC	46820.40000000	USDT	0.02341020	USDT	\N	2021-08-14 18:58:47+02	2021-08-14 18:58:54.145104+02	\N
339	61178f832e113d29230c30e0	ASK	284	BTC/USDT	0.00100000	BTC	47864.00000000	USDT	0.04786400	USDT	\N	2021-08-14 11:40:19+02	2021-08-14 11:40:26.27292+02	\N
362	611fd5742e113d2923807106	ASK	302	BTC/USDT	0.00100000	BTC	48924.10000000	USDT	0.04892410	USDT	\N	2021-08-20 18:16:52+02	2021-08-20 18:16:58.024901+02	\N
352	611812652e113d292354f105	BID	294	BTC/USDT	0.00100000	BTC	46740.40000000	USDT	0.04674040	USDT	\N	2021-08-14 20:58:45+02	2021-08-14 20:58:50.40003+02	\N
340	611793d92e113d29231017e6	BID	285	BTC/USDT	0.00100000	BTC	46869.70000000	USDT	0.04686970	USDT	\N	2021-08-14 11:58:49+02	2021-08-14 11:58:56.337912+02	\N
329	6116d1422e113d2923bba21a	ASK	276	BTC/USDT	0.00016405	BTC	47591.70000000	USDT	0.00780742	USDT	\N	2021-08-13 22:08:34+02	2021-08-13 22:08:39.224415+02	2021-08-14 21:58:52.395671+02
330	6116d1422e113d2923bba219	ASK	276	BTC/USDT	0.00083595	BTC	47594.30000000	USDT	0.03978646	USDT	\N	2021-08-13 22:08:34+02	2021-08-13 22:08:39.227286+02	2021-08-14 21:58:52.398876+02
350	6117f6472e113d29234875bc	BID	292	BTC/USDT	0.00050000	BTC	46820.40000000	USDT	0.02341020	USDT	\N	2021-08-14 18:58:47+02	2021-08-14 18:58:59.570793+02	\N
363	6120257c2e113d2923a4f2aa	ASK	303	BTC/USDT	0.00100000	BTC	49266.60000000	USDT	0.04926660	USDT	\N	2021-08-20 23:58:21+02	2021-08-20 23:58:25.925074+02	\N
309	61125bdd2e113d2923640a7d	BID	259	BTC/USDT	0.00021922	BTC	45358.20000000	USDT	0.00994342	USDT	\N	2021-08-10 12:58:37+02	2021-08-10 12:58:43.530447+02	2021-08-10 20:58:54.079656+02
322	6112cc612e113d2923a65b0f	BID	269	BTC/USDT	0.00100000	BTC	45285.80000000	USDT	0.04528580	USDT	\N	2021-08-10 20:58:41+02	2021-08-10 20:58:54.086483+02	\N
364	6120258e2e113d2923a515e3	ASK	304	BTC/USDT	0.00100000	BTC	49232.30000000	USDT	0.04923230	USDT	\N	2021-08-20 23:58:38+02	2021-08-20 23:58:43.917564+02	\N
365	6120258f2e113d2923a5169f	ASK	306	BTC/USDT	0.00100000	BTC	49244.20000000	USDT	0.04924420	USDT	\N	2021-08-20 23:58:39+02	2021-08-20 23:58:49.942515+02	\N
366	6120258f2e113d2923a5164c	ASK	305	BTC/USDT	0.00100000	BTC	49244.20000000	USDT	0.04924420	USDT	\N	2021-08-20 23:58:39+02	2021-08-20 23:58:49.946137+02	\N
346	6117da292e113d29233b36b2	BID	290	BTC/USDT	0.00100000	BTC	46887.00000000	USDT	0.04688700	USDT	\N	2021-08-14 16:58:49+02	2021-08-14 16:59:00.342391+02	\N
343	6117aff92e113d2923257b88	BID	287	BTC/USDT	0.00100000	BTC	46554.80000000	USDT	0.04655480	USDT	\N	2021-08-14 13:58:49+02	2021-08-14 13:58:55.565087+02	\N
341	6117a1e92e113d29231cac79	BID	286	BTC/USDT	0.00023374	BTC	46223.40000000	USDT	0.01080426	USDT	\N	2021-08-14 12:58:49+02	2021-08-14 12:58:57.47327+02	2021-08-14 21:58:52.401483+02
342	6117a1e92e113d29231cac78	BID	286	BTC/USDT	0.00076626	BTC	46218.30000000	USDT	0.03541523	USDT	\N	2021-08-14 12:58:49+02	2021-08-14 12:58:57.481938+02	2021-08-14 21:58:52.404889+02
353	611820762e113d29235bb956	BID	295	BTC/USDT	0.00100000	BTC	47037.90000000	USDT	0.04703790	USDT	\N	2021-08-14 21:58:46+02	2021-08-14 21:58:52.408413+02	\N
354	6119f9b51eefb021844fcf94	ASK	296	UNI/USDT	1.00000000	UNI	30.73700000	USDT	0.03073700	USDT	\N	2021-08-16 07:37:57+02	2021-08-16 07:38:03.050193+02	\N
367	612025a62e113d2923a53718	ASK	307	BTC/USDT	0.00100000	BTC	49240.50000000	USDT	0.04924050	USDT	\N	2021-08-20 23:59:03+02	2021-08-20 23:59:07.947598+02	\N
368	612025b62e113d2923a54a5b	ASK	308	BTC/USDT	0.00100000	BTC	49278.50000000	USDT	0.04927850	USDT	\N	2021-08-20 23:59:18+02	2021-08-20 23:59:23.936493+02	\N
369	6120dd0a2e113d2923f5ed23	ASK	309	BTC/USDT	0.00100000	BTC	49372.60000000	USDT	0.04937260	USDT	\N	2021-08-21 13:01:30+02	2021-08-21 13:01:42.080737+02	\N
370	6120ea732e113d2923ff49fe	BID	310	BTC/USDT	0.00100000	BTC	48548.40000000	USDT	0.04854840	USDT	\N	2021-08-21 13:58:43+02	2021-08-21 13:58:48.087908+02	\N
371	6120ea751eefb02184dc1acf	BID	311	UNI/USDT	1.00000000	UNI	28.70990000	USDT	0.02870990	USDT	\N	2021-08-21 13:58:45+02	2021-08-21 13:58:54.048609+02	\N
372	61214ce51eefb02184e2e8ca	BID	312	UNI/USDT	1.00000000	UNI	28.89570000	USDT	0.02889570	USDT	\N	2021-08-21 20:58:45+02	2021-08-21 20:58:56.315778+02	\N
373	61215af61eefb02184e403a3	BID	314	UNI/USDT	1.00000000	UNI	28.28360000	USDT	0.02828360	USDT	\N	2021-08-21 21:58:46+02	2021-08-21 21:58:58.343041+02	\N
374	61215af62e113d29233782c4	BID	313	BTC/USDT	0.00100000	BTC	48913.20000000	USDT	0.04891320	USDT	\N	2021-08-21 21:58:46+02	2021-08-21 21:58:58.347318+02	\N
375	612169071eefb02184e510ca	BID	316	UNI/USDT	1.00000000	UNI	28.30990000	USDT	0.02830990	USDT	\N	2021-08-21 22:58:47+02	2021-08-21 22:58:56.371235+02	\N
376	612169072e113d29233df522	BID	315	BTC/USDT	0.00100000	BTC	48914.40000000	USDT	0.04891440	USDT	\N	2021-08-21 22:58:47+02	2021-08-21 22:58:56.375409+02	\N
377	612177171eefb02184e5be6d	BID	317	UNI/USDT	1.00000000	UNI	28.55700000	USDT	0.02855700	USDT	\N	2021-08-21 23:58:47+02	2021-08-21 23:58:59.12479+02	\N
378	612185271eefb02184e6853f	BID	318	UNI/USDT	1.00000000	UNI	28.60930000	USDT	0.02860930	USDT	\N	2021-08-22 00:58:47+02	2021-08-22 00:58:56.359462+02	\N
379	612193361eefb02184e777a2	BID	320	UNI/USDT	1.00000000	UNI	28.22730000	USDT	0.02822730	USDT	\N	2021-08-22 01:58:46+02	2021-08-22 01:58:54.362658+02	\N
380	612193362e113d29234f65f5	BID	319	BTC/USDT	0.00050000	BTC	48853.50000000	USDT	0.02442675	USDT	\N	2021-08-22 01:58:46+02	2021-08-22 01:59:00.365618+02	\N
381	612193362e113d29234f65f4	BID	319	BTC/USDT	0.00050000	BTC	48853.50000000	USDT	0.02442675	USDT	\N	2021-08-22 01:58:46+02	2021-08-22 01:59:00.368584+02	\N
391	6121a1462e113d292356915d	BID	321	BTC/USDT	0.00100000	BTC	49009.20000000	USDT	0.04900920	USDT	\N	2021-08-22 02:58:46+02	2021-08-22 02:58:58.40981+02	\N
411	61305dbf1eefb02184380b35	BID	340	UNI/USDT	0.20000000	UNI	30.42840000	USDT	0.00608568	USDT	\N	2021-09-02 07:14:39+02	2021-09-02 07:14:52.505262+02	\N
382	6121a1471eefb02184e88acc	BID	322	UNI/USDT	0.19590000	UNI	28.64810000	USDT	0.00561216	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.380832+02	2021-08-23 02:58:17.569978+02
383	6121a1471eefb02184e88acb	BID	322	UNI/USDT	0.18070000	UNI	28.64550000	USDT	0.00517624	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.384831+02	2021-08-23 02:58:17.573012+02
384	6121a1471eefb02184e88aca	BID	322	UNI/USDT	0.02500000	UNI	28.64540000	USDT	0.00071614	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.38762+02	2021-08-23 02:58:17.57608+02
385	6121a1471eefb02184e88ac9	BID	322	UNI/USDT	0.03770000	UNI	28.64420000	USDT	0.00107989	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.390574+02	2021-08-23 02:58:17.579094+02
386	6121a1471eefb02184e88ac8	BID	322	UNI/USDT	0.05480000	UNI	28.64400000	USDT	0.00156969	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.393623+02	2021-08-23 02:58:17.58206+02
387	6121a1471eefb02184e88ac7	BID	322	UNI/USDT	0.37710000	UNI	28.64240000	USDT	0.01080105	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.397145+02	2021-08-23 02:58:17.585068+02
388	6121a1471eefb02184e88ac6	BID	322	UNI/USDT	0.03480000	UNI	28.64220000	USDT	0.00099675	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.400166+02	2021-08-23 02:58:17.587967+02
389	6121a1471eefb02184e88ac5	BID	322	UNI/USDT	0.05680000	UNI	28.64110000	USDT	0.00162681	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.403684+02	2021-08-23 02:58:17.590878+02
390	6121a1471eefb02184e88ac4	BID	322	UNI/USDT	0.03720000	UNI	28.64010000	USDT	0.00106541	USDT	\N	2021-08-22 02:58:47+02	2021-08-22 02:58:58.40712+02	2021-08-23 02:58:17.593852+02
395	612f162b1eefb021841729b5	ASK	326	UNI/USDT	1.00000000	UNI	29.92940000	USDT	0.02992940	USDT	\N	2021-09-01 07:56:59+02	2021-09-01 07:57:08.250324+02	\N
392	6121af571eefb02184e96838	BID	323	UNI/USDT	1.00000000	UNI	28.64070000	USDT	0.02864070	USDT	\N	2021-08-22 03:58:47+02	2021-08-22 03:58:56.408397+02	\N
396	612f169e1eefb021841735e1	ASK	327	UNI/USDT	1.00000000	UNI	29.99440000	USDT	0.02999440	USDT	\N	2021-09-01 07:58:54+02	2021-09-01 07:59:02.164017+02	\N
397	612f16b51eefb02184173ace	ASK	328	UNI/USDT	1.00000000	UNI	30.03000000	USDT	0.03003000	USDT	\N	2021-09-01 07:59:17+02	2021-09-01 07:59:24.141304+02	\N
398	612f16cc1eefb02184173fd5	ASK	329	UNI/USDT	1.00000000	UNI	30.06560000	USDT	0.03006560	USDT	\N	2021-09-01 07:59:40+02	2021-09-01 07:59:46.157624+02	\N
399	612f1a351eefb02184179f8d	ASK	331	UNI/USDT	1.00000000	UNI	30.37440000	USDT	0.03037440	USDT	\N	2021-09-01 08:14:13+02	2021-09-01 08:14:22.168744+02	\N
400	612f1a351eefb02184179f89	ASK	330	UNI/USDT	1.00000000	UNI	30.37440000	USDT	0.03037440	USDT	\N	2021-09-01 08:14:13+02	2021-09-01 08:14:22.17705+02	\N
401	612f1a361eefb02184179f96	ASK	332	UNI/USDT	1.00000000	UNI	30.37440000	USDT	0.03037440	USDT	\N	2021-09-01 08:14:14+02	2021-09-01 08:14:22.179791+02	\N
402	612f1a371eefb02184179fca	ASK	333	UNI/USDT	1.00000000	UNI	30.36780000	USDT	0.03036780	USDT	\N	2021-09-01 08:14:15+02	2021-09-01 08:14:22.183216+02	\N
403	612f20b11eefb02184187f74	ASK	334	UNI/USDT	1.00000000	UNI	30.45220000	USDT	0.03045220	USDT	\N	2021-09-01 08:41:53+02	2021-09-01 08:41:57.553207+02	\N
412	61306bce1eefb0218439465f	BID	341	UNI/USDT	1.00000000	UNI	30.32120000	USDT	0.03032120	USDT	\N	2021-09-02 08:14:38+02	2021-09-02 08:14:46.547795+02	\N
413	613079df1eefb021843ac7d6	BID	342	UNI/USDT	1.00000000	UNI	30.51470000	USDT	0.03051470	USDT	\N	2021-09-02 09:14:39+02	2021-09-02 09:14:48.500301+02	\N
414	613087f01eefb021843c1f91	BID	343	UNI/USDT	1.00000000	UNI	30.39340000	USDT	0.03039340	USDT	\N	2021-09-02 10:14:40+02	2021-09-02 10:14:46.492328+02	\N
415	613095ff1eefb021843d3ce9	BID	344	UNI/USDT	1.00000000	UNI	30.47800000	USDT	0.03047800	USDT	\N	2021-09-02 11:14:39+02	2021-09-02 11:14:48.522271+02	\N
416	6130a4101eefb021843e4f45	BID	345	UNI/USDT	1.00000000	UNI	30.34930000	USDT	0.03034930	USDT	\N	2021-09-02 12:14:40+02	2021-09-02 12:14:47.275939+02	\N
417	613232412e113d292370e71d	ASK	346	BTC/USDT	0.00100000	BTC	50998.90000000	USDT	0.05099890	USDT	\N	2021-09-03 16:33:37+02	2021-09-03 16:33:43.219531+02	\N
418	61323be11eefb021846439e0	BID	347	UNI/USDT	1.00000000	UNI	29.08930000	USDT	0.02908930	USDT	\N	2021-09-03 17:14:41+02	2021-09-03 17:14:49.22867+02	\N
419	613249f01eefb02184654e83	BID	348	UNI/USDT	1.00000000	UNI	29.09860000	USDT	0.02909860	USDT	\N	2021-09-03 18:14:40+02	2021-09-03 18:14:44.740334+02	\N
420	61352e412e113d29237e9549	ASK	349	BTC/USDT	0.00100000	BTC	51377.20000000	USDT	0.05137720	USDT	\N	2021-09-05 22:53:21+02	2021-09-05 22:53:26.116464+02	\N
393	6121bd671eefb02184ea5396	BID	325	UNI/USDT	1.00000000	UNI	28.36160000	USDT	0.02836160	USDT	\N	2021-08-22 04:58:47+02	2021-08-22 04:58:58.440283+02	\N
394	6121bd662e113d292361da13	BID	324	BTC/USDT	0.00100000	BTC	48844.90000000	USDT	0.04884490	USDT	\N	2021-08-22 04:58:46+02	2021-08-22 04:58:58.444526+02	\N
404	612f21551eefb0218418973e	ASK	335	UNI/USDT	1.00000000	UNI	30.60820000	USDT	0.03060820	USDT	\N	2021-09-01 08:44:37+02	2021-09-01 08:44:41.55318+02	\N
405	6130257d1eefb02184337ff5	BID	336	UNI/USDT	1.00000000	UNI	30.49300000	USDT	0.03049300	USDT	\N	2021-09-02 03:14:37+02	2021-09-02 03:14:44.158952+02	\N
406	6130338e1eefb02184355513	BID	337	UNI/USDT	1.00000000	UNI	30.54720000	USDT	0.03054720	USDT	\N	2021-09-02 04:14:38+02	2021-09-02 04:14:46.113489+02	\N
407	6130419e1eefb02184363cfa	BID	338	UNI/USDT	1.00000000	UNI	30.44140000	USDT	0.03044140	USDT	\N	2021-09-02 05:14:38+02	2021-09-02 05:14:44.493137+02	\N
408	61304faf1eefb021843720cd	BID	339	UNI/USDT	1.00000000	UNI	30.32480000	USDT	0.03032480	USDT	\N	2021-09-02 06:14:39+02	2021-09-02 06:14:46.589395+02	\N
409	61305dbf1eefb02184380b36	BID	340	UNI/USDT	0.60000000	UNI	30.42850000	USDT	0.01825710	USDT	\N	2021-09-02 07:14:39+02	2021-09-02 07:14:42.50487+02	\N
410	61305dbf1eefb02184380b34	BID	340	UNI/USDT	0.20000000	UNI	30.42840000	USDT	0.00608568	USDT	\N	2021-09-02 07:14:39+02	2021-09-02 07:14:48.53683+02	\N
421	61352e422e113d29237e966a	ASK	350	BTC/USDT	0.00100000	BTC	51363.50000000	USDT	0.05136350	USDT	\N	2021-09-05 22:53:22+02	2021-09-05 22:53:26.157959+02	\N
422	61352e422e113d29237e971d	ASK	351	BTC/USDT	0.00100000	BTC	51399.50000000	USDT	0.05139950	USDT	\N	2021-09-05 22:53:22+02	2021-09-05 22:53:26.173282+02	\N
423	61352e422e113d29237e97c5	ASK	352	BTC/USDT	0.00100000	BTC	51398.10000000	USDT	0.05139810	USDT	\N	2021-09-05 22:53:22+02	2021-09-05 22:53:31.124713+02	\N
424	61352e442e113d29237e9a3b	ASK	353	BTC/USDT	0.00100000	BTC	51391.30000000	USDT	0.05139130	USDT	\N	2021-09-05 22:53:24+02	2021-09-05 22:53:31.130861+02	\N
425	6136062d1eefb02184acea24	BID	355	UNI/USDT	1.00000000	UNI	28.91140000	USDT	0.02891140	USDT	\N	2021-09-06 14:14:37+02	2021-09-06 14:14:41.998918+02	\N
426	6136062c2e113d2923ca0f81	BID	354	BTC/USDT	0.00200000	BTC	51282.00000000	USDT	0.10256400	USDT	\N	2021-09-06 14:14:36+02	2021-09-06 14:14:42.014929+02	\N
427	6136143c1eefb02184ae46a2	BID	357	UNI/USDT	1.00000000	UNI	28.97310000	USDT	0.02897310	USDT	\N	2021-09-06 15:14:36+02	2021-09-06 15:14:45.99227+02	\N
428	6136143c2e113d2923d0fc7c	BID	356	BTC/USDT	0.00200000	BTC	51293.40000000	USDT	0.10258680	USDT	\N	2021-09-06 15:14:36+02	2021-09-06 15:14:45.995759+02	\N
461	613777481eefb02184cf9e2f	ASK	367	UNI/USDT	0.53090000	UNI	25.68270000	USDT	0.01363495	USDT	\N	2021-09-07 16:29:29+02	2021-09-07 16:29:36.017801+02	2021-09-08 16:29:28.859527+02
465	61377bd01eefb02184d05f58	ASK	369	UNI/USDT	0.40050000	UNI	24.64270000	USDT	0.00986940	USDT	\N	2021-09-07 16:48:50+02	2021-09-07 16:48:55.748655+02	2021-09-08 16:48:48.870039+02
471	613780b42e113d2923815a12	ASK	375	BTC/USDT	0.00060000	BTC	42615.00000000	USDT	0.02556900	USDT	\N	2021-09-07 17:09:41+02	2021-09-07 17:09:44.838855+02	\N
453	613777461eefb02184cf9d1e	ASK	365	UNI/USDT	0.76590000	UNI	25.76220000	USDT	0.01973127	USDT	\N	2021-09-07 16:29:27+02	2021-09-07 16:29:35.999334+02	2021-09-08 16:29:28.841883+02
436	6137773f1eefb02184cf9ab9	ASK	359	UNI/USDT	1.00000000	UNI	25.85550000	USDT	0.02585550	USDT	\N	2021-09-07 16:29:19+02	2021-09-07 16:29:22.874016+02	\N
437	613777401eefb02184cf9b1e	ASK	360	UNI/USDT	1.00000000	UNI	25.85000000	USDT	0.02585000	USDT	\N	2021-09-07 16:29:20+02	2021-09-07 16:29:22.971835+02	\N
438	613777401eefb02184cf9b44	ASK	361	UNI/USDT	1.00000000	UNI	25.85000000	USDT	0.02585000	USDT	\N	2021-09-07 16:29:21+02	2021-09-07 16:29:29.911321+02	\N
439	613777411eefb02184cf9b76	ASK	362	UNI/USDT	1.00000000	UNI	25.83830000	USDT	0.02583830	USDT	\N	2021-09-07 16:29:22+02	2021-09-07 16:29:29.914682+02	\N
446	613777451eefb02184cf9cd4	ASK	363	UNI/USDT	0.13600000	UNI	25.78000000	USDT	0.00350608	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:29.933256+02	\N
458	613777481eefb02184cf9e46	ASK	368	UNI/USDT	1.00000000	UNI	25.68090000	USDT	0.02568090	USDT	\N	2021-09-07 16:29:29+02	2021-09-07 16:29:36.010856+02	\N
440	613777451eefb02184cf9cde	ASK	363	UNI/USDT	0.16560000	UNI	25.76610000	USDT	0.00426687	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:29.91752+02	2021-09-08 16:29:24.985305+02
467	61377bd61eefb02184d06108	ASK	371	UNI/USDT	1.00000000	UNI	24.40000000	USDT	0.02440000	USDT	\N	2021-09-07 16:48:55+02	2021-09-07 16:49:01.968463+02	\N
429	6136d7d62e113d292322e03e	BID	358	BTC/USDT	0.00051574	BTC	52836.00000000	USDT	0.02724964	USDT	\N	2021-09-07 05:09:10+02	2021-09-07 05:09:14.445654+02	2021-09-07 20:14:46.866024+02
431	6136d7d62e113d292322e03c	BID	358	BTC/USDT	0.00002074	BTC	52836.00000000	USDT	0.00109582	USDT	\N	2021-09-07 05:09:10+02	2021-09-07 05:09:14.458984+02	2021-09-07 20:14:46.873823+02
466	61377bd01eefb02184d05f63	ASK	370	UNI/USDT	1.00000000	UNI	24.58030000	USDT	0.02458030	USDT	\N	2021-09-07 16:48:51+02	2021-09-07 16:48:55.753263+02	\N
470	613780b42e113d2923815882	BID	373	BTC/USDT	0.00105516	BTC	42882.00000000	USDT	0.04524737	USDT	\N	2021-09-07 17:09:40+02	2021-09-07 17:09:44.834958+02	2021-09-07 20:14:46.88957+02
452	613777461eefb02184cf9cfc	ASK	364	UNI/USDT	1.00000000	UNI	25.76670000	USDT	0.02576670	USDT	\N	2021-09-07 16:29:27+02	2021-09-07 16:29:35.997109+02	\N
473	613780b42e113d2923815a13	ASK	375	BTC/USDT	0.00046616	BTC	42563.00000000	USDT	0.01984117	USDT	\N	2021-09-07 17:09:41+02	2021-09-07 17:09:50.822088+02	2021-09-07 20:14:46.89717+02
447	613777451eefb02184cf9cdc	ASK	363	UNI/USDT	0.06880000	UNI	25.77270000	USDT	0.00177316	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:35.975041+02	2021-09-08 16:29:25.003749+02
449	613777451eefb02184cf9cd9	ASK	363	UNI/USDT	0.03920000	UNI	25.77360000	USDT	0.00101033	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:35.982396+02	2021-09-08 16:29:25.022603+02
435	6136d7d62e113d292322e03f	BID	358	BTC/USDT	0.00007943	BTC	52836.00000000	USDT	0.00419676	USDT	\N	2021-09-07 05:09:10+02	2021-09-07 05:09:18.475635+02	2021-09-07 20:14:46.861536+02
444	613777451eefb02184cf9cd6	ASK	363	UNI/USDT	0.01340000	UNI	25.77770000	USDT	0.00034542	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:29.928015+02	2021-09-08 16:29:25.040692+02
432	6136d7d62e113d292322e03b	BID	358	BTC/USDT	0.00003831	BTC	52836.00000000	USDT	0.00202415	USDT	\N	2021-09-07 05:09:10+02	2021-09-07 05:09:14.462138+02	2021-09-07 20:14:46.877916+02
445	613777451eefb02184cf9cd5	ASK	363	UNI/USDT	0.08770000	UNI	25.77980000	USDT	0.00226089	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:29.930597+02	2021-09-08 16:29:25.045175+02
451	613777451eefb02184cf9cd3	ASK	363	UNI/USDT	0.27270000	UNI	25.78180000	USDT	0.00703070	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:35.994882+02	2021-09-08 16:29:25.048765+02
476	613781e22e113d29238236e9	BID	376	BTC/USDT	0.00200000	BTC	45405.40000000	USDT	0.09081080	USDT	\N	2021-09-07 17:14:42+02	2021-09-07 17:15:44.763239+02	\N
433	6136d7d62e113d292322e03a	BID	358	BTC/USDT	0.00013845	BTC	52836.00000000	USDT	0.00731514	USDT	\N	2021-09-07 05:09:10+02	2021-09-07 05:09:14.46492+02	2021-09-07 20:14:46.881697+02
472	613780b42e113d2923815a14	ASK	375	BTC/USDT	0.00093384	BTC	42562.50000000	USDT	0.03974657	USDT	\N	2021-09-07 17:09:41+02	2021-09-07 17:09:50.818622+02	2021-09-07 20:14:46.893734+02
460	613777481eefb02184cf9e30	ASK	367	UNI/USDT	0.19870000	UNI	25.68150000	USDT	0.00510291	USDT	\N	2021-09-07 16:29:29+02	2021-09-07 16:29:36.015397+02	2021-09-08 16:29:28.856511+02
463	613777481eefb02184cf9e2d	ASK	367	UNI/USDT	0.06790000	UNI	25.68450000	USDT	0.00174398	USDT	\N	2021-09-07 16:29:29+02	2021-09-07 16:29:36.022791+02	2021-09-08 16:29:28.865301+02
459	613777481eefb02184cf9e31	ASK	367	UNI/USDT	0.14890000	UNI	25.68090000	USDT	0.00382389	USDT	\N	2021-09-07 16:29:29+02	2021-09-07 16:29:36.01292+02	2021-09-08 16:29:28.853818+02
468	61377bda1eefb02184d06207	ASK	372	UNI/USDT	0.62840959	UNI	24.40000000	USDT	0.01533319	USDT	\N	2021-09-07 16:49:00+02	2021-09-07 16:49:05.978258+02	2021-09-08 16:49:00.838028+02
469	61377bda1eefb02184d06206	ASK	372	UNI/USDT	0.37159041	UNI	24.40010000	USDT	0.00906684	USDT	\N	2021-09-07 16:49:00+02	2021-09-07 16:49:05.981243+02	2021-09-08 16:49:00.840804+02
430	6136d7d62e113d292322e03d	BID	358	BTC/USDT	0.00002746	BTC	52836.00000000	USDT	0.00145088	USDT	\N	2021-09-07 05:09:10+02	2021-09-07 05:09:14.456187+02	2021-09-07 20:14:46.870048+02
475	613781e41eefb02184d13ba8	BID	377	UNI/USDT	1.00000000	UNI	21.58200000	USDT	0.02158200	USDT	\N	2021-09-07 17:14:44+02	2021-09-07 17:15:44.717259+02	\N
441	613777451eefb02184cf9cdd	ASK	363	UNI/USDT	0.01870000	UNI	25.76840000	USDT	0.00048187	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:29.920631+02	2021-09-08 16:29:24.996235+02
442	613777451eefb02184cf9cdb	ASK	363	UNI/USDT	0.01520000	UNI	25.77270000	USDT	0.00039175	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:29.923237+02	2021-09-08 16:29:25.010057+02
443	613777451eefb02184cf9cd8	ASK	363	UNI/USDT	0.01530000	UNI	25.77530000	USDT	0.00039436	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:29.925542+02	2021-09-08 16:29:25.028715+02
450	613777451eefb02184cf9cd7	ASK	363	UNI/USDT	0.07320000	UNI	25.77750000	USDT	0.00188691	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:35.987452+02	2021-09-08 16:29:25.034979+02
464	61377bd01eefb02184d05f59	ASK	369	UNI/USDT	0.59950000	UNI	24.58020000	USDT	0.01473583	USDT	\N	2021-09-07 16:48:50+02	2021-09-07 16:48:55.742213+02	2021-09-08 16:48:48.867691+02
448	613777451eefb02184cf9cda	ASK	363	UNI/USDT	0.09420000	UNI	25.77340000	USDT	0.00242785	USDT	\N	2021-09-07 16:29:26+02	2021-09-07 16:29:35.979945+02	2021-09-08 16:29:25.016562+02
434	6136d7d62e113d292322e039	BID	358	BTC/USDT	0.00002950	BTC	52836.00000000	USDT	0.00155866	USDT	\N	2021-09-07 05:09:10+02	2021-09-07 05:09:14.468139+02	2021-09-07 20:14:46.885565+02
474	613780b42e113d29238158b1	ASK	374	BTC/USDT	0.00200000	BTC	42788.50000000	USDT	0.08557700	USDT	\N	2021-09-07 17:09:41+02	2021-09-07 17:09:50.825568+02	\N
485	6137ba231eefb02184d89315	BID	385	UNI/USDT	1.00000000	UNI	24.19840000	USDT	0.02419840	USDT	\N	2021-09-07 21:14:43+02	2021-09-07 21:14:51.276497+02	\N
487	6137d6411eefb02184db94b3	BID	387	UNI/USDT	1.00000000	UNI	23.84540000	USDT	0.02384540	USDT	\N	2021-09-07 23:14:41+02	2021-09-07 23:14:49.268924+02	\N
482	61379e031eefb02184d4806f	BID	382	UNI/USDT	1.00000000	UNI	24.93780000	USDT	0.02493780	USDT	\N	2021-09-07 19:14:43+02	2021-09-07 19:14:54.42823+02	\N
477	613784b71eefb02184d184e3	ASK	378	UNI/USDT	1.00000000	UNI	22.91590000	USDT	0.02291590	USDT	\N	2021-09-07 17:26:48+02	2021-09-07 17:27:27.639219+02	\N
478	61378ff41eefb02184d2d09a	BID	380	UNI/USDT	1.00000000	UNI	24.17560000	USDT	0.02417560	USDT	\N	2021-09-07 18:14:44+02	2021-09-07 18:14:55.781218+02	\N
479	61378ff02e113d29238f2ec5	BID	379	BTC/USDT	0.00200000	BTC	47019.20000000	USDT	0.09403840	USDT	\N	2021-09-07 18:14:40+02	2021-09-07 18:14:55.820661+02	\N
480	61379e012e113d29239a6718	BID	381	BTC/USDT	0.00088365	BTC	47306.30000000	USDT	0.04180221	USDT	\N	2021-09-07 19:14:41+02	2021-09-07 19:14:50.484826+02	2021-09-07 20:14:46.900365+02
481	61379e012e113d29239a6717	BID	381	BTC/USDT	0.00111635	BTC	47306.30000000	USDT	0.05281039	USDT	\N	2021-09-07 19:14:41+02	2021-09-07 19:14:50.490808+02	2021-09-07 20:14:46.903792+02
483	6137ac112e113d2923a5da62	BID	383	BTC/USDT	0.00200000	BTC	46416.10000000	USDT	0.09283220	USDT	\N	2021-09-07 20:14:42+02	2021-09-07 20:14:46.906555+02	\N
454	613777461eefb02184cf9d1d	ASK	365	UNI/USDT	0.22380000	UNI	25.76410000	USDT	0.00576601	USDT	\N	2021-09-07 16:29:27+02	2021-09-07 16:29:36.00155+02	2021-09-08 16:29:28.844167+02
484	6137ac131eefb02184d6a586	BID	384	UNI/USDT	1.00000000	UNI	23.68840000	USDT	0.02368840	USDT	\N	2021-09-07 20:14:43+02	2021-09-07 20:14:48.928203+02	\N
455	613777461eefb02184cf9d1c	ASK	365	UNI/USDT	0.01030000	UNI	25.76610000	USDT	0.00026539	USDT	\N	2021-09-07 16:29:27+02	2021-09-07 16:29:36.003692+02	2021-09-08 16:29:28.846496+02
486	6137c8321eefb02184d9f084	BID	386	UNI/USDT	1.00000000	UNI	24.04080000	USDT	0.02404080	USDT	\N	2021-09-07 22:14:42+02	2021-09-07 22:14:46.885514+02	\N
456	613777471eefb02184cf9ded	ASK	366	UNI/USDT	0.94590000	UNI	25.69420000	USDT	0.02430414	USDT	\N	2021-09-07 16:29:28+02	2021-09-07 16:29:36.006514+02	2021-09-08 16:29:28.849004+02
457	613777471eefb02184cf9dec	ASK	366	UNI/USDT	0.05410000	UNI	25.69530000	USDT	0.00139012	USDT	\N	2021-09-07 16:29:28+02	2021-09-07 16:29:36.008905+02	2021-09-08 16:29:28.851456+02
462	613777481eefb02184cf9e2e	ASK	367	UNI/USDT	0.05360000	UNI	25.68310000	USDT	0.00137661	USDT	\N	2021-09-07 16:29:29+02	2021-09-07 16:29:36.020374+02	2021-09-08 16:29:28.862496+02
\.


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cassandre_trading_bot
--

SELECT pg_catalog.setval('public.orders_id_seq', 387, true);


--
-- Name: positions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cassandre_trading_bot
--

SELECT pg_catalog.setval('public.positions_id_seq', 199, true);


--
-- Name: strategies_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cassandre_trading_bot
--

SELECT pg_catalog.setval('public.strategies_id_seq', 2, true);


--
-- Name: trades_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cassandre_trading_bot
--

SELECT pg_catalog.setval('public.trades_id_seq', 487, true);


--
-- Name: backtesting_tickers backtesting_tickers_primary_key; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.backtesting_tickers
    ADD CONSTRAINT backtesting_tickers_primary_key PRIMARY KEY (test_session_id, response_sequence_id, currency_pair);


--
-- Name: databasechangeloglock databasechangeloglock_pkey; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.databasechangeloglock
    ADD CONSTRAINT databasechangeloglock_pkey PRIMARY KEY (id);


--
-- Name: orders orders_primary_key; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_primary_key PRIMARY KEY (id);


--
-- Name: positions positions_primary_key; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_primary_key PRIMARY KEY (id);


--
-- Name: strategies strategies_primary_key; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.strategies
    ADD CONSTRAINT strategies_primary_key PRIMARY KEY (id);


--
-- Name: trades trades_primary_key; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT trades_primary_key PRIMARY KEY (id);


--
-- Name: orders unique_orders_order_id; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT unique_orders_order_id UNIQUE (order_id);


--
-- Name: trades unique_trades_trade_id; Type: CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT unique_trades_trade_id UNIQUE (trade_id);


--
-- Name: idx_backtesting_tickers_response_sequence_id; Type: INDEX; Schema: public; Owner: cassandre_trading_bot
--

CREATE INDEX idx_backtesting_tickers_response_sequence_id ON public.backtesting_tickers USING btree (test_session_id DESC, response_sequence_id DESC);


--
-- Name: idx_orders_order_id; Type: INDEX; Schema: public; Owner: cassandre_trading_bot
--

CREATE INDEX idx_orders_order_id ON public.orders USING btree (order_id DESC);


--
-- Name: idx_positions_position_id; Type: INDEX; Schema: public; Owner: cassandre_trading_bot
--

CREATE INDEX idx_positions_position_id ON public.positions USING btree (position_id DESC);


--
-- Name: idx_positions_status; Type: INDEX; Schema: public; Owner: cassandre_trading_bot
--

CREATE INDEX idx_positions_status ON public.positions USING btree (status DESC);


--
-- Name: idx_strategies_strategy_id; Type: INDEX; Schema: public; Owner: cassandre_trading_bot
--

CREATE INDEX idx_strategies_strategy_id ON public.strategies USING btree (strategy_id DESC);


--
-- Name: idx_trades_trade_id; Type: INDEX; Schema: public; Owner: cassandre_trading_bot
--

CREATE INDEX idx_trades_trade_id ON public.trades USING btree (trade_id DESC);


--
-- Name: orders fk_orders_strategy_id; Type: FK CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk_orders_strategy_id FOREIGN KEY (FK_STRATEGY_UID) REFERENCES public.strategies(id);


--
-- Name: positions fk_positions_close_order_id; Type: FK CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT fk_positions_close_order_id FOREIGN KEY (FK_CLOSING_ORDER_UID) REFERENCES public.orders(id);


--
-- Name: positions fk_positions_open_order_id; Type: FK CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT fk_positions_open_order_id FOREIGN KEY (FK_OPENING_ORDER_UID) REFERENCES public.orders(id);


--
-- Name: positions fk_positions_strategy_id; Type: FK CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT fk_positions_strategy_id FOREIGN KEY (FK_STRATEGY_UID) REFERENCES public.strategies(id);


--
-- Name: trades fk_trades_order_id; Type: FK CONSTRAINT; Schema: public; Owner: cassandre_trading_bot
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT fk_trades_order_id FOREIGN KEY (FK_ORDER_UID) REFERENCES public.orders(id);


--
-- PostgreSQL database dump complete
--

