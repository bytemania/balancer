package com.github.bytemania.balancer.domain.entities;

import lombok.Value;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class Balance {
    String cryptoSymbol;
    BigDecimal balancedValueInFiat;
}
