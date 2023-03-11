package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SubscribeActionEnum {

    FIND_ALL("findAll"),
    DELETE("delete"),
    DELETE_ALL("deleteAll");

    private final String name;
}
