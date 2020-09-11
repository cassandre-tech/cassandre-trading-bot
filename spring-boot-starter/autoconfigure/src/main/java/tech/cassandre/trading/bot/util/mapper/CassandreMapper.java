package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.Map;

/**
 * Cassandre mapper.
 */
@Mapper
public interface CassandreMapper {

    /**
     * Map CurrencyPair to CurrencyPairDTO.
     *
     * @param source CurrencyPair
     * @return CurrencyPairDTO
     */
    @Mapping(source = "base", target = "baseCurrency")
    @Mapping(source = "counter", target = "quoteCurrency")
    CurrencyPairDTO mapToCurrencyPairDTO(CurrencyPair source);

    /**
     * Map Currency to CurrencyDTO.
     *
     * @param source Currency
     * @return CurrencyDTO
     */
    @Mapping(source = "currencyCode", target = "code")
    CurrencyDTO mapToCurrencyDTO(Currency source);

    /**
     * Map AccountInfo to AccountDTO.
     *
     * @param source AccountInfo
     * @return AccountDTO
     */
    @Mapping(source = "wallets", target = "accounts")
    UserDTO mapToUserDTO(AccountInfo source);

    /**
     * Map Wallet to WalletDTO.
     *
     * @param source Wallet
     * @return WalletDTO
     */
    AccountDTO mapToWalletDTO(Wallet source);

    /**
     * Map balance.
     *
     * @param source map of Currency and Balance
     * @return Map of CurrencyDTO and BalanceDTO
     */
    Map<CurrencyDTO, BalanceDTO> mapToCurrencyDTOAndBalanceDTO(Map<Currency, Balance> source);

    /**
     * Map Balance to BalanceDTO.
     *
     * @param source Balance
     * @return BalanceDTO
     */
    BalanceDTO mapToBalanceDTO(Balance source);

    /**
     * Map Ticker to TickerDTO.
     *
     * @param source Ticker
     * @return TickerDTO
     */
    TickerDTO mapToTickerDTO(Ticker source);

    /**
     * Map Order to OrderDTO.
     *
     * @param source LimitOrder
     * @return OrderDTO
     */
    OrderDTO mapToOrderDTO(LimitOrder source);

    /**
     * Map UserTrade to TradeDTO.
     *
     * @param source UserTrade
     * @return TradeDTO
     */
    TradeDTO mapToTradeDTO(UserTrade source);

    /**
     * Map to OrderTypeDTO.
     *
     * @param source XChange order type
     * @return OrderTypeDTO
     */
    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK"),
            @ValueMapping(source = "EXIT_BID", target = "BID"),
            @ValueMapping(source = "EXIT_ASK", target = "ASK")
    })
    OrderTypeDTO mapToOrderTypeDTO(Order.OrderType source);

    /**
     * Map to OrderTypeDTO.
     *
     * @param source order type
     * @return OrderType
     */
    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK")
    })
    Order.OrderType mapToOrderType(OrderTypeDTO source);

}
