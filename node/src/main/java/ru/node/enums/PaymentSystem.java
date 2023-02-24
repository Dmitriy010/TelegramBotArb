package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum PaymentSystem {

    PAYEER("Payeer", "24", "Payeer"),
    SBERBANK("SberBank", "29", "RosBankNew"),
    RAIFFEISENBANK("RaiffeisenBank", "36", "RaiffeisenBank"),
    TINKOFF("Tinkoff", "28", "TinkoffNew"),
    ALFABANK("AlfaBank", "25", ""),
    QIWI("Qiwi", "9", "QIWI"),
    ANY("Любая", null, null);

    private final String name;
    private final String nameHuobi;
    private final String nameBinance;

    private static final Map<String, PaymentSystem> mapPs = new HashMap<>();

    static {
        Arrays.stream(PaymentSystem.values())
                .forEach(paymentSystem -> mapPs.put(paymentSystem.getNameBinance(), paymentSystem));
    }

    public static PaymentSystem getByNameBinance(String nameBinance){
        return mapPs.get(nameBinance);
    }
}
