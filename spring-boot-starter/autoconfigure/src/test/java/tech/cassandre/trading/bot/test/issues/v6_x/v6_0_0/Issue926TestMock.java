package tech.cassandre.trading.bot.test.issues.v6_x.v6_0_0;

import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.util.Collections;

import static java.math.BigDecimal.ZERO;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
@SuppressWarnings("unused")
public class Issue926TestMock extends BaseMock {

    @Override
    public AccountService getXChangeAccountServiceMock() throws IOException {
        final AccountService mockAccountService = mock(AccountService.class);
        // Returns an account with null accountId.
        given(mockAccountService.getAccountInfo()).willReturn(
                new AccountInfo(
                        new Wallet(null,
                                null,
                                Collections.emptySet(),
                                Collections.emptySet(),
                                ZERO,
                                ZERO))
        );
        return mockAccountService;
    }

}
