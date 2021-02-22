package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.ExchangeAccount;

import java.util.Optional;

/**
 * Exchange account repository.
 */
@Repository
public interface ExchangeAccountRepository extends CrudRepository<ExchangeAccount, Long> {

    /**
     * Find the exchange account with the exchange name and exchange account.
     *
     * @param exchange exchange name
     * @param account  exchange account
     * @return exchange account
     */
    Optional<ExchangeAccount> findByExchangeAndAccount(String exchange, String account);

}
