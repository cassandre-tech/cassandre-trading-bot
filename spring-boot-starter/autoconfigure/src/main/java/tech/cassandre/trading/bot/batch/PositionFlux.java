package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.util.base.BaseFlux;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Position flux - push {@link PositionDTO}.
 */
public class PositionFlux extends BaseFlux<PositionDTO> {

    /** Position service. */
    private final PositionService positionService;

    /** Previous values. */
    private final Map<Long, Long> previousValues = new LinkedHashMap<>();

    /** Position repository. */
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param newPositionService    position service
     * @param newPositionRepository position repository
     */
    public PositionFlux(final PositionService newPositionService, final PositionRepository newPositionRepository) {
        this.positionService = newPositionService;
        this.positionRepository = newPositionRepository;
    }

    @Override
    protected final Set<PositionDTO> getNewValues() {
        getLogger().debug("PositionFlux - Retrieving new values");
        Set<PositionDTO> newValues = new LinkedHashSet<>();

        // Finding which positions has been updated.
        positionService.getPositions().forEach(position -> {
            getLogger().debug("PositionFlux - Treating position : {}", position.getId());
            Long previousVersion = previousValues.get(position.getId());
            if (previousVersion == null || !previousVersion.equals(position.getVersion())) {
                getLogger().debug("PositionFlux - Flux {} has changed : {}", position.getId(), position);
                previousValues.put(position.getId(), position.getVersion());
                newValues.add(position);
            }
        });

        getLogger().debug("PositionFlux - {} position(s) updated", newValues.size());
        return newValues;
    }

    @Override
    public final void backupValue(final PositionDTO newValue) {
        Optional<Position> p = positionRepository.findById(newValue.getId());
        if (p.isPresent()) {
            positionRepository.save(getMapper().mapToPosition(newValue));
        } else {
            // Position was not found.
            getLogger().error("Position {} was not saved because it was not found in database", newValue.getId());
        }
    }

    @Override
    public final void restoreValues() {
        getLogger().info("PositionFlux - Restoring positions from database");
        positionRepository.findAll().forEach(position -> {
            PositionDTO p = getMapper().mapToPositionDTO(position);
            previousValues.put(p.getId(), 0L);
            positionService.restorePosition(p);
            getLogger().info("PositionFlux - Position " + position.getId() + " restored : " + p);
        });
    }

}
