package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Ticker DTO")
public class TickerDTOTest {

	@Test
	@DisplayName("EqualTo")
	public void equalToForTickers() throws ParseException {
		// Currency pairs.
		final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
		final CurrencyPairDTO cp2 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT);

		// Dates.
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Timestamp date1 = new Timestamp(dateFormat.parse("23/09/2017").getTime());
		Timestamp date2 = new Timestamp(dateFormat.parse("02/10/2018").getTime());

		// Ticker 1 - ETH/BTC, date1, 1.
		TickerDTO t01 = TickerDTO.builder().currencyPair(cp1).timestamp(date1).bid(new BigDecimal("1")).create();

		// Ticker 2 - ETH/BTC, date1, 2.
		TickerDTO t02 = TickerDTO.builder().currencyPair(cp1).timestamp(date1).bid(new BigDecimal("2")).create();
		assertEquals(t01, t02);
		assertEquals(t02, t01);

		// Ticker 3 - ETH/BTC, date2, 1.
		TickerDTO t03 = TickerDTO.builder().currencyPair(cp1).timestamp(date2).bid(new BigDecimal("1")).create();
		assertNotEquals(t01, t03);
		assertNotEquals(t03, t01);

		// Ticker 4 - ETH/USDT, date1, 1.
		TickerDTO t04 = TickerDTO.builder().currencyPair(cp2).timestamp(date1).bid(new BigDecimal("1")).create();
		assertNotEquals(t01, t04);
		assertNotEquals(t04, t01);
	}

}
