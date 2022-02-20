package com.github.bytemania.balancer.domain.balance;

import com.github.bytemania.balancer.domain.entities.Balance;
import com.github.bytemania.balancer.domain.entities.Crypto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BalanceAmountByCapImpl implements BalanceAmountByCap {

    private static BigDecimal calculateAmountByCap(BigDecimal amount, BigDecimal unit, Double cap) {
        BigDecimal amountByCap = amount.multiply(BigDecimal.valueOf(cap / 100));
        if (amountByCap.compareTo(unit) <= 0) {
            return unit;
        } else {
            BigDecimal numberOfUnitsRoundedUp = amountByCap.divide(unit, 0, RoundingMode.UP);
            return numberOfUnitsRoundedUp.multiply(unit);
        }

    }

    @Override
    public List<Balance> balance(BigDecimal amount, BigDecimal unit,
                                 List<Crypto> cryptos) {
        BigDecimal rest = amount;

        List<Balance> balances = new ArrayList<>();
        Iterator<Crypto> it = cryptos.iterator();
        while (rest.compareTo(unit) >= 0 && it.hasNext()) {
            Crypto crypto = it.next();
            String symbol = crypto.getSymbol();
            Double cap = crypto.getCap();
            BigDecimal amountToInvest = calculateAmountByCap(amount, unit, cap);

            balances.add(Balance.of(symbol, amountToInvest));
            rest = rest.subtract(amountToInvest);
        }

        return balances;
    }
}
