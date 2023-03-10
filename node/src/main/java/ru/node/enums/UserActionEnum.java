package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum UserActionEnum {

    UPDATE("update"),
    FIND_ALL("findAll"),
    DELETE("delete"),
    ADD("add"),
    DELETE_ALL("deleteAll");

    private final String name;

    private static final Map<String, UserActionEnum> mapUserAction = new HashMap<>();

    static {
        Arrays.stream(UserActionEnum.values())
                .forEach(userActionEnumExEnum -> mapUserAction.put(userActionEnumExEnum.getName(), userActionEnumExEnum));
    }

    public static UserActionEnum getByName(String name){
        return mapUserAction.get(name);
    }
}
