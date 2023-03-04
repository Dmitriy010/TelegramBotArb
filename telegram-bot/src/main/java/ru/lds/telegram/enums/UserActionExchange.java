package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum UserActionExchange {

    REGISTER_USER("registerUser"),
    FIND_ALL_EXCHANGES("findAllExchanges"),
    FIND_ALL_USER_EXCHANGES("findAllUserExchanges"),
    DELETE_EXCHANGES("deleteExchanges"),
    ADD_EXCHANGES("addExchanges"),
    DELETE_ALL_EXCHANGES("deleteAllExchanges");

    private final String name;

    private static final Map<String, UserActionExchange> mapUserAction = new HashMap<>();

    static {
        Arrays.stream(UserActionExchange.values())
                .forEach(userActionExchange -> mapUserAction.put(userActionExchange.getName(), userActionExchange));
    }

    public static UserActionExchange getByName(String name){
        return mapUserAction.get(name);
    }
}
