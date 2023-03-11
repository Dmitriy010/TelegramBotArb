package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserActionExchange {

    FIND_ALL("findAll"),
    DELETE("delete"),
    ADD("add"),
    DELETE_ALL("deleteAll");

    private final String name;
}
