package tech.cassandre.trading.bot.tmp.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("DTO - TickerDTO")
@Disabled
public class TickerDTOTest {

	@Test
	@Tag("notReviewed")
	@DisplayName("Check equalTo")
	public void checkEqualToForTickers() throws ParseException {
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

	@Test
	@Tag("notReviewed")
	@DisplayName("Check builder with String and epoch")
	public void checkBuilderWithStringAndEpoch() {
		TickerDTO t01 = TickerDTO.builder()
				.last("0.1")
				.ask("0.2")
				.askSize("0.3")
				.bid("0.4")
				.bidSize("0.5")
				.high("0.6")
				.low("0.7")
				.open("0.8")
				.quoteVolume("0.9")
				.timestampAsEpochInSeconds(1596499200)
				.create();
		assertEquals(BigDecimal.valueOf(0.1), t01.getLast());
		assertEquals(BigDecimal.valueOf(0.2), t01.getAsk());
		assertEquals(BigDecimal.valueOf(0.3), t01.getAskSize());
		assertEquals(BigDecimal.valueOf(0.4), t01.getBid());
		assertEquals(BigDecimal.valueOf(0.5), t01.getBidSize());
		assertEquals(BigDecimal.valueOf(0.6), t01.getHigh());
		assertEquals(BigDecimal.valueOf(0.7), t01.getLow());
		assertEquals(BigDecimal.valueOf(0.8), t01.getOpen());
		assertEquals(BigDecimal.valueOf(0.9), t01.getQuoteVolume());
		// Date.
		assertEquals(2020, t01.getTimestamp().getYear());
		assertEquals(8, t01.getTimestamp().getMonthValue());
		assertEquals(4, t01.getTimestamp().getDayOfMonth());
	}

}
