package tech.cassandre.trading.bot.issues.v4_2_2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.DefaultCancelOrderByCurrencyPairAndIdParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 584")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@Import(BaseMock.class)
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue584Test extends BaseTest {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private org.knowm.xchange.service.trade.TradeService mockTradeService;

    @Test
    @DisplayName("Require currency pair")
    public void requireCurrencyPair() throws Exception {
        var cp = new CurrencyPairDTO("BTC", "EUR");
        var orderId = "my-order-1";
        var params = new DefaultCancelOrderByCurrencyPairAndIdParams(dto2xchange(cp), orderId);
        when(mockTradeService.cancelOrder(anyString())).thenThrow(new IOException("Boom!"));
        when(mockTradeService.cancelOrder(params)).thenReturn(true);
        when(mockTradeService.cancelOrder(any(CancelOrderParams.class))).thenReturn(false);

        assertThat(mockTradeService).isNotNull();


        assertThat(tradeService.cancelOrder("some id", cp)).isFalse();
    }

    private CurrencyPair dto2xchange(CurrencyPairDTO source) {
        return new CurrencyPair(source.getBaseCurrency().getCurrencyCode(), source.getQuoteCurrency().getCurrencyCode());
    }
}
