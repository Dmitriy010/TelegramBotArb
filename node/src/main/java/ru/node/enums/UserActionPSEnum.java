package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum UserActionPSEnum {

    FIND_ALL("findAll"),
    DELETE("delete"),
    ADD("add"),
    DELETE_ALL("deleteAll");

    private final String name;

    private static final Map<String, UserActionPSEnum> mapUserAction = new HashMap<>();

    static {
        Arrays.stream(UserActionPSEnum.values())
                .forEach(userActionExEnum -> mapUserAction.put(userActionExEnum.getName(), userActionExEnum));
    }

    public static UserActionPSEnum getByName(String name){
        return mapUserAction.get(name);
    }
}