package com.github.bytemania.balancer.domain.balance;

import com.github.bytemania.balancer.domain.entities.Balance;
import com.github.bytemania.balancer.domain.entities.Crypto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceAmountByCapTest {

    private static final BalanceAmountByCap balanceAmountByCap = new BalanceAmountByCapImpl();

    private static List<Balance> createAllocation(List<Crypto> cryptos) {
        return createAllocation(new BigDecimal("1000"), new BigDecimal("25"), cryptos);
    }

    private static List<Balance> createAllocation(BigDecimal amount,
                                                            BigDecimal unit,
                                                               List<Crypto> cryptos) {
        return balanceAmountByCap.balance(amount, unit, cryptos);
    }

    private static BigDecimal calculateBalanceTotal(List<Balance> balances) {
        return balances.stream().map(Balance::getBalancedValueInFiat).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    @DisplayName("Balance should return a empty map if amount is lower than unit")
    void balanceShouldReturnEmptyMapIfAmountIsLowerThanUnit() {
        assertThat(createAllocation(BigDecimal.ZERO, BigDecimal.ONE, List.of()))
                .isEqualTo(Collections.EMPTY_LIST);
        assertThat(createAllocation(BigDecimal.ZERO, BigDecimal.ONE, List.of(Crypto.of("BTC", 100.0))))
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    @DisplayName("Balance should return a empty map if the list of cryptos is empty")
    void balanceShouldReturnTheEmptyMapIfTheListOfCryptosIsEmpty() {
        assertThat(createAllocation(List.of())).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    @DisplayName("Balance Should allocate the values properly")
    void balanceShouldAllocateTheValuesProperly() {
        assertThat(createAllocation(Collections.singletonList(Crypto.of("BTC", 100.0))))
                .isEqualTo(Collections.singletonList(Balance.of("BTC", new BigDecimal("1000"))));

        assertThat(createAllocation(List.of(Crypto.of("BTC", 99.93), Crypto.of("ETH", 00.07))))
                .isEqualTo(Collections.singletonList(Balance.of("BTC", new BigDecimal("1000"))));

        assertThat(createAllocation(List.of(Crypto.of("BTC", 99.93), Crypto.of("ETH", 00.07))))
                .isEqualTo(Collections.singletonList(Balance.of("BTC", new BigDecimal("1000"))));

        var cryptos = List.of(
                Crypto.of("BTC", 42),
                Crypto.of("BUSD", 20),
                Crypto.of("ETH", 19),
                Crypto.of("BNB", 4),
                Crypto.of("XRP", 3),
                Crypto.of("ADA", 2),
                Crypto.of("SOL", 2),
                Crypto.of("LUNA", 2),
                Crypto.of("AVAX", 2),
                Crypto.of("DOGE", 2),
                Crypto.of("DOT", 1),
                Crypto.of("SHIB", 1)
        );

        var balanceNotFullyDistributed = createAllocation(cryptos);
        assertThat(balanceNotFullyDistributed).isEqualTo(
                List.of(
                    Balance.of("BTC", new BigDecimal("425")),
                    Balance.of("BUSD", new BigDecimal("200")),
                    Balance.of("ETH", new BigDecimal("200")),
                    Balance.of("BNB", new BigDecimal("50")),
                    Balance.of("XRP", new BigDecimal("50")),
                    Balance.of("ADA", new BigDecimal("25")),
                    Balance.of("SOL", new BigDecimal("25")),
                    Balance.of("LUNA", new BigDecimal("25"))
                )
        );
        assertThat(calculateBalanceTotal(balanceNotFullyDistributed)).isEqualTo(new BigDecimal("1000"));

        var balanceFullyDistributed = createAllocation(new BigDecimal("1000"), BigDecimal.ONE, cryptos);
        assertThat(balanceFullyDistributed).isEqualTo(
                List.of(
                        Balance.of("BTC", new BigDecimal("420")),
                        Balance.of("BUSD", new BigDecimal("200")),
                        Balance.of("ETH", new BigDecimal("190")),
                        Balance.of("BNB", new BigDecimal("40")),
                        Balance.of("XRP", new BigDecimal("30")),
                        Balance.of("ADA", new BigDecimal("20")),
                        Balance.of("SOL", new BigDecimal("20")),
                        Balance.of("LUNA", new BigDecimal("20")),
                        Balance.of("AVAX", new BigDecimal("20")),
                        Balance.of("DOGE", new BigDecimal("20")),
                        Balance.of("DOT", new BigDecimal("10")),
                        Balance.of("SHIB", new BigDecimal("10"))
                )
        );
        assertThat(calculateBalanceTotal(balanceNotFullyDistributed)).isEqualTo(new BigDecimal("1000"));

        var cryptosNotFullyCap = List.of(
                Crypto.of("BTC", 42),
                Crypto.of("BUSD", 20),
                Crypto.of("ETH", 19),
                Crypto.of("BNB", 4)
        );

        var balanceNotFullyCap = createAllocation(cryptosNotFullyCap);
        assertThat(balanceNotFullyCap).isEqualTo(
                List.of(
                        Balance.of("BTC", new BigDecimal("425")),
                        Balance.of("BUSD", new BigDecimal("200")),
                        Balance.of("ETH", new BigDecimal("200")),
                        Balance.of("BNB", new BigDecimal("50"))
                )
        );
    }
}