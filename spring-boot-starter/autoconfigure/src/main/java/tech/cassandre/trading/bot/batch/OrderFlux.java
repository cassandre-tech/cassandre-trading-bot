package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Order flux - push {@link OrderDTO}.
 * Two methods override from super class:
 * - getNewValues(): calling trade service to retrieve orders from exchange.
 * - saveValues(): saving/updating orders in database.
 * To get a deep understanding of how it works, read the documentation of {@link BaseFlux}.
 */
@RequiredArgsConstructor
public class OrderFlux extends BaseFlux<OrderDTO> {

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade service. */
    private final TradeService tradeService;

    @Override
    protected final Set<OrderDTO> getNewValues() {
        return tradeService.getOrders()
                .stream()
                .peek(orderDTO -> logger.debug("Retrieved order from exchange: {}", orderDTO))
                .<OrderDTO>mapMulti((orderDTO, consumer) -> {
                    final Optional<Order> orderInDatabase = orderRepository.findByOrderId(orderDTO.getOrderId());

                    // We consider that we have a new value to send to strategies in those cases:
                    if (orderInDatabase.isEmpty() && orderDTO.getStrategy() != null) {
                        // If the order is not in database, we insert it only if strategy is set on that order.
                        // If strategy is not set, it means that Cassandre did not yet save its locally created order.
                        logger.debug("New order: {}", orderDTO);
                        consumer.accept(orderDTO);
                    }
                    if (orderInDatabase.isPresent() && !ORDER_MAPPER.mapToOrderDTO(orderInDatabase.get()).equals(orderDTO)) {
                        // If the local order is already saved in database and the order retrieved from the exchange
                        // is different from what we have, then, we update the order in database.
                        logger.debug("Updated order: {}", orderDTO);
                        consumer.accept(orderDTO);
                    }
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    protected final Set<OrderDTO> saveValues(final Set<OrderDTO> newValues) {
        return newValues.stream()
                .peek(orderDTO -> logger.debug("Checking order in database: {}", orderDTO))
                // We check in the database if it's a new order and an order to update.
                .<Order>mapMulti((orderDTO, consumer) -> {
                    final Optional<Order> orderInDatabase = orderRepository.findByOrderId(orderDTO.getOrderId());

                    // We check if the order exists in database.
                    if (orderInDatabase.isEmpty()) {
                        // The order does not exist in database, we create it.
                        logger.debug("Updating the order: {}", orderDTO);
                        consumer.accept(ORDER_MAPPER.mapToOrder(orderDTO));
                    } else {
                        // The order exists in database, we update it.
                        logger.debug("Creating a new order: {}", orderDTO);
                        ORDER_MAPPER.updateOrder(orderDTO, orderInDatabase.get());
                        consumer.accept(orderInDatabase.get());
                    }
                })
                // We save the order in database.
                .map(orderRepository::save)
                // We transform it to OrderDTO in order to return it to Cassandre.
                .map(ORDER_MAPPER::mapToOrderDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
