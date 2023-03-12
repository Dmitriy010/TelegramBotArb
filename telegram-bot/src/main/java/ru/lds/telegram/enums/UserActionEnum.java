package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserActionEnum {

    FIND_ALL("findAll"),
    DELETE_ALL("deleteAll");

    private final String name;
}
