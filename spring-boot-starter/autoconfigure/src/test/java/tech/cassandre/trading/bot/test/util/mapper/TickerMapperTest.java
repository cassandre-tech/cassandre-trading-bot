package tech.cassandre.trading.bot.test.util.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.mapstruct.factory.Mappers;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;
import tech.cassandre.trading.bot.util.mapper.TickerMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.*;

public class TickerMapperTest extends BaseMock {

    @Test
    @DisplayName("TickerMapperTest")
    public void tickerMapperTest() {
        TickerMapper tickerMapper = Mappers.getMapper(TickerMapper.class);
        Ticker tickerToMap = getGeneratedTicker(new CurrencyPair(Currency.BTC, Currency.EUR), BigDecimal.valueOf(10));
        CurrencyPairDTO currencyPairToMap = CurrencyPairDTO.builder()
                .baseCurrency(BTC)
                .quoteCurrency(EUR)
                .baseCurrencyPrecision(2)
                .quoteCurrencyPrecision(3)
                .build();
        TickerDTO tickerResult = tickerMapper.mapToTickerDTO(tickerToMap,currencyPairToMap);

        assertEquals(tickerResult.getCurrencyPair(),currencyPairToMap);

    }
}
