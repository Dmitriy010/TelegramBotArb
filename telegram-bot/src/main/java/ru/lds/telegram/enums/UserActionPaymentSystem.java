package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserActionPaymentSystem {

    FIND_ALL_PAYMENT_SYSTEMS("findAllPaymentSystems"),
    FIND_ALL_USER_PAYMENT_SYSTEMS("findAllUserPaymentSystems"),
    DELETE_PAYMENT_SYSTEMS("deletePaymentSystems"),
    ADD_PAYMENT_SYSTEMS("addPaymentSystems"),
    DELETE_ALL_PAYMENT_SYSTEMS("deleteAllPaymentSystems");

    private final String name;
}
