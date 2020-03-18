package tech.cassandre.trading.bot.service;

import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.util.base.BaseService;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Trade service - XChange implementation.
 */
public class TradeServiceXChangeImplementation extends BaseService implements TradeService {

	/** XChange Trade data service. */
	private final org.knowm.xchange.service.trade.TradeService tradeService;

	/**
	 * Constructor.
	 *
	 * @param rate            rate in ms
	 * @param newTradeService market data service
	 */
	public TradeServiceXChangeImplementation(final long rate, final org.knowm.xchange.service.trade.TradeService newTradeService) {
		super(rate);
		this.tradeService = newTradeService;
	}

	/**
	 * Creates market order.
	 *
	 * @param orderTypeDTO order type
	 * @param currencyPair currency pair
	 * @param amount       amount
	 * @return order creation result
	 */
	private OrderCreationResultDTO createMarketOrder(final OrderTypeDTO orderTypeDTO, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
		try {
			// Making the order.
			MarketOrder m = new MarketOrder(getMapper().mapToOrderType(orderTypeDTO), amount, getCurrencyPair(currencyPair));
			getLogger().debug("TradeServiceXChangeImplementation - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

			// Sending the order.
			final OrderCreationResultDTO result = new OrderCreationResultDTO(tradeService.placeMarketOrder(m));
			getLogger().debug("TradeServiceXChangeImplementation - Order creation result : {}", result);
			return result;
		} catch (Exception e) {
			getLogger().error("Error calling createBuyMarketOrder : {}", e.getMessage());
			return new OrderCreationResultDTO("Error calling createBuyMarketOrder  : " + e.getMessage(), e);
		}
	}

	/**
	 * Creates limit order.
	 *
	 * @param orderTypeDTO order type
	 * @param currencyPair currency pair
	 * @param amount       amount
	 * @param limitPrice   In a BID this is the highest acceptable price, in an ASK this is the lowest acceptable price
	 * @return order creation result
	 */
	private OrderCreationResultDTO createLimitOrder(final OrderTypeDTO orderTypeDTO, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
		try {
			// Making the order.
			LimitOrder l = new LimitOrder(getMapper().mapToOrderType(orderTypeDTO), amount, getCurrencyPair(currencyPair), null, null, limitPrice);
			getLogger().debug("TradeServiceXChangeImplementation - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

			// Sending the order.
			final OrderCreationResultDTO result = new OrderCreationResultDTO(tradeService.placeLimitOrder(l));
			getLogger().debug("TradeServiceXChangeImplementation - Order creation result : {}", result);
			return result;
		} catch (Exception e) {
			getLogger().error("Error calling createLimitOrder : {}", e.getMessage());
			return new OrderCreationResultDTO("Error calling createLimitOrder  : " + e.getMessage(), e);
		}
	}

	@Override
	public final OrderCreationResultDTO createBuyMarketOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
		return createMarketOrder(OrderTypeDTO.BID, currencyPair, amount);
	}

	@Override
	public final OrderCreationResultDTO createSellMarketOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
		return createMarketOrder(OrderTypeDTO.ASK, currencyPair, amount);
	}

	@Override
	public final OrderCreationResultDTO createBuyLimitOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
		return createLimitOrder(OrderTypeDTO.BID, currencyPair, amount, limitPrice);
	}

	@Override
	public final OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
		return createLimitOrder(OrderTypeDTO.ASK, currencyPair, amount, limitPrice);
	}

	@Override
	public final Optional<OrderDTO> getOpenOrderByOrderId(final String orderId) {
		if (orderId != null) {
			return getOpenOrders()
					.stream()
					.filter(o -> orderId.equalsIgnoreCase(o.getId()))
					.findFirst();
		} else {
			return Optional.empty();
		}
	}

	@Override
	public final Set<OrderDTO> getOpenOrders() {
		getLogger().debug("TradeServiceXChangeImplementation - Getting open orders from exchange");
		try {
			// Consume a token from the token bucket.
			// If a token is not available this method will block until the refill adds one to the bucket.
			getBucket().asScheduler().consume(1);

			Set<OrderDTO> results = new LinkedHashSet<>();
			tradeService.getOpenOrders().getOpenOrders().forEach(order -> results.add(getMapper().mapToOrderDTO(order)));
			getLogger().debug("TradeServiceXChangeImplementation - {} order(s) found", results.size());
			return results;
		} catch (IOException e) {
			getLogger().error("Error retrieving open orders : {}", e.getMessage());
			return Collections.emptySet();
		} catch (InterruptedException e) {
			getLogger().error("InterruptedException : {}", e.getMessage());
			return Collections.emptySet();
		}
	}

	@Override
	public final boolean cancelOrder(final String orderId) {
		getLogger().debug("TradeServiceXChangeImplementation - Canceling order {}", orderId);
		if (orderId != null) {
			try {
				getLogger().debug("TradeServiceXChangeImplementation - Successfully canceled order {}", orderId);
				return tradeService.cancelOrder(orderId);
			} catch (Exception e) {
				getLogger().error("Error canceling order {} : {}", orderId, e.getMessage());
				return false;
			}
		} else {
			getLogger().error("Error canceling order, order id is null");
			return false;
		}
	}

}
