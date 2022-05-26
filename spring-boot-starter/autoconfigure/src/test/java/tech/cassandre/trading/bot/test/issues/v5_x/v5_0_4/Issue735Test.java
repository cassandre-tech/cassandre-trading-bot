package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_4;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 735")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue735Test {

    @Test
    @DisplayName("CurrencyDTO serialization failure")
    public void testCurrencySerialization() {
        XmlMapper mapper = new XmlMapper();
        try {
            mapper.writeValueAsString(BTC);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Serialization fail " + e.getMessage());
        }
    }

}
