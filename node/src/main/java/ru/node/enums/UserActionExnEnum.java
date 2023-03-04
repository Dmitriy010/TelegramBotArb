package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum UserActionExnEnum {

    REGISTER_USER("registerUser"),
    FIND_ALL_EXCHANGES("findAllExchanges"),
    FIND_ALL_USER_EXCHANGES("findAllUserExchanges"),
    DELETE_EXCHANGES("deleteExchanges"),
    ADD_EXCHANGES("addExchanges"),
    DELETE_ALL_EXCHANGES("deleteAllExchanges");

    private final String name;

    private static final Map<String, UserActionExnEnum> mapUserAction = new HashMap<>();

    static {
        Arrays.stream(UserActionExnEnum.values())
                .forEach(userActionExnEnum -> mapUserAction.put(userActionExnEnum.getName(), userActionExnEnum));
    }

    public static UserActionExnEnum getByName(String name){
        return mapUserAction.get(name);
    }
}
