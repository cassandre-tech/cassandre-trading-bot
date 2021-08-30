package tech.cassandre.trading.bot.test.integration.kucoin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.ExchangeService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
		"cassandre.trading.bot.exchange.driver-class-name=${KUCOIN_NAME}",
		"cassandre.trading.bot.exchange.modes.sandbox=true",
		"cassandre.trading.bot.exchange.modes.dry=false",
		"cassandre.trading.bot.exchange.username=${KUCOIN_USERNAME}",
		"cassandre.trading.bot.exchange.passphrase=${KUCOIN_PASSPHRASE}",
		"cassandre.trading.bot.exchange.key=${KUCOIN_KEY}",
		"cassandre.trading.bot.exchange.secret=${KUCOIN_SECRET}",
		"cassandre.trading.bot.exchange.rates.account=1000",
		"cassandre.trading.bot.exchange.rates.ticker=1001",
		"cassandre.trading.bot.exchange.rates.trade=1002",
		"cassandre.trading.bot.database.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver",
		"cassandre.trading.bot.database.datasource.url=jdbc:hsqldb:mem:cassandre-database;shutdown=true",
		"cassandre.trading.bot.database.datasource.username=sa",
		"cassandre.trading.bot.database.datasource.password=",
		"testableStrategy.enabled=true",
		"invalidStrategy.enabled=false"
})
@DisplayName("Kucoin - Exchange service")
public class ExchangeServiceTest {

	@Autowired
	private ExchangeService exchangeService;

	@Test
	@Tag("integration")
	@DisplayName("Check get available currency pairs")
	public void checkGetAvailableCurrencyPairs() {
		// Expected values.
		final int expectedMinimumNumberOfAvailableCurrencyPairs = 4;

		// =============================================================================================================
		// Retrieve the available currency pairs.
		Set<CurrencyPairDTO> currencyPairs = exchangeService.getAvailableCurrencyPairs();

		// ====================================symbols=========================================================================
		// Tests results.
		assertTrue(expectedMinimumNumberOfAvailableCurrencyPairs < currencyPairs.size());

		assertTrue(currencyPairs.contains(new CurrencyPairDTO("KCS", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(KCS, USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("ETH", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(ETH, USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("BTC", "USDT")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(BTC, USDT)));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO("ETH", "BTC")));
		assertTrue(currencyPairs.contains(new CurrencyPairDTO(ETH, BTC)));
	}

}
