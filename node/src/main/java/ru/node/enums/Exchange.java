package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Exchange {

    BINANCE("Binance"),
    HUOBI("Huobi"),
    ANY("Любая");


    private final String name;
}
