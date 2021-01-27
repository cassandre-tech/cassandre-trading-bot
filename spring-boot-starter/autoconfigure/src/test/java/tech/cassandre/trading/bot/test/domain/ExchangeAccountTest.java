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
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_ACCOUNT;

@SpringBootTest
@DisplayName("Domain - ExchangeAccount - Creation")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_ACCOUNT, value = "100")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class ExchangeAccountTest {

    @Autowired
    private ExchangeAccountRepository exchangeAccountRepository;

    @Test
    @CaseId(26)
    @DisplayName("Check exchange account from database when ")
    public void checkExchangeAccountFromDatabase() {
        assertEquals(1, exchangeAccountRepository.count());
        final Optional<ExchangeAccount> ea = exchangeAccountRepository.findById(1L);
        assertTrue(ea.isPresent());
        assertEquals("kucoin", ea.get().getExchange());
        assertEquals("cassandre.crypto.bot@gmail.com", ea.get().getAccount());

        // Test equals.
        final Optional<ExchangeAccount> eaBis = exchangeAccountRepository.findById(1L);
        assertTrue(eaBis.isPresent());
        assertEquals(ea.get(), eaBis.get());

    }

}
