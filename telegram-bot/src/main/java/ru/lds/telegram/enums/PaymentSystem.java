package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentSystem {

    TINKOFF("Tinkoff"),
    ROSBANK("RosBank"),
    RAIFFEISENBANK("Raiffeisenbank"),
    PAYEER("Payeer");

    private final String name;
}
