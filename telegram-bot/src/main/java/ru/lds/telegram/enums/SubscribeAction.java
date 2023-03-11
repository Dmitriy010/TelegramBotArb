package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SubscribeAction {

    FIND_ALL("findAll"),
    DELETE("delete"),
    DELETE_ALL("deleteAll");

    private final String name;
}
