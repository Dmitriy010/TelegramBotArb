package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentSystem {

    TINKOFF("Tinkoff"),
    ROSBANK("SberBank"),
    RAIFFEISENBANK("RaiffeisenBank"),
    PAYEER("Payeer"),
    QIWI("Qiwi"),
    ANY("Любая");

    private final String name;
}
