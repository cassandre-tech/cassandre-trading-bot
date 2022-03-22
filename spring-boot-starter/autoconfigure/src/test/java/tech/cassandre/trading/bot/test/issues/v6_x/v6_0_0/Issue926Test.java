package tech.cassandre.trading.bot.test.issues.v6_x.v6_0_0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 926")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(Issue926TestMock.class)
public class Issue926Test {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("AccountId with null value")
    public void accountIdWithNullValue() {
        // We wait for initialized to be sure all data have been imported.
        await().untilAsserted(() -> assertTrue(strategy.isInitialized()));
        final Optional<AccountDTO> defaultAccount = strategy.getTradeAccount();
        assertTrue(defaultAccount.isPresent());
        assertNotNull(defaultAccount.get().getAccountId());
    }

}
