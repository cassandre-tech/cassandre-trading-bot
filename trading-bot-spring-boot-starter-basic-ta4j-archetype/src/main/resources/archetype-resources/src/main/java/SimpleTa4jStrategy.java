#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
        package ${package};

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.Duration;

import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;

/**
 * Simple strategy.
 * Please, create your own Kucoin sandbox account and do not make orders with this account.
 * How to do it : https://trading-bot.cassandre.tech/how-tos/how-to-create-a-kucoin-sandbox-account
 */
@CassandreStrategy(name = "Simple ta4j strategy")
public final class SimpleTa4jStrategy extends BasicTa4jCassandreStrategy {

    @Override
    public CurrencyPairDTO getRequestedCurrencyPair() {
        return new CurrencyPairDTO(BTC, USDT);
    }

    @Override
    public int getMaximumBarCount() {
        return 10;
    }

    @Override
    public Duration getDelayBetweenTwoBars() {
        return Duration.ofDays(1);
    }

    @Override
    public Strategy getStrategy() {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(getSeries());
        SMAIndicator sma = new SMAIndicator(closePrice, getMaximumBarCount());
        return new BaseStrategy(new OverIndicatorRule(sma, closePrice), new UnderIndicatorRule(sma, closePrice));
    }

    @Override
    public void onTickerUpdate(TickerDTO ticker) {
        // Display all received tickers
        System.out.println("New ticker " + ticker);
    }

    @Override
    public void onPositionUpdate(PositionDTO position) {
        // Display the position number when a ticker has been opened.
        if (position.getStatus().equals(OPENED)) {
            System.out.println(" > Position " + position.getId() + " opened");
        }
        // Display the position number and gain when it's closed.
        if (position.getStatus().equals(CLOSED)) {
            System.out.println(" >> Position " + position.getId() + " closed - gain : " + position.getGain().getAmount());
        }
    }

    @Override
    public void shouldEnter() {
        // Create rules.
        PositionRulesDTO rules = PositionRulesDTO
                .builder()
                .stopGainPercentage(10)
                .stopLossPercentage(5)
                .create();
        // Create position.
        getPositionService().createPosition(
                new CurrencyPairDTO(BTC, USDT),
                new BigDecimal("0.01"),
                rules);
    }

    @Override
    public void shouldExit() {
    }

}
