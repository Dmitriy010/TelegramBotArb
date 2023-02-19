package ru.node.clients.response.binance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
public class BinanceResponse {

    private List<BinanceData> data;
}
