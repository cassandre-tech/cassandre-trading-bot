package tech.cassandre.trading.bot.integration.kucoin;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
		"cassandre.trading.bot.exchange.name=${KUCOIN_NAME}",
		"cassandre.trading.bot.exchange.modes.sandbox=true",
		"cassandre.trading.bot.exchange.modes.dry=false",
		"cassandre.trading.bot.exchange.username=${KUCOIN_USERNAME}",
		"cassandre.trading.bot.exchange.passphrase=${KUCOIN_PASSPHRASE}",
		"cassandre.trading.bot.exchange.key=${KUCOIN_KEY}",
		"cassandre.trading.bot.exchange.secret=${KUCOIN_SECRET}",
		"cassandre.trading.bot.exchange.rates.account=100",
		"cassandre.trading.bot.exchange.rates.ticker=101",
		"cassandre.trading.bot.exchange.rates.trade=102",
		"testableStrategy.enabled=true",
		"invalidStrategy.enabled=false"
})
@DisplayName("Kucoin - Exchange service")
public class ExchangeServiceTest {

	/** Exchange service. */
	@Autowired
	private ExchangeService exchangeService;

	@Test
	@DisplayName("Get available currency pairs")
	@Disabled("Bug in XChange currency pairs list") // TODO Fix when issue https://github.com/knowm/XChange/issues/3609 is fixed	
	public void testGetAvailableCurrencyPairs() {
		// Expected values.
		final int expectedMinimumNumberOfAvailableCurrencyPairs = 15;

		// =============================================================================================================
		// Retrieve the available currency pairs.
		Set<CurrencyPairDTO> currencyPairs = exchangeService.getAvailableCurrencyPairs();

		// ====================================symbols=========================================================================
		// Tests results.
		assertEquals(expectedMinimumNumberOfAvailableCurrencyPairs, currencyPairs.size());
		// EOS.
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("EOS", "BTC")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.EOS, CurrencyDTO.BTC)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("EOS", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.EOS, CurrencyDTO.USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("EOS", "ETH")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.EOS, CurrencyDTO.ETH)));
		// LTC.
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("LTC", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.LTC, CurrencyDTO.USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("LTC", "ETH")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.LTC, CurrencyDTO.ETH)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("LTC", "BTC")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.LTC, CurrencyDTO.BTC)));
		// KCS.
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("KCS", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.KCS, CurrencyDTO.USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("KCS", "ETH")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.KCS, CurrencyDTO.ETH)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("KCS", "BTC")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.KCS, CurrencyDTO.BTC)));
		// ETH.
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("ETH", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("ETH", "BTC")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC)));
		// BTC.
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("BTC", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.BTC, CurrencyDTO.USDT)));
		// XRP.
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("XRP", "BTC")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.XRP, CurrencyDTO.BTC)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("XRP", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.XRP, CurrencyDTO.USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("XRP", "ETH")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.XRP, CurrencyDTO.ETH)));
		// Non existing.
		assertFalse(currencyPairs.contains(new CurrencyPairDTO("ABC", "DEF")));
		assertFalse(currencyPairs.contains(new CurrencyPairDTO(CurrencyDTO.BGN, CurrencyDTO.AFN)));
		assertFalse(currencyPairs.contains(new CurrencyPairDTO("BGN", "AFN")));
	}

}
