package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum Exchange {

    BINANCE("Binance"),
    HUOBI("Huobi"),
    MY_EXCHANGE("Мои биржи"),
    ANY("Любая");

    private final String name;

    private static final Map<String, Exchange> mapExchange = new HashMap<>();

    static {
        Arrays.stream(Exchange.values())
                .forEach(exchange -> mapExchange.put(exchange.getName(), exchange));
    }

    public static Exchange getByName(String name){
        return mapExchange.get(name);
    }
}
