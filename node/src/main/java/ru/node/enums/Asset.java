package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Asset {

    BTC("1", "BTC"),
    USDT("2", "USDT"),
    ETH("3", "ETH");

    private final String nameHuobi;
    private final String nameBinance;
}
