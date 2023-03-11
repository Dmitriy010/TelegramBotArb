package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum UserActionExEnum {

    FIND_ALL("findAll"),
    DELETE("delete"),
    ADD("add"),
    DELETE_ALL("deleteAll");

    private final String name;

    private static final Map<String, UserActionExEnum> mapUserAction = new HashMap<>();

    static {
        Arrays.stream(UserActionExEnum.values())
                .forEach(userActionExEnum -> mapUserAction.put(userActionExEnum.getName(), userActionExEnum));
    }

    public static UserActionExEnum getByName(String name){
        return mapUserAction.get(name);
    }
}
