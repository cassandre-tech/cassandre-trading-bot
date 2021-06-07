package tech.cassandre.trading.bot.util.dry;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * AOP for exchange service in dry mode.
 */
@Aspect
@Component
@ConditionalOnExpression("${cassandre.trading.bot.exchange.modes.dry:true}")
@RequiredArgsConstructor
public class ExchangeServiceDryModeAOP extends BaseService {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /**
     * getExchangeMetaData() AOP for dry mode.
     *
     * @param pjp ProceedingJoinPoint
     * @return list of supported currency pairs
     */
    @Around("execution(* org.knowm.xchange.Exchange.getExchangeMetaData())")
    public final ExchangeMetaData getExchangeMetaData(final ProceedingJoinPoint pjp) {
        Map<CurrencyPair, CurrencyPairMetaData> supportedCurrencyPairs = applicationContext
                .getBeansWithAnnotation(CassandreStrategy.class)
                .values()  // We get the list of all required cp of all strategies.
                .stream()
                .map(o -> ((CassandreStrategyInterface) o))
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .distinct()
                .map(currencyMapper::mapToCurrencyPair)
                .collect(HashMap::new, (map, cp) -> map.put(cp, null), Map::putAll);

        return new ExchangeMetaData(supportedCurrencyPairs,
                null,
                null,
                null,
                null);
    }

}
