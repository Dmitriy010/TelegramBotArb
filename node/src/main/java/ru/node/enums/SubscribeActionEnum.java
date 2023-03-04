package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SubscribeActionEnum {

    FIND_ALL("findAll"),
    DELETE("delete");

    private final String name;
}
