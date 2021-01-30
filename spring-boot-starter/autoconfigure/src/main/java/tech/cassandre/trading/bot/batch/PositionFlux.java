package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.util.base.BaseInternalFlux;

import java.util.Optional;

/**
 * Position flux - push {@link PositionDTO}.
 */
public class PositionFlux extends BaseInternalFlux<PositionDTO> {

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /**
     * Constructor.
     *
     * @param newPositionRepository position repository
     * @param newOrderRepository    order repository
     */
    public PositionFlux(final PositionRepository newPositionRepository,
                        final OrderRepository newOrderRepository) {
        this.positionRepository = newPositionRepository;
        this.orderRepository = newOrderRepository;
    }

    @Override
    public final void saveValue(final PositionDTO newValue) {
        Optional<Position> positionInDatabase = positionRepository.findById(newValue.getId());
        positionInDatabase.ifPresentOrElse(position -> {
            positionMapper.updatePosition(newValue, position);
            // Setting opening & closing order.
            if (newValue.getOpeningOrder() != null) {
                final Optional<Order> openingOrder = orderRepository.findByOrderId(newValue.getOpeningOrder().getOrderId());
                openingOrder.ifPresent(position::setOpeningOrder);
            }
            if (newValue.getClosingOrder() != null && newValue.getClosingOrder().getId() != null) {
                final Optional<Order> closingOrder = orderRepository.findByOrderId(newValue.getClosingOrder().getOrderId());
                closingOrder.ifPresent(position::setClosingOrder);
            }
            positionRepository.save(position);
            logger.debug("PositionFlux - Position {} updated in database", position);

        }, () -> logger.error("PositionFlux - Position {} was not found in database", newValue.getId()));
    }

}
