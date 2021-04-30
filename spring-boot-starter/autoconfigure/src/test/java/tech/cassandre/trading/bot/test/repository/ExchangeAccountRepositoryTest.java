package tech.cassandre.trading.bot.test.repository;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cassandre.trading.bot.domain.ExchangeAccount;
import tech.cassandre.trading.bot.repository.ExchangeAccountRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_NAME_DEFAULT_VALUE;

@SpringBootTest
@DisplayName("Repository - ExchangeAccount")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@ActiveProfiles("schedule-disabled")
@Testcontainers
public class ExchangeAccountRepositoryTest {

    @Autowired
    private ExchangeAccountRepository exchangeAccountRepository;

    @Test
    @CaseId(61)
    @DisplayName("Check imported data")
    public void checkImportedOrders() {
        // Testing a non existing exchange account.
        Optional<ExchangeAccount> ea = exchangeAccountRepository.findByExchangeAndAccount("temp", "temp");
        assertFalse(ea.isPresent());
        // Testing a non existing exchange account.
        ea = exchangeAccountRepository.findByExchangeAndAccount(PARAMETER_NAME_DEFAULT_VALUE, "cassandre.crypto.bot@gmail.com");
        assertTrue(ea.isPresent());
    }

}
