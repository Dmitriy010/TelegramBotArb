package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SubscribeAction {

    FIND_ALL("findAll"),
    DELETE("delete");

    private final String name;
}
