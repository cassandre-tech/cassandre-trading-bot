package tech.cassandre.trading.bot.test.domain;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.domain.ExchangeAccount;
import tech.cassandre.trading.bot.repository.ExchangeAccountRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_NAME_DEFAULT_VALUE;

@SpringBootTest
@DisplayName("Domain - ExchangeAccount - After restart")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class ExchangeAccountExistingTest {

    @Autowired
    private ExchangeAccountRepository exchangeAccountRepository;

    @Test
    @CaseId(27)
    @DisplayName("Check only one exchange account is in database when data already exists")
    public void checkExchangeAccountFromDatabase() {
        assertEquals(1, exchangeAccountRepository.count());
        final Optional<ExchangeAccount> exchangeAccount = exchangeAccountRepository.findById(1L);
        assertTrue(exchangeAccount.isPresent());
        assertEquals(PARAMETER_NAME_DEFAULT_VALUE, exchangeAccount.get().getExchange());
        assertEquals("cassandre.crypto.bot@gmail.com", exchangeAccount.get().getAccount());
    }

}
