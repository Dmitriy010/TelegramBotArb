package ru.node.clients.response.binance;

import lombok.Data;

import java.util.List;

@Data
public class BinanceResponse {

    private List<BinanceData> data;
}
