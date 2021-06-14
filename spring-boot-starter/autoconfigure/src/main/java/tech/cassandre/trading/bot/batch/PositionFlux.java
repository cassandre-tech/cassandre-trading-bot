package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;

/**
 * Position flux - push {@link PositionDTO}.
 */
@RequiredArgsConstructor
public class PositionFlux extends BaseFlux<PositionDTO> {

    /** Position repository. */
    private final PositionRepository positionRepository;

    @Override
    protected final Set<PositionDTO> getNewValues() {
        // We return an empty set because positions updates only comes from inside cassandre.
        return Collections.emptySet();
    }

    @Override
    public final Set<PositionDTO> saveValues(final Set<PositionDTO> newValues) {
        Set<Position> positions = new LinkedHashSet<>();

        // We save every positions sent to the flux.
        newValues.forEach(positionDTO -> {
            final Optional<Position> position = positionRepository.findById(positionDTO.getId());
            if (position.isPresent()) { // If the position is in database (which must be always true), we update it.
                // We should not override some status in the position (due to multi thread).
                // TODO Replace this by a status calculation in Position.
                PositionStatusDTO statusToSet = positionDTO.getStatus();
                switch (position.get().getStatus()) {
                    case OPENED:
                        // If the position is OPENED in database and an order update tries to set it back to OPENING.
                        if (positionDTO.getStatus().equals(OPENING)) {
                            statusToSet = OPENED;
                        }
                        break;
                    case CLOSED:
                        // If the position is OPENED in database and an order update tries to set it back to OPENING.
                        statusToSet = CLOSED;
                        break;
                    default:
                        statusToSet = positionDTO.getStatus();
                        break;
                }
                positionMapper.updatePosition(positionDTO, position.get());
                position.get().setStatus(statusToSet);
                positions.add(positionRepository.save(position.get()));
                logger.debug("PositionFlux - Updating position in database: {}", positionDTO);
            }
        });

        return positions.stream()
                .map(positionMapper::mapToPositionDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
