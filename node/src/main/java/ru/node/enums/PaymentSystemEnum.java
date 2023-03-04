package ru.node.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum PaymentSystemEnum {

    PAYEER("Payeer", "24", "Payeer"),
    SBERBANK("SberBank", "29", "RosBankNew"),
    RAIFFEISENBANK("RaiffeisenBank", "36", "RaiffeisenBank"),
    TINKOFF("Tinkoff", "28", "TinkoffNew"),
    QIWI("Qiwi", "9", "QIWI"),
    MY_PAYMENT_SYSTEM("Мои платежные системы", null, null),
    ANY("Любая", null, null);

    private final String name;
    private final String nameHuobi;
    private final String nameBinance;

    private static final Map<String, PaymentSystemEnum> mapPs = new HashMap<>();

    static {
        Arrays.stream(PaymentSystemEnum.values())
                .forEach(paymentSystem -> mapPs.put(paymentSystem.getNameBinance(), paymentSystem));
    }

    public static PaymentSystemEnum getByNameBinance(String nameBinance){
        return mapPs.get(nameBinance);
    }
}
