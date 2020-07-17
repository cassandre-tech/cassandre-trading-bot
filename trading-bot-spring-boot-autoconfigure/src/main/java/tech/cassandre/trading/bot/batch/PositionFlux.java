package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.util.base.BaseFlux;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Position flux.
 */
public class PositionFlux extends BaseFlux<PositionDTO> {

    /** Position service. */
    private final PositionService positionService;

    /** Previous values. */
    private final Map<Long, PositionDTO> previousValues = new LinkedHashMap<>();

    /**
     * Constructor.
     *
     * @param newPositionService position service
     */
    public PositionFlux(final PositionService newPositionService) {
        this.positionService = newPositionService;
    }

    @Override
    @SuppressWarnings("unused")
    protected final Set<PositionDTO> getNewValues() {
        getLogger().debug("PositionFlux - Retrieving new values");
        Set<PositionDTO> newValues = new LinkedHashSet<>();

        // Finding which positions has been updated.
        positionService.getPositions().forEach(position -> {
            getLogger().debug("PositionFlux - Treating position : {}", position.getId());
            PositionDTO existingPosition = previousValues.get(position.getId());
            if (existingPosition == null || !existingPosition.equals(position)) {
                getLogger().debug("PositionFlux - Flux {} has changed : {}", position.getId(), position);
                previousValues.put(position.getId(), position);
                newValues.add(position);
            }
        });

        getLogger().debug("PositionFlux - {} position(s) updated", newValues.size());
        return newValues;
    }

}
