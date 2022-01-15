package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Order data fetcher.
 */
@DgsComponent
@RequiredArgsConstructor
public class OrderDataFetcher extends BaseDataFetcher {

    /** Trade repository. */
    private final OrderRepository orderRepository;

    /**
     * Returns all the orders.
     *
     * @return all orders
     */
    @DgsQuery
    public final List<OrderDTO> orders() {
        return orderRepository.findAll()
                .stream()
                .map(ORDER_MAPPER::mapToOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the order with the corresponding id value.
     *
     * @param id id
     * @return order
     */
    @DgsQuery
    public OrderDTO order(@InputArgument final long id) {
        return orderRepository.findById(id)
                .map(ORDER_MAPPER::mapToOrderDTO)
                .orElse(null);
    }

    /**
     * Returns the order with the corresponding orderId value.
     *
     * @param orderId order id
     * @return trade
     */
    @DgsQuery
    public OrderDTO orderByOrderId(@InputArgument final String orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(ORDER_MAPPER::mapToOrderDTO)
                .orElse(null);
    }

}
