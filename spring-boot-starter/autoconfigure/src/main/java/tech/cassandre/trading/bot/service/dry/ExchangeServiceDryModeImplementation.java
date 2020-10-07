package tech.cassandre.trading.bot.service.dry;

import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;

import java.util.Map;
import java.util.Set;

/**
 * Exchange service in dry mode.
 */
public class ExchangeServiceDryModeImplementation implements ExchangeService {

    /** Strategy. */
    private final CassandreStrategyInterface strategy;

    /**
     * Constructor.
     *
     * @param applicationContext application context
     */
    public ExchangeServiceDryModeImplementation(final ApplicationContext applicationContext) {
        final Map<String, Object> strategyBeans = applicationContext.getBeansWithAnnotation(CassandreStrategy.class);
        Object o = strategyBeans.values().iterator().next();
        strategy = (CassandreStrategyInterface) o;
    }

    @Override
    public final Set<CurrencyPairDTO> getAvailableCurrencyPairs() {
        return strategy.getRequestedCurrencyPairs();
    }

}
