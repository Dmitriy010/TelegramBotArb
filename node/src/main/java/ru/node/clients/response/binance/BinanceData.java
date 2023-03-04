package ru.node.clients.response.binance;

import lombok.Data;
import ru.node.enums.ExchangeEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static ru.node.constants.Constants.ZONE_ID;

@Data
public class BinanceData {

    private Adv adv;
    private Advertiser advertiser;
    private LocalDateTime date = LocalDateTime.now(ZoneId.of(ZONE_ID));
    private String exchange = ExchangeEnum.BINANCE.getName();
}
