package ru.node.clients.response.binance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class TradeMethods {

    public String identifier;
    public String tradeMethodName;
}
