package tech.cassandre.trading.bot.tmp.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("DTO - OrderDTO")
@Disabled
public class OrderDTOTest {

	@Test
	@Tag("notReviewed")
	@SuppressWarnings({ "checkstyle:MagicNumber", "checkstyle:MethodLength" })
	@DisplayName("Check equalTo")
	public void checkEqualToForOrder() {
		// Currency pairs.
		final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
		final CurrencyPairDTO cp2 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT);

		// Order 1.
		OrderDTO order01 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();

		// Order 2 - same as order 1.
		OrderDTO order02 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertEquals(order01, order02);
		assertEquals(order02, order01);

		// Order 3 - type changed.
		OrderDTO order03 = OrderDTO.builder()
				.type(OrderTypeDTO.BID)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order03);
		assertNotEquals(order03, order01);

		// Order 4 - original amount changed.
		OrderDTO order04 = OrderDTO.builder()
				.type(OrderTypeDTO.BID)
				.originalAmount(new BigDecimal(9))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order04);
		assertNotEquals(order04, order01);

		// Order 5 - currency pair changed.
		OrderDTO order05 = OrderDTO.builder()
				.type(OrderTypeDTO.BID)
				.originalAmount(new BigDecimal("1"))
				.currencyPair(cp2)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order05);
		assertNotEquals(order05, order01);

		// Order 6 - id changed.
		OrderDTO order06 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000002")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order06);
		assertNotEquals(order06, order01);

		// Order 7 - user reference changed.
		OrderDTO order07 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_2")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order07);
		assertNotEquals(order07, order01);

		// Order 8 - timestamp changed.
		OrderDTO order08 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 2, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order08);
		assertNotEquals(order08, order01);

		// Order 9 - status changed.
		OrderDTO order09 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.PENDING_NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order09);
		assertNotEquals(order09, order01);

		// Order 10 - cumulative amount changed.
		OrderDTO order10 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(9))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order10);
		assertNotEquals(order10, order01);

		// Order 11 - average price changed.
		OrderDTO order11 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(9))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order11);
		assertNotEquals(order11, order01);

		// Order 12 - fee changed.
		OrderDTO order12 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(9))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order12);
		assertNotEquals(order12, order01);

		// Order 13 - leverage changed.
		OrderDTO order13 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage2")
				.limitPrice(new BigDecimal(5))
				.create();
		assertNotEquals(order01, order13);
		assertNotEquals(order13, order01);

		// Order 14 - limit price changed.
		OrderDTO order14 = OrderDTO.builder()
				.type(OrderTypeDTO.ASK)
				.originalAmount(new BigDecimal(1))
				.currencyPair(cp1)
				.id("000001")
				.userReference("MY_REF_1")
				.timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
				.status(OrderStatusDTO.NEW)
				.cumulativeAmount(new BigDecimal(2))
				.averagePrice(new BigDecimal(3))
				.fee(new BigDecimal(4))
				.leverage("leverage1")
				.limitPrice(new BigDecimal(9))
				.create();
		assertNotEquals(order01, order14);
		assertNotEquals(order14, order01);

		// Tests for null objects.
		OrderDTO order15 = OrderDTO.builder().create();
		OrderDTO order16 = OrderDTO.builder().create();
		assertEquals(order15, order16);
		assertEquals(order16, order15);
	}

}
