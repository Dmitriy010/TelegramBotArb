package ru.node.clients.response.binance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class BinanceData {

    private Adv adv;
    private Advertiser advertiser;
}
