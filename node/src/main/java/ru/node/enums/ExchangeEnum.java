package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExchangeEnum {

    BINANCE("Binance"),
    HUOBI("Huobi"),
    MY_EXCHANGE("Мои биржи"),
    ANY("Любая");


    private final String name;
}
