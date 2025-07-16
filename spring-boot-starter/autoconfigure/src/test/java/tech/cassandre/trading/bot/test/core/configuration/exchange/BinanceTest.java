package tech.cassandre.trading.bot.test.core.configuration.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.*;

@SpringBootTest
@DisplayName("Configuration - Exchange - Binance")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRIVER_CLASS_NAME, value = "binance"),
        @Property(key = PARAMETER_EXCHANGE_SANDBOX, value = "true"),
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false"),
        @Property(key = PARAMETER_EXCHANGE_USERNAME, value = "fake-user"),
        @Property(key = PARAMETER_EXCHANGE_KEY, value = "VUdQSkMwfad4OIM3rd3oHyVZ3agVrcuUsG1PT8ai6GlfR4d9M7IxKpKHg9AEKbbA"),
        @Property(key = PARAMETER_EXCHANGE_SECRET, value = "VFkJ0L4drBKZ7Xmf8bILROxYbj86CcOeaZsHxuHMzMjXbl2fS0Ne22dRyxsEVKfo"),
        @Property(key = PARAMETER_EXCHANGE_RATE_ACCOUNT, value = "100"),
        @Property(key = PARAMETER_EXCHANGE_RATE_TICKER, value = "100"),
        @Property(key = PARAMETER_EXCHANGE_RATE_TRADE, value = "100")
})
public class BinanceTest extends BaseTest {

    @Autowired
    private Exchange exchange;

    @Test
    @DisplayName("Check Binance SPOT wallet ")
    public void checkWalletSpot() {

        Object type = exchange.getExchangeSpecification().getExchangeSpecificParametersItem("Exchange_Type");
        assertNotNull(type);
        assertEquals("SPOT", type.toString());
    }
}
