package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;

import java.util.List;

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
                .toList();
    }

    /**
     * Returns the order with the corresponding uid value.
     *
     * @param uid order uid
     * @return order
     */
    @DgsQuery
    public OrderDTO order(@InputArgument final long uid) {
        return orderRepository.findById(uid)
                .map(ORDER_MAPPER::mapToOrderDTO)
                .orElse(null);
    }

    /**
     * Returns the order with the corresponding order id value.
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
