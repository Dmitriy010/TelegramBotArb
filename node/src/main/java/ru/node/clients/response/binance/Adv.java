package ru.node.clients.response.binance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
public class Adv {

    private String tradeType;
    private String asset;
    private String fiatUnit;
    private Double price;
    private String maxSingleTransAmount;
    private String minSingleTransAmount;
    private List<TradeMethods> tradeMethods;
    private String tradableQuantity;
}
