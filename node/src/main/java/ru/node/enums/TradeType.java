package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TradeType {

    BUY("Продать","buy", "BUY"),
    SELL("Купить","sell", "SELL");

    private final String name;
    private final String nameHuobi;
    private final String nameBinance;
}
