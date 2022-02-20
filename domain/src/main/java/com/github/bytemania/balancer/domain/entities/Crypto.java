package com.github.bytemania.balancer.domain.entities;

import lombok.Value;

@Value(staticConstructor = "of")
public class Crypto {
    String symbol;
    double cap;
}
