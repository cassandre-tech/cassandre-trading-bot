package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.util.base.BaseInternalFlux;

import java.util.Optional;

/**
 * Position flux - push {@link PositionDTO}.
 */
public class PositionFlux extends BaseInternalFlux<PositionDTO> {

    /** Position repository. */
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param newPositionRepository position repository
     */
    public PositionFlux(final PositionRepository newPositionRepository) {
        this.positionRepository = newPositionRepository;
    }

    @Override
    public final void backupValue(final PositionDTO newValue) {
        Optional<Position> p = positionRepository.findById(newValue.getId());
        if (p.isPresent()) {
            positionRepository.save(positionMapper.mapToPosition(newValue));
        } else {
            getLogger().error("Position {} was not saved because it was not found in database", newValue.getId());
        }
    }

}
