package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserActionPaymentSystem {

    FIND_ALL("findAll"),
    DELETE("delete"),
    ADD("add"),
    DELETE_ALL("deleteAll");

    private final String name;
}
