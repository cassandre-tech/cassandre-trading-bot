package tech.cassandre.trading.bot.util.xchange;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.service.trade.params.CancelOrderByCurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderByIdParams;
import org.knowm.xchange.service.trade.params.CancelOrderByOrderTypeParams;

/**
 * Cancel order params.
 * CancelOrderParams has to be instanceof:
 * - BinanceTradeService: CancelOrderByCurrencyPair (currencyPair) and CancelOrderByIdParams (id)
 * - CoinbaseProTradeService: CancelOrderByIdParams (id)
 * - CoinbaseTradeService (v1 and v2): not available
 * - GeminiTradeService: CancelOrderByIdParams (id)
 * - KucoinTradeService: CancelOrderByIdParams (id)
 * - SimulatedTradeService:
 * CancelOrderByUserReferenceParams does not appear to be used
 */
@RequiredArgsConstructor
@Getter
public class CancelOrderParams  implements CancelOrderByOrderTypeParams, CancelOrderByCurrencyPair, CancelOrderByIdParams {

    /** Order id. */
    private final String orderId;

    /** currency pair. */
    private final CurrencyPair currencyPair;

    /** order type. */
    private final Order.OrderType orderType;

}
