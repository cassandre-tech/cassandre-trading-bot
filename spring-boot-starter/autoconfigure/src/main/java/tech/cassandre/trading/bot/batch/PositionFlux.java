package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.util.base.BaseFlux;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Position flux - push {@link PositionDTO}.
 */
public class PositionFlux extends BaseFlux<PositionDTO> {

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
    protected final Set<PositionDTO> getNewValues() {
        return Collections.emptySet();
    }

    @Override
    public final void backupValue(final PositionDTO newValue) {
        Optional<Position> p = positionRepository.findById(newValue.getId());
        if (p.isPresent()) {
            positionRepository.save(getMapper().mapToPosition(newValue));
        } else {
            getLogger().error("Position {} was not saved because it was not found in database", newValue.getId());
        }
    }

    @Override
    public final void restoreValues() {
        // TODO to be removed ?
        getLogger().info("PositionFlux - Restoring positions from database");
        positionRepository.findAll().forEach(position -> {
            PositionDTO p = getMapper().mapToPositionDTO(position);
            // positionService.restorePosition(p);
            getLogger().info("PositionFlux - Position " + position.getId() + " restored : " + p);
        });
    }

}
