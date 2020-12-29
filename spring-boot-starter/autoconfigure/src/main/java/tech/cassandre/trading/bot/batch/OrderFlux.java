package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseExternalFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Order flux - push {@link OrderDTO}.
 */
public class OrderFlux extends BaseExternalFlux<OrderDTO> {

    /** Trade service. */
    private final TradeService tradeService;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /**
     * Constructor.
     *
     * @param newTradeService    trade service
     * @param newOrderRepository order repository
     */
    public OrderFlux(final TradeService newTradeService, final OrderRepository newOrderRepository) {
        this.tradeService = newTradeService;
        this.orderRepository = newOrderRepository;
    }

    @Override
    protected final Set<OrderDTO> getNewValues() {
        logger.debug("OrderFlux - Retrieving new values");
        Set<OrderDTO> newValues = new LinkedHashSet<>();

        // Finding which order has been updated.
        tradeService.getOrders().forEach(order -> {
            logger.debug("OrderFlux - Treating order : {}", order.getOrderId());
            final Optional<Order> orderInDatabase = orderRepository.findByOrderId(order.getOrderId());
            // If it does not exist or something changed, we do it.
            if (orderInDatabase.isEmpty() || !orderMapper.mapToOrderDTO(orderInDatabase.get()).equals(order)) {
                logger.debug("OrderFlux - Order {} has changed : {}", order.getOrderId(), order);
                newValues.add(order);
            }
        });
        logger.debug("OrderFlux - {} order(s) updated", newValues.size());
        return newValues;
    }

    @Override
    public final void backupValue(final OrderDTO newValue) {
        final Optional<Order> orderInDatabase = orderRepository.findByOrderId(newValue.getOrderId());
        orderInDatabase.ifPresentOrElse(order -> {
            // Update order.
            orderMapper.updateOrder(newValue, order);
            orderRepository.save(order);
        }, () -> {
            // Create order.
            orderRepository.save(orderMapper.mapToOrder(newValue));
        });
    }

}
