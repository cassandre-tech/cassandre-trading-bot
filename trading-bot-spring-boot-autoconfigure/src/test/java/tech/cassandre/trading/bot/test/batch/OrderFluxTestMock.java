package tech.cassandre.trading.bot.test.batch;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Flux and services mocks.
 */
@TestConfiguration
public class OrderFluxTestMock {

    /**
     * Replace ticker flux by mock.
     *
     * @return mock
     */
    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(marketService());
    }

    /**
     * Replace account flux by mock.
     *
     * @return mock
     */
    @Bean
    @Primary
    public AccountFlux accountFlux() {
        return new AccountFlux(userService());
    }

    /**
     * Replace order flux by mock.
     *
     * @return mock
     */
    @Bean
    @Primary
    public OrderFlux orderFlux() {
        return new OrderFlux(tradeService());
    }

    /**
     * UserService mock.
     *
     * @return mocked service
     */
    @Bean
    @Primary
    public UserService userService() {
        UserService service = mock(UserService.class);
        given(service.getUser()).willReturn(Optional.empty());
        return service;
    }

    /**
     * MarketService mock.
     *
     * @return mocked service
     */
    @Bean
    @Primary
    public MarketService marketService() {
        MarketService service = mock(MarketService.class);
        given(service.getTicker(any())).willReturn(Optional.empty());
        return service;
    }

    /**
     * TradeService mock.
     *
     * @return mocked service
     */
    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public TradeService tradeService() {
        // Creates the mock.
        TradeService tradeService = mock(TradeService.class);
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);

        // =========================================================================================================
        // First reply : 3 orders.

        // Order 000001.
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

        // Order 000002.
        OrderDTO order02 = OrderDTO.builder()
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

        // Order 000003.
        OrderDTO order03 = OrderDTO.builder()
                .type(OrderTypeDTO.ASK)
                .originalAmount(new BigDecimal(1))
                .currencyPair(cp1)
                .id("000003")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(OrderStatusDTO.NEW)
                .cumulativeAmount(new BigDecimal(2))
                .averagePrice(new BigDecimal(3))
                .fee(new BigDecimal(4))
                .leverage("leverage1")
                .limitPrice(new BigDecimal(5))
                .create();

        Set<OrderDTO> reply01 = new LinkedHashSet<>();
        reply01.add(order01);
        reply01.add(order02);
        reply01.add(order03);

        // =========================================================================================================
        // Second reply.
        // Order 000003 : the original amount changed.
        // Order 000004 : new order.

        // Order 000001.
        OrderDTO order04 = OrderDTO.builder()
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

        // Order 000002.
        OrderDTO order05 = OrderDTO.builder()
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

        // Order 000003 : the original amount changed.
        OrderDTO order06 = OrderDTO.builder()
                .type(OrderTypeDTO.ASK)
                .originalAmount(new BigDecimal(2))
                .currencyPair(cp1)
                .id("000003")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(OrderStatusDTO.NEW)
                .cumulativeAmount(new BigDecimal(2))
                .averagePrice(new BigDecimal(3))
                .fee(new BigDecimal(4))
                .leverage("leverage1")
                .limitPrice(new BigDecimal(5))
                .create();

        // Order 000004 : new order.
        OrderDTO order07 = OrderDTO.builder()
                .type(OrderTypeDTO.ASK)
                .originalAmount(new BigDecimal(1))
                .currencyPair(cp1)
                .id("000004")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(OrderStatusDTO.NEW)
                .cumulativeAmount(new BigDecimal(2))
                .averagePrice(new BigDecimal(3))
                .fee(new BigDecimal(4))
                .leverage("leverage1")
                .limitPrice(new BigDecimal(5))
                .create();

        Set<OrderDTO> reply02 = new LinkedHashSet<>();
        reply02.add(order04);
        reply02.add(order05);
        reply02.add(order06);
        reply02.add(order07);

        // =========================================================================================================
        // Second reply.
        // Order 000002 : average prince changed.
        // Order 000004 : fee changed.

        // Order 000001.
        OrderDTO order08 = OrderDTO.builder()
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

        // Order 000002 : average price changed.
        OrderDTO order09 = OrderDTO.builder()
                .type(OrderTypeDTO.ASK)
                .originalAmount(new BigDecimal(1))
                .currencyPair(cp1)
                .id("000002")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(OrderStatusDTO.NEW)
                .cumulativeAmount(new BigDecimal(2))
                .averagePrice(new BigDecimal(1))
                .fee(new BigDecimal(4))
                .leverage("leverage1")
                .limitPrice(new BigDecimal(5))
                .create();

        // Order 000003.
        OrderDTO order10 = OrderDTO.builder()
                .type(OrderTypeDTO.ASK)
                .originalAmount(new BigDecimal(2))
                .currencyPair(cp1)
                .id("000003")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(OrderStatusDTO.NEW)
                .cumulativeAmount(new BigDecimal(2))
                .averagePrice(new BigDecimal(3))
                .fee(new BigDecimal(4))
                .leverage("leverage1")
                .limitPrice(new BigDecimal(5))
                .create();

        // Order 000004 : fee changed.
        OrderDTO order11 = OrderDTO.builder()
                .type(OrderTypeDTO.ASK)
                .originalAmount(new BigDecimal(1))
                .currencyPair(cp1)
                .id("000004")
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .status(OrderStatusDTO.NEW)
                .cumulativeAmount(new BigDecimal(2))
                .averagePrice(new BigDecimal(3))
                .fee(new BigDecimal(1))
                .leverage("leverage1")
                .limitPrice(new BigDecimal(5))
                .create();

        Set<OrderDTO> reply03 = new LinkedHashSet<>();
        reply03.add(order08);
        reply03.add(order09);
        reply03.add(order10);
        reply03.add(order11);

        // Creating the mock.
        given(tradeService.getOpenOrders())
                .willReturn(reply01,
                        new LinkedHashSet<>(),
                        reply02,
                        reply03);
        return tradeService;
    }

}
