package tech.cassandre.trading.bot.tmp.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("DTO - AccountDTO")
@Disabled
public class AccountDTOTest {

	@Test
	@Tag("notReviewed")
	@DisplayName("Check equalTo on account id & name")
	public void checkEqualToForAccountIdAndName() {
		// Account 1 (null).
		AccountDTO account1 = AccountDTO.builder().id(null).name(null).balances(null).create();
		// Account 2.
		AccountDTO account2 = AccountDTO.builder().id("01").name("01").balances(null).create();
		assertNotEquals(account2, account1);
		assertNotEquals(account1, account2);
		// Account 3 - Same.
		AccountDTO account3 = AccountDTO.builder().id("01").name("01").balances(null).create();
		assertEquals(account2, account3);
		assertEquals(account3, account2);
		// Account 4 - id changed.
		AccountDTO account4 = AccountDTO.builder().id("CHANGED").name("01").balances(null).create();
		assertNotEquals(account2, account4);
		assertNotEquals(account4, account2);
		// Account 5 - Name changed.
		AccountDTO account5 = AccountDTO.builder().id("01").name("CHANGED").balances(null).create();
		assertNotEquals(account2, account5);
		assertNotEquals(account5, account2);
	}

	@Test
	@Tag("notReviewed")
	@DisplayName("Check equalTo on balances list")
	public void checkEqualToForBalancesList() {
		Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();

		// Account 1 - No balances.
		AccountDTO account1 = AccountDTO.builder().balances(balances).create();

		// Account 2 - One more balance in account 1.
		balances.put(CurrencyDTO.BTC, BalanceDTO.builder().create());
		AccountDTO account2 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account2);
		assertNotEquals(account2, account1);

		// Account 3 - One ETH & one BTC.
		balances.put(CurrencyDTO.ETH, BalanceDTO.builder().create());
		balances.put(CurrencyDTO.BTC, BalanceDTO.builder().create());
		AccountDTO account3 = AccountDTO.builder().balances(balances).create();
		balances.clear();

		// Account 4 - One BTC & one ETH (inverted).
		balances.put(CurrencyDTO.BTC, BalanceDTO.builder().create());
		balances.put(CurrencyDTO.ETH, BalanceDTO.builder().create());
		AccountDTO account4 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertEquals(account3, account4);
		assertEquals(account4, account3);

		// Account 5 - One BTC & one USDT (inverted).
		balances.put(CurrencyDTO.BTC, BalanceDTO.builder().create());
		balances.put(CurrencyDTO.USDT, BalanceDTO.builder().create());
		AccountDTO account5 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account4, account5);
		assertNotEquals(account5, account4);
	}

	@Test
	@Tag("notReviewed")
	@SuppressWarnings("checkstyle:MethodLength")
	@DisplayName("Check equalTo on balances values")
	public void checkEqualToForBalancesValues() {
		Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();

		// Account 1.
		BalanceDTO account1Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account1Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account1Balance1);
		balances.put(CurrencyDTO.ETH, account1Balance2);
		AccountDTO account1 = AccountDTO.builder().balances(balances).create();
		balances.clear();

		// Account 2 - same values.
		BalanceDTO account2Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account2Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.ETH, account2Balance1);
		balances.put(CurrencyDTO.BTC, account2Balance2);
		AccountDTO account2 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertEquals(account1, account1);
		assertEquals(account2, account2);

		// Account 3 - available changes.
		BalanceDTO account3Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("11"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account3Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account3Balance2);
		balances.put(CurrencyDTO.ETH, account3Balance1);
		AccountDTO account3 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account3);
		assertNotEquals(account3, account1);

		// Account 4 - borrowed changed.
		BalanceDTO account4Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("11"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account4Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account4Balance2);
		balances.put(CurrencyDTO.ETH, account4Balance1);
		AccountDTO account4 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account4);
		assertNotEquals(account4, account1);

		// Account 5 - currency changed.
		BalanceDTO account5Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.USDT)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account5Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account5Balance2);
		balances.put(CurrencyDTO.ETH, account5Balance1);
		AccountDTO account5 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account5);
		assertNotEquals(account5, account1);

		// Account 6 - depositing changed.
		BalanceDTO account6Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("11"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account6Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account6Balance2);
		balances.put(CurrencyDTO.ETH, account6Balance1);
		AccountDTO account6 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account6);
		assertNotEquals(account6, account1);

		// Account 7 - frozen changed.
		BalanceDTO account7Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("11"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account7Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account7Balance2);
		balances.put(CurrencyDTO.ETH, account7Balance1);
		AccountDTO account7 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account7);
		assertNotEquals(account7, account1);

		// Account 8 - loaned changed.
		BalanceDTO account8Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("11"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account8Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account8Balance2);
		balances.put(CurrencyDTO.ETH, account8Balance1);
		AccountDTO account8 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account8);
		assertNotEquals(account8, account1);

		// Account 9 - total changed.
		BalanceDTO account9Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("11"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("11"))
				.withdrawing(new BigDecimal("1"))
				.create();
		BalanceDTO account9Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account9Balance1);
		balances.put(CurrencyDTO.ETH, account9Balance2);
		AccountDTO account9 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account9);
		assertNotEquals(account9, account1);

		// Account 10 - withdrawing changed.
		BalanceDTO account10Balance1 = BalanceDTO.builder()
				.available(new BigDecimal("1"))
				.borrowed(new BigDecimal("1"))
				.currency(CurrencyDTO.BTC)
				.depositing(new BigDecimal("1"))
				.frozen(new BigDecimal("1"))
				.loaned(new BigDecimal("1"))
				.total(new BigDecimal("1"))
				.withdrawing(new BigDecimal("11"))
				.create();
		BalanceDTO account10Balance2 = BalanceDTO.builder()
				.available(new BigDecimal("2"))
				.borrowed(new BigDecimal("2"))
				.currency(CurrencyDTO.ETH)
				.depositing(new BigDecimal("2"))
				.frozen(new BigDecimal("2"))
				.loaned(new BigDecimal("2"))
				.total(new BigDecimal("2"))
				.withdrawing(new BigDecimal("2"))
				.create();
		balances.put(CurrencyDTO.BTC, account10Balance1);
		balances.put(CurrencyDTO.ETH, account10Balance2);
		AccountDTO account10 = AccountDTO.builder().balances(balances).create();
		balances.clear();
		assertNotEquals(account1, account10);
		assertNotEquals(account10, account1);
	}

}
