package tech.cassandre.trading.bot.batch;

import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
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
            p.get().setId(newValue.getId());
            p.get().setStatus(newValue.getStatus().toString());
            if (newValue.getRules().isStopGainPercentageSet()) {
                p.get().setStopGainPercentageRule(newValue.getRules().getStopGainPercentage());
            }
            if (newValue.getRules().isStopLossPercentageSet()) {
                p.get().setStopLossPercentageRule(newValue.getRules().getStopLossPercentage());
            }
            newValue.getTrades().forEach(tradeDTO -> {
                Trade t = new Trade();
                t.setId(tradeDTO.getId());
                t.setOrderId(tradeDTO.getOrderId());
                t.setType(tradeDTO.getType().toString());
                t.setOriginalAmount(tradeDTO.getOriginalAmount());
                t.setCurrencyPair(tradeDTO.getCurrencyPair().toString());
                t.setPrice(tradeDTO.getPrice());
                t.setTimestamp(tradeDTO.getTimestamp());
                t.setFeeAmount(tradeDTO.getFee().getValue());
                t.setFeeCurrency(tradeDTO.getFee().getCurrency().toString());
                p.get().getTrades().add(t);
            });
            p.get().setOpenOrderId(newValue.getOpenOrderId());
            p.get().setCloseOrderId(newValue.getCloseOrderId());
            p.get().setLowestPrice(newValue.getLowestPrice());
            p.get().setHighestPrice(newValue.getHighestPrice());
            positionRepository.save(p.get());
        } else {
            // Position was not found.
            getLogger().error("Position {} was not saved because it was not found in database", newValue.getId());
        }
    }

    @Override
    public final void restoreValues() {
        getLogger().info("Restoring positions from database");
        positionRepository.findAll().forEach(position -> {
            PositionRulesDTO rules = PositionRulesDTO.builder().create();
            boolean stopGainRuleSet = position.getStopGainPercentageRule() != null;
            boolean stopLossRuleSet = position.getStopLossPercentageRule() != null;
            // Two rules set.
            if (stopGainRuleSet && stopLossRuleSet) {
                rules = PositionRulesDTO.builder()
                        .stopGainPercentage(position.getStopGainPercentageRule())
                        .stopLossPercentage(position.getStopLossPercentageRule())
                        .create();
            }
            // Stop gain set.
            if (stopGainRuleSet && !stopLossRuleSet) {
                rules = PositionRulesDTO.builder()
                        .stopGainPercentage(position.getStopGainPercentageRule())
                        .create();
            }
            // Stop loss set.
            if (!stopGainRuleSet && stopLossRuleSet) {
                rules = PositionRulesDTO.builder()
                        .stopLossPercentage(position.getStopLossPercentageRule())
                        .create();
            }
            Set<TradeDTO> positionTrades = new LinkedHashSet<>();
            position.getTrades().forEach(trade -> {
                TradeDTO t = TradeDTO.builder()
                        .id(trade.getId())
                        .orderId(trade.getOrderId())
                        .type(OrderTypeDTO.valueOf(trade.getType()))
                        .originalAmount(trade.getOriginalAmount())
                        .currencyPair(new CurrencyPairDTO(trade.getCurrencyPair()))
                        .price(trade.getPrice())
                        .timestamp(trade.getTimestamp())
                        .feeAmount(trade.getFeeAmount())
                        .feeCurrency(new CurrencyDTO(trade.getFeeCurrency()))
                        .create();
                positionTrades.add(t);
            });

            PositionDTO p = new PositionDTO(position.getId(),
                    PositionStatusDTO.valueOf(position.getStatus()),
                    new CurrencyPairDTO(position.getCurrencyPair()),
                    position.getAmount(),
                    rules,
                    position.getOpenOrderId(),
                    position.getCloseOrderId(),
                    positionTrades,
                    position.getLowestPrice(),
                    position.getHighestPrice());
            previousValues.put(p.getId(), 0L);
            positionService.restorePosition(p);
            getLogger().info("Position " + position.getId() + " restored : " + p);
        });
    }

}
