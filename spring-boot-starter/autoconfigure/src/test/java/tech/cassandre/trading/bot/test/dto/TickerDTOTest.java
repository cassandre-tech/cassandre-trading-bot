package tech.cassandre.trading.bot.test.dto;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.beta.util.junit.BaseTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@DisplayName("DTO - TickerDTO")
public class TickerDTOTest extends BaseTest {

	@Test
	@CaseId(53)
	@DisplayName("Check equals()")
	public void checkEqualToForTickers() {
		// Currency pairs.
		final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);
		final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);

		// Dates.
		ZonedDateTime date1 = createZonedDateTime("23-09-2017");
		ZonedDateTime date2 = createZonedDateTime("02-10-2018");

		// Ticker 1 - ETH/BTC, date1, 1 BTC.
		TickerDTO t01 = TickerDTO.builder().currencyPair(cp1).timestamp(date1).last(new BigDecimal("1")).build();

		// Ticker 2 - ETH/BTC, date1, 2 BTC.
		TickerDTO t02 = TickerDTO.builder().currencyPair(cp1).timestamp(date1).last(new BigDecimal("2")).build();
		assertEquals(t01, t02);
		assertEquals(t02, t01);

		// Ticker 3 - ETH/BTC, date2, 1 BTC.
		TickerDTO t03 = TickerDTO.builder().currencyPair(cp1).timestamp(date2).last(new BigDecimal("1")).build();
		assertNotEquals(t01, t03);
		assertNotEquals(t03, t01);

		// Ticker 4 - ETH/USDT, date1, 1 USDT.
		TickerDTO t04 = TickerDTO.builder().currencyPair(cp2).timestamp(date1).last(new BigDecimal("1")).build();
		assertNotEquals(t01, t04);
		assertNotEquals(t04, t01);
	}

}
