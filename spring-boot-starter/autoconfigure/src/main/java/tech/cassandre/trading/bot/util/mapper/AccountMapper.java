package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;

import java.util.Map;

/**
 * Account mapper.
 */
@Mapper(uses = CurrencyMapper.class)
public interface AccountMapper {

    // =================================================================================================================
    // XChange to DTO.

    /**
     * Map AccountInfo to UserDTO.
     *
     * @param source AccountInfo
     * @return UserDTO
     */
    @Mapping(source = "username", target = "id")
    @Mapping(source = "wallets", target = "accounts")
    UserDTO mapToUserDTO(AccountInfo source);

    /**
     * Map Wallet to AccountDTO.
     *
     * @param source Wallet
     * @return AccountDTO
     */
    AccountDTO mapToWalletDTO(Wallet source);

    /**
     * Map Balance to BalanceDTO.
     *
     * @param source Balance
     * @return BalanceDTO
     */
    BalanceDTO mapToBalanceDTO(Balance source);

    /**
     * Map balance.
     *
     * @param source map of Currency and Balance
     * @return Map of CurrencyDTO and BalanceDTO
     */
    Map<CurrencyDTO, BalanceDTO> mapToCurrencyDTOAndBalanceDTO(Map<Currency, Balance> source);

}
