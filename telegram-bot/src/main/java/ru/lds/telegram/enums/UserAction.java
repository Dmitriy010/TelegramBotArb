package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserAction {

    FIND_ALL_LIMITS("findAllLimits"),
    UPDATE_LIMITS("updateLimits");

    private final String name;
}
