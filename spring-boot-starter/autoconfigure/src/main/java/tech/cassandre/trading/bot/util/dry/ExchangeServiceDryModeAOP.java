package tech.cassandre.trading.bot.util.dry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AOP for exchange service in dry mode.
 */
@Aspect
@Configuration
@ConditionalOnExpression("${cassandre.trading.bot.exchange.modes.dry:true}")
public class ExchangeServiceDryModeAOP extends BaseService {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     */
    public ExchangeServiceDryModeAOP(final ApplicationContext newApplicationContext) {
        this.applicationContext = newApplicationContext;
    }

    /**
     * getAvailableCurrencyPairs() AOP for dry mode.
     *
     * @param pjp ProceedingJoinPoint
     * @return list of currency pairs
     */
    @Around("execution(* tech.cassandre.trading.bot.service.ExchangeService.getAvailableCurrencyPairs())")
    public final Set<CurrencyPairDTO> getAvailableCurrencyPairs(final ProceedingJoinPoint pjp) {
        return applicationContext.getBeansWithAnnotation(CassandreStrategy.class)
                .values()  // We get the list of all required cp of all strategies.
                .stream()
                .map(o -> ((CassandreStrategyInterface) o))
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
