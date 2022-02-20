package com.github.bytemania.balancer.domain.balance;

import com.github.bytemania.balancer.domain.entities.Balance;
import com.github.bytemania.balancer.domain.entities.Crypto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Redistribute an amount of money by the market cap (list of crypto currencies) with a minimum amount of money (unit)
 */
@FunctionalInterface
public interface BalanceAmountByCap {

    /**
     * Split the money based on market cap.
     * Also allocate part of the money (FIAT) in a stable coin based on a percentage
     * The allocation should always be calculated in amount * cap rounded up in blocks of unit
     * (n * unit or unit + rest of money).
     * The sum of all caps must be 100
     * The Map is calculated ordered from the bigcap to lower.
     * @param amount amount of money to allocate
     * @param unit minimum amount to allocate
     * @param cryptos String with the crypto and the cap. The list is ordered in the descending order by cap
     * @return Map with the crypto and the amount to invest (FIAT)
     */
    List<Balance> balance(BigDecimal amount, BigDecimal unit,
                          List<Crypto> cryptos);
}
