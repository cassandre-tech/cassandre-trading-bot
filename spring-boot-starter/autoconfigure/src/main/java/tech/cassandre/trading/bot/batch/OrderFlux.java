package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.batch.BaseSequentialExternalFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Order flux - push {@link OrderDTO}.
 */
public class OrderFlux extends BaseSequentialExternalFlux<OrderDTO> {

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
        tradeService.getOrders()
                .forEach(order -> {
                    logger.debug("OrderFlux - Treating order : {}", order.getOrderId());
                    final Optional<Order> orderInDatabase = orderRepository.findByOrderId(order.getOrderId());

                    // If it's not in database, we insert it only if strategy is set - meaning it's the local order.
                    if (orderInDatabase.isEmpty() && order.getStrategy() != null) {
                        logger.debug("OrderFlux - Local order {} saved : {}", order.getOrderId(), order);
                        newValues.add(order);
                    }

                    // If the local order is already saved in database and this update change the data, it's a change.
                    if (orderInDatabase.isPresent() && !orderMapper.mapToOrderDTO(orderInDatabase.get()).equals(order)) {
                        logger.debug("OrderFlux - Order {} has changed : {}", order.getOrderId(), order);
                        newValues.add(order);
                    }
                });

        logger.debug("OrderFlux - {} order(s) updated", newValues.size());
        return newValues;
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
