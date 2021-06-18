package tech.cassandre.trading.bot.test.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Positions strategy.
 */
@SuppressWarnings("unused")
@CassandreStrategy
@ConditionalOnProperty(
        value = "POSITIONS_STRATEGY_ENABLED",
        havingValue = "true")
public final class PositionsStrategy extends BasicCassandreStrategy {

    /** Logger. */
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /** BTC/USDT. */
    private final CurrencyPairDTO BTC_USDT = new CurrencyPairDTO(BTC, USDT);

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdateReceived = new LinkedList<>();

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return Collections.singleton(BTC_USDT);
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream().filter(a -> "trade".equals(a.getAccountId())).findFirst();
    }

    @Override
    public final void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        // We have 200 000 USDT & 100 Ether.
        tickers.values().forEach(tickerDTO -> logger.info("Ticker received: {} \n", tickerDTO));

        final TickerDTO tickerBTCUSDT = tickers.get(BTC_USDT);
        if (tickerBTCUSDT != null) {

            // We use a simple order to buy 1 BTC to make a short position later.
            if (tickerBTCUSDT.getLast().compareTo(new BigDecimal("10000")) == 0) {
                final OrderCreationResultDTO buyMarketOrder = createBuyMarketOrder(BTC_USDT, new BigDecimal(2));
                logger.info("- Buying 2 BTC at 10 000 USDT, order n°{}\n", buyMarketOrder.getOrderId());
            }

            if (tickerBTCUSDT.getLast().compareTo(new BigDecimal("20000")) == 0) {
                // Position 1 (long) opened for 2 BTC at 20 000 USDT - will sell at 60 000 USDT (200% gain).
                final PositionCreationResultDTO position1Result = createLongPosition(BTC_USDT,
                        new BigDecimal("2"),
                        PositionRulesDTO.builder().stopGainPercentage(200f).build());
                if (position1Result.isSuccessful()) {
                    logger.info("- Position n°{} created : {}\n",
                            position1Result.getPosition().getPositionId(),
                            position1Result.getPosition().getDescription());
                }
            }

            if (tickerBTCUSDT.getLast().compareTo(new BigDecimal("50000")) == 0) {
                // Position 2 (long) opened for 1 BTC at 50 000 USDT - will sell at 25 000 USDT (50% loss).
                final PositionCreationResultDTO position1Result = createLongPosition(BTC_USDT,
                        new BigDecimal("1"),
                        PositionRulesDTO.builder().stopLossPercentage(20f).build());
                if (position1Result.isSuccessful()) {
                    logger.info("- Position n°{} created : {}\n",
                            position1Result.getPosition().getPositionId(),
                            position1Result.getPosition().getDescription());
                }
            }

            if (tickerBTCUSDT.getLast().compareTo(new BigDecimal("40000")) == 0) {
                // Position 3 (long) opened for 1 BTC at 40 000 USDT - will sell at 60 000 USDT (50% gain).
                final PositionCreationResultDTO position1Result = createShortPosition(BTC_USDT,
                        new BigDecimal("1"),
                        PositionRulesDTO.builder().stopLossPercentage(10f).build());
                if (position1Result.isSuccessful()) {
                    logger.info("- Position n°{} created : {}\n",
                            position1Result.getPosition().getPositionId(),
                            position1Result.getPosition().getDescription());
                }
            }

            if (tickerBTCUSDT.getLast().compareTo(new BigDecimal("70000")) == 0) {
                // Position 4 (short) opened for 1 BTC at 70 000 USDT - will sell at 35 000 USDT (100% gain).
                final PositionCreationResultDTO position1Result = createShortPosition(BTC_USDT,
                        new BigDecimal("1"),
                        PositionRulesDTO.builder().stopGainPercentage(100f).build());
                if (position1Result.isSuccessful()) {
                    logger.info("- Position n°{} created : {}\n",
                            position1Result.getPosition().getPositionId(),
                            position1Result.getPosition().getDescription());
                }
            }

        }
        tickersUpdateReceived.addAll(tickers.values());
    }

    @Override
    public void onPositionsUpdates(Map<Long, PositionDTO> positions) {
        positions.values().forEach(positionDTO -> logger.info("- Position update : {}\n", positionDTO));
    }

    /**
     * Getter tickersUpdateReceived.
     *
     * @return tickersUpdateReceived
     */
    public final List<TickerDTO> getTickersUpdateReceived() {
        return tickersUpdateReceived;
    }

}
