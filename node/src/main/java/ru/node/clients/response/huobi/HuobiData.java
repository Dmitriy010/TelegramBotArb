package ru.node.clients.response.huobi;

import lombok.Data;
import ru.node.enums.ExchangeEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static ru.node.constants.Constants.ZONE_ID;

@Data
public class HuobiData {
    private String userName;
    private List<PayMethod> payMethods;
    private String minTradeLimit;
    private String maxTradeLimit;
    private String price;
    private String tradeCount;
    private Integer tradeMonthTimes;
    private String orderCompleteRate;
    private LocalDateTime date = LocalDateTime.now(ZoneId.of(ZONE_ID));
    private String exchange = ExchangeEnum.HUOBI.getName();
}
