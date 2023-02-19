package ru.node.clients.request.binance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BinanceBody {

    private Boolean proMerchantAds;
    private Integer page;
    private Integer rows;
    private List<String> payTypes;
    private List<String> countries;
    private String publisherType;
    private Integer transAmount;
    private String asset;
    private String fiat;
    private String tradeType;
}
