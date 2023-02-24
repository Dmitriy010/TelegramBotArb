package ru.node.clients.response.binance;

import lombok.Data;

@Data
public class Advertiser {

    private String nickName;
    private Long monthOrderCount;
    private Double monthFinishRate;
}
