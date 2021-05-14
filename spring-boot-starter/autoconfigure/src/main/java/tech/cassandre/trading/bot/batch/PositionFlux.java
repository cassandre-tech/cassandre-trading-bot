package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.util.base.batch.BaseSequentialInternalFlux;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Position flux - push {@link PositionDTO}.
 */
public class PositionFlux extends BaseSequentialInternalFlux<PositionDTO> {

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
    public final Optional<PositionDTO> saveValue(final PositionDTO newValue) {
        AtomicReference<Position> valueToSave = new AtomicReference<>();

        positionRepository.findById(newValue.getId())
                .ifPresentOrElse(position -> {
                    positionMapper.updatePosition(newValue, position);
                    // Setting opening & closing order.
                    if (newValue.getOpeningOrder() == null && newValue.getOpeningOrderId() != null) {
                        orderRepository.findByOrderId(newValue.getOpeningOrderId())
                                .ifPresent(position::setOpeningOrder);
                    }
                    if (newValue.getClosingOrder() == null && newValue.getClosingOrderId() != null) {
                        orderRepository.findByOrderId(newValue.getClosingOrderId())
                                .ifPresent(position::setClosingOrder);
                    }
                    valueToSave.set(position);
                    logger.debug("PositionFlux - Updating position in database {}", position);
                }, () -> logger.error("PositionFlux - Position {} was not found in database", newValue));

        return Optional.ofNullable(positionMapper.mapToPositionDTO(positionRepository.save(valueToSave.get())));
    }

}
