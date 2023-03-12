package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum ExchangeEnum {

    BINANCE("Binance"),
    HUOBI("Huobi"),
    MY_EXCHANGE("Мои биржи"),
    ANY("Любая");

    private final String name;

    private static final Map<String, ExchangeEnum> mapExchange = new HashMap<>();

    static {
        Arrays.stream(ExchangeEnum.values())
                .forEach(exchange -> mapExchange.put(exchange.getName(), exchange));
    }

    public static ExchangeEnum getByName(String name){
        return mapExchange.get(name);
    }
}
