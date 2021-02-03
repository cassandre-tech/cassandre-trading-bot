package tech.cassandre.trading.bot.batch;

import com.google.common.collect.Sets;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.batch.BaseExternalFlux;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
        HashMap<String, OrderDTO> newValues = new LinkedHashMap<>();

        // Finding which order has been updated.
        tradeService.getOrders().forEach(order -> {
            logger.debug("OrderFlux - Treating order : {}", order.getOrderId());
            final Optional<Order> orderInDatabase = orderRepository.findByOrderId(order.getOrderId());
            // If it does not exist or something changed, we add it to new values.
            if (orderInDatabase.isEmpty() || !orderMapper.mapToOrderDTO(orderInDatabase.get()).equals(order)) {
                logger.debug("OrderFlux - Order {} has changed : {}", order.getOrderId(), order);
                newValues.put(order.getOrderId(), order);
            }
        });
        logger.debug("OrderFlux - {} order(s) updated", newValues.size());
        return Sets.newHashSet(newValues.values());
    }

    @Override
    protected final Optional<OrderDTO> saveValue(final OrderDTO newValue) {
        AtomicReference<Order> valueToSave = new AtomicReference<>();

        orderRepository.findByOrderId(newValue.getOrderId())
                .ifPresentOrElse(order -> {
                    // Update order.
                    orderMapper.updateOrder(newValue, order);
                    valueToSave.set(order);
                    logger.debug("OrderFlux - Updating order in database {}", order);

                }, () -> {
                    // Create order.
                    valueToSave.set(orderMapper.mapToOrder(newValue));
                    logger.debug("OrderFlux - Creating order in database {}", newValue);
                });

        return Optional.ofNullable(orderMapper.mapToOrderDTO(orderRepository.save(valueToSave.get())));
    }

}
