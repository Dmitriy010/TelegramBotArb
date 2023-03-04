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
    MY_PAYMENT_SYSTEM("Мои платежные системы"),
    ANY("Любая");

    private final String name;
}
