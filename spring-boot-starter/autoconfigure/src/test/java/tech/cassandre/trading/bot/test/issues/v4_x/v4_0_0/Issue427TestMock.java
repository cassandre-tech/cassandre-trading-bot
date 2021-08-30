package tech.cassandre.trading.bot.test.issues.v4_x.v4_0_0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SuppressWarnings("unchecked")
@TestConfiguration
public class Issue427TestMock extends BaseTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(applicationContext, marketService());
    }

    @Bean
    @Primary
    public OrderFlux orderFlux() {
        return new OrderFlux(orderRepository, tradeService());
    }

    @Bean
    @Primary
    public TradeFlux tradeFlux() {
        return new TradeFlux(orderRepository, tradeRepository, tradeService());
    }

    @Bean
    @Primary
    public MarketService marketService() {
        // Creates the mock.
        MarketService marketService = mock(MarketService.class);

        // Replies for ETH / BTC.
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);
        given(marketService
                .getTicker(cp1))
                .willReturn(
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(1)).last(new BigDecimal("0.2")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(2)).last(new BigDecimal("0.3")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(3)).last(new BigDecimal("0.4")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp1).timestamp(createZonedDateTime(4)).last(new BigDecimal("0.4")).build())
                );
        // Replies for ETH / USDT.
        final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);
        given(marketService
                .getTicker(cp2))
                .willReturn(
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(5)).last(new BigDecimal("0.3")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(6)).last(new BigDecimal("0.3")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(7)).last(new BigDecimal("0.6")).build()),
                        Optional.of(TickerDTO.builder().currencyPair(cp2).timestamp(createZonedDateTime(8)).last(new BigDecimal("0.1")).build())
                );
        return marketService;
    }

    @Bean
    @Primary
    public TradeService tradeService() {
        TradeService service = mock(TradeService.class);

        LinkedHashSet<OrderDTO> orders = new LinkedHashSet<>();
        orders.add(OrderDTO.builder()
                .orderId("ORDER_000001")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", ETH_BTC.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", ETH_BTC.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", ETH_BTC.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .build());

        orders.add(OrderDTO.builder()
                .orderId("ORDER_000002")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", ETH_BTC.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", ETH_BTC.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", ETH_BTC.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
                .build());
        // getOrders() replies.
        given(service.getOrders()).willReturn(orders);

        return service;
    }

}
