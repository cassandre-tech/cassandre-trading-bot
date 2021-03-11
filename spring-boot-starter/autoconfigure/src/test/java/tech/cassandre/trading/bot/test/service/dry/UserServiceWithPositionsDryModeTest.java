package tech.cassandre.trading.bot.test.service.dry;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.exception.PositionException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Service - Dry - User service with positions")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UserServiceWithPositionsDryModeTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private AccountFlux accountFlux;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @CaseId(100)
    @DisplayName("Check user balances updates with positions")
    public void checkUserBalancesUpdatesWithPosition() throws InterruptedException {
        final PositionRulesDTO rules = PositionRulesDTO.builder()
                .stopGainPercentage(100f)
                .build();

        // This is what we have in our trade account.
        // BTC  0.99962937
        // USDT 1000
        // ETH  10
        accountFlux.update();
        await().until(() -> !strategy.getAccountsUpdatesReceived().isEmpty());
        Map<CurrencyDTO, BalanceDTO> balances = getBalances();
        assertEquals(0, new BigDecimal("0.99962937").compareTo(balances.get(BTC).getAvailable()));
        assertEquals(0, new BigDecimal("1000").compareTo(balances.get(USDT).getAvailable()));
        assertEquals(0, new BigDecimal("10").compareTo(balances.get(ETH).getAvailable()));
        assertEquals(0, strategy.getAmountsLockedByPosition().size());

        // =============================================================================================================
        // We set the currency pair prices.
        // CP1 : ETH/BTC    - Price is 0.03 meaning 1 ETH costs 0.03 BTC.
        // CP2 : ETH/USDT   - Price is 1 500 meaning 1 ETH costs 1500 USDT.
        // CP3 : BTC/USDT   - Price is 50 000 meaning 1 BTC costs 50000 USDT.
        // CP4 : KCS/USDT   - Price is 4 meaning 1 KCS costs 4 USDT.
        // CP5 : BTC/ETH    - Price is 50 meaning 1 BTC costs 50 ETH.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.03")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp2).last(new BigDecimal("1500")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp3).last(new BigDecimal("50000")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp4).last(new BigDecimal("4")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp5).last(new BigDecimal("50")).build());
        await().untilAsserted(() -> assertEquals(5, strategy.getTickersUpdateReceived().size()));

        // =============================================================================================================
        // We check what we can do thx to canBuy() methods.
        // With our BTC, we should be able to buy 10 ETH but not 100 ETH.
        assertTrue(strategy.canBuy(cp1, new BigDecimal("10")));
        assertFalse(strategy.canBuy(cp1, new BigDecimal("100")));
        // With our USDT, we should be able to buy 0.5 ETH but not 1 ETH.
        assertTrue(strategy.canBuy(cp2, new BigDecimal("0.5")));
        assertFalse(strategy.canBuy(cp2, new BigDecimal("1")));
        // With our USDT, we should be able to buy 0.01 BTC but not 0.1 BTC.
        assertTrue(strategy.canBuy(cp3, new BigDecimal("0.01")));
        assertFalse(strategy.canBuy(cp3, new BigDecimal("0.1")));
        // With our USDT, we should be able to buy 200 KCS but not 300 KCS.
        assertTrue(strategy.canBuy(cp4, new BigDecimal("200")));
        assertFalse(strategy.canBuy(cp4, new BigDecimal("300")));
        // With our ETH, we should be able to buy 0.2 BTC but not 0.25 BTC.
        assertTrue(strategy.canBuy(cp5, new BigDecimal("0.2")));
        assertFalse(strategy.canBuy(cp5, new BigDecimal("0.25")));

        // =============================================================================================================
        // We check what we can do thx to canSell() methods.
        // We can sell 0.99962937 BTC but not 1 BTC.
        assertTrue(strategy.canSell(BTC, new BigDecimal("0.99962937")));
        assertFalse(strategy.canSell(BTC, new BigDecimal("1")));
        // We can sell 1000 USDT.
        assertTrue(strategy.canSell(USDT, new BigDecimal("1000")));
        assertFalse(strategy.canSell(USDT, new BigDecimal("1001")));
        // We can sell 10 ETH.
        assertTrue(strategy.canSell(ETH, new BigDecimal("10")));
        assertFalse(strategy.canSell(ETH, new BigDecimal("11")));

        // =============================================================================================================
        // We create a first long position on ETH/USDT.
        // CP2 : ETH/USDT - 1 ETH costs 1500 USDT - We buy 0.5 ETH and it will cost 750 USDT.
        // Before.              After.
        // 0.99962937 BTC   =>  0.99962937 BTC.
        // 1000 USDT        =>  250 USDT.
        // 10 ETH           =>  10.5 ETH (0.5 locked in positions).
        final PositionCreationResultDTO position1 = strategy.createLongPosition(cp2, new BigDecimal("0.5"), rules);
        long position1Id = position1.getPosition().getPositionId();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        balances = getBalances();
        assertEquals(0, new BigDecimal("0.99962937").compareTo(balances.get(BTC).getAvailable()));
        assertEquals(0, new BigDecimal("250").compareTo(balances.get(USDT).getAvailable()));
        assertEquals(0, new BigDecimal("10.5").compareTo(balances.get(ETH).getAvailable()));

        // We check that the position locked amount is well stored.
        assertEquals(1, strategy.getAmountsLockedByPosition().size());
        final CurrencyAmountDTO currencyAmountForPosition1 = strategy.getAmountsLockedByPosition().get(position1Id);
        assertNotNull(currencyAmountForPosition1);
        assertEquals(0, new BigDecimal("0.5").compareTo(currencyAmountForPosition1.getValue()));
        assertEquals(ETH, currencyAmountForPosition1.getCurrency());

        // As we now have 10.5 ETH and 0.5 locked in positions, we should not be able to sell 10 ETH but not 10.5 ETH.
        assertEquals(0, new BigDecimal("10.5").compareTo(balances.get(ETH).getAvailable()));
        assertEquals(0, new BigDecimal("0.5").compareTo(strategy.getAmountsLockedByCurrency(ETH)));
        assertTrue(strategy.canSell(ETH, new BigDecimal("10")));
        assertFalse(strategy.canSell(ETH, new BigDecimal("10.5")));

        // As we now have 10.5 ETH and 0.5 locked in positions, we should still be able to buy 0.2 but not more.
        assertTrue(strategy.canBuy(cp5, new BigDecimal("0.2")));
        assertFalse(strategy.canBuy(cp5, new BigDecimal("0.20001")));

        // Price update for CP2.
        // CP2 : ETH/USDT - 1 ETH costs 10 USDT - We buy 10 ETH and it will cost 100 USDT.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp2).last(new BigDecimal("10")).build());
        await().untilAsserted(() -> assertEquals(6, strategy.getTickersUpdateReceived().size()));

        // =============================================================================================================
        // We create a second long position on ETH/USDT.
        // CP2 : ETH/USDT - 1 ETH costs 10 USDT - We buy 10 ETH and it will cost 100 USDT.
        // Before.              After.
        // 0.99962937 BTC   =>  0.99962937 BTC.
        // 250 USDT         =>  150 USDT.
        // 10.5 ETH         =>  20.5 ETH (10.5 locked in positions).
        final PositionCreationResultDTO position2 = strategy.createLongPosition(cp2, new BigDecimal("10"), rules);
        long position2Id = position2.getPosition().getPositionId();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position2Id).getStatus()));
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        balances = getBalances();
        assertEquals(0, new BigDecimal("0.99962937").compareTo(balances.get(BTC).getAvailable()));
        assertEquals(0, new BigDecimal("150").compareTo(balances.get(USDT).getAvailable()));
        assertEquals(0, new BigDecimal("20.5").compareTo(balances.get(ETH).getAvailable()));

        // We check that the position locked amount is well stored.
        assertEquals(2, strategy.getAmountsLockedByPosition().size());
        assertEquals(0, new BigDecimal("10.5").compareTo(strategy.getAmountsLockedByCurrency(ETH)));
        final CurrencyAmountDTO currencyAmountForPosition2 = strategy.getAmountsLockedByPosition().get(position2Id);
        assertNotNull(currencyAmountForPosition2);
        assertEquals(0, new BigDecimal("10").compareTo(currencyAmountForPosition2.getValue()));
        assertEquals(ETH, currencyAmountForPosition2.getCurrency());

        // As we now have 20.5 ETH and 10.5 locked in positions, we should not be able to sell 10 ETH but not 10.1 ETH.
        assertEquals(0, new BigDecimal("20.5").compareTo(balances.get(ETH).getAvailable()));
        assertTrue(strategy.canSell(ETH, new BigDecimal("10")));
        assertFalse(strategy.canSell(ETH, new BigDecimal("10.1")));
        assertFalse(strategy.canSell(ETH, new BigDecimal("20.5")));

        // As we now have 20.5 ETH and 10.5 locked in positions, we should still be able to buy 0.2 but not more.
        assertTrue(strategy.canBuy(cp5, new BigDecimal("0.2")));
        assertFalse(strategy.canBuy(cp5, new BigDecimal("0.20001")));

        // =============================================================================================================
        // We create a third long position on KCS/USDT.
        // CP4 : KCS/USDT - 1 KCS costs 4 USDT - We buy 20 KCS and it will costs 80 USDT.
        // Before.              After.
        // 0.99962937 BTC   =>  0.99962937 BTC.
        // 150 USDT         =>  70 USDT.
        // 20.5 ETH ETH     =>  20.5 ETH (10.5 locked in positions).
        // 0 KCS            =>  20 KCS (20 lock in positions).
        final PositionCreationResultDTO position3 = strategy.createLongPosition(cp4, new BigDecimal("20"), rules);
        long position3Id = position3.getPosition().getPositionId();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position3Id).getStatus()));
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        balances = getBalances();
        assertEquals(0, new BigDecimal("0.99962937").compareTo(balances.get(BTC).getAvailable()));
        assertEquals(0, new BigDecimal("70").compareTo(balances.get(USDT).getAvailable()));
        assertEquals(0, new BigDecimal("20.5").compareTo(balances.get(ETH).getAvailable()));
        assertEquals(0, new BigDecimal("20").compareTo(balances.get(KCS).getAvailable()));

        // We check that the position locked amount is well stored.
        assertEquals(3, strategy.getAmountsLockedByPosition().size());
        assertEquals(0, new BigDecimal("10.5").compareTo(strategy.getAmountsLockedByCurrency(ETH)));
        assertEquals(0, new BigDecimal("20").compareTo(strategy.getAmountsLockedByCurrency(KCS)));
        final CurrencyAmountDTO currencyAmountForPosition3 = strategy.getAmountsLockedByPosition().get(position3Id);
        assertNotNull(currencyAmountForPosition3);
        assertEquals(0, new BigDecimal("20").compareTo(currencyAmountForPosition3.getValue()));
        assertEquals(KCS, currencyAmountForPosition3.getCurrency());

        // As we now have 20 KCS locked in positions, we should not be able to sell them.
        assertEquals(0, new BigDecimal("20").compareTo(balances.get(KCS).getAvailable()));
        assertFalse(strategy.canSell(KCS, new BigDecimal("10")));

        // =============================================================================================================
        // We create a first short position on ETH/USDT.
        // CP2 : ETH/USDT - 1 ETH costs 10 USDT - We sell 1 ETH and it will give us 10 USDT.
        // Before.              After.
        // 0.99962937 BTC   =>  0.99962937 BTC
        // 70 USDT          =>  80 USDT (10 locked in positions).
        // 20.5 ETH         =>  19.5 (10.5 locked in positions).
        // 0 KCS            =>  20 KCS (20 lock in positions).
        final PositionCreationResultDTO position4 = strategy.createShortPosition(cp2, new BigDecimal("1"), rules);
        long position4Id = position4.getPosition().getPositionId();
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position4Id).getStatus()));
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        balances = getBalances();
        assertEquals(0, new BigDecimal("0.99962937").compareTo(balances.get(BTC).getAvailable()));
        assertEquals(0, new BigDecimal("80").compareTo(balances.get(USDT).getAvailable()));
        assertEquals(0, new BigDecimal("19.5").compareTo(balances.get(ETH).getAvailable()));
        assertEquals(0, new BigDecimal("20").compareTo(balances.get(KCS).getAvailable()));

        // We check that the position locked amount is well stored.
        assertEquals(4, strategy.getAmountsLockedByPosition().size());
        assertEquals(0, new BigDecimal("10.5").compareTo(strategy.getAmountsLockedByCurrency(ETH)));
        assertEquals(0, new BigDecimal("20").compareTo(strategy.getAmountsLockedByCurrency(KCS)));
        final CurrencyAmountDTO currencyAmountForPosition4 = strategy.getAmountsLockedByPosition().get(position4Id);
        assertNotNull(currencyAmountForPosition4);
        assertEquals(0, new BigDecimal("10").compareTo(currencyAmountForPosition4.getValue()));
        assertEquals(USDT, currencyAmountForPosition4.getCurrency());

        // As we now have 10 USDT locked in positions, we should not be able sell 80 but 70.
        assertEquals(0, new BigDecimal("80").compareTo(balances.get(USDT).getAvailable()));
        assertFalse(strategy.canSell(USDT, new BigDecimal("80")));
        assertTrue(strategy.canSell(USDT, new BigDecimal("70")));

        // =============================================================================================================
        // We will now close position 2 on ETH/USDT (long) & position 4 on ETH/USDT (short).
        // They both have a 100% stop gain rule.
        // Actual price
        // CP2 : ETH/USDT - 1 ETH costs 10 USDT - We buy 10 ETH and it will cost 100 USDT.

        // For the position long 2, we send :
        // CP2 : ETH/USDT - 1 ETH costs 100 USDT - We sell 10 ETH and it will give us 1000 USDT.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp2).last(new BigDecimal("100")).build());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position2Id).getStatus()));
        // For the position short 4, we send :
        // CP2 : ETH/USDT - 1 ETH costs 1 USDT - We buy 1 ETH and it will costs us 8 USDT.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp2).last(new BigDecimal("1")).build());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position4Id).getStatus()));

        // We should now have the following amounts
        // Before.              After.
        // 0.99962937 BTC       =>  0.99962937 BTC.
        // 80 USDT              =>  80 + 1 000 (position 2) - 1 (position 4)
        // 19.5 ETH             =>  19.5 - 10 (position 2) + 1 (position 4)
        // 0 KCS                =>  20 KCS (20 lock in positions).
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        balances = getBalances();
        assertEquals(0, new BigDecimal("0.99962937").compareTo(balances.get(BTC).getAvailable()));
        assertEquals(0, new BigDecimal("1079").compareTo(balances.get(USDT).getAvailable()));
        assertEquals(0, new BigDecimal("10.5").compareTo(balances.get(ETH).getAvailable()));
        assertEquals(0, new BigDecimal("20").compareTo(balances.get(KCS).getAvailable()));

        // We check that the position don't lock amount anymore
        // Only 0.5 ETH locked because of position 1.
        // 20 KCS because of position 3.
        assertEquals(2, strategy.getAmountsLockedByPosition().size());
        assertEquals(0, new BigDecimal("0.5").compareTo(strategy.getAmountsLockedByCurrency(ETH)));
        assertEquals(0, new BigDecimal("20").compareTo(strategy.getAmountsLockedByCurrency(KCS)));

        // As we now have 1079 ETH and 10.5 locked in positions, we should be able to buy 0.02 BTC but not 0.03 BTC.
        assertTrue(strategy.canBuy(cp3, new BigDecimal("0.02")));
        assertFalse(strategy.canBuy(cp3, new BigDecimal("0.03")));
    }

    /**
     * Retrieve position from database.
     *
     * @param id position id
     * @return position
     */
    private PositionDTO getPositionDTO(final long id) {
        final Optional<PositionDTO> p = positionService.getPositionById(id);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new PositionException("Position not found : " + id);
        }
    }

    /**
     * Returns the updated balances of the trade account.
     *
     * @return balances
     */
    private Map<CurrencyDTO, BalanceDTO> getBalances() {
        final Optional<UserDTO> user = userService.getUser();
        return user.map(userDTO -> userDTO.getAccounts().get("trade").getBalances()).orElse(null);
    }

}
