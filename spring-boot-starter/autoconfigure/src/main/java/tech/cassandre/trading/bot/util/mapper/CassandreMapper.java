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
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
     * Map Strategy to StrategyDTO.
     *
     * @param source Strategy
     * @return StrategyDTO
     */
    StrategyDTO mapToStrategyDTO(Strategy source);

    /**
     * Map StrategyDTO to Strategy.
     *
     * @param source StrategyDTO
     * @return Strategy
     */
    Strategy mapToStrategy(StrategyDTO source);

    /**
     * Map AccountInfo to AccountDTO.
     *
     * @param source AccountInfo
     * @return AccountDTO
     */
    @Mapping(source = "username", target = "id")
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
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapLimitOrderToOrderDTOAmount")
    @Mapping(source = "source", target = "cumulativeAmount", qualifiedByName = "mapLimitOrderToOrderDTOCumulativeAmount")
    @Mapping(source = "source", target = "averagePrice", qualifiedByName = "mapLimitOrderToOrderDTOAveragePrice")
    @Mapping(source = "source", target = "fee", qualifiedByName = "mapLimitOrderToOrderDTOFee")
    @Mapping(source = "source", target = "limitPrice", qualifiedByName = "mapLimitOrderToOrderDTOLimitPrice")
    OrderDTO mapToOrderDTO(LimitOrder source);

    @Named("mapLimitOrderToOrderDTOAmount")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOAmount(LimitOrder source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getOriginalAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getOriginalAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapLimitOrderToOrderDTOCumulativeAmount")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOCumulativeAmount(LimitOrder source) {
        // TODO Add null variables in the constructor.
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getCumulativeAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getCumulativeAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapLimitOrderToOrderDTOAveragePrice")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOAveragePrice(LimitOrder source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getAveragePrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAveragePrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapLimitOrderToOrderDTOFee")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOFee(LimitOrder source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getFee() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getFee(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapLimitOrderToOrderDTOLimitPrice")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOLimitPrice(LimitOrder source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getLimitPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLimitPrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    /**
     * Map UserTrade to TradeDTO.
     *
     * @param source UserTrade
     * @return TradeDTO
     */
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapUserTradeToTradeDTOAmount")
    @Mapping(source = "source", target = "price", qualifiedByName = "mapUserTradeToTradeDTOPrice")
    @Mapping(source = "source", target = "fee", qualifiedByName = "mapUserTradeToTradeDTOFee")
    TradeDTO mapToTradeDTO(UserTrade source);

    @Named("mapUserTradeToTradeDTOAmount")
    default CurrencyAmountDTO mapUserTradeToTradeDTOAmount(UserTrade source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getOriginalAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getOriginalAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapUserTradeToTradeDTOPrice")
    default CurrencyAmountDTO mapUserTradeToTradeDTOPrice(UserTrade source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getPrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapUserTradeToTradeDTOFee")
    default CurrencyAmountDTO mapUserTradeToTradeDTOFee(UserTrade source) {
        if (source.getFeeAmount() != null && source.getFeeCurrency() != null) {
            return new CurrencyAmountDTO(source.getFeeAmount(), mapToCurrencyDTO(source.getFeeCurrency()));
        } else {
            return new CurrencyAmountDTO();
        }
    }

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

    /**
     * Map to Trade.
     *
     * @param source tradeDTO
     * @return trade
     */
    @Mapping(source = "amount.value", target = "amount")
    @Mapping(source = "price.value", target = "price")
    @Mapping(source = "fee.value", target = "feeAmount")
    @Mapping(source = "fee.currency", target = "feeCurrency")
    Trade mapToTrade(TradeDTO source);

    /**
     * mapToCurrencyPair - Used by mapToTrade.
     *
     * @param source CurrencyPairDTO
     * @return String
     */
    default String mapToCurrencyPair(CurrencyPairDTO source) {
        return source.toString();
    }

    /**
     * mapToCurrency - Used by mapToTrade.
     *
     * @param source CurrencyDTO
     * @return String
     */
    default String mapToCurrency(CurrencyDTO source) {
        if (source != null) {
            return source.toString();
        } else {
            return null;
        }
    }

    /**
     * Map to OrderDTO.
     *
     * @param source order
     * @return OrderDTO
     */
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapOrderToOrderDTOAmount")
    @Mapping(source = "source", target = "cumulativeAmount", qualifiedByName = "mapOrderToOrderDTOCumulativeAmount")
    @Mapping(source = "source", target = "averagePrice", qualifiedByName = "mapOrderToOrderDTOAveragePrice")
    @Mapping(source = "source", target = "fee", qualifiedByName = "mapOrderToOrderDTOFee")
    @Mapping(source = "source", target = "limitPrice", qualifiedByName = "mapOrderToOrderDTOLimitPrice")
    @Mapping(source = "trades", target = "trades")
    OrderDTO mapToOrderDTO(tech.cassandre.trading.bot.domain.Order source);

    @Named("mapOrderToOrderDTOAmount")
    default CurrencyAmountDTO mapOrderToOrderDTOAmount(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getAmount() != null & source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapOrderToOrderDTOCumulativeAmount")
    default CurrencyAmountDTO mapOrderToOrderDTOCumulativeAmount(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getCumulativeAmount() != null & source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getCumulativeAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapOrderToOrderDTOAveragePrice")
    default CurrencyAmountDTO mapOrderToOrderDTOAveragePrice(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getAveragePrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAveragePrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapOrderToOrderDTOFee")
    default CurrencyAmountDTO mapOrderToOrderDTOFee(tech.cassandre.trading.bot.domain.Order source) {
        if (source.getFee() != null && source.getFeeCurrency() != null) {
            return new CurrencyAmountDTO(source.getFee(), new CurrencyDTO(source.getFeeCurrency()));
        } else {
            return new CurrencyAmountDTO();
        }
    }


    @Named("mapOrderToOrderDTOLimitPrice")
    default CurrencyAmountDTO mapOrderToOrderDTOLimitPrice(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getLimitPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLimitPrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    /**
     * Map to order.
     *
     * @param source source
     * @return Order
     */
    @Mapping(target = "amount", source = "amount.value")
    @Mapping(target = "cumulativeAmount", source = "cumulativeAmount.value")
    @Mapping(target = "averagePrice", source = "averagePrice.value")
    @Mapping(target = "fee", source = "fee.value")
    @Mapping(target = "feeCurrency", source = "fee.currency")
    @Mapping(target = "limitPrice", source = "limitPrice.value")
    tech.cassandre.trading.bot.domain.Order mapToOrder(OrderDTO source);

    /**
     * Map to tradeDTO.
     *
     * @param source trade
     * @return tradeDRO
     */
    @Mapping(target = "amount", source = "source", qualifiedByName = "mapTradeToTradeDTOAmount")
    @Mapping(target = "price", source = "source", qualifiedByName = "mapTradeToTradeDTOPrice")
    @Mapping(target = "fee", source = "source", qualifiedByName = "mapTradeToTradeDTOFee")
    TradeDTO mapToTradeDTO(Trade source);

    @Named("mapTradeToTradeDTOAmount")
    default CurrencyAmountDTO mapTradeToTradeDTOAmount(Trade source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapTradeToTradeDTOPrice")
    default CurrencyAmountDTO mapTradeToTradeDTOPrice(Trade source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getPrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapTradeToTradeDTOFee")
    default CurrencyAmountDTO mapTradeToTradeDTOFee(Trade source) {
        if (source.getFeeAmount() != null && source.getFeeCurrency() != null) {
            return new CurrencyAmountDTO(source.getFeeAmount(), new CurrencyDTO(source.getFeeCurrency()));
        } else {
            return new CurrencyAmountDTO();
        }
    }

    /**
     * Map to positionDTO.
     *
     * @param source position
     * @return positionDTO
     */
    @Mapping(target = "rules", source = "source")
    @Mapping(target = "amount", source = "source", qualifiedByName = "mapPositionToPositionDTOAmount")
    @Mapping(target = "lowestPrice", source = "source", qualifiedByName = "mapPositionToPositionDTOLowestPrice")
    @Mapping(target = "highestPrice", source = "source", qualifiedByName = "mapPositionToPositionDTOHighestPrice")
    @Mapping(target = "latestPrice", source = "source", qualifiedByName = "mapPositionToPositionDTOLatestPrice")
    PositionDTO mapToPositionDTO(Position source);

    @Named("mapPositionToPositionDTOAmount")
    default CurrencyAmountDTO mapPositionToPositionDTOAmount(Position source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapPositionToPositionDTOLowestPrice")
    default CurrencyAmountDTO mapPositionToPositionDTOLowestPrice(Position source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getLowestPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLowestPrice(), cp.getQuoteCurrency());
        } else {
            return null;
        }
    }

    @Named("mapPositionToPositionDTOHighestPrice")
    default CurrencyAmountDTO mapPositionToPositionDTOHighestPrice(Position source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getHighestPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getHighestPrice(), cp.getQuoteCurrency());
        } else {
            return null;
        }
    }

    @Named("mapPositionToPositionDTOLatestPrice")
    default CurrencyAmountDTO mapPositionToPositionDTOLatestPrice(Position source) {
        CurrencyPairDTO cp = mapToCurrencyPairDTO(source.getCurrencyPair());
        if (source.getLatestPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLatestPrice(), cp.getQuoteCurrency());
        } else {
            return null;
        }
    }

    default PositionRulesDTO mapToPositionRulesDTO(Position source) {
        PositionRulesDTO rules = PositionRulesDTO.builder().build();
        boolean stopGainRuleSet = source.getStopGainPercentageRule() != null;
        boolean stopLossRuleSet = source.getStopLossPercentageRule() != null;
        // Two rules set.
        if (stopGainRuleSet && stopLossRuleSet) {
            rules = PositionRulesDTO.builder()
                    .stopGainPercentage(source.getStopGainPercentageRule())
                    .stopLossPercentage(source.getStopLossPercentageRule())
                    .build();
        }
        // Stop gain set.
        if (stopGainRuleSet && !stopLossRuleSet) {
            rules = PositionRulesDTO.builder()
                    .stopGainPercentage(source.getStopGainPercentageRule())
                    .build();
        }
        // Stop loss set.
        if (!stopGainRuleSet && stopLossRuleSet) {
            rules = PositionRulesDTO.builder()
                    .stopLossPercentage(source.getStopLossPercentageRule())
                    .build();
        }
        return rules;
    }

    /**
     * Map a trade set to a tradeDTO hashmap.
     *
     * @param source set
     * @return tradeDTO hashmap
     */
    default Map<String, TradeDTO> map(Set<Trade> source) {
        Map<String, TradeDTO> results = new LinkedHashMap<>();
        source.forEach(trade -> results.put(trade.getId(), mapToTradeDTO(trade)));
        return results;
    }

    /**
     * Map to position.
     *
     * @param source PositionDTO
     * @return position
     */
    @Mapping(source = "rules.stopGainPercentage", target = "stopGainPercentageRule")
    @Mapping(source = "rules.stopLossPercentage", target = "stopLossPercentageRule")
    @Mapping(source = "amount.value", target = "amount")
    @Mapping(source = "lowestPrice.value", target = "lowestPrice")
    @Mapping(source = "highestPrice.value", target = "highestPrice")
    @Mapping(source = "latestPrice.value", target = "latestPrice")
    Position mapToPosition(PositionDTO source);

    /***
     * Map to CurrencyPairDTO.
     * @param value string
     * @return CurrencyPairDTO
     */
    default CurrencyPairDTO mapToCurrencyPairDTO(String value) {
        return new CurrencyPairDTO(value);
    }

    /**
     * Map to CurrencyDTO.
     *
     * @param value string
     * @return CurrencyDTO
     */
    default CurrencyDTO mapToCurrencyDTO(String value) {
        return new CurrencyDTO(value);
    }

}
