package tech.cassandre.trading.bot.test.domain;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DisplayName("Domain - ExchangeAccount")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class ExchangeAccountTest {
}
