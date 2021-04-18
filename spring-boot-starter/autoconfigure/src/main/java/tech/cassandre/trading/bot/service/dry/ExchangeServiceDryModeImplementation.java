package tech.cassandre.trading.bot.service.dry;

import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exchange service (dry mode implementation).
 */
public class ExchangeServiceDryModeImplementation implements ExchangeService {

    /** Currency pairs retrieved from the strategy. */
    private final Set<CurrencyPairDTO> currencyPairs = new LinkedHashSet<>();

    /**
     * Constructor.
     *
     * @param applicationContext application context
     */
    public ExchangeServiceDryModeImplementation(final ApplicationContext applicationContext) {
        currencyPairs.addAll(applicationContext.getBeansWithAnnotation(CassandreStrategy.class)
                .values()  // We get the list of all required cp of all strategies.
                .stream()
                .map(o -> ((CassandreStrategyInterface) o))
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    @Override
    public final Set<CurrencyPairDTO> getAvailableCurrencyPairs() {
        return currencyPairs;
    }

}
