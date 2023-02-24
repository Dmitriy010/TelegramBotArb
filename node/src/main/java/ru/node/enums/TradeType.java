package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TradeType {

    BUY("buy", "BUY"),
    SELL("sell", "SELL");

    private final String nameHuobi;
    private final String nameBinance;
}
