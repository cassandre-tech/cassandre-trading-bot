package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Position flux - push {@link PositionDTO}.
 * Two methods override from super class:
 * - getNewValues(): positions are only created inside cassandre, so we don't need to get new values from outside.
 * - saveValues(): update positions when they are sent to this flux (they are not created in the flux).
 * To get a deep understanding of how it works, read the documentation of {@link BaseFlux}.
 */
@RequiredArgsConstructor
public class PositionFlux extends BaseFlux<PositionDTO> {

    /** Position repository. */
    private final PositionRepository positionRepository;

    @Override
    protected final Set<PositionDTO> saveValues(final Set<PositionDTO> newValues) {
        return newValues.stream()
                .peek(positionDTO -> logger.debug("Checking position in database: {}", positionDTO))
                .<Position>mapMulti((positionDTO, consumer) -> {
                    final Optional<Position> position = positionRepository.findById(positionDTO.getUid());

                    // We update (the position creation can't be made here).
                    if (position.isPresent()) {
                        // If the position is in database (which should be always true), we update it.
                        POSITION_MAPPER.updatePosition(positionDTO, position.get());
                        consumer.accept(positionRepository.save(position.get()));
                        logger.debug("Updating the position: {}", positionDTO);
                    } else {
                        // This should NEVER append.
                        logger.error("Position not found in database: {}", positionDTO);
                    }
                })
                // We save the position in database.
                .map(positionRepository::save)
                // We transform it to PositionDTO in order to return it to Cassandre.
                .map(POSITION_MAPPER::mapToPositionDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
