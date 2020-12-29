package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@DisplayName("DTO - OrderDTO")
public class OrderDTOTest {

	@Test
	@SuppressWarnings({ "checkstyle:MagicNumber", "checkstyle:MethodLength" })
	@DisplayName("Check equalTo")
	public void checkEqualToForOrder() {
		// Currency pairs.
		final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);
		final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);

		// Order 1.
		OrderDTO order01 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();

		// Order 2 - same as order 1.
		OrderDTO order02 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertEquals(order01, order02);
		assertEquals(order02, order01);

		// Order 3 - type changed.
		OrderDTO order03 = OrderDTO.builder()
				.type(BID)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order03);
		assertNotEquals(order03, order01);

		// Order 4 - original amount changed.
		OrderDTO order04 = OrderDTO.builder()
				.type(BID)
				.amount(new CurrencyAmountDTO("9", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order04);
		assertNotEquals(order04, order01);

		// Order 5 - currency pair changed.
		OrderDTO order05 = OrderDTO.builder()
				.type(BID)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp2)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order05);
		assertNotEquals(order05, order01);

		// Order 6 - id changed.
		OrderDTO order06 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000002")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order06);
		assertNotEquals(order06, order01);

		// Order 7 - user reference changed.
		OrderDTO order07 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_2")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order07);
		assertNotEquals(order07, order01);

		// Order 8 - timestamp changed.
		OrderDTO order08 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 2, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order08);
		assertNotEquals(order08, order01);

		// Order 9 - status changed.
		OrderDTO order09 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.PENDING_NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order09);
		assertNotEquals(order09, order01);

		// Order 10 - cumulative amount changed.
		OrderDTO order10 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("9", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order10);
		assertNotEquals(order10, order01);

		// Order 11 - average price changed.
		OrderDTO order11 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("9", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order11);
		assertNotEquals(order11, order01);

		// Order 12 - leverage changed.
		OrderDTO order12 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage2")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order12);
		assertNotEquals(order12, order01);

		// Order 13 - leverage changed.
		OrderDTO order13 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage2")
				.limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order13);
		assertNotEquals(order13, order01);

		// Order 14 - limit price changed.
		OrderDTO order14 = OrderDTO.builder()
				.type(ASK)
				.amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
				.currencyPair(cp1)
				.orderId("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(NEW)
				.cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
				.averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
				.leverage("leverage1")
				.limitPrice(new CurrencyAmountDTO("9", cp1.getQuoteCurrency()))
				.build();
		assertNotEquals(order01, order14);
		assertNotEquals(order14, order01);

		// Tests for null objects.
		OrderDTO order15 = OrderDTO.builder().build();
		OrderDTO order16 = OrderDTO.builder().build();
		assertEquals(order15, order16);
		assertEquals(order16, order15);
	}

}
