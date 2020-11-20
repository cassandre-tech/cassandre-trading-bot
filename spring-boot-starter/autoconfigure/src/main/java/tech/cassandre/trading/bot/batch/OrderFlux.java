package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Order flux - push {@link OrderDTO}.
 */
public class OrderFlux extends BaseFlux<OrderDTO> {

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
        getLogger().debug("OrderFlux - Retrieving new values");
        Set<OrderDTO> newValues = new LinkedHashSet<>();

        // Finding which order has been updated.
        tradeService.getOpenOrders().forEach(order -> {
            getLogger().debug("OrderFlux - Treating order : {}", order.getId());
            final Optional<Order> orderInDatabase = orderRepository.findById(order.getId());
            // If it does not exist or something changed, we do it.
            if (orderInDatabase.isEmpty() || !getMapper().mapToOrderDTO(orderInDatabase.get()).equals(order)) {
                getLogger().debug("OrderFlux - Order {} has changed : {}", order.getId(), order);
                newValues.add(order);
            }
        });
        getLogger().debug("OrderFlux - {} order(s) updated", newValues.size());
        return newValues;
    }

    @Override
    public final void backupValue(final OrderDTO newValue) {
        orderRepository.save(getMapper().mapToOrder(newValue));
    }

}
