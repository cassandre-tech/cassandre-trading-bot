package tech.cassandre.trading.bot.strategy;

import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.ImportedTickersRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Cassandre's strategy interface.
 * This interface defines how Cassandre interacts with a user strategy.
 */
@SuppressWarnings("unused")
public interface CassandreStrategyInterface {

    /**
     * Setter strategyDTO.
     *
     * @param newStrategyDTO strategy DTO.
     */
    void setStrategy(StrategyDTO newStrategyDTO);

    /**
     * Initialize strategy accounts with exchange accounts data.
     *
     * @param accounts accounts
     */
    void initializeAccounts(Map<String, AccountDTO> accounts);

    /**
     * Setter position flux.
     *
     * @param newPositionFlux position flux
     */
    void setPositionFlux(PositionFlux newPositionFlux);

    /**
     * Setter order repository.
     *
     * @param newOrderRepository order repository
     */
    void setOrderRepository(OrderRepository newOrderRepository);

    /**
     * Setter trade repository.
     *
     * @param newTradeRepository trade repository.
     */
    void setTradeRepository(TradeRepository newTradeRepository);

    /**
     * Setter positionRepository.
     *
     * @param newPositionRepository the positionRepository to set
     */
    void setPositionRepository(PositionRepository newPositionRepository);

    /**
     * Setter ImportedTickersRepository.
     *
     * @param newImportedTickersRepository ImportedTickers Repository
     */
    void setImportedTickersRepository(ImportedTickersRepository newImportedTickersRepository);

    /**
     * Setter for exchangeService.
     *
     * @param newExchangeService exchange service
     */
    void setExchangeService(ExchangeService newExchangeService);

    /**
     * Setter for tradeService.
     *
     * @param newTradeService the tradeService to set
     */
    void setTradeService(TradeService newTradeService);

    /**
     * Setter for positionService.
     *
     * @param newPositionService position service
     */
    void setPositionService(PositionService newPositionService);

    /**
     * Method called by streams on accounts updates.
     *
     * @param accounts accounts updates
     */
    void accountsUpdates(Set<AccountDTO> accounts);

    /**
     * Method called by streams on tickers updates.
     *
     * @param tickers tickers updates
     */
    void tickersUpdates(Set<TickerDTO> tickers);

    /**
     * Method called by streams on orders updates.
     *
     * @param orders orders updates
     */
    void ordersUpdates(Set<OrderDTO> orders);

    /**
     * Method called by streams on trades updates.
     *
     * @param trades trades updates
     */
    void tradesUpdates(Set<TradeDTO> trades);

    /**
     * Method called by streams on positions updates.
     *
     * @param positions positions updates
     */
    void positionsUpdates(Set<PositionDTO> positions);

    /**
     * Implements this method to tell the bot which currency pairs your strategy will receive.
     *
     * @return the list of currency pairs tickers your want to receive in this strategy
     */
    Set<CurrencyPairDTO> getRequestedCurrencyPairs();

    /**
     * Implements this method to tell the bot which account from the accounts you own is the one you use for trading.
     *
     * @param accounts all your accounts
     * @return your trading account
     */
    Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts);

    /**
     * Returns the trading account.
     *
     * @return your trading account
     */
    Optional<AccountDTO> getTradeAccount();

    /**
     * This method is called by Cassandre before flux are started.
     * For example, you can implement this method to prepare your historical data.
     */
    void initialize();

    /**
     * Method called by Cassandre when there are accounts updates.
     *
     * @param accounts accounts updates
     */
    void onAccountsUpdates(Map<String, AccountDTO> accounts);

    /**
     * Method called by Cassandre when there are tickers updates.
     *
     * @param tickers tickers updates
     */
    void onTickersUpdates(Map<CurrencyPairDTO, TickerDTO> tickers);

    /**
     * Method called by Cassandre when there are orders updates.
     *
     * @param orders orders updates
     */
    void onOrdersUpdates(Map<String, OrderDTO> orders);

    /**
     * Method called by Cassandre when there are trades updates.
     *
     * @param trades trades updates
     */
    void onTradesUpdates(Map<String, TradeDTO> trades);

    /**
     * Method called by Cassandre when there are positions updates.
     *
     * @param positions positions updates
     */
    void onPositionsUpdates(Map<Long, PositionDTO> positions);

    /**
     * Method called by Cassandre when there are positions status updates.
     *
     * @param positions positions status updates
     */
    void onPositionsStatusUpdates(Map<Long, PositionDTO> positions);

}
