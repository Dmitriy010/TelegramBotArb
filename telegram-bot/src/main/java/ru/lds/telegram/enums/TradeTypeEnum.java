package ru.lds.telegram.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TradeTypeEnum {

    BUY("Продать"),
    SELL("Купить");

    private final String name;
}
